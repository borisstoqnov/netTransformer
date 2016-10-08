/*
 * iTransformer is an open source tool able to discover and transform
 *  IP network infrastructures.
 *  Copyright (C) 2012  http://itransformers.net
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.itransformers.idiscover.v2.core.parallel;

import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetails;
import net.itransformers.idiscover.api.*;
import net.itransformers.idiscover.v2.core.*;
import net.itransformers.idiscover.v2.core.factory.DiscoveryWorkerFactory;
import net.itransformers.idiscover.v2.core.factory.NodeFactory;
import net.itransformers.idiscover.api.models.network.Node;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;


public class ParallelNetworkNodeDiscovererImpl extends NetworkNodeDiscoverer {
    static Logger logger = Logger.getLogger(ParallelNetworkNodeDiscovererImpl.class);


    protected ExecutorService eventExecutorService;
    protected ExecutorCompletionService eventExecutorCompletionService;

    protected PausableThreadPoolExecutor executorService;
    protected ExecutorCompletionService<NodeDiscoveryResult> executorCompletionService;
    protected NodeFactory nodeFactory = new NodeFactory();
    protected int futureCounter;
    protected int eventFutureCount;

    protected DiscoveryWorkerFactory discoveryWorkerFactory;
    protected Set<ConnectionDetails> discoveredConnectionDetails = new HashSet<>();
    /**
     * Contains for a given parent node all the futures created for the neighbour (child) nodes.
     * We use this data structure to determine when all neighbours (children) of given parent are discovered
     */
    protected Map<String, List<Future<NodeDiscoveryResult>>> nodeNeighbourFuturesMap = new HashMap<>();
    protected Map<String, NodeDiscoveryResult> nodeDiscoveryResultMap = new HashMap<>();


    public ParallelNetworkNodeDiscovererImpl() {

    }

    public ParallelNetworkNodeDiscovererImpl(DiscoveryWorkerFactory discoveryWorkerFactory, NodeFactory nodeFactory) {
        this.discoveryWorkerFactory = discoveryWorkerFactory;
        this.nodeFactory = nodeFactory;
    }

    @Override
    public void startDiscovery(Set<ConnectionDetails> connectionDetailsList) {
        try {
            long startTime = System.currentTimeMillis();
            futureCounter = 0;
            eventFutureCount = 0;
            createInitialDiscoveryWorkers(connectionDetailsList);
            while (futureCounter > 0) {
                logger.debug("futureCounter: " + futureCounter);
                try {

                    Future<NodeDiscoveryResult> future = executorCompletionService.take();
                    futureCounter--;
                    NodeDiscoveryResult result = future.get();
                    this.fireNodeDiscoveredEvent((NodeDiscoveryResult) result.clone());
                    String nodeId = result.getNodeId();
                    String parentId = result.getParentId();
                    boolean neighbourIsDiscovered = checkIfNeighbourIsDiscovered(future, nodeId, parentId);

                    if (neighbourIsDiscovered) {
                        NodeDiscoveryResult parentDiscoveryResult = nodeDiscoveryResultMap.remove(parentId);
                        this.fireNeighboursDiscoveredEvent(parentDiscoveryResult);
                    }
                    if (nodeId == null) {
                        logger.debug("Unable to discover node with parent: " + parentId + ", for connection details=" + result.getDiscoveryConnectionDetails());
                        continue;
                    }
                    if (nodes.containsKey(nodeId)) {
                        logger.debug("Node already discovered: nodeId=" + nodeId + ", for connection details=" + result.getDiscoveryConnectionDetails());
                        continue;
                    }
                    Set<String> nodeAliases = result.getNodeAliases();
                    this.updateNodesStructure(parentId, nodeId, nodeAliases);
                    nodeDiscoveryResultMap.put(nodeId, result);
                    ArrayList<Future<NodeDiscoveryResult>> neighbourFutures = createNewDiscoveryWorkers(nodeId, result.getNeighboursConnectionDetails(), result.getOwnConnectionDetails());
                    logger.info("Adding node neighbours for discovery... nodeId=" + nodeId + ", neighbour future size=" + neighbourFutures.size());
                    if (neighbourFutures.size() > 0) {
                        nodeNeighbourFuturesMap.put(nodeId, neighbourFutures);
                    } else {
                        this.fireNeighboursDiscoveredEvent(result);
                    }
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }

            this.waitEventExecutors();
            NetworkDiscoveryResult result = new NetworkDiscoveryResult(nodes);
            this.fireNetworkDiscoveredEvent(result);
            logger.info("Shutting down discovery task executor service");
            this.executorService.shutdown();
            logger.info("Shutting down discovery event executor service");
            this.eventExecutorService.shutdown();

            logger.info("Discovery finished in " + ((System.currentTimeMillis() - startTime) / 1000) + " seconds.");
        } finally {
            nodes.clear();
            discoveredConnectionDetails.clear();
            nodeNeighbourFuturesMap.clear();
            nodeDiscoveryResultMap.clear();
        }
    }



    private boolean checkIfNeighbourIsDiscovered(Future<NodeDiscoveryResult> future, String nodeId, String parentId) {
        if (parentId != null) {
            List<Future<NodeDiscoveryResult>> parentNeighbourFutures = nodeNeighbourFuturesMap.get(parentId);
            logger.debug("Removing node neighbour for parentId=" + parentId + ", nodeId in future=" + nodeId);
            parentNeighbourFutures.remove(future);
            if (parentNeighbourFutures.isEmpty()) {
                nodeNeighbourFuturesMap.remove(parentId);
                return true;
            }
        }
        return false;
    }

    private void createInitialDiscoveryWorkers(Set<ConnectionDetails> connectionDetailsList) {
        for (ConnectionDetails connectionDetails : connectionDetailsList) {
            discoveredConnectionDetails.add(connectionDetails);
            executorCompletionService.submit(discoveryWorkerFactory.createDiscoveryWorker(nodeDiscoverers, connectionDetails, null));
            futureCounter++;
        }
    }

    private ArrayList<Future<NodeDiscoveryResult>> createNewDiscoveryWorkers(String nodeId,
                                                                             Set<ConnectionDetails> aNeighboursConnectionDetailsSet,
                                                                             Set<ConnectionDetails> aOwnConnectionDetailsSet
                                                                             ) {
        Set<ConnectionDetails> neighboursConnectionDetailsSet = new HashSet<>(aNeighboursConnectionDetailsSet);
        discoveredConnectionDetails.addAll(aOwnConnectionDetailsSet);

        neighboursConnectionDetailsSet.removeAll(discoveredConnectionDetails);

        ArrayList<Future<NodeDiscoveryResult>> neighbourFutures = new ArrayList<>();

        for (ConnectionDetails neighboursConnectionDetails : neighboursConnectionDetailsSet) {
            discoveredConnectionDetails.add(neighboursConnectionDetails);
            DiscoveryWorker discoveryWorker = discoveryWorkerFactory.createDiscoveryWorker(nodeDiscoverers, neighboursConnectionDetails, nodeId);
            Future<NodeDiscoveryResult> nodeNeighbourFuture = executorCompletionService.submit(discoveryWorker);
            neighbourFutures.add(nodeNeighbourFuture);
            futureCounter++;
        }
        return neighbourFutures;
    }

    private void updateNodesStructure(String parentId, String nodeId, Set<String> nodeAliases) {
        Node node = null;
        if (nodeAliases != null){
            for (String nodeAliase : nodeAliases) {
                Node foundNodeByAlias = nodesByAliases.get(nodeAliase);
                if (foundNodeByAlias != null) {
                    node = foundNodeByAlias;
                    break;
                }
            }

        }
        if (node == null) {
            node = nodeFactory.createNode(nodeId);
        }
        node.setAliases(nodeAliases);
        nodes.put(nodeId, node);
        if (nodeAliases!=null) {
            for (String nodeAliase : nodeAliases) {
                nodesByAliases.put(nodeAliase, node);

            }
        }

        Node parentNode = nodes.get(parentId);

        if (parentNode != null) {
            parentNode.addNeighbour(node);
        }
    }

    private void waitEventExecutors() {
        while (eventFutureCount > 0) {
            try {
                eventFutureCount--;
                eventExecutorCompletionService.take();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
        logger.info("Shutting down event executor service");



    }

    public synchronized boolean isStopped() {
        return executorService.isTerminated();
    }

    public synchronized boolean isRunning() {
        return !executorService.isTerminated();

    }

    public synchronized void fireNodeDiscoveredEvent(final NodeDiscoveryResult discoveryResult) {
        if (nodeDiscoveryListeners != null) {
            for (final NodeDiscoveryListener nodeDiscoveryListener : nodeDiscoveryListeners) {
                eventFutureCount++;
                eventExecutorCompletionService.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            nodeDiscoveryListener.nodeDiscovered(discoveryResult);
                        }catch (RuntimeException rte){
                            logger.error(rte.getMessage(),rte);
                        }
                    }
                }, null);
            }
        }
    }

    protected void fireNeighboursDiscoveredEvent(final NodeDiscoveryResult nodeDiscoveryResult) {
        if (nodeNeighbourDiscoveryListeners != null) {
            String nodeId = nodeDiscoveryResult.getNodeId();

            final Node node = nodes.get(nodeId);
            for (final NodeNeighboursDiscoveryListener nodeNeighboursDiscoveryListener : nodeNeighbourDiscoveryListeners) {
                eventFutureCount++;
                eventExecutorCompletionService.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            nodeNeighboursDiscoveryListener.handleNodeNeighboursDiscovered(node, nodeDiscoveryResult);
                        }catch (RuntimeException rte){
                            logger.error(rte.getMessage(),rte);
                        }
                    }
                }, null);

            }
        }
    }

    protected void fireNetworkDiscoveredEvent(final NetworkDiscoveryResult result) {
        if (networkDiscoveryListeners != null)
            for (final NetworkDiscoveryListener networkDiscoveryListener : networkDiscoveryListeners) {
                eventFutureCount++;
                eventExecutorCompletionService.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            networkDiscoveryListener.networkDiscovered(result);
                        }catch (RuntimeException rte){
                            logger.error(rte.getMessage(),rte);
                        }
                    }
                }, null);

            }
    }

    public ExecutorService getEventExecutorService() {
        return eventExecutorService;
    }

    public void setEventExecutorService(ExecutorService eventExecutorService) {
        this.eventExecutorService = eventExecutorService;
    }

    public ExecutorCompletionService getEventExecutorCompletionService() {
        return eventExecutorCompletionService;
    }

    public void setEventExecutorCompletionService(ExecutorCompletionService eventExecutorCompletionService) {
        this.eventExecutorCompletionService = eventExecutorCompletionService;
    }

    public PausableThreadPoolExecutor getExecutorService() {
        return executorService;
    }

    public void setExecutorService(PausableThreadPoolExecutor executorService) {
        this.executorService = executorService;
    }

    public ExecutorCompletionService<NodeDiscoveryResult> getExecutorCompletionService() {
        return executorCompletionService;
    }

    public void setExecutorCompletionService(ExecutorCompletionService<NodeDiscoveryResult> executorCompletionService) {
        this.executorCompletionService = executorCompletionService;
    }

    public int getFutureCounter() {
        return futureCounter;
    }

    public int getEventFutureCount() {
        return eventFutureCount;
    }

    public Set<String> getNodeNeighbourFuturesMapKeys() {
        return new HashSet<>(nodeNeighbourFuturesMap.keySet());
    }

    public Set<ConnectionDetails> getDiscoveredConnectionDetails() {
        return discoveredConnectionDetails;
    }

    public Set<String> getNodeDiscoveryResultMapKeys() {
        return new HashSet<>(nodeDiscoveryResultMap.keySet());
    }

    public DiscoveryWorkerFactory getDiscoveryWorkerFactory() {
        return discoveryWorkerFactory;
    }

    public void setDiscoveryWorkerFactory(DiscoveryWorkerFactory discoveryWorkerFactory) {
        this.discoveryWorkerFactory = discoveryWorkerFactory;
    }


    @Override
    public synchronized void stopDiscovery() {
        executorService.shutdown();
    }

    @Override
    public void pauseDiscovery() {
        this.executorService.pause();
    }

    @Override
    public void resumeDiscovery() {
        this.executorService.resume();
    }

}