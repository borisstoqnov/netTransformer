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

import net.itransformers.idiscover.core.DiscoveryHelper;
import net.itransformers.idiscover.core.DiscoveryResourceManager;
import net.itransformers.idiscover.core.RawDeviceData;
import net.itransformers.idiscover.core.Subnet;
import net.itransformers.idiscover.discoveryhelpers.xml.XmlDiscoveryHelperFactory;
import net.itransformers.idiscover.discoveryhelpers.xml.XmlDiscoveryHelperV2;
import net.itransformers.idiscover.networkmodel.DiscoveredDeviceData;
import net.itransformers.idiscover.networkmodelv2.DeviceNeighbour;
import net.itransformers.idiscover.networkmodelv2.DiscoveredDevice;
import net.itransformers.idiscover.util.DeviceTypeResolver;
import net.itransformers.idiscover.v2.core.NodeDiscoverer;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.snmp2xml4j.snmptoolkit.MibLoaderHolder;
import net.itransformers.snmp2xml4j.snmptoolkit.SnmpManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;

public class SnmpParallelNodeDiscoverer extends SnmpNodeDiscoverer implements NodeDiscoverer {
    static Logger logger = Logger.getLogger(SnmpParallelNodeDiscoverer.class);


    public SnmpParallelNodeDiscoverer(XmlDiscoveryHelperFactory discoveryHelperFactory, String[] discoveryTypes, DiscoveryResourceManager discoveryResource, MibLoaderHolder mibLoaderHolder,boolean useOnlyTheFirstSnmpBeingMatched) throws Exception {
        super(discoveryHelperFactory, discoveryTypes, discoveryResource, mibLoaderHolder, useOnlyTheFirstSnmpBeingMatched);
    }


    @Override
    public NodeDiscoveryResult discover(ConnectionDetails connectionDetails) {
        String deviceId = null;

        String deviceName = connectionDetails.getParam("deviceName");
        String ipAddressStr = connectionDetails.getParam("ipAddress");
        String deviceType = connectionDetails.getParam("deviceType");
        Map<String, String> resourceSelectionParams = getConnectionDetailsParams(deviceName, deviceType, ipAddressStr);
        //String dnsFQDN = doReverseDnsLookup(ipAddressStr);

        HashMap<String,String> resultParams = new HashMap<>();

        if (deviceName != null) {
            if (!deviceName.isEmpty()) {
                resultParams.put("deviceName", deviceName);
                deviceId = deviceName;
            }

        }
        if (deviceType !=null ){
            if (!deviceType.isEmpty())
                resultParams.put("deviceType",deviceType);
        }
        if (ipAddressStr!=null){
            if (!ipAddressStr.isEmpty())
                    resultParams.put("ipAddress", ipAddressStr);
        }

        NodeDiscoveryResult result = new NodeDiscoveryResult();

        SnmpManager snmpManager = getSnmpManager(resourceSelectionParams, ipAddressStr);

        if (snmpManager!=null) {
            String hostNameBySnmp = null;
            String deviceTypeBySnmp = "UNKNOWN";

            try {
                String deviceSysDescr = snmpManager.snmpGet("1.3.6.1.2.1.1.1.0");
                hostNameBySnmp = subStringDeviceName(snmpManager.snmpGet("1.3.6.1.2.1.1.5.0"));
                deviceTypeBySnmp = DeviceTypeResolver.getDeviceType(deviceSysDescr);

                resultParams.put("hostName",hostNameBySnmp);
                resultParams.put("deviceType",deviceTypeBySnmp);
                resultParams.put("sysDescr",deviceSysDescr);
            } catch (IOException ioe) {
                logger.error(ioe.getMessage());

            }

            if (deviceId==null)
                deviceId = hostNameBySnmp;

            DiscoveryHelper discoveryHelper = discoveryHelperFactory.createDiscoveryHelper(deviceType);

            XmlDiscoveryHelperV2 discoveryHelperV2 = createXmlDiscoveryHelper(deviceType);


            RawDeviceData rawData = getRawData(snmpManager, discoveryHelper);
            DiscoveredDeviceData discoveredDeviceData = getDeviceData(discoveryHelper, rawData);
            DiscoveredDevice discoveredDevice = discoveryHelperV2.createDevice(discoveredDeviceData,deviceName);

            HashMap<String,String> deviceParams = discoveredDevice.getParams();
            deviceParams.putAll(resultParams);
            discoveredDevice.setParams(deviceParams);

            List<DeviceNeighbour> neighbours = discoveredDevice.getDeviceNeighbours();
            List<Subnet> subnets = discoveredDevice.getDeviceSubnetsFromActiveInterfaces();
            Set<ConnectionDetails> neighboursConnDetails = new HashSet<ConnectionDetails>();

            if (neighbours != null) {
                NeighbourConnectionDetails neighbourConnectionDetails = new NeighbourConnectionDetails(neighbours);
                neighboursConnDetails = neighbourConnectionDetails.getConnectionDetailses();
            }

            if (subnets != null) {
                SubnetConnectionDetails subnetConnectionDetails1 = new SubnetConnectionDetails(subnets);
                neighboursConnDetails.addAll(subnetConnectionDetails1.getSubnetConnectionDetails());
            }
            result.setNeighboursConnectionDetails(neighboursConnDetails);
            result.setDiscoveredData("deviceData", discoveredDeviceData);
            result.setDiscoveredData("DiscoveredDevice", discoveredDevice);
            result.setDiscoveredData("rawData", rawData.getData());
        }
        result.setNodeId(deviceId);
        return result;
    }



}
