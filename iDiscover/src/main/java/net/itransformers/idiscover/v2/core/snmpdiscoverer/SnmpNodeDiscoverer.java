/*
 * SnmpNodeDiscoverer.java
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

package net.itransformers.idiscover.v2.core.snmpdiscoverer;

import net.itransformers.idiscover.core.*;
import net.itransformers.idiscover.discoveryhelpers.xml.SnmpForXslt;
import net.itransformers.idiscover.discoveryhelpers.xml.XmlDiscoveryHelperFactory;
import net.itransformers.idiscover.networkmodel.DiscoveredDeviceData;
import net.itransformers.idiscover.v2.core.NeighborDiscoveryListener;
import net.itransformers.idiscover.v2.core.NodeDiscoverer;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.resourcemanager.config.ResourceType;
import net.itransformers.snmp2xml4j.snmptoolkit.*;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SnmpNodeDiscoverer implements NodeDiscoverer {
    static Logger logger = Logger.getLogger(SnmpNodeDiscoverer.class);
    private XmlDiscoveryHelperFactory discoveryHelperFactory;
    private String[] discoveryTypes;
    private DiscoveryResourceManager discoveryResource;
    protected List<NeighborDiscoveryListener> neighborDiscoveryListeners;
    private SnmpManager snmpManager;
    private MibLoaderHolder mibLoaderHolder;


    public SnmpNodeDiscoverer(XmlDiscoveryHelperFactory discoveryHelperFactory, String[] discoveryTypes, DiscoveryResourceManager discoveryResource, List<NeighborDiscoveryListener> neighborDiscoveryListeners, MibLoaderHolder mibLoaderHolder) throws Exception {
        this.discoveryHelperFactory = discoveryHelperFactory;
        this.discoveryTypes = discoveryTypes;
        this.discoveryResource = discoveryResource;
        this.neighborDiscoveryListeners = neighborDiscoveryListeners;
        this.mibLoaderHolder = mibLoaderHolder;

//        SnmpForXslt.setNeighborDiscoveryListeners(neighborDiscoveryListeners);
    }


    @Override
    public NodeDiscoveryResult discover(ConnectionDetails connectionDetails) {



        NodeDiscoveryResult result = new NodeDiscoveryResult();
        String deviceName = connectionDetails.getParam("deviceName");
        Map<String,String> params1 = new HashMap<String, String>();
        String deviceType = connectionDetails.getParam("deviceType");
        params1.put("deviceName", connectionDetails.getParam("deviceName"));
        params1.put("deviceType", deviceType);
        String ipAddressStr = connectionDetails.getParam("ipAddress");
        params1.put("ipAddress", ipAddressStr);


        ResourceType snmpResource = this.discoveryResource.returnResourceByParam(params1);
        Map<String, String> snmpConnParams = this.discoveryResource.getParamMap(snmpResource, "snmp");

        snmpConnParams.put("ipAddress", ipAddressStr);


        try {
            setSnmpManager(snmpConnParams);
            snmpManager.setParameters(snmpConnParams);
            snmpManager.init();
            String sysDescr = snmpGet("1.3.6.1.2.1.1.1.0");
            if (sysDescr == null) {
                logger.info("Can't connect to: " + ipAddressStr + " with " + snmpConnParams);
                return null;
            }
            deviceType = getDeviceType(sysDescr);


        } catch (IOException e) {
            e.printStackTrace();
        }


        DiscoveryHelper discoveryHelper = discoveryHelperFactory.createDiscoveryHelper(deviceType);
        String[] requestParamsList = discoveryHelper.getRequestParams(discoveryTypes);


        Node rawDatNode = null;
        RawDeviceData rawData = null;
        try {
            rawDatNode = snmpManager.snmpWalk(requestParamsList);

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (rawDatNode != null) {
            SnmpXmlPrinter snmpXmlPrinter = new SnmpXmlPrinter(mibLoaderHolder.getLoader(), rawDatNode);
            rawData = new RawDeviceData(snmpXmlPrinter.printTreeAsXML().getBytes());

            result.setDiscoveredData("rawData", rawData.getData());
            System.out.println(new String(rawData.getData()));

        } else {

            return null;
        }

        discoveryHelper.setDryRun(true);

        SnmpForXslt.setSnmpManager(snmpManager);


        DiscoveredDeviceData discoveredDeviceData = discoveryHelper.parseDeviceRawData(rawData, discoveryTypes, snmpConnParams);


//        SnmpForXslt.resolveIPAddresses(discoveryResource, "snmp");

        result.setNodeId(deviceName);
        result.setDiscoveredData("deviceData", discoveredDeviceData);
        result.setConnParams(snmpConnParams);
        Device device = discoveryHelper.createDevice(discoveredDeviceData);

        List<DeviceNeighbour> neighbours = device.getDeviceNeighbours();
        List<ConnectionDetails> neighboursConnDetails = null;
        if (neighbours != null) {
            neighboursConnDetails = createNeighbourConnectionDetails(neighbours);
            result.setNeighboursConnectionDetails(neighboursConnDetails);
        }
        return result;
    }


    public String getSymbolByOid(String mibName, String oid) {
        return mibLoaderHolder.getSymbolByOid(mibName, oid);
    }

    public String getDeviceType(String sysDescr) {

        if (sysDescr == null) return "UNKNOWN";
        if (sysDescr.contains("ProCurve".toUpperCase())) {
            return "HP";
        } else if (sysDescr.contains("Huawei".toUpperCase())) {
            return "HUAWEI";
        } else if (sysDescr.contains("Juniper".toUpperCase())) {
            return "JUNIPER";
        } else if (sysDescr.contains("Cisco".toUpperCase())) {
            return "CISCO";
        } else if (sysDescr.contains("Tellabs".toUpperCase())) {
            return "TELLABS";
        } else if (sysDescr.contains("SevOne".toUpperCase())) {
            return "SEVONE";
        } else if (sysDescr.contains("Riverstone".toUpperCase())) {
            return "RIVERSTONE";
        } else if (sysDescr.contains("ALCATEL".toUpperCase())) {
            return "ALCATEL";
        } else if (sysDescr.contains("Linux".toUpperCase())) {
            return "LINUX";
        } else if (sysDescr.contains("Windows".toUpperCase())) {
            return "WINDOWS";
        } else {
            return "UNKNOWN";
        }
    }

    private String snmpGet(String oidString) {


        try {
            return snmpManager.snmpGet(oidString);


        } catch (IOException e) {
            logger.info(e.getMessage());
            return null;
        }


    }

    private void setSnmpManager(Map<String, String> snmpConnParams) throws IOException {

        String version = snmpConnParams.get("version");
        String protocol = snmpConnParams.get("protocol");


        if ("3".equals(version) && "udp".equals(protocol)) {

            snmpManager = new SnmpUdpV3Manager(mibLoaderHolder.getLoader());


        } else if ("3".equals(version) && "tcp".equals(protocol)) {

            snmpManager = new SnmpTcpV3Manager(mibLoaderHolder.getLoader());

        } else if ("2".equals(version) && "udp".equals(protocol)) {

            snmpManager = new SnmpUdpV2Manager(mibLoaderHolder.getLoader());


        } else if ("2c".equals(version) && "udp".equals(protocol)) {

            snmpManager = new SnmpUdpV2Manager(mibLoaderHolder.getLoader());


        } else if ("2".equals(version) && "tcp".equals(protocol)) {
            snmpManager = new SnmpTcpV2Manager(mibLoaderHolder.getLoader());


        } else if ("2c".equals(version) && "tcp".equals(protocol)) {
            snmpManager = new SnmpTcpV2Manager(mibLoaderHolder.getLoader());


        } else if ("1".equals(version) && "udp".equals(protocol)) {

            snmpManager = new SnmpTcpV1Manager(mibLoaderHolder.getLoader());


        } else if ("1".equals(version) && "tcp".equals(protocol)) {

            snmpManager = new SnmpTcpV1Manager(mibLoaderHolder.getLoader());


        } else {

            logger.info("Unsupported combination of protocol: " + protocol + " and version " + version);

        }

    }

//    public NodeDiscoveryResult mockDiscover(ConnectionDetails connectionDetails,String hostName ) {
//        if (hostName == null) {
//            return null;
//        }
//        NodeDiscoveryResult result = new NodeDiscoveryResult();
//        Map<String,String> params1 = new HashMap<String, String>();
//        params1.put("deviceName",hostName);
//        params1.put("deviceType",connectionDetails.getParam("deviceType"));
//        String ipAddressStr = connectionDetails.getParam("ipAddress");
//        params1.put("ipAddress", ipAddressStr);
//        IPv4Address ipAddress = new IPv4Address(ipAddressStr, null);
//
//
//        ResourceType snmpResource = this.discoveryResource.returnResourceByParam(params1);
//        Map<String, String> snmpConnParams = this.discoveryResource.getParamMap(snmpResource, "snmp");
//
//
//        Resource resource = new Resource(hostName,ipAddress,connectionDetails.getParam("deviceType"), Integer.parseInt(snmpConnParams.get("port")), snmpConnParams);
//
//        resource.getAttributes().put("neighbourIPDryRun","true");
//
//        String devName = walker.getDeviceName(resource);
//        if (devName == null) {
//            logger.info("Device name is null for resource: " + resource);
//            return null;
//        }
//        if  (devName.contains(".")){
//            devName=devName.substring(0,devName.indexOf("."));
//        }
//        result.setNodeId(devName);
//        String deviceType = walker.getDeviceType(resource);
//        resource.setDeviceType(deviceType);
//        DiscoveryHelper discoveryHelper = discoveryHelperFactory.createDiscoveryHelper(deviceType);
//        String[] requestParamsList = discoveryHelper.getRequestParams(discoveryTypes);
//
//        RawDeviceData rawData = walker.getRawDeviceData(resource, requestParamsList);
//        if (rawData!=null){
//            result.setDiscoveredData("rawData", rawData.getData());
//
//        }  else {
//            logger.info("Rawdata is null with resource: "+resource);
//
//            return null;
//        }
//
//        discoveryHelper.setDryRun(true);
//
//
//        DiscoveredDeviceData discoveredDeviceData1 = discoveryHelper.parseDeviceRawData(rawData, discoveryTypes, resource);
//
//        OutputStream os  = null;
//
////        try {
////            os = new ByteArrayOutputStream();
////            JaxbMarshalar.marshal(discoveredDeviceData1, os, "DiscoveredDevice");
////            String str = os.toString();
////        } catch (JAXBException e) {
////            logger.error(e.getMessage(),e);
////
////       } finally {
////            if (os != null) try {os.close();} catch (IOException e) {}
////        }
//
//
//        SnmpForXslt.resolveIPAddresses(discoveryResource, "snmp");
//
//
//        discoveryHelper.setDryRun(false);
//
//        DiscoveredDeviceData discoveredDeviceData2 = discoveryHelper.parseDeviceRawData(rawData, discoveryTypes, resource);
//
//        try {
//            os = new ByteArrayOutputStream();
//            JaxbMarshalar.marshal(discoveredDeviceData2, os, "DiscoveredDevice");
//            String str = os.toString();
//            System.out.println(str);
//        } catch (JAXBException e) {
//            logger.error(e.getMessage(),e);
//        } finally {
//            if (os != null) try {os.close();} catch (IOException e) {}
//        }
//        result.setDiscoveredData("deviceData", discoveredDeviceData2);
//        Device device = discoveryHelper.createDevice(discoveredDeviceData2);
//
//        List<DeviceNeighbour> neighbours = device.getDeviceNeighbours();
//
//        List<ConnectionDetails> neighboursConnDetails = createNeighbourConnectionDetails(neighbours);
//        result.setNeighboursConnectionDetails(neighboursConnDetails);
//        return result;
//    }

    private List<ConnectionDetails> createNeighbourConnectionDetails(List<DeviceNeighbour> neighbours) {
        List<ConnectionDetails> neighboursConnDetails = new ArrayList<ConnectionDetails>();
        for (DeviceNeighbour neighbour : neighbours) {
            ConnectionDetails neighbourConnectionDetails = new ConnectionDetails();
            String ipAddress = neighbour.getIpAddress();

            if (InetAddressValidator.getInstance().isValid(ipAddress)) {

                neighbourConnectionDetails.put("deviceType", neighbour.getDeviceType());
                neighbourConnectionDetails.put("deviceName", neighbour.getHostName());
                neighbourConnectionDetails.put("ipAddress", ipAddress);
                neighbourConnectionDetails.setConnectionType("snmp");
                neighboursConnDetails.add(neighbourConnectionDetails);
            } else {
                logger.info("Device has an invalid ipAddreess " + neighbour);
                // InetAddress.getByName(ipAddress);
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
