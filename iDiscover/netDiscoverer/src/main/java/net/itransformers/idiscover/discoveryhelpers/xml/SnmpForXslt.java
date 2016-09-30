/*
 * SnmpForXslt.java
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

package net.itransformers.idiscover.discoveryhelpers.xml;

import net.itransformers.idiscover.core.DiscoveryResourceManager;
//import net.itransformers.idiscover.v2.core.NeighborDiscoveryResult;
import net.itransformers.resourcemanager.config.ConnectionParamsType;
import net.itransformers.resourcemanager.config.ParamType;
import net.itransformers.resourcemanager.config.ResourceType;
import net.itransformers.snmp2xml4j.snmptoolkit.*;
import net.itransformers.utils.CIDRUtils;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SnmpForXslt {

    static Logger logger = Logger.getLogger(SnmpForXslt.class);

    private static InetAddressValidator ipAddressValidator = new InetAddressValidator();
    private static CIDRUtils cidrUtils;

//    private static List<NeighborDiscoveryListener> neighborDiscoveryListeners;
    private static Map<String, HashMap<String, String>> discoveredIPs = new HashMap<String, HashMap<String, String>>();
    private static Map<String, HashMap<String, String>> discoveredMACs = new HashMap<String, HashMap<String, String>>();

    private static MockSnmpForXslt mockSnmpForXslt = null;

    private static MibLoaderHolder mibLoaderHolder;


    public static void setMockSnmpForXslt(MockSnmpForXslt mockSnmpForXslt){
        SnmpForXslt.mockSnmpForXslt = mockSnmpForXslt;
    }


    public static String checkBogons(String ipAddress) {
        if (ipAddress!=null) {
            try {
                cidrUtils = new CIDRUtils("0.0.0.0/8");

                if (cidrUtils.isInRange(ipAddress)) {
                    return null;
                }
                cidrUtils = new CIDRUtils("127.0.0.0/8");
                if (cidrUtils.isInRange(ipAddress)) {
                    return null;
                }
                cidrUtils = new CIDRUtils("169.254.0.0/16");
                if (cidrUtils.isInRange(ipAddress)) {
                    return null;
                }
                cidrUtils = new CIDRUtils("192.0.0.0/24");
                if (cidrUtils.isInRange(ipAddress)) {
                    return null;
                }
                cidrUtils = new CIDRUtils("192.0.2.0/24");
                if (cidrUtils.isInRange(ipAddress)) {
                    return null;
                }
                cidrUtils = new CIDRUtils("224.0.0.0/4");
                if (cidrUtils.isInRange(ipAddress)) {
                    return null;
                }
                cidrUtils = new CIDRUtils("240.0.0.0/4");
                if (cidrUtils.isInRange(ipAddress)) {
                    return null;
                }
                cidrUtils = new CIDRUtils("255.255.255.255/32");
                if (cidrUtils.isInRange(ipAddress)) {
                    return null;
                }
                return ipAddress;
            } catch (UnknownHostException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            return null;
        }else {
            return null;
        }

    }

    public static String getSubnetFromPrefix(String prefix) {
        try {
            cidrUtils = new CIDRUtils(prefix);
            return  cidrUtils.getNetworkAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;

    }


    public static String getBroadCastFromPrefix(String prefix) {

        try {
            CIDRUtils cidrUtils = new CIDRUtils(prefix);
            return  cidrUtils.getBroadcastAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;

    }

    public static String getNameByDNS(String ipAddress)  {
        if (mockSnmpForXslt != null) {
            return mockSnmpForXslt.getNameByDNS(ipAddress);
        }

//        InetAddress inetAddress = new InetAddress();
        InetAddress address = null;
        try {
            address = InetAddress.getByName(ipAddress);
        } catch (UnknownHostException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        return address.getHostAddress();



    }

    public static String getName(String ipAddress, String neighbourIPDryRun) throws Exception {


        HashMap<String, String> deviceNameMap = discoveredIPs.get(ipAddress);

        if(neighbourIPDryRun.equals("true")){


            if (deviceNameMap != null) {
                return null;
            } else {
                deviceNameMap = new HashMap<String, String>();
                discoveredIPs.put(ipAddress, deviceNameMap);
                return null;
            }

        } else {

            if (deviceNameMap != null) {
                String deviceName = deviceNameMap.get("snmp");
                if ("".equals(deviceName) || deviceName == null) {
                    return null;
                } else  {
                    return deviceName;
                }
            }   else {
                return null;
            }
        }


    }

    public static String getDeviceType(String ipAddress, String neighbourIPDryRun) throws Exception {


        HashMap<String, String> deviceMap = discoveredIPs.get(ipAddress);

        if(neighbourIPDryRun.equals("true")){


            if (deviceMap != null) {
                return null;
            }

        }

        String deviceType = null;
        if (deviceMap!=null) {
             deviceType = deviceMap.get("deviceType");
        } else{
            return "UNKNOWN";
        }

        if ("".equals(deviceType) || deviceType == null) {
            return "UNKNOWN";
        } else  {
            return deviceType;
        }

    }



    public static String getSymbolByOid(String mibName, String oid) throws Exception {

        return mibLoaderHolder.getSymbolByOid(mibName, oid);
    }

    public static String getByOid(String ipAddress, String oid, String community,String timeout, String retries) throws Exception {
        if (mockSnmpForXslt != null) {
            return mockSnmpForXslt.getByOid(ipAddress, oid, community, timeout, retries);
        }

        HashMap<String, String> deviceNameMap = discoveredIPs.get(ipAddress);


        String deviceName = null;
        if(!ipAddressValidator.isValid(ipAddress))
            return  null;

        if (deviceNameMap != null) {
            deviceName = deviceNameMap.get(community);
        }

        if ("".equals(deviceName)) {
            return null;
        } else if (deviceName == null) {

            if ("".equals(retries) || retries==null) retries = "1";
            if ("".equals(timeout) || timeout==null) timeout = "300";
            Map<String, String> resourceParams = new HashMap<String, String>();
            resourceParams.put("snmpCommunity", community);
            resourceParams.put("version", "1");
            resourceParams.put("retries", retries);
            resourceParams.put("timeout", timeout);
            resourceParams.put("ipAddress",ipAddress);

            SnmpManager snmpManager = createSnmpManager(resourceParams);
            final String oidValue = snmpManager.snmpGet(oid);
            logger.debug("hostname:" + ipAddress + ", community: " + community + ", oidValue:" + oidValue);
            return oidValue;

        } else {
            return null;
        }
    }

//    public boolean setByOID(String hostName, String oid, String community, String value) throws Exception {
//        if (mockSnmpForXslt != null) {
//            return mockSnmpForXslt.setByOID(hostName, oid, community, value);
//        }
//
//
//        Map<String, String> resourceParams = new HashMap<String, String>();
//        resourceParams.put("community-rw", community);
//        resourceParams.put("version", "1");
//        Resource resource = new Resource(hostName, null, resourceParams);// TODO specify port
//        discovererFactory = new DefaultDiscovererFactory();
//        SnmpWalker discoverer = (SnmpWalker) discovererFactory.createDiscoverer(resource);
//        final String result = discoverer.setByOid(resource, oid, value);
//        logger.debug("snmpSet for hostname:" + hostName + ", community: " + community + ", oid " + oid + ", value " + value + " = " + result);
//        return true;
//
//    }

//    public String walkByString(String hostName, String[] params, String community) throws Exception {
//        if (mockSnmpForXslt != null) {
//            return mockSnmpForXslt.walkByString(hostName, params, community);
//        }
//        Map<String, String> resourceParams = new HashMap<String, String>();
//        resourceParams.put("community-rw", community);
//        resourceParams.put("version", "1");
//        resourceParams.put("retries", "0");
//        Resource resource = new Resource(hostName, null, resourceParams);
//        discovererFactory = new DefaultDiscovererFactory();
//        SnmpWalker discoverer = (SnmpWalker) discovererFactory.createDiscoverer(resource);
//        final String result = discoverer.snmpWalkDevice(resource, params);
//        return result;
//
//    }

    public static Map<String, HashMap<String, String>> getDiscoveredIPs() {
        return discoveredIPs;
    }

    public static void setDiscoveredIPs(Map<String, HashMap<String, String>> discoveredIPs) {
        SnmpForXslt.discoveredIPs = discoveredIPs;
    }


    public static void resolveIPAddresses(DiscoveryResourceManager resourceManager, String connectionType) {
        for (String ipAddress : discoveredIPs.keySet()) {

            HashMap<String, String> deviceNameMap = discoveredIPs.get(ipAddress);


            if (!deviceNameMap.containsKey("snmp")) {

                deviceNameMap = new HashMap<String, String>();

            } else {
                //DeviceMap already filled in.
                continue;
            }

            if (!ipAddressValidator.isValidInet4Address(ipAddress))
                continue;

            Map<String, String> resourceParameters = new HashMap<String, String>();
            resourceParameters.put("ipAddress", ipAddress);

            ResourceType resourceType = resourceManager.returnResourceByParam(resourceParameters);

            List<ResourceType> snmpResources = resourceManager.returnResourcesByConnectionType("snmp");


            Map<String, String> connParams = new HashMap<String, String>();

            List<ConnectionParamsType> connectionParams = resourceType.getConnectionParams();

            for (ConnectionParamsType connectionParam : connectionParams) {
                if (connectionParam.getConnectionType().equals(connectionType)) {
                    for (ParamType param : connectionParam.getParam()) {
                        connParams.put(param.getName(), param.getValue());
                    }
                    break;
                }
            }
            connParams.put("ipAddress", ipAddress);


            String deviceNameFromSNMP = null;
            String deviceTypeFromSNMP = null;
            try {
                SnmpManager snmpManager = createSnmpManager(connParams);

              //  snmpManager.setParameters(connParams);
                snmpManager.init();
                deviceNameFromSNMP = snmpManager.snmpGet("1.3.6.1.2.1.1.5.0");

                if (deviceNameFromSNMP == null) {
                    snmpManager.closeSnmp();
                    for (ResourceType snmpResource : snmpResources) {
                        connParams = resourceManager.getParamMap(snmpResource, "snmp");
                        connParams.put("ipAddress", ipAddress);
                        if (!resourceType.getName().equals(snmpResource.getName())) {
                            logger.info("Trying ipAddress " + ipAddress + " with " + connParams);
                            snmpManager = createSnmpManager(connParams);
                            snmpManager.init();
                            deviceNameFromSNMP = snmpManager.snmpGet("1.3.6.1.2.1.1.5.0");
                            if (deviceNameFromSNMP == null) {
                                snmpManager.closeSnmp();

                            }   else {
                                final String sysDescr = snmpManager.snmpGet("1.3.6.1.2.1.1.1.0");
                                deviceTypeFromSNMP = getDeviceType(sysDescr);
                                snmpManager.closeSnmp();

                            }
                        }
                    }
                }
                deviceNameMap.put("snmp", deviceNameFromSNMP);



                deviceNameMap.put("deviceType", deviceTypeFromSNMP);

                discoveredIPs.put(ipAddress, deviceNameMap);
//                NeighborDiscoveryResult neighborDiscoveryResult = new NeighborDiscoveryResult();
//                neighborDiscoveryResult.setDiscoveredIpAddress(ipAddress);
//                neighborDiscoveryResult.setNeighborType(deviceTypeFromSNMP);
//                neighborDiscoveryResult.setNodeId(deviceNameFromSNMP);
//                neighborDiscoveryResult.setConnParams(connParams);
//
//                if (neighborDiscoveryListeners != null) {
//                    //Fire neighbourDiscoveredEvent
//                    for (NeighborDiscoveryListener neighbourDiscoveryListerner : neighborDiscoveryListeners) {
//
//                        neighbourDiscoveryListerner.neighborDiscovered(neighborDiscoveryResult);
//
//                    }
//                }


            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

        public static String getDeviceType(String sysDescr) {
        String sysDescrToUpper = sysDescr.toUpperCase();


            if (sysDescr == null) return "UNKNOWN";
        if (sysDescrToUpper.contains("ProCurve".toUpperCase())) {
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
//
//    public static List<NeighborDiscoveryListener> getNeighborDiscoveryListeners() {
//        return neighborDiscoveryListeners;
//    }
//
//    public static void setNeighborDiscoveryListeners(List<NeighborDiscoveryListener> neighborDiscoveryListeners) {
//        SnmpForXslt.neighborDiscoveryListeners = neighborDiscoveryListeners;
//    }

    public static MibLoaderHolder getMibLoaderHolder() {
        return mibLoaderHolder;
    }

    public static void setMibLoaderHolder(MibLoaderHolder mibLoaderHolder) {
        SnmpForXslt.mibLoaderHolder = mibLoaderHolder;
    }

    private static SnmpManager createSnmpManager(Map<String, String> snmpConnParams) throws IOException {

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
}
