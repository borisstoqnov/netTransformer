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
    protected Map<ConnectionDetails, Set<String>> neighbourToParentNodesMap = new HashMap<ConnectionDetails, Set<String>>();
    protected Map<String, Set<ConnectionDetails>> nodeToNeighboursMap = new HashMap<String, Set<ConnectionDetails>>();
    protected final Map<String, Node> nodes = new HashMap<String, Node>();
    protected Map<String, NodeDiscoveryResult> nodeDiscoveryResultMap= new HashMap<String, NodeDiscoveryResult>();

    protected Set<ConnectionDetails> discoveringConnectionDetails = new HashSet<ConnectionDetails>();
    protected Set<ConnectionDetails> discoveredConnectionDetails = new HashSet<ConnectionDetails>();
    protected Set<ConnectionDetails> notDiscoveredConnectionDetails = new HashSet<ConnectionDetails>();

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

    public Set<ConnectionDetails> getDiscoveringConnectionDetails() {
        return discoveringConnectionDetails;
    }

    public void setDiscoveringConnectionDetails(Set<ConnectionDetails> discoveringConnectionDetails) {
        this.discoveringConnectionDetails = discoveringConnectionDetails;
    }

    public Set<ConnectionDetails> getDiscoveredConnectionDetails() {
        return discoveredConnectionDetails;
    }

    public void setDiscoveredConnectionDetails(Set<ConnectionDetails> discoveredConnectionDetails) {
        this.discoveredConnectionDetails = discoveredConnectionDetails;
    }

    public Set<ConnectionDetails> getNotDiscoveredConnectionDetails() {
        return notDiscoveredConnectionDetails;
    }

    public void setNotDiscoveredConnectionDetails(Set<ConnectionDetails> notDiscoveredConnectionDetails) {
        this.notDiscoveredConnectionDetails = notDiscoveredConnectionDetails;
    }

    public synchronized void fireNodeNotDiscoveredEvent(ConnectionDetails connectionDetails) {
        notDiscoveredConnectionDetails.add(connectionDetails);
        discoveringConnectionDetails.remove(connectionDetails);
        handleNodeDiscoveredOrNotDiscoveredEvent(connectionDetails);
    }

    public synchronized void fireNodeDiscoveredEvent(ConnectionDetails connectionDetails, NodeDiscoveryResult discoveryResult) {
        logger.info("A new node is discovered with connectionDetails "+connectionDetails);
        discoveredConnectionDetails.add(connectionDetails);
        discoveringConnectionDetails.remove(connectionDetails);
        String nodeId = discoveryResult.getNodeId();
        Set<ConnectionDetails> neighbourConnectionDetails = discoveryResult.getNeighboursConnectionDetails();
        if (nodeToNeighboursMap.containsKey(nodeId)) {
            logger.debug("Node is already discovered: nodeId=" + nodeId);
            //TODO we have to figure out what do we do with the connection Details in this case
            return;
        }
        HashSet<ConnectionDetails> neighbourConnectionDetailsCopy = new HashSet<ConnectionDetails>();
        neighbourConnectionDetailsCopy.addAll(neighbourConnectionDetails);
        logger.info("Discovered neighbour connections:" + neighbourConnectionDetailsCopy.size());

        neighbourConnectionDetailsCopy.removeAll(discoveredConnectionDetails);
        logger.info("Discovered neighbour connections - discovered:" + neighbourConnectionDetailsCopy.size());

        neighbourConnectionDetailsCopy.removeAll(notDiscoveredConnectionDetails);
        logger.info("Discovered neighbour connections - notdiscovered:" + neighbourConnectionDetailsCopy.size());


        nodeToNeighboursMap.put(nodeId, neighbourConnectionDetailsCopy);
        nodeDiscoveryResultMap.put(nodeId,discoveryResult);
        for (ConnectionDetails neighbourConnectionDetail : neighbourConnectionDetails) {
            Set<String> parentNodes = neighbourToParentNodesMap.get(neighbourConnectionDetail);
            if (parentNodes == null) {
                parentNodes = new HashSet<String>();
                neighbourToParentNodesMap.put(neighbourConnectionDetail, parentNodes);
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


    private void handleNodeDiscoveredOrNotDiscoveredEvent(ConnectionDetails connectionDetails) {
        Set<String> parentNodeIds = neighbourToParentNodesMap.get(connectionDetails);
        if (parentNodeIds!=null) {

            for (String parentNodeId : parentNodeIds) {
                Set<ConnectionDetails> neigboursConnectionDetails = nodeToNeighboursMap.get(parentNodeId);
                neigboursConnectionDetails.remove(connectionDetails);
                logger.info("Removing from " + parentNodeId + " connection details " + connectionDetails);

                if (neigboursConnectionDetails.isEmpty()) {
                    NodeDiscoveryResult nodeDiscoveryResult = nodeDiscoveryResultMap.remove(parentNodeId);

                    fireNeighboursDiscoveredEvent(nodeDiscoveryResult);
                }    else {
                    logger.info("parentNodeId " + parentNodeId + " still has " + neigboursConnectionDetails.size() + " to be discovered !!!");
                    logger.info(neigboursConnectionDetails.toString());

                }
            }
        }   else {
            logger.info("No parent has been found for " + connectionDetails + " !!!");

        }
    }

    protected void fireNeighboursDiscoveredEvent(final NodeDiscoveryResult nodeDiscoveryResult) {

            if( nodeDiscoveryResult !=null && nodeNeighbourDiscoveryListeners!=null){

            String nodeId = nodeDiscoveryResult.getNodeId();
            final Node node = nodes.get(nodeId);
            logger.info("---------------Node: "+nodeId+" is discovered!---------------");

                    for (final NodeNeighboursDiscoveryListener nodeNeighboursDiscoveryListener: nodeNeighbourDiscoveryListeners){

                // Fire this event in a new thread, so that the other workers will not be blocked by the thread
                // that is holding this object lock and working on event processing.
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        nodeNeighboursDiscoveryListener.handleNodeNeighboursDiscovered(node, nodeDiscoveryResult);
                    }
                }).start();

            }
        } else {

                logger.info("NodeDiscoveryResult is null---------------");

            }
    }

    protected void fireNetworkDiscoveredEvent(NetworkDiscoveryResult result) {
        logger.debug("Discovered" + discoveredConnectionDetails.size() + " | "+ discoveredConnectionDetails);
        logger.debug("Discovering" + discoveringConnectionDetails.size() + " | " +discoveringConnectionDetails);
        logger.debug("NotDiscovered" + notDiscoveredConnectionDetails.size()+ " | " +notDiscoveredConnectionDetails);

        if (networkDiscoveryListeners != null)
            for (NetworkDiscoveryListener networkDiscoveryListener : networkDiscoveryListeners) {
                networkDiscoveryListener.networkDiscovered(result);
            }
    }

}
