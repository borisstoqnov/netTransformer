

/*
 * NetworkDiscoveryResult.java
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

import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetails;
import net.itransformers.idiscover.v2.core.model.Node;

import java.util.HashMap;
import java.util.Map;

public class NetworkDiscoveryResult {
    Map<String,NodeDiscoveryResult> discoveredData = new HashMap<String,  NodeDiscoveryResult>();

    Map<String,ConnectionDetails> sourceConnectionDetails = new HashMap<String,  ConnectionDetails>();
    Map<String, Node> nodes = new HashMap<String, Node>();

    public NetworkDiscoveryResult(Map<String, Node> nodes) {
        this.nodes = nodes;
    }

    public Object getDiscoveredData(String sourceId){
        return discoveredData.get(sourceId);
    }
    public void addDiscoveredData(String sourceId, NodeDiscoveryResult discoveredNodeData){

        discoveredData.put(sourceId,discoveredNodeData);
    }

    public Map<String,NodeDiscoveryResult> getDiscoveredData() {
        return discoveredData;
    }

    public void setDiscoveredData(Map<String,NodeDiscoveryResult> discoveredData) {
        this.discoveredData = discoveredData;
    }
    public Map<String, ConnectionDetails> getSourceConnectionDetails() {
        return sourceConnectionDetails;
    }

    public void setSourceConnectionDetails(Map<String, ConnectionDetails> sourceConnectionDetails) {
        this.sourceConnectionDetails = sourceConnectionDetails;
    }

    public Map<String, Node> getNodes() {
        return nodes;
    }

    public void setNodes(Map<String, Node> nodes) {
        this.nodes = nodes;
    }

    @Override
    public String toString() {
        return "NetworkDiscoveryResult{" +
                "discoveredData=" + discoveredData +
                '}';
    }
}
