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
    NodeDiscoverFilter nodeDiscoverFilter;

    public Map<String,Node> discoverNodes(List<ConnectionDetails> connectionDetailsList) {
        return discoverNodes(connectionDetailsList, -1);
    }

    public Map<String,Node> discoverNodes(List<ConnectionDetails> connectionDetailsList, int depth) {
        Map<String,Node> nodes = new HashMap<String, Node>();
        doDiscoverNodes(connectionDetailsList, nodes, null, 0, depth);
        return nodes;
    }

    void doDiscoverNodes(List<ConnectionDetails> connectionDetailsList, Map<String, Node> nodes,
                                 Node initialNode, int level, int depth) {
        for (ConnectionDetails connectionDetails : connectionDetailsList) {
            String connectionType = connectionDetails.getConnectionType();
            NodeDiscoverer nodeDiscoverer = nodeDiscoverers.get(connectionType);
            // Limit discovery by depth
            if (level == depth) return;
            // Limit discovery by Filter
            if (nodeDiscoverFilter != null && nodeDiscoverFilter.match(connectionDetails)) return;
            NodeDiscoveryResult discoveryResult = nodeDiscoverer.discover(connectionDetails);
            if (discoveryResult == null) return; // in case some error during discovery
            fireNodeDiscoveredEvent(discoveryResult);

            String nodeId = discoveryResult.getNodeId();

            Node currentNode = nodes.get(nodeId);
            if (currentNode == null) {
                if (logger.getLevel() == Level.INFO) {
                    logger.info("Discovered node: '"+nodeId+"'");
                } else {
                    logger.debug("Node '"+nodeId+"' discovered with connection details: "+connectionDetails);
                }
                currentNode = new Node(nodeId,Arrays.asList(connectionDetails));
                nodes.put(nodeId, currentNode);
            } else {
                logger.debug("Node '" + currentNode.getId() + "' is already discovered. Skipping it.");
                return;
            }

            if (initialNode != null) initialNode.addNeighbour(currentNode);
            List<ConnectionDetails> neighboursConnectionDetails = discoveryResult.getNeighboursConnectionDetails();
            logger.debug("Found Neighbours, connection details: "+neighboursConnectionDetails);
            doDiscoverNodes(neighboursConnectionDetails, nodes, currentNode, level+1, depth);
        }
    }

    private void fireNodeDiscoveredEvent(NodeDiscoveryResult discoveryResult) {
        if (nodeDiscoveryListeners != null)
        for (NodeDiscoveryListener nodeDiscoveryListener : nodeDiscoveryListeners) {
            nodeDiscoveryListener.nodeDiscovered(discoveryResult);
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
}