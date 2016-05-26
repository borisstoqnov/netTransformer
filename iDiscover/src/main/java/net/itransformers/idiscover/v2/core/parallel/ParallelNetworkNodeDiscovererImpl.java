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
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.idiscover.v2.core.model.Node;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.*;


public class ParallelNetworkNodeDiscovererImpl extends NetworkNodeDiscoverer implements DiscoveryWorkerContext {
    static Logger logger = Logger.getLogger(ParallelNetworkNodeDiscovererImpl.class);

    private Set<ConnectionDetails> usedConnectionDetails = new HashSet<ConnectionDetails>();

    ForkJoinPool pool = new ForkJoinPool();

    public NetworkDiscoveryResult discoverNetwork(Set<ConnectionDetails> connectionDetailsList, int depth) {
        nodes.clear();
        List<DiscoveryWorker> discoveryWorkerList = new ArrayList<DiscoveryWorker>();
        for (ConnectionDetails connectionDetails : connectionDetailsList) {
            usedConnectionDetails.add(connectionDetails);
            DiscoveryWorker discoveryWork = new DiscoveryWorker(connectionDetails, null, 1, this);
            discoveryWorkerList.add(discoveryWork);
        }
        for (DiscoveryWorker discoveryWorker : discoveryWorkerList) {
            pool.invoke(discoveryWorker);
        }
        NetworkDiscoveryResult result = new NetworkDiscoveryResult();
        result.setNodes(nodes);
        fireNetworkDiscoveredEvent(result);
        return result;
    }

    public synchronized void stop() {
        pool.shutdown();
    }

    public synchronized boolean isStopped() {
        return pool.isTerminated();
    }

    public synchronized boolean isRunning() {
        return !pool.isTerminated();
    }

    @Override
    public Map<String, Node> getNodes() {
        return nodes;
    }

    @Override
    public NodeDiscoverer getNodeDiscoverer(String connectionType) {
        return nodeDiscoverers.get(connectionType);
    }

    @Override
    public Set<ConnectionDetails> getUsedConnectionDetails() {
        return usedConnectionDetails;
    }
}