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
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.*;

public class NetworkDiscoverer {
    static Logger logger = Logger.getLogger(NetworkDiscoverer.class);
    Map<String, NodeDiscoverer> nodeDiscoverers;
    List<NodeDiscoveryListener> nodeDiscoveryListeners;
    List<NetworkDiscoveryListener> networkDiscoveryListeners;
    private boolean isRunning;
    private boolean isPaused;
    private boolean isStopped;


    //    List<NetworkDiscoveryListener> networkDiscoveryListeners;
    NodeDiscoverFilter nodeDiscoverFilter;


    public Map<String, Node> discoverNodes(List<ConnectionDetails> connectionDetailsList) {
        isRunning=true;
        Map<String, Node> nodes = new HashMap<String, Node>();
        nodes=discoverNodes(connectionDetailsList, -1);
        fireNetworkDiscoveredEvent(nodes);
        stop();
        return nodes;

    }

    public Map<String, Node> discoverNodes(List<ConnectionDetails> connectionDetailsList, int depth) {
        isRunning=true;
        Map<String, Node> nodes = new HashMap<String, Node>();
        doDiscoverNodes(connectionDetailsList, nodes, null, 0, depth);
        fireNetworkDiscoveredEvent(nodes);
        stop();
        return nodes;
    }

    void doDiscoverNodes(List<ConnectionDetails> connectionDetailsList, Map<String, Node> nodes,
                         Node initialNode, int level, int depth) {
        for (ConnectionDetails connectionDetails : connectionDetailsList) {
            if (isStopped) return;
            if (isPaused) doPause();
            String connectionType = connectionDetails.getConnectionType();
            NodeDiscoverer nodeDiscoverer = nodeDiscoverers.get(connectionType);
            if (level == depth) return;
            // Limit snmpDiscovery by Filter
            if (nodeDiscoverFilter != null && nodeDiscoverFilter.match(connectionDetails)) return;
            String nodeId = nodeDiscoverer.probe(connectionDetails);

            if (nodeId != null) {

                if (logger.getLevel() == Level.INFO) {
                    logger.info("Node discovered: '" + nodeId + "'");
                } else {
                    logger.debug("Node '" + nodeId + "' discovered with connection details: " + connectionDetails);
                }
                Node currentNode = nodes.get(nodeId);
                if (currentNode == null) {
                    NodeDiscoveryResult discoveryResult = nodeDiscoverer.discover(connectionDetails);
                    if (discoveryResult == null) return; // in case some error during snmpDiscovery
                    fireNodeDiscoveredEvent(discoveryResult);

                    //String nodeId = discoveryResult.getNodeId();


                    currentNode = new Node(nodeId, Arrays.asList(connectionDetails));
                    nodes.put(nodeId, currentNode);
                    if (initialNode != null) initialNode.addNeighbour(currentNode);
                    List<ConnectionDetails> neighboursConnectionDetails = discoveryResult.getNeighboursConnectionDetails();
                    logger.debug("Found Neighbours, connection details: " + neighboursConnectionDetails);
                    if (neighboursConnectionDetails != null) {
                        doDiscoverNodes(neighboursConnectionDetails, nodes, currentNode, level + 1, depth);
                    }
                } else {
                    logger.debug("Node '" + currentNode.getId() + "' is already discovered. Skipping it.");
                    return;
                }


            }
        }
    }

    private void fireNodeDiscoveredEvent(NodeDiscoveryResult discoveryResult) {
        if (nodeDiscoveryListeners != null)
            for (NodeDiscoveryListener nodeDiscoveryListener : nodeDiscoveryListeners) {
                nodeDiscoveryListener.nodeDiscovered(discoveryResult);
            }
    }

    private void fireNetworkDiscoveredEvent(Map<String, Node> network) {
        if (networkDiscoveryListeners != null)
            for (NetworkDiscoveryListener networkDiscoveryListener : networkDiscoveryListeners) {
                networkDiscoveryListener.networkDiscovered(network);
            }
    }

    public void setNodeDiscoverers(Map<String, NodeDiscoverer> nodeDiscoverers) {
        this.nodeDiscoverers = nodeDiscoverers;
    }

    public void setNodeDiscoverFilter(NodeDiscoverFilter filter) {
        this.nodeDiscoverFilter = filter;
    }

    public List<NodeDiscoveryListener> getNodeDiscoveryListeners() {
        return nodeDiscoveryListeners;
    }

    public void setNodeDiscoveryListeners(List<NodeDiscoveryListener> nodeDiscoveryListeners) {
        this.nodeDiscoveryListeners = nodeDiscoveryListeners;
    }

    public List<NetworkDiscoveryListener> getNetworkDiscoveryListeners() {
        return networkDiscoveryListeners;
    }

    public void setNetworkDiscoveryListeners(List<NetworkDiscoveryListener> networkDiscoveryListeners) {
        this.networkDiscoveryListeners = networkDiscoveryListeners;
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