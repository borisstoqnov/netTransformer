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

import net.itransformers.idiscover.core.*;
import net.itransformers.idiscover.discoverers.DefaultDiscovererFactory;
import net.itransformers.idiscover.discoverers.SnmpWalker;
import net.itransformers.idiscover.discoveryhelpers.xml.XmlDiscoveryHelperFactory;
import net.itransformers.idiscover.networkmodel.DiscoveredDeviceData;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.resourcemanager.config.ResourceType;

import java.util.*;

public class SnmpNodeDiscoverer implements NodeDiscoverer {
    private SnmpWalker walker;
    private XmlDiscoveryHelperFactory discoveryHelperFactory;
    private String[] discoveryTypes;
    private DiscoveryResourceManager discoveryResource;

    public SnmpNodeDiscoverer(Map<String, String> attributes, XmlDiscoveryHelperFactory discoveryHelperFactory, String[] discoveryTypes, DiscoveryResourceManager discoveryResource) throws Exception {
        this.discoveryHelperFactory = discoveryHelperFactory;
        this.discoveryTypes = discoveryTypes;
        this.discoveryResource = discoveryResource;
        Resource resource = new Resource("", "", attributes);
        walker = (SnmpWalker) new DefaultDiscovererFactory().createDiscoverer(resource);
    }

    @Override
    public NodeDiscoveryResult discover(ConnectionDetails connectionDetails) {
        Map<String, String> params = connectionDetails.getParams();
        NodeDiscoveryResult result = new NodeDiscoveryResult();

        //esource resource = new Resource(params.get("ipAddress"),params.get("deviceType"),params);
        String hostName = params.get("host");
        IPv4Address ipAddress = new IPv4Address(params.get("ipAddress"), params.get("netMask"));


        Map<String,String> params1 = new HashMap<String, String>();
        params1.put("DeviceName",hostName);
        //params1.put("DeviceType",params.get("deviceType"));
        params1.put("protocol","SNMP");

        ResourceType SNMP = this.discoveryResource.ReturnResourceByParam(params1);
        Map<String, String> SNMPconnParams = new HashMap<String, String>();
        SNMPconnParams = this.discoveryResource.getParamMap(SNMP);
        Resource resource = new Resource(hostName,ipAddress,params.get("deviceType"), Integer.parseInt(SNMPconnParams.get("port")), SNMPconnParams);



        String devName = walker.getDeviceName(resource);
        result.setNodeId(devName);
        String deviceType = walker.getDeviceType(resource);
        resource.setDeviceType(deviceType);
        DiscoveryHelper discoveryHelper = discoveryHelperFactory.createDiscoveryHelper(deviceType);
        String[] requestParamsList = discoveryHelper.getRequestParams(discoveryTypes);

        RawDeviceData rawData = walker.getRawDeviceData(resource, requestParamsList);
        DiscoveredDeviceData discoveredDeviceData = discoveryHelper.parseDeviceRawData(rawData, discoveryTypes, resource);

        Device device = discoveryHelper.createDevice(discoveredDeviceData);

        List<DeviceNeighbour> neighbours = device.getDeviceNeighbours();

        List<ConnectionDetails> neighboursConnDetails = createNeighbourConnectionDetails(connectionDetails, params, neighbours);
        result.setNeighboursConnectionDetails(neighboursConnDetails);
        return result;
    }

    private List<ConnectionDetails> createNeighbourConnectionDetails(ConnectionDetails connectionDetails, Map<String, String> params, List<DeviceNeighbour> neighbours) {
        List<ConnectionDetails> neighboursConnDetails = new ArrayList<ConnectionDetails>();
        for (DeviceNeighbour neighbour : neighbours) {
            params.put("deviceType",neighbour.getDeviceType());
            if (neighbour.getStatus()){ // if reachable
                params.put("host",neighbour.getHostName());
                params.put("ipAddress",neighbour.getIpAddress().getIpAddress());
                params.put("netMask",neighbour.getIpAddress().getNetMask());
                params.put("community-ro",""+neighbour.getROCommunity());
                ConnectionDetails neighbourConnectionDetails = new ConnectionDetails();
                neighbourConnectionDetails.setConnectionType("SNMP");
                neighbourConnectionDetails.setParams(params);
                neighboursConnDetails.add(neighbourConnectionDetails);
            }
        }
        return neighboursConnDetails;
    }
}
