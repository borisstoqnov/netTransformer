/*
 * iMap is an open source tool able to upload Internet BGP peering information
 *  and to visualize the beauty of Internet BGP Peering in 2D map.
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkDiscoverer {
    Map<String, NodeDiscoverer> nodeDiscoverers;

    public Map<String,Node> discoverNodes(List<ConnectionDetails> connectionDetailsList) {
        Map<String,Node> nodes = new HashMap<String, Node>();
        doDiscoverNodes(connectionDetailsList, nodes, null);
        return nodes;
    }

    private void doDiscoverNodes(List<ConnectionDetails> connectionDetailsList, Map<String, Node> nodes, Node initialNode) {
        for (ConnectionDetails connectionDetails : connectionDetailsList) {
            String connectionType = connectionDetails.getConnectionType();
//            NodeDiscoverer nodeDiscoverer = NodeDiscovererFactory.createNodeDiscoverer(connectionType);
            NodeDiscoverer nodeDiscoverer = nodeDiscoverers.get(connectionType);
            NodeDiscoveryResult discoveryResult = nodeDiscoverer.discover(connectionDetails);
            String nodeId = discoveryResult.getNodeId();
            Node currentNode = nodes.get(nodeId);
            if (currentNode == null) {
                currentNode = new Node(nodeId,connectionDetailsList);
                nodes.put(nodeId, currentNode);
            }
            if (initialNode != null){
                initialNode.addNeighbour(currentNode);
            }
            List<ConnectionDetails> neighboursConnectionDetails = discoveryResult.getNeighboursConnectionDetails();
            doDiscoverNodes(neighboursConnectionDetails, nodes, currentNode);
        }
    }

    public void setNodeDiscoverers(Map<String, NodeDiscoverer> nodeDiscoverers) {
        this.nodeDiscoverers = nodeDiscoverers;
    }

    public static void main(String[] args) {
        ConnectionDetails connectionDetails = new ConnectionDetails();;
        connectionDetails.setConnectionType("SNMP");
        Map<String, String> params = new HashMap();
//        params.put("host","172.16.36.1");
//        params.put("version","1");
//        params.put("community-ro","test-r");
//        params.put("community-rw","test-rw");

        params.put("host","88.203.200.94");
        params.put("version","1");
        params.put("community-ro","test-r");
        params.put("community-rw","test-rw");

//        params.put("host","10.0.1.1");
//        params.put("version","1");
//        params.put("community-ro","test-r");
//        params.put("community-rw","test-rw");
        params.put("timeout","500");
        params.put("retries","1");
        params.put("port","161");
        params.put("max-repetitions","65535");
        params.put("mibDir","snmptoolkit/mibs");
        connectionDetails.setParams(params);
        NetworkDiscoverer discoverer = new NetworkDiscoverer();
        Map<String, Node> result = discoverer.discoverNodes(Arrays.asList(connectionDetails));
        System.out.println(result);
    }
}

/*<resource name="SNMP">
        <param name="protocol">SNMP</param>
        <connection-params connection-type="snmp">
            <param name="version">1</param>
            <param name="community-ro">europack-r</param>
            <param name="community-rw">itransformer-rw</param>
            <param name="timeout">500</param>
            <param name="retries">1</param>
                        <param name="port">161</param>
            <param name="max-repetitions">65535</param>
        </connection-params>
    </resource>*/