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

import java.util.Map;

public class NeighborDiscoveryResult {
    String neighborId;
    String discoveredIpAddress;
    String neighborType;
    Map<String, String> connParams;


    public String getNodeId() {
        return neighborId;
    }

    public void setNodeId(String nodeId) {
        this.neighborId = nodeId;
    }

    public String getDiscoveredIpAddress() {
        return discoveredIpAddress;
    }

    public void setDiscoveredIpAddress(String discoveredIpAddress) {
        this.discoveredIpAddress = discoveredIpAddress;
    }

    public String getNeighborType() {
        return neighborType;
    }

    public void setNeighborType(String neighborType) {
        this.neighborType = neighborType;
    }

    public Map<String, String> getConnParams() {
        return connParams;
    }

    public void setConnParams(Map<String, String> connParams) {
        this.connParams = connParams;
    }


    @Override
    public String toString() {
        return "NeighborDiscoveryResult{" +
                "neighborId='" + neighborId + '\'' + "\ndiscoveredIpAddress=" + discoveredIpAddress + "\nneighborType= " + neighborType + "\nconnParams" + connParams.toString() +
                '}';
    }
}
