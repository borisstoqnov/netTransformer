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

import net.itransformers.idiscover.v2.core.model.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworkDiscoverer {
    public Map<String,Node> discoverNodes(List<ConnectionDetails> connectionDetailsList) {
        Map<String,Node> nodes = new HashMap<String, Node>();
        for (ConnectionDetails connectionDetails : connectionDetailsList) {
            String connectionType = connectionDetails.getConnectionType();
            NodeDiscoverer nodeDiscoverer = NodeDiscovererFactory.createNodeDiscoverer(connectionType);
            NodeDiscoveryResult discoveryResult = nodeDiscoverer.discover(connectionDetails);
            String nodeId = discoveryResult.getNodeId();
            List<ConnectionDetails> neighboursConnectionDetails = discoveryResult.getNeighboursConnectionDetails();

        }
        return nodes;
    }

}
