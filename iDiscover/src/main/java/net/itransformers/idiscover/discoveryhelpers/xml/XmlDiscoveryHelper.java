

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
    private boolean dryRun;




    public XmlDiscoveryHelper(DeviceType deviceType, StopCriteriaType stopCriteria) {
        this.deviceType = deviceType;
        this.stopCriteria = stopCriteria;
        List<DiscoveryMethodType> list = this.deviceType.getDiscoveryMethod();
        for (DiscoveryMethodType discoveryMethod : list){
            discoveryMethodTypeMap.put(discoveryMethod.getName(),discoveryMethod);
        }
    }

    @Override
    public void setDryRun(boolean dryRun) {
        this.dryRun = dryRun;
    }



    public DiscoveredDeviceData parseDeviceRawData(RawDeviceData rawData, String[] discoveryTypes, Resource resource) {

        byte[] data = rawData.getData();
        ByteArrayOutputStream deviceXmlOutputStream = new ByteArrayOutputStream();
        parseDeviceRawData(data, deviceXmlOutputStream, deviceType.getXslt(), resource);

        ByteArrayInputStream is = new ByteArrayInputStream(deviceXmlOutputStream.toByteArray());
        System.out.println(deviceXmlOutputStream.toByteArray());
        try {
            DiscoveredDeviceData discoveredDeviceData = JaxbMarshalar.unmarshal(DiscoveredDeviceData.class, is);
            return discoveredDeviceData;
        } catch (JAXBException e) {
            System.err.println("Error unmarshal the xml:");
            System.err.println(new String(deviceXmlOutputStream.toByteArray()));
            e.printStackTrace();
            return null;
        }
    }

    public Device createDevice(DiscoveredDeviceData discoveredDeviceData) {
        Device device = new Device(discoveredDeviceData.getName());
        List<DeviceNeighbour> deviceNeighbours = createDeviceNeighbours(discoveredDeviceData);
        device.setDeviceNeighbours(deviceNeighbours);
        return device;
    }

    private List<DeviceNeighbour> createDeviceNeighbours(DiscoveredDeviceData discoveredDeviceData) {
        //DiscoveredDevice/object/object[objectType='Discovered Neighbor']/name
        List<ObjectType> objList1 = discoveredDeviceData.getObject();
        List<DeviceNeighbour> result = new ArrayList<DeviceNeighbour>();
        for (ObjectType objectType1: objList1) {
            List<ObjectType> objList2 = objectType1.getObject();

            if (objectType1.getObjectType().equals("DeviceLogicalData")) {
                        objectType1.getName();


            }

//            TODO Check for Device Logical Data and for the neighbors bellow it

            for (ObjectType objectType2 : objList2) {
                if (objectType2.getObjectType().equals("Discovered Neighbor")) {
                    ParametersType params2 = objectType2.getParameters();
                    HashMap<String, String> params2Map = new HashMap<String, String>();
                    for (ParameterType params2Param : params2.getParameter()){
                        params2Map.put(params2Param.getName(), params2Param.getValue());
                    }
                    String Reachable = params2Map.get("Reachable");
                    String ipAddress = params2Map.get("Neighbor IP Address");
                    String hostName = params2Map.get("Neighbor hostname");
                    String deviceType = params2Map.get("Neighbor Device Type");
                    String snmpCommunity = params2Map.get("SNMP Community");
                    DeviceNeighbour deviceNeighbour;
                    if (Reachable.equals("YES")){
                       deviceNeighbour = new DeviceNeighbour(hostName, new IPv4Address(ipAddress,null),deviceType,snmpCommunity,true);
                    }else{
                       if (hostName != null){
                        deviceNeighbour = new DeviceNeighbour(hostName,deviceType,snmpCommunity,false);
                        }else if (ipAddress != null){
                        deviceNeighbour = new DeviceNeighbour(ipAddress,deviceType,snmpCommunity,false);
                        } else {
                            deviceNeighbour = new DeviceNeighbour(objectType2.getName(),deviceType,snmpCommunity,false);
                        }
                    }
                    result.add(deviceNeighbour);
                }

            }
        }
        return result;
    }

    private void parseDeviceRawData(byte[] rawData, OutputStream outputStream, String xsltFileName, Resource resource) {
        XsltTransformer transformer = new XsltTransformer();

        if(dryRun)
            resource.getAttributes().put("neighbourIPDryRun","true");
        else
            resource.getAttributes().put("neighbourIPDryRun",null);

        try {
            if (resource.getIpAddress()!=null){
                transformer.transformXML(new ByteArrayInputStream(rawData), new File(System.getProperty("base.dir"),xsltFileName), outputStream, resource.getAttributes(),resource.getIpAddress().getIpAddress());
            }  else {
                transformer.transformXML(new ByteArrayInputStream(rawData), new File(System.getProperty("base.dir"),xsltFileName), outputStream, resource.getAttributes(),null);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            logger.error("Unable to parse device raw data xml:"+new String(rawData));
        }
    }
    public boolean checkStopCriteria(Resource host) {
        List<MatchNotType> notMatches = this.stopCriteria.getMatchNot();
        for (MatchNotType notMatch : notMatches){
            String propertyName = notMatch.getProperty();
            String propVal = (String) getProperty(host, propertyName);
            logger.debug("PropName="+propertyName+",propVal="+propVal+",notMatch.value="+notMatch.getValue());
            if (propVal != null && propVal.matches(notMatch.getValue())) {
                return false;
            }
        }
        List<MatchType> matches = this.stopCriteria.getMatch();
        for (MatchType match : matches){
            String propertyName = match.getProperty();
            String propVal = (String) getProperty(host, propertyName);
            logger.debug("PropName="+propertyName+",propVal="+propVal+",Match.value="+match.getValue());
            if (propVal != null && propVal.matches(match.getValue())) {
                return true;
            }
        }
        return false;
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
