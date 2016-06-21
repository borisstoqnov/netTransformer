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

import net.itransformers.idiscover.v2.core.NetworkDiscoveryResult;
import net.itransformers.idiscover.v2.core.NetworkNodeDiscoverer;
import net.itransformers.idiscover.v2.core.NodeDiscoverer;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
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
    ExecutorService executorService = Executors.newFixedThreadPool(10);
    ExecutorCompletionService<NodeDiscoveryResult> pool = new ExecutorCompletionService<NodeDiscoveryResult>(executorService);
    NodeFactory nodeFactory = new NodeFactory();


    DiscoveryWorkerFactory discoveryWorkerFactory = new DiscoveryWorkerFactory();

    public NetworkDiscoveryResult discoverNetwork(Set<ConnectionDetails> connectionDetailsList, int depth) {
        nodes.clear();
        int futureCounter = 0;
        Set<ConnectionDetails> discoveredConnectionDetais = new HashSet<ConnectionDetails>();
        for (ConnectionDetails connectionDetails : connectionDetailsList) {
            discoveredConnectionDetais.add(connectionDetails);
            pool.submit(discoveryWorkerFactory.createDiscoveryWorker(nodeDiscoverers, connectionDetails));
            futureCounter++;
        }
        Map<String,List<Future<NodeDiscoveryResult>>> nodeNeighbourFuturesMap =
                new HashMap<String, List<Future<NodeDiscoveryResult>>>();
        while (futureCounter > 0) {
            try {
                Future<NodeDiscoveryResult> future = pool.take();
                futureCounter--;
                NodeDiscoveryResult result = future.get();
                fireNodeDiscoveredEvent(result);
                List<Future<NodeDiscoveryResult>> parentNeighbourFutures = nodeNeighbourFuturesMap.get(result.getParentId());
                parentNeighbourFutures.remove(future);
                if (parentNeighbourFutures.isEmpty()) {
//                    fireNeighboursDiscoveredEvent();
                }
                if (result.getNodeId() != null) {
                    Set<ConnectionDetails> neighboursConnectionDetailsSet = result.getNeighboursConnectionDetails();
                    neighboursConnectionDetailsSet.removeAll(discoveredConnectionDetais);
                    ArrayList<Future<NodeDiscoveryResult>> neighboutFutures = new ArrayList<Future<NodeDiscoveryResult>>();
                    nodeNeighbourFuturesMap.put(result.getNodeId(), neighboutFutures);
                    for (ConnectionDetails neighboursConnectionDetails : neighboursConnectionDetailsSet) {
                        discoveredConnectionDetais.add(neighboursConnectionDetails);
                        DiscoveryWorker discoveryWorker = new DiscoveryWorker(nodeDiscoverers, neighboursConnectionDetails);
                        Future<NodeDiscoveryResult> nodeNeighbourFuture = pool.submit(discoveryWorker);
                        neighboutFutures.add(nodeNeighbourFuture);
                        futureCounter++;
                    }
                }
            } catch (InterruptedException e) {
                logger.error(e.getMessage(),e);
            } catch (ExecutionException e) {
                logger.error(e.getMessage(), e);
            }
        }
        NetworkDiscoveryResult result = new NetworkDiscoveryResult();
        result.setNodes(nodes);
        fireNetworkDiscoveredEvent(result);
        eventExecutorService.shutdown();
//        eventExecutorService.awaitTermination();
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
}