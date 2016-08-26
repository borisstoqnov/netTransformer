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
import net.itransformers.idiscover.util.DeviceTypeResolver;
import net.itransformers.idiscover.v2.core.NodeDiscoverer;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.idiscover.v2.core.node_discoverers.snmpdiscoverer.snmpmanager.SnmpManagerCreator;
import net.itransformers.resourcemanager.config.ResourceType;
import net.itransformers.snmp2xml4j.snmptoolkit.MibLoaderHolder;
import net.itransformers.snmp2xml4j.snmptoolkit.Node;
import net.itransformers.snmp2xml4j.snmptoolkit.SnmpManager;
import net.itransformers.snmp2xml4j.snmptoolkit.SnmpXmlPrinter;

import java.io.IOException;
import java.net.InetAddress;
import java.util.*;

public class SnmpSequentialNodeDiscoverer extends SnmpNodeDiscoverer implements NodeDiscoverer {

    public SnmpSequentialNodeDiscoverer(XmlDiscoveryHelperFactory discoveryHelperFactory, String[] discoveryTypes, DiscoveryResourceManager discoveryResource, MibLoaderHolder mibLoaderHolder, boolean icmpStatus) throws Exception {
        super(discoveryHelperFactory, discoveryTypes, discoveryResource, mibLoaderHolder,icmpStatus);
    }


    @Override
    public NodeDiscoveryResult discover(ConnectionDetails connectionDetails) {
        Map<String, String> params1 = new HashMap<String, String>();
        String deviceName = connectionDetails.getParam("deviceName");

        NodeDiscoveryResult result = new NodeDiscoveryResult();

        if (deviceName!=null && !deviceName.isEmpty()){
            params1.put("deviceName", deviceName);

        }
        String deviceType = connectionDetails.getParam("deviceType");
        if (deviceType!=null && !deviceType.isEmpty()){
            params1.put("deviceType", deviceType);

        }
        String ipAddressStr = connectionDetails.getParam("ipAddress");

        if (ipAddressStr!=null && !ipAddressStr.isEmpty()){
            params1.put("ipAddress", ipAddressStr);

        }
        String dnsCanonicalName=null;
        String dnsShortName=null;
        InetAddress inetAddress =null;
        SnmpManager snmpManager = null;
        Map<String, String> snmpConnParams = new HashMap<String, String>();

        ResourceType snmpResource = this.discoveryResource.returnResourceByParam(params1);
        ArrayList<ResourceType> snmpResources = this.discoveryResource.returnResourcesByConnectionType("snmp");
        String sysDescr;
        snmpConnParams = this.discoveryResource.getParamMap(snmpResource, "snmp");
        snmpConnParams.put("ipAddress", ipAddressStr);
        boolean reachable;
        SnmpManagerCreator snmpManagerCreator = new SnmpManagerCreator(mibLoaderHolder);

        try {
            reachable = inetAddress.isReachable(Integer.parseInt(snmpConnParams.get("timeout")));
            logger.info("Device with "+inetAddress.getHostAddress() + "is " + reachable);
            //Try first with the most probable snmp Resource
            snmpManager = snmpManagerCreator.create(snmpConnParams);
            snmpManager.init();
            sysDescr = snmpGet(snmpManager,"1.3.6.1.2.1.1.1.0");

            //If it does not work try with the rest

            if (sysDescr==null) {
                snmpManager.closeSnmp();
                logger.info("Can't connect to: " + ipAddressStr + " with " + snmpConnParams);

                for (ResourceType resourceType : snmpResources) {
                    snmpConnParams = this.discoveryResource.getParamMap(resourceType, "snmp");
                    snmpConnParams.put("ipAddress", ipAddressStr);

                    if (!resourceType.getName().equals(snmpResource.getName())) {

                            snmpManager = snmpManagerCreator.create(snmpConnParams);
                            snmpManager.init();
                            sysDescr = snmpGet(snmpManager, "1.3.6.1.2.1.1.1.0");
                            if (sysDescr == null) {
                                logger.info("Can't connect to: " + ipAddressStr + " with " + snmpConnParams);
                                snmpManager.closeSnmp();
                            } else {
                                deviceType = DeviceTypeResolver.getDeviceType(sysDescr);
                                logger.info("Connected to: " + ipAddressStr + " with " + snmpConnParams);
                                deviceName = subStringDeviceName(snmpGet(snmpManager, "1.3.6.1.2.1.1.5.0"));
                                break;
                            }
                    }
                }
            }else {
                deviceType = DeviceTypeResolver.getDeviceType(sysDescr);
                logger.info("Connected to: " + ipAddressStr + " with " + snmpConnParams);
                deviceName = subStringDeviceName(snmpGet(snmpManager, "1.3.6.1.2.1.1.5.0"));
            }
        } catch (IOException e) {
            logger.error("Something went wrong in SNMP communication with "+ipAddressStr+":Check the stacktrace \n"+e.getStackTrace());
            return null;
        }

        //Despite all our efforts we got nothing from that device!
        if (sysDescr == null) {
            if (deviceName!=null) {
                 result = new NodeDiscoveryResult(deviceName, null);
            }else if (dnsCanonicalName!=null){
                result =  new NodeDiscoveryResult(dnsShortName, null);
            }else {
                result = new NodeDiscoveryResult(ipAddressStr,null);
            }

            return result;
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
         discoveryHelper.parseDeviceRawData(rawData, discoveryTypes, snmpConnParams);


        SnmpForXslt.resolveIPAddresses(discoveryResource, "snmp");
        snmpConnParams.put("neighbourIPDryRun", "false");

        DiscoveredDeviceData discoveredDeviceData = discoveryHelper.parseDeviceRawData(rawData, discoveryTypes, snmpConnParams);

        Device device = discoveryHelper.createDevice(discoveredDeviceData);


        List<DeviceNeighbour> neighbours = device.getDeviceNeighbours();
        List<Subnet> subnets = device.getDeviceSubnets();
        Set<ConnectionDetails> neighboursConnDetails = null;
        if (neighbours != null) {

            //TODO adapt for new algorthm and new DiscoveredDevice
            NeighbourConnectionDetails neighbourConnectionDetails = null;
            neighboursConnDetails = neighbourConnectionDetails.getConnectionDetailses();
        }

        Set<ConnectionDetails> subnetConnectionDetails=null;

       if (subnets!=null){
           SubnetConnectionDetails subnetConnectionDetailsCreator = new SubnetConnectionDetails(subnets);
           neighboursConnDetails.addAll(subnetConnectionDetails);

       }

        result = new NodeDiscoveryResult(deviceName, neighboursConnDetails);
        result.setDiscoveredData("deviceData", discoveredDeviceData);
        result.setDiscoveredData("rawData", rawData.getData());
        return result;
    }

}
