/*
 * XmlDiscoveryHelper.java
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

import net.itransformers.idiscover.core.RawDeviceData;
import net.itransformers.idiscover.discoveryhelpers.xml.discoveryParameters.DeviceType;
import net.itransformers.idiscover.discoveryhelpers.xml.discoveryParameters.DiscoveryMethodType;
import net.itransformers.idiscover.networkmodel.DiscoveredDeviceData;
import net.itransformers.idiscover.networkmodel.ObjectType;
import net.itransformers.idiscover.networkmodel.ParameterType;
import net.itransformers.idiscover.networkmodel.ParametersType;
import net.itransformers.idiscover.networkmodelv2.*;
import net.itransformers.idiscover.util.JaxbMarshalar;
import net.itransformers.utils.XsltTransformer;
import net.itransformers.utils.hash.HashGenerator;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.*;

public class XmlDiscoveryHelperV2 {
    static Logger logger = Logger.getLogger(XmlDiscoveryHelperV2.class);

    private DeviceType deviceType;
    private Map<String, DiscoveryMethodType> discoveryMethodTypeMap = new HashMap<String, DiscoveryMethodType>();


    public XmlDiscoveryHelperV2(DeviceType deviceType) {
        this.deviceType = deviceType;
        List<DiscoveryMethodType> list = this.deviceType.getDiscoveryMethod();
        for (DiscoveryMethodType discoveryMethod : list) {
            discoveryMethodTypeMap.put(discoveryMethod.getName(), discoveryMethod);
        }
    }


    public DiscoveredDeviceData parseDeviceRawData(RawDeviceData rawData, String[] discoveryTypes, Map<String, String> params) {

        byte[] data = rawData.getData();
        ByteArrayOutputStream deviceXmlOutputStream = new ByteArrayOutputStream();
        parseDeviceRawData(data, deviceXmlOutputStream, deviceType.getXslt(), params);

        ByteArrayInputStream is = new ByteArrayInputStream(deviceXmlOutputStream.toByteArray());
        try {
            return JaxbMarshalar.unmarshal(DiscoveredDeviceData.class, is);
        } catch (JAXBException e) {
            System.err.println("Error unmarshal the xml:" + e.toString());
            System.err.println(new String(deviceXmlOutputStream.toByteArray()));
            return null;
        }
    }

    public DiscoveredDevice createDevice(DiscoveredDeviceData discoveredDeviceData,String deviceName) {
         String id = null;

        try {
            if (deviceName!=null && !deviceName.isEmpty())
                id = HashGenerator.generateMd5(deviceName);
            else
                logger.error("Trying to generate an id for an empty device!!!");

        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return null;
        }
        DiscoveredDevice device = new DiscoveredDevice(deviceName, id);
        List<DiscoveredInterface> discoveredInterfaces = new ArrayList<DiscoveredInterface>();
        LogicalDeviceData logicalDeviceData = null;
        HashMap<String,String> params = convertParams(discoveredDeviceData.getParameters());
        for (ObjectType objectType : discoveredDeviceData.getObject()) {
            String objectTypeType = objectType.getObjectType();
            if (objectTypeType.equals("Discovery Interface")) {
                discoveredInterfaces.add(createDeviceInterfaces(objectType));
            } else if (objectTypeType.equals("DeviceLogicalData")) {
                logicalDeviceData = createLogicalData(objectType);
            } else {
                logger.info("Unrecognized Object type: " + objectTypeType);
            }
        }
        device.setParams(params);

        device.setInterfaceList(discoveredInterfaces);
        device.setLogicalDeviceData(logicalDeviceData);
        return device;
    }

    private LogicalDeviceData createLogicalData(ObjectType objectType) {
        List<DeviceNeighbour> neighbours = new ArrayList<>();
        List<ObjectType> objList2 = objectType.getObject();

        for (ObjectType type : objList2) {
            if (type.getObjectType().equals("Discovered Neighbor")) {
                neighbours.add(createNeighbour(objectType));
            } else {
                logger.info("Unrecognized logicalData object type " + type.getObjectType());
            }
        }


        return new LogicalDeviceData(neighbours);
    }

    private DiscoveredInterface createDeviceInterfaces(ObjectType objectType) {

        String interfaceName = objectType.getName();
        ParametersType params = objectType.getParameters();
        HashMap<String, String> params2Map = convertParams(params);
        DiscoveredInterface discoveredInterface = new DiscoveredInterface(interfaceName, params2Map);
        String ifType=params2Map.get("ifType");
        discoveredInterface.setType(ifType);

        List<ObjectType> objList2 = objectType.getObject();
        List<DeviceNeighbour> interfaceNeighbours = new ArrayList<>();
        List<DiscoveredIPv4Address> interfaceIPv4Addresses = new ArrayList<>();
        List<DiscoveredIPv6Address> interfaceIPv6Addresses = new ArrayList<>();

        for (ObjectType objectType2 : objList2) {
            String objectType2Type = objectType2.getObjectType();

            if (objectType2Type.equals("Discovered Neighbor")) {
                interfaceNeighbours.add(createNeighbour(objectType2));
            } else if (objectType2Type.equals("IPv4 Address")) {
                interfaceIPv4Addresses.add(createDiscoveredIPv4Address(objectType2));
            } else if (objectType2Type.equals("IPv6 Address")) {
                interfaceIPv6Addresses.add(createDiscoveredIPv6Address(objectType2));
            }
        }
        discoveredInterface.setNeighbours(interfaceNeighbours);
        discoveredInterface.setiPv4AddressList(interfaceIPv4Addresses);
        discoveredInterface.setIpv6AddressList(interfaceIPv6Addresses);


        return discoveredInterface;

    }

    private DiscoveredIPv6Address createDiscoveredIPv6Address(ObjectType objectType) {
        ParametersType params2 = objectType.getParameters();
        HashMap<String, String> params = convertParams(params2);
        return new DiscoveredIPv6Address(objectType.getName(), params);

    }


    private HashMap<String, String> convertParams(ParametersType params) {
        HashMap<String, String> params2Map = new HashMap<String, String>();

        for (ParameterType params2Param : params.getParameter()) {
            params2Map.put(params2Param.getName(), params2Param.getValue());
        }

        return params2Map;
    }


    private DeviceNeighbour createNeighbour(ObjectType objectType) {
        ParametersType params2 = objectType.getParameters();
        HashMap<String, String> params2Map = convertParams(params2);
        String ipAddress = params2Map.get("Neighbor IP Address");
        String hostName = params2Map.get("Neighbour HostName");
        String physicalAddress = params2Map.get("Neighbor MAC Address");

        return new DeviceNeighbour(hostName, ipAddress, params2Map);

    }

    private DiscoveredIPv4Address createDiscoveredIPv4Address(ObjectType objectType) {
        ParametersType params2 = objectType.getParameters();
        HashMap<String, String> params2Map = convertParams(params2);
        DiscoveredIPv4Address discoveredIPv4Address = new DiscoveredIPv4Address(objectType.getName(), params2Map);
        return discoveredIPv4Address;
    }

    private void parseDeviceRawData(byte[] rawData, OutputStream outputStream, String xsltFileName, Map<String, String> params) {
        XsltTransformer transformer = new XsltTransformer();
        try {
            transformer.transformXML(new ByteArrayInputStream(rawData), new File(System.getProperty("base.dir"), xsltFileName), outputStream, params);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            logger.error("Unable to parse device raw data xml:" + new String(rawData));
        }
    }


}
