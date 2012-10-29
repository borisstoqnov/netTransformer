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

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.*;

public class SnmpNodeDiscoverer implements NodeDiscoverer {
    private SnmpWalker walker;
    private XmlDiscoveryHelperFactory discoveryHelperFactory;

    public SnmpNodeDiscoverer() {
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.put("mibDir","snmptoolkit/mibs");
        Resource resource = new Resource("", "", attributes);
        try {
            walker = (SnmpWalker) new DefaultDiscovererFactory().createDiscoverer(resource);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        HashMap<String, String> discoveryHelperParams = new HashMap<String, String>();
        discoveryHelperParams.put("fileName","iDiscover/conf/xml/discoveryParameters.xml");
        discoveryHelperParams.put("resourceXML","iDiscover/conf/xml/discoveryResource.xml");
        try {
            discoveryHelperFactory = new XmlDiscoveryHelperFactory(discoveryHelperParams);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public NodeDiscoveryResult discover(ConnectionDetails connectionDetails) {
        Map<String, String> params = connectionDetails.getParams();
        NodeDiscoveryResult result = new NodeDiscoveryResult();
        Resource resource = new Resource(params.get("host"),params.get("deviceType"),params);
        String devName = walker.getDeviceName(resource);
        result.setNodeId(devName);
        String[] discoveryTypes = new String[]{"PHYSICAL","NEXT_HOP","OSPF","ISIS","BGP","RIP","ADDITIONAL","IPV6"};
        String deviceType = walker.getDeviceType(resource);
        resource.setDeviceType(deviceType);
        DiscoveryHelper discoveryHelper = discoveryHelperFactory.createDiscoveryHelper(deviceType);
        String[] requestParamsList = discoveryHelper.getRequestParams(discoveryTypes);
        RawDeviceData rawData = walker.getRawDeviceData(resource, requestParamsList);
        DiscoveredDeviceData discoveredDeviceData = discoveryHelper.parseDeviceRawData(rawData, discoveryTypes, resource);

        Device device = discoveryHelper.createDevice(discoveredDeviceData);

        List<DeviceNeighbour> neighbours = device.getDeviceNeighbours();
        List<ConnectionDetails> neighboursConnDetails = createNeigbourConnectionDetails(connectionDetails, params, neighbours);
        result.setNeighboursConnectionDetails(neighboursConnDetails);
        return result;
    }

    private List<ConnectionDetails> createNeigbourConnectionDetails(ConnectionDetails connectionDetails, Map<String, String> params, List<DeviceNeighbour> neighbours) {
        List<ConnectionDetails> neighboursConnDetails = new ArrayList<ConnectionDetails>();
        for (DeviceNeighbour neighbour : neighbours) {
            params.put("deviceType",neighbour.getDeviceType());
            if (neighbour.getStatus()){ // if reachable
                params.put("host",neighbour.getHostName());
                params.put("ipAddress",""+neighbour.getIpAddress());
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
