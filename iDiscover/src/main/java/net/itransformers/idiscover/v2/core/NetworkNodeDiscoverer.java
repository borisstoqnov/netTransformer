/*
 * NetworkNodeDiscoverer.java
 *
 * This work is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * This work is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * Copyright (c) 2010-2016 iTransformers Labs. All rights reserved.
 */

package net.itransformers.idiscover.v2.core;

import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.idiscover.v2.core.model.Node;

import java.util.*;


public abstract class NetworkNodeDiscoverer implements NetworkDiscoverer {
    protected Map<String, NodeDiscoverer> nodeDiscoverers;
    protected List<NodeDiscoveryListener> nodeDiscoveryListeners;
    protected List<NodeNeighboursDiscoveryListener> nodeNeighbourDiscoveryListeners;
    protected List<NetworkDiscoveryListener> networkDiscoveryListeners;
    protected NodeDiscoverFilter nodeDiscoverFilter;
    protected Map<ConnectionDetails, Set<String>> neighbourToParentNodesMap = new HashMap<ConnectionDetails, Set<String>>();
    protected Map<String, Set<ConnectionDetails>> nodeToNeighboursMap = new HashMap<String, Set<ConnectionDetails>>();
    protected final Map<String, Node> nodes = new HashMap<String, Node>();

    public NetworkDiscoveryResult discoverNetwork(Set<ConnectionDetails> connectionDetailsList) {
        return discoverNetwork(connectionDetailsList, -1);
    }

    public void setNodeDiscoverFilter(NodeDiscoverFilter filter) {
        this.nodeDiscoverFilter = filter;
    }


    public void setNodeDiscoverers(Map<String, NodeDiscoverer> nodeDiscoverers) {
        this.nodeDiscoverers = nodeDiscoverers;
    }

    public List<NodeNeighboursDiscoveryListener> getNodeNeighbourDiscoveryListeners() {
        return nodeNeighbourDiscoveryListeners;
    }

    public void setNodeNeighbourDiscoveryListeners(List<NodeNeighboursDiscoveryListener> nodeNeighbourDiscoveryListeners) {
        this.nodeNeighbourDiscoveryListeners = nodeNeighbourDiscoveryListeners;
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


    public void fireNodeNotDiscoveredEvent(ConnectionDetails connectionDetails) {
        handleNodeDiscoveredOrNotDiscoveredEvent(connectionDetails);
    }

    public void fireNodeDiscoveredEvent(ConnectionDetails connectionDetails, NodeDiscoveryResult discoveryResult) {
        String nodeId = discoveryResult.getNodeId();
        Set<ConnectionDetails> neighbourConnectionDetails = discoveryResult.getNeighboursConnectionDetails();
        if (nodeToNeighboursMap.containsKey(nodeId)) {
            throw new RuntimeException("Node is already discovered: nodeId=" + nodeId);
        }
        nodeToNeighboursMap.put(nodeId, neighbourConnectionDetails);
        for (ConnectionDetails neighboutConnectionDetails : neighbourConnectionDetails) {
            Set<String> parentNodes = neighbourToParentNodesMap.get(neighboutConnectionDetails);
            if (parentNodes == null) {
                parentNodes = new HashSet<String>();
                neighbourToParentNodesMap.put(neighboutConnectionDetails, parentNodes);
            }
            parentNodes.add(nodeId);
        }
        handleNodeDiscoveredOrNotDiscoveredEvent(connectionDetails);

        if (nodeDiscoveryListeners != null) {
            for (NodeDiscoveryListener nodeDiscoveryListener : nodeDiscoveryListeners) {
                nodeDiscoveryListener.nodeDiscovered(discoveryResult);
            }
        }
    }


    protected void handleNodeDiscoveredOrNotDiscoveredEvent(ConnectionDetails connectionDetails) {
        Set<String> parentNodeIds = neighbourToParentNodesMap.get(connectionDetails);
        for (String parentNodeId : parentNodeIds) {
            Set<ConnectionDetails> neigboursConnectionDetails = nodeToNeighboursMap.get(parentNodeId);
            neigboursConnectionDetails.remove(connectionDetails);
            if (neigboursConnectionDetails.isEmpty()) {
                fireNeighboursDiscoveredEvent(parentNodeId);
            }
        }
    }

    protected void fireNeighboursDiscoveredEvent(String nodeId) {
        if (nodeDiscoveryListeners != null){
            Node node = nodes.get(nodeId);
            for (NodeNeighboursDiscoveryListener nodeNeighboursDiscoveryListener: nodeNeighbourDiscoveryListeners){
                nodeNeighboursDiscoveryListener.handleNodeNeighboursDiscovered(node);
            }
        }
    }

    protected void fireNetworkDiscoveredEvent(NetworkDiscoveryResult result) {
        if (networkDiscoveryListeners != null)
            for (NetworkDiscoveryListener networkDiscoveryListener : networkDiscoveryListeners) {
                networkDiscoveryListener.networkDiscovered(result);
            }
    }

}
