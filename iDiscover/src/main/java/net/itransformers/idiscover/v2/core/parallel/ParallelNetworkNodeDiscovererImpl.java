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

    ExecutorService eventExecutorService = Executors.newFixedThreadPool(10);
    ExecutorCompletionService eventExecutorCompletionService = new ExecutorCompletionService(eventExecutorService);
    Collection<Future> eventFutures = new LinkedList<Future>();
    ExecutorService executorService = Executors.newFixedThreadPool(10);
    ExecutorCompletionService<NodeDiscoveryResult> executorCompletionService = new ExecutorCompletionService<NodeDiscoveryResult>(executorService);
    NodeFactory nodeFactory = new NodeFactory();

    DiscoveryWorkerFactory discoveryWorkerFactory = new DiscoveryWorkerFactory();

    public NetworkDiscoveryResult discoverNetwork(Set<ConnectionDetails> connectionDetailsList, int depth) {
        nodes.clear();
        Set<ConnectionDetails> discoveredConnectionDetais = new HashSet<ConnectionDetails>();
        Map<String, List<Future<NodeDiscoveryResult>>> nodeNeighbourFuturesMap = new HashMap<String, List<Future<NodeDiscoveryResult>>>();
        Map<String, NodeDiscoveryResult> nodeDiscoveryResultMap = new HashMap<String, NodeDiscoveryResult>();

        int futureCounter = 0;
        for (ConnectionDetails connectionDetails : connectionDetailsList) {
            discoveredConnectionDetais.add(connectionDetails);
            executorCompletionService.submit(discoveryWorkerFactory.createDiscoveryWorker(nodeDiscoverers, connectionDetails));
            futureCounter++;
        }
        while (futureCounter > 0) {
            try {
                Future<NodeDiscoveryResult> future = executorCompletionService.take();
                futureCounter--;
                NodeDiscoveryResult result = future.get();
                if (result.getParentId() != null) {
                    List<Future<NodeDiscoveryResult>> parentNeighbourFutures = nodeNeighbourFuturesMap.get(result.getParentId());
                    parentNeighbourFutures.remove(future);
                    if (parentNeighbourFutures.isEmpty()) {
                        NodeDiscoveryResult parentDiscoveryResult = nodeDiscoveryResultMap.remove(result.getParentId());
                        nodeNeighbourFuturesMap.remove(result.getParentId());
                        fireNeighboursDiscoveredEvent(parentDiscoveryResult);
                    }
                }
                if (result.getNodeId() != null) {
                    createNode(result);
                    nodeDiscoveryResultMap.put(result.getNodeId(), result);
                    fireNodeDiscoveredEvent(result);
                    Set<ConnectionDetails> neighboursConnectionDetailsSet = result.getNeighboursConnectionDetails();
                    neighboursConnectionDetailsSet.removeAll(discoveredConnectionDetais);
                    ArrayList<Future<NodeDiscoveryResult>> neighboutFutures = new ArrayList<Future<NodeDiscoveryResult>>();
                    nodeNeighbourFuturesMap.put(result.getNodeId(), neighboutFutures);
                    for (ConnectionDetails neighboursConnectionDetails : neighboursConnectionDetailsSet) {
                        discoveredConnectionDetais.add(neighboursConnectionDetails);
                        DiscoveryWorker discoveryWorker = new DiscoveryWorker(nodeDiscoverers, neighboursConnectionDetails);
                        Future<NodeDiscoveryResult> nodeNeighbourFuture = executorCompletionService.submit(discoveryWorker);
                        neighboutFutures.add(nodeNeighbourFuture);
                        futureCounter++;
                    }
                }
            } catch (InterruptedException e) {
                logger.error(e.getMessage(), e);
            } catch (ExecutionException e) {
                logger.error(e.getMessage(), e);
            }
        }
        NetworkDiscoveryResult result = new NetworkDiscoveryResult();
        result.setNodes(nodes);
        fireNetworkDiscoveredEvent(result);
        for (Future eventFuture : eventFutures) {
            try {
                eventFuture.get();
            } catch (InterruptedException e) {
                logger.error(e.getMessage(),e);
            } catch (ExecutionException e) {
                logger.error(e.getMessage(),e);
            }
        }
        return result;
    }

    private void createNode(NodeDiscoveryResult result) {
        Node node = nodeFactory.createNode(result.getNodeId());
        nodes.put(result.getNodeId(),node);
        Node parentNode = nodes.get(result.getParentId());
        if (parentNode != null) {
            parentNode.addNeighbour(node);
        }
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


    public synchronized void fireNodeDiscoveredEvent(final NodeDiscoveryResult discoveryResult) {
        if (nodeDiscoveryListeners != null) {
            for (final NodeDiscoveryListener nodeDiscoveryListener : nodeDiscoveryListeners) {
                eventFutures.add(eventExecutorCompletionService.submit(new Runnable() {
                    @Override
                    public void run() {
                        nodeDiscoveryListener.nodeDiscovered(discoveryResult);
                    }
                }, null));
            }
        }
    }

    protected void fireNeighboursDiscoveredEvent(final NodeDiscoveryResult nodeDiscoveryResult) {
        if (nodeNeighbourDiscoveryListeners != null) {
            String nodeId = nodeDiscoveryResult.getNodeId();
            final Node node = nodes.get(nodeId);
            for (final NodeNeighboursDiscoveryListener nodeNeighboursDiscoveryListener : nodeNeighbourDiscoveryListeners) {
                eventFutures.add(eventExecutorCompletionService.submit(new Runnable() {
                    @Override
                    public void run() {
                        nodeNeighboursDiscoveryListener.handleNodeNeighboursDiscovered(node, nodeDiscoveryResult);
                    }
                },null));

            }
        }
    }

    protected void fireNetworkDiscoveredEvent(final NetworkDiscoveryResult result) {
        if (networkDiscoveryListeners != null)
            for (final NetworkDiscoveryListener networkDiscoveryListener : networkDiscoveryListeners) {
                eventFutures.add(eventExecutorCompletionService.submit(new Runnable() {
                    @Override
                    public void run() {
                        networkDiscoveryListener.networkDiscovered(result);
                    }
                },null));

            }
    }
}