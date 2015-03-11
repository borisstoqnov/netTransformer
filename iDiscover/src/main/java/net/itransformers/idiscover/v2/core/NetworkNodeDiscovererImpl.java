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

package net.itransformers.idiscover.v2.core;

import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.idiscover.v2.core.model.Node;
import org.apache.log4j.Logger;

import java.util.*;

public class NetworkNodeDiscovererImpl extends NetworkNodeDiscoverer {
    static Logger logger = Logger.getLogger(NetworkNodeDiscovererImpl.class);
    private boolean isRunning;
    private boolean isPaused;
    private boolean isStopped;

    public NetworkDiscoveryResult discoverNetwork(List<ConnectionDetails> connectionDetailsList, int depth) {
        isRunning = true;
        Map<String, Node> nodes = new HashMap<String, Node>();
        NetworkDiscoveryResult result = new NetworkDiscoveryResult();
        doDiscoverNodes(connectionDetailsList, nodes, null, 0, depth, result);
        result.setNodes(nodes);
        fireNetworkDiscoveredEvent(result);
        stop();
        return result;
    }

    void doDiscoverNodes(List<ConnectionDetails> connectionDetailsList, Map<String, Node> nodes,
                         Node initialNode, int level, int depth, NetworkDiscoveryResult result) {
        for (ConnectionDetails connectionDetails : connectionDetailsList) {
            if (isStopped) return;
            if (isPaused) doPause();
            String connectionType = connectionDetails.getConnectionType();
            NodeDiscoverer nodeDiscoverer = nodeDiscoverers.get(connectionType);
            if (nodeDiscoverer == null) {
                logger.info("No Node Discoverers found");

                return;
            }
            if (level == depth) return;

            if (nodeDiscoverFilter != null && nodeDiscoverFilter.match(connectionDetails)) return;
            NodeDiscoveryResult discoveryResult = nodeDiscoverer.discover(connectionDetails);
            if (discoveryResult == null) return; // in case some error during snmpDiscovery
//            if (result != null) result.addDiscoveredData(discoveryResult.getNodeId(),discoveryResult);
            fireNodeDiscoveredEvent(discoveryResult);
            String nodeId = discoveryResult.getNodeId();
            Node currentNode = new Node(nodeId, Arrays.asList(connectionDetails));
            nodes.put(nodeId, currentNode);
            if (initialNode != null) initialNode.addNeighbour(currentNode);
            List<ConnectionDetails> neighboursConnectionDetails = discoveryResult.getNeighboursConnectionDetails();
            logger.debug("Found Neighbours, connection details: " + neighboursConnectionDetails);
            if (neighboursConnectionDetails != null) {
                doDiscoverNodes(neighboursConnectionDetails, nodes, currentNode, level + 1, depth, result);
            }
        }
    }


    private synchronized void doPause() {

        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
}