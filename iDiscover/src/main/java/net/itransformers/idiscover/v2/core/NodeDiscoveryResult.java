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

package net.itransformers.idiscover.v2.core;/*
 * iTransformer is an open source tool able to discover IP networks
 * and to perform dynamic data data population into a xml based inventory system.
 * Copyright (C) 2010  http://itransformers.net
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.idiscover.v2.core.model.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeDiscoveryResult {
    String nodeId;
    List<ConnectionDetails> neighboursConnectionDetails;
    Map<String,Object> discoveredData = new HashMap<String,Object>();

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public List<ConnectionDetails> getNeighboursConnectionDetails() {
        return neighboursConnectionDetails;
    }

    public void setNeighboursConnectionDetails(List<ConnectionDetails> neighboursConnectionDetails) {
        this.neighboursConnectionDetails = neighboursConnectionDetails;
    }
    public Object getDiscoveredData(String key){
        return discoveredData.get(key);
    }
    public void setDiscoveredData(String key, Object data){
        discoveredData.put(key,data);
    }

    @Override
    public String toString() {
        return "NodeDiscoveryResult{" +
                "nodeId='" + nodeId + '\'' +
                ", neighboursConnectionDetails=" + neighboursConnectionDetails +
                ", discoveredData=" + discoveredData.toString() +
                '}';
    }
}
