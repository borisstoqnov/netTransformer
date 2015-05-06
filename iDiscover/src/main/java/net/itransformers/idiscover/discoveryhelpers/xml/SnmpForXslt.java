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

package net.itransformers.idiscover.discoveryhelpers.xml;

import net.itransformers.idiscover.core.Discoverer;
import net.itransformers.idiscover.core.DiscovererFactory;
import net.itransformers.idiscover.core.DiscoveryResourceManager;
import net.itransformers.idiscover.core.Resource;
import net.itransformers.idiscover.discoverers.DefaultDiscovererFactory;
import net.itransformers.idiscover.discoverers.SnmpWalker;
import net.itransformers.resourcemanager.config.ConnectionParamsType;
import net.itransformers.resourcemanager.config.ParamType;
import net.itransformers.resourcemanager.config.ResourceType;
import net.itransformers.utils.CIDRUtils;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SnmpForXslt {

    static Logger logger = Logger.getLogger(SnmpForXslt.class);

    private static DiscovererFactory discovererFactory;
    private static InetAddressValidator ipAddressValidator = new InetAddressValidator();
    private static CIDRUtils cidrUtils;

    private static Map<String, HashMap<String, String>> discoveredDevices = new HashMap<String, HashMap<String, String>>();
    private static MockSnmpForXslt mockSnmpForXslt = null;

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

        System.out.println(address.getHostAddress());


        return address.getHostAddress();



    }

    public static String getName(String ipAddress, String neighbourIPDryRun) throws Exception {



        HashMap<String, String> deviceNameMap = discoveredDevices.get(ipAddress);

        if(neighbourIPDryRun.equals("true")){


            if (deviceNameMap != null) {
                return null;
            } else {
                deviceNameMap = new HashMap<String, String>();
                discoveredDevices.put(ipAddress,deviceNameMap);
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



        HashMap<String, String> deviceMap = discoveredDevices.get(ipAddress);

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


        discovererFactory = new DefaultDiscovererFactory();
        SnmpWalker discoverer = (SnmpWalker) discovererFactory.createDiscoverer(null);
        return discoverer.getSymbolByOid(mibName, oid);
    }

    public static String getByOid(String ipAddress, String oid, String community,String timeout, String retries) throws Exception {
        if (mockSnmpForXslt != null) {
            return mockSnmpForXslt.getByOid(ipAddress, oid, community, timeout, retries);
        }

        HashMap<String, String> deviceNameMap = discoveredDevices.get(ipAddress);


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
            resourceParams.put("community-ro", community);
            resourceParams.put("version", "1");
            resourceParams.put("retries", retries);
            resourceParams.put("timeout", timeout);
            Resource resource = new Resource(ipAddress, null, resourceParams);// TODO specify port
            discovererFactory = new DefaultDiscovererFactory();
            SnmpWalker discoverer = (SnmpWalker) discovererFactory.createDiscoverer(resource);
            final String oidValue = discoverer.getByOid(resource, oid);
            logger.debug("hostname:" + ipAddress + ", community: " + community + ", oidValue:" + oidValue);
            return oidValue;

        } else {
            return null;
        }
    }

    public boolean setByOID(String hostName, String oid, String community, String value) throws Exception {
        if (mockSnmpForXslt != null) {
            return mockSnmpForXslt.setByOID(hostName, oid, community, value);
        }


        Map<String, String> resourceParams = new HashMap<String, String>();
        resourceParams.put("community-rw", community);
        resourceParams.put("version", "1");
        Resource resource = new Resource(hostName, null, resourceParams);// TODO specify port
        discovererFactory = new DefaultDiscovererFactory();
        SnmpWalker discoverer = (SnmpWalker) discovererFactory.createDiscoverer(resource);
        final String result = discoverer.setByOid(resource, oid, value);
        logger.debug("snmpSet for hostname:" + hostName + ", community: " + community + ", oid " + oid + ", value " + value + " = " + result);
        return true;

    }

    public String walkByString(String hostName, String[] params, String community) throws Exception {
        if (mockSnmpForXslt != null) {
            return mockSnmpForXslt.walkByString(hostName, params, community);
        }
        Map<String, String> resourceParams = new HashMap<String, String>();
        resourceParams.put("community-rw", community);
        resourceParams.put("version", "1");
        resourceParams.put("retries", "0");
        Resource resource = new Resource(hostName, null, resourceParams);
        discovererFactory = new DefaultDiscovererFactory();
        SnmpWalker discoverer = (SnmpWalker) discovererFactory.createDiscoverer(resource);
        final String result = discoverer.snmpWalkDevice(resource, params);
        return result;

    }

    public static Map<String, HashMap<String, String>> getDiscoveredDevices() {
        return discoveredDevices;
    }

    public static void setDiscoveredDevices(Map<String, HashMap<String, String>> discoveredDevices) {
        SnmpForXslt.discoveredDevices = discoveredDevices;
    }
    public static void resolveIPAddresses(DiscoveryResourceManager resourceManager, String connectionType){
        for (String ipAddress : discoveredDevices.keySet()) {

            HashMap<String, String> deviceNameMap = discoveredDevices.get(ipAddress);


            if (!deviceNameMap.containsKey("snmp")) {

                deviceNameMap = new HashMap<String, String>();

            }else {
                //DeviceMap already filled in.
                continue;
            }
            if(!ipAddressValidator.isValidInet4Address(ipAddress))
                continue;

            Map<String,String> resourceParameters = new HashMap<String, String>();
            resourceParameters.put("ipAddress", ipAddress);

            ResourceType resourceType = resourceManager.returnResourceByParam(resourceParameters);


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

            Resource resource = new Resource(ipAddress, null, connParams);// TODO specify port
            discovererFactory = new DefaultDiscovererFactory();

            Discoverer discoverer = null;
            try {
                discoverer = discovererFactory.createDiscoverer(resource);
            } catch (Exception e) {
                e.printStackTrace();
            }

            final String deviceNameFromSNMP = discoverer.getDeviceName(resource);

            final String deviceTypeFromSNMP = discoverer.getDeviceType(resource);

            deviceNameMap.put("snmp", deviceNameFromSNMP);

            deviceNameMap.put("deviceType", deviceTypeFromSNMP);

            discoveredDevices.put(ipAddress, deviceNameMap);


        }

    }
}
