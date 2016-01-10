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

package net.itransformers.idiscover.v2.core.snmpdiscoverer;/*
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
import net.itransformers.idiscover.discoveryhelpers.xml.SnmpForXslt;
import net.itransformers.idiscover.discoveryhelpers.xml.XmlDiscoveryHelperFactory;
import net.itransformers.idiscover.networkmodel.DiscoveredDeviceData;
import net.itransformers.idiscover.util.JaxbMarshalar;
import net.itransformers.idiscover.v2.core.NeighborDiscoveryListener;
import net.itransformers.idiscover.v2.core.NodeDiscoverer;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.resourcemanager.config.ResourceType;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SnmpNodeDiscoverer implements NodeDiscoverer {
    static Logger logger = Logger.getLogger(SnmpNodeDiscoverer.class);
    private SnmpWalker walker;
    private XmlDiscoveryHelperFactory discoveryHelperFactory;
    private String[] discoveryTypes;
    private DiscoveryResourceManager discoveryResource;
    protected List<NeighborDiscoveryListener> neighborDiscoveryListeners;


    public SnmpNodeDiscoverer(Map<String, String> attributes, XmlDiscoveryHelperFactory discoveryHelperFactory, String[] discoveryTypes, DiscoveryResourceManager discoveryResource, List<NeighborDiscoveryListener> neighborDiscoveryListeners) throws Exception {
        this.discoveryHelperFactory = discoveryHelperFactory;
        this.discoveryTypes = discoveryTypes;
        this.discoveryResource = discoveryResource;
        this.neighborDiscoveryListeners = neighborDiscoveryListeners;
        Resource resource = new Resource("", "", attributes);
        walker = (SnmpWalker) new DefaultDiscovererFactory().createDiscoverer(resource);
        SnmpForXslt.setNeighborDiscoveryListeners(neighborDiscoveryListeners);
    }

    private String probe(ConnectionDetails connectionDetails) {
        Map<String,String> params1 = new HashMap<String, String>();

        String hostName = connectionDetails.getParam("deviceName");
        if (hostName!=null){
            params1.put("deviceName",hostName);

        }
        IPv4Address ipAddress = new IPv4Address(connectionDetails.getParam("ipAddress"), connectionDetails.getParam("netMask"));
        String deviceType= connectionDetails.getParam("deviceType");
        if (deviceType!=null) {
            params1.put("deviceType", deviceType);
        }
        String ipAddress1 =connectionDetails.getParam("ipAddress");
        if (ipAddress1 !=null) {
            params1.put("ipAddress", ipAddress1);

        }

        ResourceType snmp = this.discoveryResource.returnResourceByParam(params1);
        Map<String, String> snmpConnParams = this.discoveryResource.getParamMap(snmp,"snmp");

        Resource resource = new Resource(hostName,ipAddress,connectionDetails.getParam("deviceType"), Integer.parseInt(snmpConnParams.get("port")), snmpConnParams);
        hostName = walker.getDeviceName(resource);
        if  (hostName!=null && hostName.contains(".")){
            hostName=hostName.substring(0,hostName.indexOf("."));
        }
        if(deviceType==null){
            deviceType = walker.getDeviceType(resource);
            connectionDetails.put("deviceType",deviceType);
        }

        return hostName;
    }

    @Override
    public NodeDiscoveryResult discover(ConnectionDetails connectionDetails) {
        String hostName = probe(connectionDetails);
        if (hostName == null) {
            return null;
        }
        NodeDiscoveryResult result = new NodeDiscoveryResult();
        Map<String,String> params1 = new HashMap<String, String>();
        params1.put("deviceName",hostName);
        params1.put("deviceType",connectionDetails.getParam("deviceType"));
        String ipAddressStr = connectionDetails.getParam("ipAddress");
        params1.put("ipAddress", ipAddressStr);
        IPv4Address ipAddress = new IPv4Address(ipAddressStr, null);


        ResourceType snmpResource = this.discoveryResource.returnResourceByParam(params1);
        Map<String, String> snmpConnParams = this.discoveryResource.getParamMap(snmpResource, "snmp");


        Resource resource = new Resource(hostName,ipAddress,connectionDetails.getParam("deviceType"), Integer.parseInt(snmpConnParams.get("port")), snmpConnParams);

        resource.getAttributes().put("neighbourIPDryRun","true");

        String devName = walker.getDeviceName(resource);
        if (devName == null) {
            logger.info("Device name is null for resource: " + resource);
            return null;
        }
        if  (devName.contains(".")){
            devName=devName.substring(0,devName.indexOf("."));
        }
        result.setNodeId(devName);
        // TODO I have commented this out, do we need it?
//        result.setDiscoveredIpAddress(ipAddressStr);
        String deviceType = walker.getDeviceType(resource);
        resource.setDeviceType(deviceType);
        DiscoveryHelper discoveryHelper = discoveryHelperFactory.createDiscoveryHelper(deviceType);
        String[] requestParamsList = discoveryHelper.getRequestParams(discoveryTypes);

        RawDeviceData rawData = walker.getRawDeviceData(resource, requestParamsList);
        if (rawData!=null){
            result.setDiscoveredData("rawData", rawData.getData());

        }  else {
            logger.info("Rawdata is null with resource: "+resource);

            return null;
        }

        discoveryHelper.setDryRun(true);


        DiscoveredDeviceData discoveredDeviceData1 = discoveryHelper.parseDeviceRawData(rawData, discoveryTypes, resource);

        OutputStream os  = null;

        try {
            os = new ByteArrayOutputStream();
            JaxbMarshalar.marshal(discoveredDeviceData1, os, "DiscoveredDevice");
            String str = os.toString();
        } catch (JAXBException e) {
            logger.error(e.getMessage(),e);
        } finally {
            if (os != null) try {os.close();} catch (IOException e) {}
        }


        SnmpForXslt.resolveIPAddresses(discoveryResource, "snmp");


        discoveryHelper.setDryRun(false);

        DiscoveredDeviceData discoveredDeviceData2 = discoveryHelper.parseDeviceRawData(rawData, discoveryTypes, resource);

        try {
            os = new ByteArrayOutputStream();
            JaxbMarshalar.marshal(discoveredDeviceData2, os, "DiscoveredDevice");
//            String str = os.toString();
//          //  System.out.println(str);
        } catch (JAXBException e) {
            logger.error(e.getMessage(),e);
        } finally {
            if (os != null) try {os.close();} catch (IOException e) {}
        }
        result.setDiscoveredData("deviceData", discoveredDeviceData2);
        // TODO I have commented this out. Do we need it?
//        result.setConnParams(snmpConnParams);
        Device device = discoveryHelper.createDevice(discoveredDeviceData2);

        List<DeviceNeighbour> neighbours = device.getDeviceNeighbours();

        HashMap<String, List<ConnectionDetails>> neighboursConnDetails = createNeighbourConnectionDetails(neighbours);
        result.setNeighboursConnectionDetails(neighboursConnDetails);
        return result;
    }



    public NodeDiscoveryResult mockDiscover(ConnectionDetails connectionDetails,String hostName ) {
        if (hostName == null) {
            return null;
        }
        NodeDiscoveryResult result = new NodeDiscoveryResult();
        Map<String,String> params1 = new HashMap<String, String>();
        params1.put("deviceName",hostName);
        params1.put("deviceType",connectionDetails.getParam("deviceType"));
        String ipAddressStr = connectionDetails.getParam("ipAddress");
        params1.put("ipAddress", ipAddressStr);
        IPv4Address ipAddress = new IPv4Address(ipAddressStr, null);


        ResourceType snmpResource = this.discoveryResource.returnResourceByParam(params1);
        Map<String, String> snmpConnParams = this.discoveryResource.getParamMap(snmpResource, "snmp");


        Resource resource = new Resource(hostName,ipAddress,connectionDetails.getParam("deviceType"), Integer.parseInt(snmpConnParams.get("port")), snmpConnParams);

        resource.getAttributes().put("neighbourIPDryRun","true");

        String devName = walker.getDeviceName(resource);
        if (devName == null) {
            logger.info("Device name is null for resource: " + resource);
            return null;
        }
        if  (devName.contains(".")){
            devName=devName.substring(0,devName.indexOf("."));
        }
        result.setNodeId(devName);
        String deviceType = walker.getDeviceType(resource);
        resource.setDeviceType(deviceType);
        DiscoveryHelper discoveryHelper = discoveryHelperFactory.createDiscoveryHelper(deviceType);
        String[] requestParamsList = discoveryHelper.getRequestParams(discoveryTypes);

        RawDeviceData rawData = walker.getRawDeviceData(resource, requestParamsList);
        if (rawData!=null){
            result.setDiscoveredData("rawData", rawData.getData());

        }  else {
            logger.info("Rawdata is null with resource: "+resource);

            return null;
        }

        discoveryHelper.setDryRun(true);


        DiscoveredDeviceData discoveredDeviceData1 = discoveryHelper.parseDeviceRawData(rawData, discoveryTypes, resource);

        OutputStream os  = null;

//        try {
//            os = new ByteArrayOutputStream();
//            JaxbMarshalar.marshal(discoveredDeviceData1, os, "DiscoveredDevice");
//            String str = os.toString();
//        } catch (JAXBException e) {
//            logger.error(e.getMessage(),e);
//
//       } finally {
//            if (os != null) try {os.close();} catch (IOException e) {}
//        }


        SnmpForXslt.resolveIPAddresses(discoveryResource, "snmp");


        discoveryHelper.setDryRun(false);

        DiscoveredDeviceData discoveredDeviceData2 = discoveryHelper.parseDeviceRawData(rawData, discoveryTypes, resource);

        try {
            os = new ByteArrayOutputStream();
            JaxbMarshalar.marshal(discoveredDeviceData2, os, "DiscoveredDevice");
            String str = os.toString();
            System.out.println(str);
        } catch (JAXBException e) {
            logger.error(e.getMessage(),e);
        } finally {
            if (os != null) try {os.close();} catch (IOException e) {}
        }
        result.setDiscoveredData("deviceData", discoveredDeviceData2);
        Device device = discoveryHelper.createDevice(discoveredDeviceData2);

        List<DeviceNeighbour> neighbours = device.getDeviceNeighbours();

        HashMap<String, List<ConnectionDetails>> neighboursConnDetails = createNeighbourConnectionDetails(neighbours);
        result.setNeighboursConnectionDetails(neighboursConnDetails);
        return result;
    }

    private HashMap<String, List<ConnectionDetails>> createNeighbourConnectionDetails(List<DeviceNeighbour> neighbours) {
        HashMap<String, List<ConnectionDetails>> neighboursConnDetails = new HashMap<String, List<ConnectionDetails>>();
        for (DeviceNeighbour neighbour : neighbours) {
            ConnectionDetails neighbourConnectionDetails = new ConnectionDetails();
            neighbourConnectionDetails.put("deviceType",neighbour.getDeviceType());
            if (neighbour.getStatus()){ // if reachable
                neighbourConnectionDetails.put("deviceName",neighbour.getHostName());
                neighbourConnectionDetails.put("ipAddress",neighbour.getIpAddress().getIpAddress());
                neighbourConnectionDetails.setConnectionType("snmp");
                List<ConnectionDetails> connDetailsForNeighour = neighboursConnDetails.get(neighbour.getHostName());
                if (connDetailsForNeighour == null) {
                    connDetailsForNeighour = new ArrayList<ConnectionDetails>();
                    neighboursConnDetails.put(neighbour.getHostName(), connDetailsForNeighour);
                }
                connDetailsForNeighour.add(neighbourConnectionDetails);

            }
        }
        return neighboursConnDetails;
    }

    public List<NeighborDiscoveryListener> getNeighborDiscoveryListeners() {
        return neighborDiscoveryListeners;
    }

    public void setNeighborDiscoveryListeners(List<NeighborDiscoveryListener> neighborDiscoveryListeners) {
        this.neighborDiscoveryListeners = neighborDiscoveryListeners;
    }

}
