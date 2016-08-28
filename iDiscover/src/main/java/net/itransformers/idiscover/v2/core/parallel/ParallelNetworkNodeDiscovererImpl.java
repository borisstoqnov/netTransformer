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

import net.itransformers.idiscover.v2.core.*;
import net.itransformers.idiscover.v2.core.factory.DiscoveryWorkerFactory;
import net.itransformers.idiscover.v2.core.factory.NodeFactory;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.idiscover.v2.core.model.Node;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.*;


public class ParallelNetworkNodeDiscovererImpl extends NetworkNodeDiscoverer {
    static Logger logger = Logger.getLogger(ParallelNetworkNodeDiscovererImpl.class);

    ExecutorService eventExecutorService = Executors.newFixedThreadPool(50);
    ExecutorCompletionService eventExecutorCompletionService = new ExecutorCompletionService(eventExecutorService);
    Collection<Future> eventFutures = new LinkedList<Future>();
    PausableThreadPoolExecutor executorService = //Executors.newFixedThreadPool(50);
            new PausableThreadPoolExecutor(50, 50,
                    0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>());
    ExecutorCompletionService<NodeDiscoveryResult> executorCompletionService = new ExecutorCompletionService<NodeDiscoveryResult>(executorService);
    NodeFactory nodeFactory = new NodeFactory();
    int eventFutureCount;
    DiscoveryWorkerFactory discoveryWorkerFactory = new DiscoveryWorkerFactory();

    public ParallelNetworkNodeDiscovererImpl() {
    }

    public ParallelNetworkNodeDiscovererImpl(DiscoveryWorkerFactory discoveryWorkerFactory, NodeFactory nodeFactory) {
        this.discoveryWorkerFactory = discoveryWorkerFactory;
        this.nodeFactory = nodeFactory;
    }

    public NetworkDiscoveryResult discoverNetwork(Set<ConnectionDetails> connectionDetailsList, int depth) {
        long startTime = System.currentTimeMillis();
        nodes.clear();
        Set<ConnectionDetails> discoveredConnectionDetails = new HashSet<ConnectionDetails>();
        Map<String, List<Future<NodeDiscoveryResult>>> nodeNeighbourFuturesMap = new HashMap<String, List<Future<NodeDiscoveryResult>>>();
        Map<String, NodeDiscoveryResult> nodeDiscoveryResultMap = new HashMap<String, NodeDiscoveryResult>();
        Map<String, Map<String, ConnectionDetails>> neighbourConnectionDetailsMap = new HashMap<>();

        int futureCounter = 0;
        eventFutureCount = 0;
        for (ConnectionDetails connectionDetails : connectionDetailsList) {
            discoveredConnectionDetails.add(connectionDetails);
            executorCompletionService.submit(discoveryWorkerFactory.createDiscoveryWorker(nodeDiscoverers, connectionDetails, null));
            futureCounter++;
        }
        while (futureCounter > 0) {
            logger.debug("futureCounter: "+futureCounter);
            try {
                Future<NodeDiscoveryResult> future = executorCompletionService.take();
                futureCounter--;
                NodeDiscoveryResult result = future.get();
                fireNodeDiscoveredEvent((NodeDiscoveryResult) result.clone());
                String parentId = result.getParentId();
                if (parentId != null) {
                    List<Future<NodeDiscoveryResult>> parentNeighbourFutures = nodeNeighbourFuturesMap.get(parentId);
                    logger.info("Removing node neighbour for parentId=" + parentId + ", nodeId in future=" + future.get().getNodeId());
                    parentNeighbourFutures.remove(future);
                    if (result.getNodeId() != null) {
                        neighbourConnectionDetailsMap.get(parentId).put(result.getNodeId(), result.getDiscoveryConnectionDetails());
                    }
                    if (parentNeighbourFutures.isEmpty()) {
                        NodeDiscoveryResult parentDiscoveryResult = nodeDiscoveryResultMap.remove(parentId);
                        nodeNeighbourFuturesMap.remove(parentId);
                        Map<String, ConnectionDetails> neighbourConnectionDetails = neighbourConnectionDetailsMap.remove(parentId);
                        fireNeighboursDiscoveredEvent(parentDiscoveryResult, neighbourConnectionDetails);
                    }
                }
                String nodeId = result.getNodeId();
                if (nodeId == null) {
                    logger.info("Unable to discover node with parent: " + parentId+", for connection details="+result.getDiscoveryConnectionDetails());
                    continue;
                }
                if (nodes.containsKey(nodeId)) {
                    logger.info("Node already discovered: nodeId=" + nodeId+", for connection details="+result.getDiscoveryConnectionDetails());
                    continue;
                }
                Node node = nodeFactory.createNode(nodeId);
                nodes.put(nodeId, node);
                Node parentNode = nodes.get(parentId);
                if (parentNode != null) {
                    parentNode.addNeighbour(node);
                }

                nodeDiscoveryResultMap.put(nodeId, result);
                neighbourConnectionDetailsMap.put(nodeId, new HashMap<>());
                Set<ConnectionDetails> neighboursConnectionDetailsSet = new HashSet<ConnectionDetails>(result.getNeighboursConnectionDetails());
                neighboursConnectionDetailsSet.removeAll(discoveredConnectionDetails);
                ArrayList<Future<NodeDiscoveryResult>> neighbourFutures = new ArrayList<Future<NodeDiscoveryResult>>();

                for (ConnectionDetails neighboursConnectionDetails : neighboursConnectionDetailsSet) {
                    discoveredConnectionDetails.add(neighboursConnectionDetails);
                    DiscoveryWorker discoveryWorker = discoveryWorkerFactory.createDiscoveryWorker(nodeDiscoverers, neighboursConnectionDetails, nodeId);
                    Future<NodeDiscoveryResult> nodeNeighbourFuture = executorCompletionService.submit(discoveryWorker);

                    neighbourFutures.add(nodeNeighbourFuture);
                    futureCounter++;
                }
                logger.info("Adding node neighbours for discovery... nodeId=" + nodeId+", neighbour future size="+neighbourFutures.size());
                nodeNeighbourFuturesMap.put(nodeId, neighbourFutures);
            } catch (Exception e ) {
                logger.error(e.getMessage(), e);
            }
        }
        NetworkDiscoveryResult result = new NetworkDiscoveryResult();
        result.setNodes(nodes);
        fireNetworkDiscoveredEvent(result);
        while (eventFutureCount > 0) {
            try {
                eventFutureCount--;
                eventExecutorCompletionService.take();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            }
        }
        logger.info("Shutting down event executor service");
        eventExecutorService.shutdown();
        logger.info("Shutting down discovery task executor service");
        executorService.shutdown();
        logger.info("Discovery finished in "+((System.currentTimeMillis()-startTime)/1000) +" seconds.");
        return result;
    }

    public synchronized void stop() {
        executorService.shutdown();
    }

    public synchronized boolean isStopped() {
        return executorService.isTerminated();
    }

    public synchronized boolean isRunning() {
        return !executorService.isTerminated();

    }

    public synchronized void pause() {
        this.executorService.pause();
    }

    public synchronized void resume() {
        this.executorService.resume();
    }

    public synchronized void fireNodeDiscoveredEvent(final NodeDiscoveryResult discoveryResult) {
        if (nodeDiscoveryListeners != null) {
            for (final NodeDiscoveryListener nodeDiscoveryListener : nodeDiscoveryListeners) {
                eventFutureCount++;
                eventFutures.add(eventExecutorCompletionService.submit(new Runnable() {
                    @Override
                    public void run() {
                        nodeDiscoveryListener.nodeDiscovered(discoveryResult);
                    }
                }, null));
            }
        }
    }

    protected void fireNeighboursDiscoveredEvent(final NodeDiscoveryResult nodeDiscoveryResult, Map<String, ConnectionDetails> discoveredNeighbourConnections) {
        if (nodeNeighbourDiscoveryListeners != null) {
            String nodeId = nodeDiscoveryResult.getNodeId();

            final Node node = nodes.get(nodeId);
            for (final NodeNeighboursDiscoveryListener nodeNeighboursDiscoveryListener : nodeNeighbourDiscoveryListeners) {
                eventFutureCount++;
                eventFutures.add(eventExecutorCompletionService.submit(new Runnable() {
                    @Override
                    public void run() {
                        nodeNeighboursDiscoveryListener.handleNodeNeighboursDiscovered(node, nodeDiscoveryResult, discoveredNeighbourConnections);
                    }
                }, null));

            }
        }
    }

    protected void fireNetworkDiscoveredEvent(final NetworkDiscoveryResult result) {
        if (networkDiscoveryListeners != null)
            for (final NetworkDiscoveryListener networkDiscoveryListener : networkDiscoveryListeners) {
                eventFutureCount++;
                eventFutures.add(eventExecutorCompletionService.submit(new Runnable() {
                    @Override
                    public void run() {
                        networkDiscoveryListener.networkDiscovered(result);
                    }
                }, null));

            }
    }
}