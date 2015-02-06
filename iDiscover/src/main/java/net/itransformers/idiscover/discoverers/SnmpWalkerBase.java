

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

package net.itransformers.idiscover.discoverers;

import net.itransformers.idiscover.core.Discoverer;
import net.itransformers.idiscover.core.RawDeviceData;
import net.itransformers.idiscover.core.Resource;
import net.itransformers.snmptoolkit.*;
import net.itransformers.snmptoolkit.messagedispacher.MessageDispatcherAbstractFactory;
import net.itransformers.snmptoolkit.transport.TransportMappingAbstractFactory;
//import net.percederberg.mibble.MibLoaderException;
import org.apache.log4j.Logger;
import org.snmp4j.log.Log4jLogFactory;
import org.snmp4j.log.LogFactory;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.util.SnmpConfigurator;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

public class SnmpWalkerBase implements Discoverer {
    static Logger logger = Logger.getLogger(SnmpWalkerBase.class);
    private static final String MIB_DIR = "mibDir";

    private Walk walker;
    private MibLoaderHolder holder;
    private TransportMappingAbstractFactory transportFactory;
    private MessageDispatcherAbstractFactory messageDispatcherFactory;

    public SnmpWalkerBase(Resource resource,
                          TransportMappingAbstractFactory transportFactory,
                          MessageDispatcherAbstractFactory messageDispatcherFactory
    ) throws Exception {
        String mibDir = resource.getAttributes().get(MIB_DIR);
        if (mibDir==null){
            mibDir="snmptoolkit/mibs";
        }
        holder = new MibLoaderHolder(new File(System.getProperty("base.dir"),mibDir), false);
        walker = new Walk(holder, transportFactory, messageDispatcherFactory);
        this.transportFactory = transportFactory;
        this.messageDispatcherFactory = messageDispatcherFactory;
    }

    public String getDeviceType(Resource resource) {
        String deviceSysDescrOID = "1.3.6.1.2.1.1.1";
        String sysDescr = null;
        try {
             sysDescr = snmpGetNext(resource, deviceSysDescrOID);
        } catch (Exception e) {
            logger.error("Error getDevice Type (" + resource.getAddress() + "), err msg=" + e.getMessage());
            return null;
        }
        if (sysDescr == null) return null;
        if (sysDescr.contains("ProCurve")){
          return "HP";
        } else if (sysDescr.contains("Huawei")){
          return "HUAWEI";
        } else if (sysDescr.contains("Juniper")){
          return "JUNIPER";
        } else if (sysDescr.contains("Cisco")){
          return "CISCO";
        }else if (sysDescr.contains("Tellabs")){
          return "TELLABS";
        }
        else if (sysDescr.contains("SevOne")){
            return "SevOne";
        }
        else if (sysDescr.contains("Riverstone")){
          return "RIVERSTONE";
        }
        else {
          return  "DEFAULT";
        }
    }

    public String getDeviceName(Resource resource) {
        String deviceNameOID = "1.3.6.1.2.1.1.5";
        try {
            return snmpGetNext(resource, deviceNameOID);
        } catch (Exception e) {
            logger.error("Error getDevice Name (" + resource.getAddress() + "), err msg=" + e.getMessage());
            return null;
        }
    }

    public String getByOid(Resource resource, String oid) {
        try {
            return snmpGetNext(resource, oid);
        } catch (Exception e) {
            logger.error("Error get oid( " +oid.toString()+ " ) ( "+ resource.getAddress() + "), err msg=" + e.getMessage(), e);
            return null;
        }
    }
     public String setByOid(Resource resource, String oid, String value) {
        try {
            return snmpSet(resource, oid, value);
        } catch (Exception e) {
            logger.error("Error set by Oid (" + resource.getAddress() + "), err msg=" + e.getMessage(), e);
            return null;
        }
    }
    public String getSymbolByOid(String mibName, String oid) {
        return holder.getSymbolByOid(mibName, oid);
    }

    public RawDeviceData getRawDeviceData(Resource resource, String[] requestParamsList) {
        try {
            return snmpWalk(resource, requestParamsList);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return null;
//        } catch (MibLoaderException e) {
//            logger.error(e.getMessage(),e);
//            return null;
        }
    }

    public String snmpWalkDevice(Resource resource, String[] requestParamsList) {
        try {
            return snmpWalktoString(resource, requestParamsList);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            return null;
//        } catch (MibLoaderException e) {
//            logger.error(e.getMessage(),e);
//            return null;
        }
    }

    private String snmpGet(Resource resource, String oid) {
        String version =resource.getAttributes().get("version");
        int versionInt;
        if (version.equals("1")){
            versionInt = SnmpConstants.version1;
        } else if (version.equals("2c")){
            versionInt = SnmpConstants.version2c;
        }  else if(version.equals("3")){
            versionInt=  SnmpConstants.version3;
        } else {
            versionInt = SnmpConstants.version2c;
        }
     //    version = resource.getAttributes().get("version") == null ? SnmpConstants.version2c : Integer.parseInt(resource.getAttributes().get("version"));
        int retriesInt = resource.getAttributes().get("retries") == null ? 1 : Integer.parseInt(resource.getAttributes().get("retries"));
        int timeoutInt = resource.getAttributes().get("timeout") == null ? 1500 : Integer.parseInt(resource.getAttributes().get("timeout"));
        String community = resource.getAttributes().get("community-ro");
        String community2 = resource.getAttributes().get("community-rw");
        if (community == null) throw new IllegalArgumentException("Community is not specifed");
        if (resource.getAddress() == null) throw new RuntimeException("Resource address is null");
        Get get = null;
        try {
            get = new Get(oid, resource.getAddress(), versionInt, retriesInt, timeoutInt, community, transportFactory, messageDispatcherFactory);
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
            return null;
        }
        String value;
        try {
            value = get.getSNMPValue();
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
            return null;
        }
        return value;
    }

    private String snmpSet(Resource resource, String oid, String value) {

        String version =resource.getAttributes().get("version");
        int versionInt;
        if (version.equals("1")){
            versionInt = SnmpConstants.version1;
        } else if (version.equals("2c")){
            versionInt = SnmpConstants.version2c;
        }  else if(version.equals("3")){
            versionInt=  SnmpConstants.version3;
        } else {
            versionInt = SnmpConstants.version2c;
        }

        int retriesInt = resource.getAttributes().get("retries") == null ? 3 : Integer.parseInt(resource.getAttributes().get("retries"));
        int timeoutInt = resource.getAttributes().get("timeout") == null ? 1200 : Integer.parseInt(resource.getAttributes().get("timeout"));
        String community = resource.getAttributes().get("community-ro");
        String community2 = resource.getAttributes().get("community-rw");
        if (community == null) throw new IllegalArgumentException("Community is not specifed");
        if (resource.getAddress() == null) throw new RuntimeException("Resource address is null");
        SnmpSet set = null;
        try {
            set = new SnmpSet(oid, resource.getAddress(), versionInt, retriesInt, timeoutInt, community, value, transportFactory, messageDispatcherFactory);
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
            return null;
        }
        String result;
        try {
            result = set.setSNMPValue();
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
            return null;
        }
        return result;
    }
    private RawDeviceData snmpWalk(Resource resource, String[] params) throws Exception {//}, MibLoaderException {
        Properties parameters = new Properties();
        if (resource.getAddress() == null) throw new RuntimeException("Resource Address is null");
        parameters.put(SnmpConfigurator.O_ADDRESS, Arrays.asList(resource.getAddress()));
//        parameters.put(SnmpConfigurator.O_PORT, Arrays.asList(resource.getAddress()));
        parameters.put(SnmpConfigurator.O_COMMUNITY, Arrays.asList(resource.getAttributes().get("community-ro")));
        //parameters.put(SnmpConfigurator.O_VERSION, Arrays.asList("2c"));// TODO

        String version = resource.getAttributes().get("version") == null ? "2c" : resource.getAttributes().get("version");
        int retriesInt = resource.getAttributes().get("retries") == null ? 3 : Integer.parseInt(resource.getAttributes().get("retries"));
        int timeoutInt = resource.getAttributes().get("timeout") == null ? 1200 : Integer.parseInt(resource.getAttributes().get("timeout"));
        int maxrepetitions = resource.getAttributes().get("max-repetitions") == null ? 65535 : Integer.parseInt(resource.getAttributes().get("timeout"));

        parameters.put(SnmpConfigurator.O_VERSION, Arrays.asList(version));
//        parameters.put(SnmpConfigurator.O_TIMEOUT, Arrays.asList(1200));
//        parameters.put(SnmpConfigurator.O_RETRIES, Arrays.asList(2));
//        parameters.put(SnmpConfigurator.O_MAX_REPETITIONS, Arrays.asList(65535));
        parameters.put(SnmpConfigurator.O_TIMEOUT, Arrays.asList(timeoutInt));
        parameters.put(SnmpConfigurator.O_RETRIES, Arrays.asList(retriesInt));
        parameters.put(SnmpConfigurator.O_MAX_REPETITIONS, Arrays.asList(maxrepetitions));

        Node root = walker.walk(params, parameters);
        String xml = Walk.printTreeAsXML(root);
        return new RawDeviceData(xml.getBytes());
    }
    static {
        LogFactory.setLogFactory(new Log4jLogFactory());
    }

    private String snmpGetNext(Resource resource, String oid) {
        String version =resource.getAttributes().get("version");
        int versionInt;
        if (version.equals("1")){
            versionInt = SnmpConstants.version1;
        } else if (version.equals("2c")){
            versionInt = SnmpConstants.version2c;
        }  else if(version.equals("3")){
            versionInt=  SnmpConstants.version3;
        } else {
            versionInt = SnmpConstants.version2c;
        }
        //    version = resource.getAttributes().get("version") == null ? SnmpConstants.version2c : Integer.parseInt(resource.getAttributes().get("version"));
        int retriesInt = resource.getAttributes().get("retries") == null ? 1 : Integer.parseInt(resource.getAttributes().get("retries"));
        int timeoutInt = resource.getAttributes().get("timeout") == null ? 1500 : Integer.parseInt(resource.getAttributes().get("timeout"));
        String community = resource.getAttributes().get("community-ro");
        String community2 = resource.getAttributes().get("community-rw");
        if (community == null) throw new IllegalArgumentException("Community is not specifed");
        if (resource.getAddress() == null) throw new RuntimeException("Resource address is null");
        Get get = null;
        try {
            get = new Get(oid, resource.getAddress(), versionInt, retriesInt, timeoutInt, community, transportFactory, messageDispatcherFactory);
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
            return null;
        }
        String value;
        try {
            value = get.getSNMPGetNextValue();
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
            return null;
        }
        return value;
    }


    private String snmpWalktoString(Resource resource, String[] params) throws Exception {//}, MibLoaderException {
        Properties parameters = new Properties();
        if (resource.getAddress() == null) throw new RuntimeException("Resource Address is null");
        parameters.put(SnmpConfigurator.O_ADDRESS, Arrays.asList(resource.getAddress()));
//        parameters.put(SnmpConfigurator.O_PORT, Arrays.asList(resource.getAddress()));
        parameters.put(SnmpConfigurator.O_COMMUNITY, Arrays.asList(resource.getAttributes().get("community-ro")));
        //parameters.put(SnmpConfigurator.O_VERSION, Arrays.asList("2c"));// TODO

        String version = resource.getAttributes().get("version") == null ? "2c" : resource.getAttributes().get("version");
        int retriesInt = resource.getAttributes().get("retries") == null ? 3 : Integer.parseInt(resource.getAttributes().get("retries"));
        int timeoutInt = resource.getAttributes().get("timeout") == null ? 1200 : Integer.parseInt(resource.getAttributes().get("timeout"));
        int maxrepetitions = resource.getAttributes().get("max-repetitions") == null ? 65535 : Integer.parseInt(resource.getAttributes().get("timeout"));

        parameters.put(SnmpConfigurator.O_VERSION, Arrays.asList(version));
//        parameters.put(SnmpConfigurator.O_TIMEOUT, Arrays.asList(1200));
//        parameters.put(SnmpConfigurator.O_RETRIES, Arrays.asList(2));
//        parameters.put(SnmpConfigurator.O_MAX_REPETITIONS, Arrays.asList(65535));
        parameters.put(SnmpConfigurator.O_TIMEOUT, Arrays.asList(timeoutInt));
        parameters.put(SnmpConfigurator.O_RETRIES, Arrays.asList(retriesInt));
        parameters.put(SnmpConfigurator.O_MAX_REPETITIONS, Arrays.asList(maxrepetitions));

        Node root = walker.walk(params, parameters);
        String xml = Walk.printTreeAsXML(root);
        return xml;
    }
    static {
        LogFactory.setLogFactory(new Log4jLogFactory());
    }

}
