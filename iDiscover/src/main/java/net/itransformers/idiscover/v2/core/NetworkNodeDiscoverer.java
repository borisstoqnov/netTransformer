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
import org.apache.log4j.Logger;

import java.util.*;


public abstract class NetworkNodeDiscoverer implements NetworkDiscoverer {
    static Logger logger = Logger.getLogger(NetworkNodeDiscoverer.class);

    protected Map<String, NodeDiscoverer> nodeDiscoverers;
    protected List<NodeDiscoveryListener> nodeDiscoveryListeners;
    protected List<NodeNeighboursDiscoveryListener> nodeNeighbourDiscoveryListeners;
    protected List<NetworkDiscoveryListener> networkDiscoveryListeners;
    protected NodeDiscoverFilter nodeDiscoverFilter;
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

    public synchronized void fireNodeDiscoveredEvent(NodeDiscoveryResult discoveryResult) {
        if (nodeDiscoveryListeners != null) {
            for (NodeDiscoveryListener nodeDiscoveryListener : nodeDiscoveryListeners) {
                nodeDiscoveryListener.nodeDiscovered(discoveryResult);
            }
        }
    }

    protected void fireNeighboursDiscoveredEvent(final NodeDiscoveryResult nodeDiscoveryResult) {
        if (nodeNeighbourDiscoveryListeners != null) {
            String nodeId = nodeDiscoveryResult.getNodeId();
            final Node node = nodes.get(nodeId);
            for (final NodeNeighboursDiscoveryListener nodeNeighboursDiscoveryListener : nodeNeighbourDiscoveryListeners) {
                nodeNeighboursDiscoveryListener.handleNodeNeighboursDiscovered(node, nodeDiscoveryResult);
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
