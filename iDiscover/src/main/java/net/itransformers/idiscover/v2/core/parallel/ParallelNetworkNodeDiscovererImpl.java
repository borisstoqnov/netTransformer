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
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.idiscover.v2.core.model.Node;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ParallelNetworkNodeDiscovererImpl extends NetworkNodeDiscoverer implements DiscoveryWorkerContext {
    static Logger logger = Logger.getLogger(ParallelNetworkNodeDiscovererImpl.class);
    private boolean isRunning;
    private boolean isPaused;
    private boolean isStopped;

    private final Map<String, Node> nodes = new HashMap<String, Node>();

    ExecutorService executor = Executors.newFixedThreadPool(5);

    public NetworkDiscoveryResult discoverNetwork(List<ConnectionDetails> connectionDetailsList, int depth) {
        nodes.clear();
        for (ConnectionDetails connectionDetails : connectionDetailsList) {
            Runnable discoveryWork = new DiscoveryWorker(connectionDetails, null, 1, this);
            executor.execute(discoveryWork);
        }
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        isRunning = true;
        NetworkDiscoveryResult result = new NetworkDiscoveryResult();
        result.setNodes(nodes);
        fireNetworkDiscoveredEvent(result);
        return result;
    }

    public synchronized void pause() {
        isPaused = true;
    }

    public synchronized void resume() {
        isPaused = false;
        notifyAll();
    }

    public synchronized void stop() {
        isStopped = true;
        isRunning = false;
    }

    public synchronized boolean isStopped() {
        return isStopped;
    }

    public synchronized boolean isPaused() {
        return isPaused;
    }

    public synchronized boolean isRunning() {
        return isRunning;
    }

    @Override
    public void createAndExecuteNewWorker(List<ConnectionDetails> connDetails, Node currentNode, int level) {
        for (ConnectionDetails connDetail : connDetails) {
            executor.execute(new DiscoveryWorker(connDetail,currentNode,level,this));
        }

    }

    @Override
    public Map<String, Node> getNodes() {
        return nodes;
    }

    @Override
    public NodeDiscoverer getNodeDiscoverer(String connectionType) {
        return nodeDiscoverers.get(connectionType);
    }
}