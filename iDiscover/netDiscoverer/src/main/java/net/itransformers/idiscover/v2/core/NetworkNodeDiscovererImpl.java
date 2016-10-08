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

import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetails;
import net.itransformers.idiscover.api.NetworkDiscoveryListener;
import net.itransformers.idiscover.api.NetworkDiscoveryResult;
import net.itransformers.idiscover.api.NodeDiscoverer;
import net.itransformers.idiscover.api.NodeDiscoveryResult;
import net.itransformers.idiscover.api.models.network.Node;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.Set;

public class NetworkNodeDiscovererImpl extends NetworkNodeDiscoverer {
    static Logger logger = Logger.getLogger(NetworkNodeDiscovererImpl.class);
    private boolean isRunning;
    private boolean isPaused;
    private boolean isStopped;

    private NetworkDiscoveryResult discoverNetwork(Set<ConnectionDetails> connectionDetailsList, int depth) {
        isRunning = true;

        NetworkDiscoveryResult result = new NetworkDiscoveryResult(null);
        doDiscoverNodes(connectionDetailsList, nodes, null, 0, depth, result);
        result.setNodes(nodes);
        fireNetworkDiscoveredEvent(result);
        stop();
        return result;
    }

    void doDiscoverNodes(Set<ConnectionDetails> connectionDetailsList, Map<String, Node> nodes,
                         Node initialNode, int level, int depth, NetworkDiscoveryResult result) {


        if (level == depth){
            logger.debug("Level = Depth");
            return;
        }

        for (ConnectionDetails connectionDetails : connectionDetailsList) {
            if (isStopped){
                return;
            }
            if (isPaused){
                doPause();
            }
            String connectionType = connectionDetails.getConnectionType();
            NodeDiscoverer nodeDiscoverer = nodeDiscoverers.get(connectionType);
            String nodeId = connectionDetails.getParam("deviceName");

            if (nodes.get(nodeId)==null){

                    if (nodeDiscoverer == null) {
                    logger.debug("No Node Discoverers found");
                    continue;
                }


                if (nodeDiscoverFilter != null && nodeDiscoverFilter.match(connectionDetails)) return;
                NodeDiscoveryResult discoveryResult = nodeDiscoverer.discover(connectionDetails);
                if (discoveryResult == null){
                    logger.debug("No discovery result for "+ nodeId);
//                    fireNodeNotDiscoveredEvent(connectionDetails);
                    continue;
                }

//                fireNodeDiscoveredEvent(connectionDetails,discoveryResult);

                if (nodeId ==null) nodeId = discoveryResult.getNodeId();
                Node currentNode = new Node(nodeId);
                nodes.put(nodeId, currentNode);

                if (initialNode != null){
                    logger.debug("initialNode: "+initialNode.getId());
                    initialNode.addNeighbour(currentNode);
                }
                Set<ConnectionDetails> neighboursConnectionDetails = discoveryResult.getNeighboursConnectionDetails();
                logger.debug("Found Neighbours, connection details: " + neighboursConnectionDetails);
                if (neighboursConnectionDetails != null) {
                    doDiscoverNodes(neighboursConnectionDetails, nodes, currentNode, level + 1, depth, result);
                }
            }else {
                logger.info("Node: "+nodeId+" is already discovered!");
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

    @Override
    public void startDiscovery(Set<ConnectionDetails> connectionDetailsList) {
        discoverNetwork(connectionDetailsList, -1);
    }

    @Override
    public void stopDiscovery() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void pauseDiscovery() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void resumeDiscovery() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addNetworkDiscoveryListeners(NetworkDiscoveryListener networkDiscoveryListeners) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void removeNetworkDiscoveryListeners(NetworkDiscoveryListener networkDiscoveryListeners) {
        throw new UnsupportedOperationException();
    }
}