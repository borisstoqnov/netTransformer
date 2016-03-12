

/*
 * SnmpWalkerBase.java
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

package net.itransformers.idiscover.discoverers;

import net.itransformers.idiscover.core.Discoverer;
import net.itransformers.idiscover.core.RawDeviceData;
import net.itransformers.idiscover.core.Resource;
import net.itransformers.snmp2xml4j.snmptoolkit.*;
import net.itransformers.snmp2xml4j.snmptoolkit.messagedispacher.MessageDispatcherAbstractFactory;
import net.itransformers.snmp2xml4j.snmptoolkit.transport.TransportMappingAbstractFactory;
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
            sysDescr = sysDescr.toUpperCase();
        } catch (Exception e) {
            logger.error("Error getDevice Type (" + resource.getAddress() + "), err msg=" + e.getMessage());
            return "UNKNOWN";
        }
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
        }
        else {
          return  "UNKNOWN";
        }
    }

    public String getDeviceName(Resource resource) {
        String deviceNameOID = "1.3.6.1.2.1.1.5";
        try {
            return snmpGetNext(resource, deviceNameOID);
        } catch (Exception e) {
            logger.error("Error getDevice Name (" + resource.getAddress() +" " +resource.getAttributes() + "), err msg=" + e.getMessage());
            return null;
        }
    }

    public String getByOid(Resource resource, String oid) {
        try {
            return snmpGetNext(resource, oid);
        } catch (Exception e) {
            logger.error("Error get oid( " +oid.toString()+ " ) ( "+ resource.getAddress() + " "+resource.getAttributes() + "), err msg=" + e.getMessage(), e);
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
        Get get = null;

        if (community == null){
            logger.debug("community-ro parameter for "+resource.getAddress() +" has not been specifed!" +
                    "Will try with community-rw");

            if (community2 == null){
                logger.error("community-rw parameter for "+resource.getAddress() +" has also not been specifed!!"+
                        "Can't do much here! Continuing to the next device!!!");
                return null;

            } else {
                community=community2;
            }
        }

        if (resource.getAddress() == null){
            logger.error("Resource address is null");
            return null;
        }


        try {
            get = new Get(oid, resource.getAddress(), versionInt, retriesInt, timeoutInt, community, transportFactory, messageDispatcherFactory);
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }

        String value=null;
        try {
            value = get.getSNMPGetNextValue();
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }


        if (value!=null){

            return value;

        }else {

            try {
                get = new Get(oid, resource.getAddress(), versionInt, retriesInt, timeoutInt, community2, transportFactory, messageDispatcherFactory);
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
            }
            try {
                value = get.getSNMPValue();
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
            }
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

        String address = resource.getAddress();
        if (address == null) {
            logger.error("Resource Address is null! Can't walk the device");
        }

        if (address.contains(" ")){

            logger.error("Address not in the right format" + resource.getAddress());
            String [] addresses  =   resource.getAddress().split(" ");
            address = addresses[addresses.length -1];

        } else{

            address=resource.getAddress();
        }


        String deviceSysDescrOID = "1.3.6.1.2.1.1.1";
        String sysDescr = null;
        String community = null;

        //Try with community-rw
        if (snmpGetNextSingleCommunity(resource,resource.getAttributes().get("community-ro"), deviceSysDescrOID)!=null)  {

            community = resource.getAttributes().get("community-ro");

        } else if(snmpGetNextSingleCommunity(resource,resource.getAttributes().get("community-rw"), deviceSysDescrOID)!=null) {
            community = resource.getAttributes().get("community-rw");

        }  else {
            return null;
        }


        parameters.put(SnmpConfigurator.O_ADDRESS, Arrays.asList(address));
        parameters.put(SnmpConfigurator.O_COMMUNITY, Arrays.asList(community));

        String version = resource.getAttributes().get("version") == null ? "2c" : resource.getAttributes().get("version");
        int retriesInt = resource.getAttributes().get("retries") == null ? 3 : Integer.parseInt(resource.getAttributes().get("retries"));
        int timeoutInt = resource.getAttributes().get("timeout") == null ? 1200 : Integer.parseInt(resource.getAttributes().get("timeout"));
        int maxrepetitions = resource.getAttributes().get("max-repetitions") == null ? 65535 : Integer.parseInt(resource.getAttributes().get("timeout"));

        parameters.put(SnmpConfigurator.O_VERSION, Arrays.asList(version));
        parameters.put(SnmpConfigurator.O_TIMEOUT, Arrays.asList(timeoutInt));
        parameters.put(SnmpConfigurator.O_RETRIES, Arrays.asList(retriesInt));
        parameters.put(SnmpConfigurator.O_MAX_REPETITIONS, Arrays.asList(maxrepetitions));


        Node root = walker.walk(params, parameters);

        String xml = Walk.printTreeAsXML(root);
        return new RawDeviceData(xml.getBytes());
    }

    private String snmpGetNextSingleCommunity(Resource resource, String community, String deviceSysDescrOID) {
        System.out.println("snmpGetNextSingleCommunity for "+ resource.getAddress()+" community "+ community);
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
        Get get = null;

        if (community == null){
           return  null;

        }
        try {
            get = new Get(deviceSysDescrOID, resource.getAddress(), versionInt, retriesInt, timeoutInt, community, transportFactory, messageDispatcherFactory);
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }

        String value=null;
        try {
            value = get.getSNMPGetNextValue();
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }

        return value;
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
        Get get = null;

        if (community == null){
            logger.debug("community-ro parameter for "+resource.getAddress() +" has not been specifed!" +
                    "Will try with community-rw");

            if (community2 == null){
                logger.error("community-rw parameter for "+resource.getAddress() +" has also not been specifed!!"+
                "Can't do much here! Continuing to the next device!!!");
                 return null;

            } else {
                community=community2;
            }
        }

        if (resource.getAddress() == null){
            logger.error("Resource address is null");
            return null;
        }


        try {
            get = new Get(oid, resource.getAddress(), versionInt, retriesInt, timeoutInt, community, transportFactory, messageDispatcherFactory);
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }

        String value=null;
        try {
            value = get.getSNMPGetNextValue();
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }


        if (value!=null){

            return value;

        }else {

            try {
                get = new Get(oid, resource.getAddress(), versionInt, retriesInt, timeoutInt, community2, transportFactory, messageDispatcherFactory);
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
            }
            try {
                value = get.getSNMPGetNextValue();
            } catch (IOException e) {
                logger.error(e.getMessage(),e);
            }
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
