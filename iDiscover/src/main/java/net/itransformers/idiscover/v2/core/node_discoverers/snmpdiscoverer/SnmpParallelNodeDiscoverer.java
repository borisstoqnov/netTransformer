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

package net.itransformers.idiscover.v2.core.node_discoverers.snmpdiscoverer;

import net.itransformers.idiscover.core.*;
import net.itransformers.idiscover.discoveryhelpers.xml.SnmpForXslt;
import net.itransformers.idiscover.discoveryhelpers.xml.XmlDiscoveryHelperFactory;
import net.itransformers.idiscover.networkmodel.DiscoveredDeviceData;
import net.itransformers.idiscover.v2.core.NodeDiscoverer;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.idiscover.v2.core.connection_details.IPNetConnectionDetails;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.resourcemanager.config.ResourceType;
import net.itransformers.snmp2xml4j.snmptoolkit.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;

public class SnmpParallelNodeDiscoverer implements NodeDiscoverer {
    static Logger logger = Logger.getLogger(SnmpParallelNodeDiscoverer.class);
    private XmlDiscoveryHelperFactory discoveryHelperFactory;
    private String[] discoveryTypes;
    private DiscoveryResourceManager discoveryResource;
    private MibLoaderHolder mibLoaderHolder;


    public SnmpParallelNodeDiscoverer(XmlDiscoveryHelperFactory discoveryHelperFactory, String[] discoveryTypes, DiscoveryResourceManager discoveryResource, MibLoaderHolder mibLoaderHolder) throws Exception {
        this.discoveryHelperFactory = discoveryHelperFactory;
        this.discoveryTypes = discoveryTypes;
        this.discoveryResource = discoveryResource;
        this.mibLoaderHolder = mibLoaderHolder;
    }

    @Override
    public NodeDiscoveryResult discover(ConnectionDetails connectionDetails) {
        String deviceName = connectionDetails.getParam("deviceName");
        Map<String, String> params1 = new HashMap<String, String>();
        String deviceType = connectionDetails.getParam("deviceType");
        params1.put("deviceName", connectionDetails.getParam("deviceName"));
        params1.put("deviceType", deviceType);
        String ipAddressStr = connectionDetails.getParam("ipAddress");
        params1.put("ipAddress", ipAddressStr);

        SnmpManager snmpManager = null;
        Map<String, String> snmpConnParams = new HashMap<String, String>();

        ResourceType snmpResource = this.discoveryResource.returnResourceByParam(params1);
        ArrayList<ResourceType> snmpResources = this.discoveryResource.returnResourcesByConnectionType("snmp");
        String sysDescr = null;


        snmpConnParams = this.discoveryResource.getParamMap(snmpResource, "snmp");
        snmpConnParams.put("ipAddress", ipAddressStr);

        try {
            //Try first with the most probable snmp Resource
            snmpManager = createSnmpManager(snmpConnParams);
            snmpManager.init();
            sysDescr = snmpGet(snmpManager, "1.3.6.1.2.1.1.1.0");

            //If it does not work try with the rest

            if (sysDescr == null) {
                snmpManager.closeSnmp();
                logger.info("Can't connect to: " + ipAddressStr + " with " + snmpConnParams);

                for (ResourceType resourceType : snmpResources) {
                    snmpConnParams = this.discoveryResource.getParamMap(resourceType, "snmp");
                    snmpConnParams.put("ipAddress", ipAddressStr);

                    if (!resourceType.getName().equals(snmpResource.getName())) {

                        snmpManager = createSnmpManager(snmpConnParams);
                        //   snmpManager.setParameters(snmpConnParams);
                        snmpManager.init();
                        sysDescr = snmpGet(snmpManager, "1.3.6.1.2.1.1.1.0");
                        if (sysDescr == null) {
                            logger.info("Can't connect to: " + ipAddressStr + " with " + snmpConnParams);
                            snmpManager.closeSnmp();
                        } else {
                            deviceType = getDeviceType(sysDescr);
                            logger.info("Connected to: " + ipAddressStr + " with " + snmpConnParams);
                            deviceName = subStringDeviceName(snmpGet(snmpManager, "1.3.6.1.2.1.1.5.0"));
                            break;
                        }
                    }
                }
            } else {
                deviceType = getDeviceType(sysDescr);
                logger.info("Connected to: " + ipAddressStr + " with " + snmpConnParams);
                deviceName = subStringDeviceName(snmpGet(snmpManager, "1.3.6.1.2.1.1.5.0"));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        //Despite all our efforts we got nothing from that device!
        if (sysDescr == null) {
            return null;
        }
        DiscoveryHelper discoveryHelper = discoveryHelperFactory.createDiscoveryHelper(deviceType);
        String[] requestParamsList = discoveryHelper.getRequestParams(discoveryTypes);

        Node rawDatNode = null;
        RawDeviceData rawData = null;
        try {
            rawDatNode = snmpManager.snmpWalk(requestParamsList);
            snmpManager.closeSnmp();

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (rawDatNode != null) {
            SnmpXmlPrinter snmpXmlPrinter = new SnmpXmlPrinter(mibLoaderHolder.getLoader(), rawDatNode);
            rawData = new RawDeviceData(snmpXmlPrinter.printTreeAsXML().getBytes());

            logger.trace(new String(rawData.getData()));

        } else {

            return null;
        }
        SnmpForXslt.setMibLoaderHolder(mibLoaderHolder);
        snmpConnParams.put("neighbourIPDryRun", "true");
        DiscoveredDeviceData discoveredDeviceData = discoveryHelper.parseDeviceRawData(rawData, discoveryTypes, snmpConnParams);
        Device device = discoveryHelper.createDevice(discoveredDeviceData);
        List<DeviceNeighbour> neighbours = device.getDeviceNeighbours();
        List<Subnet> subnets = device.getDeviceSubnets();
        Set<ConnectionDetails> neighboursConnDetails = null;
        if (neighbours != null) {
            neighboursConnDetails = createNeighbourConnectionDetails(neighbours);
        }

        Set<ConnectionDetails> subnetConnectionDetails=null;

       if (subnets!=null){
           subnetConnectionDetails = createSubnetConnectionDetails(subnets);
           neighboursConnDetails.addAll(subnetConnectionDetails);

       }


        NodeDiscoveryResult result = new NodeDiscoveryResult(deviceName, neighboursConnDetails);
        result.setDiscoveredData("deviceData", discoveredDeviceData);
        result.setDiscoveredData("rawData", rawData.getData());
        return result;
    }


    public String getSymbolByOid(String mibName, String oid) {
        return mibLoaderHolder.getSymbolByOid(mibName, oid);
    }

    public String getDeviceType(String sysDescr) {
        if (sysDescr == null) return "UNKNOWN";
        String sysDescrToUpper = sysDescr.toUpperCase();

        if (sysDescrToUpper.toUpperCase().contains("ProCurve".toUpperCase())) {
            return "HP";
        } else if (sysDescrToUpper.contains("Huawei".toUpperCase())) {
            return "HUAWEI";
        } else if (sysDescrToUpper.contains("Juniper".toUpperCase())) {
            return "JUNIPER";
        } else if (sysDescrToUpper.contains("Cisco".toUpperCase())) {
            return "CISCO";
        } else if (sysDescrToUpper.contains("Tellabs".toUpperCase())) {
            return "TELLABS";
        } else if (sysDescrToUpper.contains("SevOne".toUpperCase())) {
            return "SEVONE";
        } else if (sysDescrToUpper.contains("Riverstone".toUpperCase())) {
            return "RIVERSTONE";
        } else if (sysDescrToUpper.contains("ALCATEL".toUpperCase())) {
            return "ALCATEL";
        } else if (sysDescrToUpper.contains("Linux".toUpperCase())) {
            return "LINUX";
        } else if (sysDescrToUpper.contains("Windows".toUpperCase())) {
            return "WINDOWS";
        } else {
            return "UNKNOWN";
        }
    }

    public String subStringDeviceName(String sysName) {
        if (sysName == null) return null;
        String hostName = StringUtils.substringBefore(sysName, ".");
        return hostName;

    }

    private String snmpGet(SnmpManager snmpManager, String oidString) {
        try {
            return snmpManager.snmpGet(oidString);
        } catch (IOException e) {
            logger.info(e.getMessage());
            return null;
        }
    }

    private SnmpManager createSnmpManager(Map<String, String> snmpConnParams) throws IOException {

        String version = snmpConnParams.get("version");
        String protocol = snmpConnParams.get("protocol");
        SnmpManager snmpManager = null;

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
            throw new RuntimeException("SnmpManager is null");

        }
        snmpManager.setParameters(snmpConnParams);
        return snmpManager;

    }

    private Set<ConnectionDetails> createNeighbourConnectionDetails(List<DeviceNeighbour> neighbours) {
        Set<ConnectionDetails> neighboursConnDetails = new HashSet<ConnectionDetails>();
        for (DeviceNeighbour neighbour : neighbours) {
            ConnectionDetails neighbourConnectionDetails = new IPNetConnectionDetails();
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
    private Set<ConnectionDetails> createSubnetConnectionDetails(List<Subnet> subnets) {
        Set<ConnectionDetails> subnetConnectionDetails = new HashSet<ConnectionDetails>();

        for (Subnet subnet : subnets) {
            ConnectionDetails subnetConnection = new IPNetConnectionDetails();
            String ipAddress = subnet.getIpAddress();
            String protocolType = subnet.getSubnetProtocolType();
            String subnetMask = subnet.getsubnetMask();
                subnetConnection.put("ipAddress", ipAddress);
                subnetConnection.put("protocolType", protocolType);
                subnetConnection.put("subnetMask", subnetMask);
                subnetConnection.setConnectionType("subnet");

                subnetConnectionDetails.add(subnetConnection);


        }
        return subnetConnectionDetails;
    }

}
