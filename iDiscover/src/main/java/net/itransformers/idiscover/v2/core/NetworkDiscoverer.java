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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkDiscoverer {
    static Logger logger = Logger.getLogger(NetworkDiscoverer.class);
    Map<String, NodeDiscoverer> nodeDiscoverers;
    NodeDiscoverFilter filter;

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
            if (filter != null && filter.match(connectionDetails)) return;
            NodeDiscoveryResult discoveryResult = nodeDiscoverer.discover(connectionDetails);
            String nodeId = discoveryResult.getNodeId();
            logger.info("Discovering node:"+nodeId);
            logger.debug("Connection details: "+connectionDetails);
            Node currentNode = nodes.get(nodeId);
            if (currentNode == null) {
                currentNode = new Node(nodeId,connectionDetailsList);
                nodes.put(nodeId, currentNode);
            } else {
                logger.debug("Node '"+currentNode.getId()+"' is already discovered. Skipping it.");
                return;
            }
            if (initialNode != null) initialNode.addNeighbour(currentNode);
            List<ConnectionDetails> neighboursConnectionDetails = discoveryResult.getNeighboursConnectionDetails();
            doDiscoverNodes(neighboursConnectionDetails, nodes, currentNode, level+1, depth);
        }
    }

    public void setNodeDiscoverers(Map<String, NodeDiscoverer> nodeDiscoverers) {
        this.nodeDiscoverers = nodeDiscoverers;
    }

    public void setNodeDiscoverFilter(NodeDiscoverFilter filter) {
        this.filter = filter;
    }
}