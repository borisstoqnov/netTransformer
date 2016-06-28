

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

import net.itransformers.idiscover.core.Subnet;
import net.itransformers.idiscover.core.*;
import net.itransformers.idiscover.discoveryhelpers.xml.discoveryParameters.*;
import net.itransformers.idiscover.networkmodel.DiscoveredDeviceData;
import net.itransformers.idiscover.networkmodel.ObjectType;
import net.itransformers.idiscover.networkmodel.ParameterType;
import net.itransformers.idiscover.networkmodel.ParametersType;
import net.itransformers.idiscover.util.JaxbMarshalar;
import net.itransformers.utils.XsltTransformer;
import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class XmlDiscoveryHelper implements DiscoveryHelper {
    static Logger logger = Logger.getLogger(XmlDiscoveryHelper.class);

    private DeviceType deviceType;
    private Map<String, DiscoveryMethodType> discoveryMethodTypeMap = new HashMap<String, DiscoveryMethodType>();
    private StopCriteriaType stopCriteria;




    public XmlDiscoveryHelper(DeviceType deviceType, StopCriteriaType stopCriteria) {
        this.deviceType = deviceType;
        this.stopCriteria = stopCriteria;
        List<DiscoveryMethodType> list = this.deviceType.getDiscoveryMethod();
        for (DiscoveryMethodType discoveryMethod : list){
            discoveryMethodTypeMap.put(discoveryMethod.getName(),discoveryMethod);
        }
    }



    public DiscoveredDeviceData parseDeviceRawData(RawDeviceData rawData, String[] discoveryTypes, Map<String, String> params) {

        byte[] data = rawData.getData();
        ByteArrayOutputStream deviceXmlOutputStream = new ByteArrayOutputStream();
        parseDeviceRawData(data, deviceXmlOutputStream, deviceType.getXslt(), params);

        ByteArrayInputStream is = new ByteArrayInputStream(deviceXmlOutputStream.toByteArray());
        System.out.println(deviceXmlOutputStream.toByteArray());
        try {
            DiscoveredDeviceData discoveredDeviceData = JaxbMarshalar.unmarshal(DiscoveredDeviceData.class, is);
            return discoveredDeviceData;
        } catch (JAXBException e) {
            System.err.println("Error unmarshal the xml:" + e.toString());
            System.err.println(new String(deviceXmlOutputStream.toByteArray()));
            return null;
        }
    }

    public Device createDevice(DiscoveredDeviceData discoveredDeviceData) {
        Device device = new Device(discoveredDeviceData.getName());
        List<DeviceNeighbour> deviceNeighbours = createDeviceNeighbours(discoveredDeviceData);
        List<Subnet> deviceSubnets = createDeviceSubnets(discoveredDeviceData);
        List<MacAddress> deviceMacAddresses = createDeviceMacAddreses(discoveredDeviceData);
        device.setDeviceNeighbours(deviceNeighbours);
        device.setDeviceSubnets(deviceSubnets);
        device.setDeviceMacAddresses(deviceMacAddresses);
        return device;
    }

    private List<MacAddress> createDeviceMacAddreses(DiscoveredDeviceData discoveredDeviceData) {
        List<MacAddress> result = new ArrayList<MacAddress>();
        List<ObjectType> objList1 = discoveredDeviceData.getObject();

        for (ObjectType objectType1 : objList1) {
            String objectType = objectType1.getObjectType();

            if (objectType.equals("Discovery Interface")) {
                String interfaceName = objectType1.getName();

                ParametersType params = objectType1.getParameters();
                HashMap<String, String> params2Map = new HashMap<String, String>();
                for (ParameterType params2Param : params.getParameter()) {
                    params2Map.put(params2Param.getName(), params2Param.getValue());
                }

                String macAddress = params2Map.get("ifPhysAddress");

                if (macAddress!=null && !macAddress.isEmpty()){
                    result.add(new MacAddress(macAddress,interfaceName));
                }
            }
        }

        return result;

    }

    private List<Subnet> createDeviceSubnets(DiscoveredDeviceData discoveredDeviceData) {

        List<ObjectType> objList1 = discoveredDeviceData.getObject();

        List<Subnet> result = new ArrayList<Subnet>();
        for (ObjectType objectType1 : objList1) {
            String objectType = objectType1.getObjectType();

            if (objectType.equals("Discovery Interface")) {
                String interfaceName = objectType1.getName();

                List<ObjectType> objList2 = objectType1.getObject();
                for (ObjectType objectType2 : objList2) {
                    String objectTypeType2 = objectType2.getObjectType();
                    if (objectTypeType2.equals("IPv4 Address")) {
                        ParametersType params2 = objectType2.getParameters();
                        HashMap<String, String> params2Map = new HashMap<String, String>();
                        for (ParameterType params2Param : params2.getParameter()) {
                            params2Map.put(params2Param.getName(), params2Param.getValue());
                        }
                        String subnetType = "IPv4";
                        String ipSubnetMask = params2Map.get("ipv4SubnetPrefix");
                        String ipv4Subnet = params2Map.get("ipv4Subnet");
                        Subnet subnet = new Subnet(ipv4Subnet + "/" + ipSubnetMask, ipv4Subnet, ipSubnetMask, interfaceName, subnetType);
                        result.add(subnet);
                        continue;
                    }
                    if (objectTypeType2.equals("IPv6 Address")){
                        ParametersType params2 = objectType2.getParameters();
                        HashMap<String, String> params2Map = new HashMap<String, String>();
                        for (ParameterType params2Param : params2.getParameter()) {
                            params2Map.put(params2Param.getName(), params2Param.getValue());
                        }

                        String subnetType = "IPv6";
                        String ipSubnetMask = params2Map.get("ipv6AddrPfxLength");
                        String ipv6Subnet = params2Map.get("ipv6Subnet");
                        Subnet subnet = new Subnet(ipv6Subnet + "/" + ipSubnetMask, ipv6Subnet, ipSubnetMask, interfaceName, subnetType);
                        result.add(subnet);
                        continue;


                    }

                }
            }

        }
        return result;
    }

    private List<DeviceNeighbour> createDeviceNeighbours(DiscoveredDeviceData discoveredDeviceData) {
        //DiscoveredDevice/object/object[objectType='Discovered Neighbor']/name
        List<ObjectType> objList1 = discoveredDeviceData.getObject();
        List<DeviceNeighbour> result = new ArrayList<DeviceNeighbour>();

        //Loop over all objects. There are a number of interfaces and finally a number
        for (ObjectType objectType1: objList1) {

            //Then loop over the objects of the first set of objects!!!

            List<ObjectType> objList2 = objectType1.getObject();

//            if (objectType1.getObjectType().equals("DeviceLogicalData")) {
                for (ObjectType objectType2 : objList2) {

                    if (objectType2.getObjectType().equals("Discovered Neighbor")) {
                        ParametersType params2 = objectType2.getParameters();
                        HashMap<String, String> params2Map = new HashMap<String, String>();
                        for (ParameterType params2Param : params2.getParameter()) {
                            params2Map.put(params2Param.getName(), params2Param.getValue());
                        }
                        String ipAddress = params2Map.get("Neighbor IP Address");
                        DeviceNeighbour deviceNeighbour;
                        deviceNeighbour = new DeviceNeighbour(ipAddress, params2Map);
                        result.add(deviceNeighbour);
                    }
                }


//            }
//
//            for (ObjectType objectType2 : objList2) {
//                if (objectType2.getObjectType().equals("Discovered Neighbor")) {
//                    ParametersType params2 = objectType2.getParameters();
//                    HashMap<String, String> params2Map = new HashMap<String, String>();
//                    for (ParameterType params2Param : params2.getParameter()){
//                        params2Map.put(params2Param.getName(), params2Param.getValue());
//                    }
//                    String ipAddress = params2Map.get("Neighbor IP Address");
//                    DeviceNeighbour deviceNeighbour;
//
//                    deviceNeighbour = new DeviceNeighbour(ipAddress, params2Map);
//                    result.add(deviceNeighbour);
//                }
//
//            }
        }
        return result;
    }

    private void parseDeviceRawData(byte[] rawData, OutputStream outputStream, String xsltFileName) {
        XsltTransformer transformer = new XsltTransformer();


        try {
            transformer.transformXML(new ByteArrayInputStream(rawData), new File(System.getProperty("base.dir"), xsltFileName), outputStream);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            logger.error("Unable to parse device raw data xml:"+new String(rawData));
        }
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



    public String[] getRequestParams(String[] discoveryTypes) {
        List<String> result = new ArrayList<String>();
        for (String discoveryType : discoveryTypes){
            DiscoveryMethodType discoveryMethod = this.discoveryMethodTypeMap.get(discoveryType);
            if (discoveryMethod != null) {
                result.addAll(Arrays.asList(discoveryMethod.getValue().trim().split(",\\s*")));
            }
        }
        this.deviceType.getGeneral();
        result.addAll(Arrays.asList(this.deviceType.getGeneral().trim().split(",\\s*")));
        return result.toArray(new String[result.size()]);
    }
    public static Object getProperty(Object o, String propertyName)
    {
        try {
          Object myValue = PropertyUtils.getNestedProperty(o, propertyName);
           return myValue;
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        } catch (InvocationTargetException e) {
          e.printStackTrace();
        } catch (NoSuchMethodException e) {
          e.printStackTrace();
        } catch (NestedNullException nne){
          return null;
        }
        return null;
    }
}
