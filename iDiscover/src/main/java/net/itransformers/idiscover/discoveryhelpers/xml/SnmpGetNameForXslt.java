///*
// * SnmpGetNameForXslt.java
// *
// * This work is free software; you can redistribute it and/or modify
// * it under the terms of the GNU General Public License as published
// * by the Free Software Foundation; either version 2 of the License,
// * or (at your option) any later version.
// *
// * This work is distributed in the hope that it will be useful, but
// * WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// * GNU General Public License for more details.
// *
// * You should have received a copy of the GNU General Public License
// * along with this program; if not, write to the Free Software
// * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
// * USA
// *
// * Copyright (c) 2010-2016 iTransformers Labs. All rights reserved.
// */
//
//package net.itransformers.idiscover.discoveryhelpers.xml;
//
//import net.itransformers.idiscover.core.Discoverer;
//import net.itransformers.idiscover.core.DiscovererFactory;
//import net.itransformers.idiscover.core.Resource;
//import net.itransformers.idiscover.discoverers.DefaultDiscovererFactory;
//import org.apache.commons.validator.routines.InetAddressValidator;
//import org.apache.log4j.Logger;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class SnmpGetNameForXslt {
//    static Logger logger = Logger.getLogger(SnmpGetNameForXslt.class);
//    private static DiscovererFactory discovererFactory;
//    private static InetAddressValidator ipAddressValidator = new InetAddressValidator();
//
//
//    public SnmpGetNameForXslt() {
//        discovererFactory = new DefaultDiscovererFactory();
//    }
//
//    public static String getName(String hostName, String community) throws Exception {
//        Map<String, String> resourceParams = new HashMap<String, String>();
//        resourceParams.put("community", community);
//        resourceParams.put("version", "1");
//        Resource resource = new Resource(hostName,null, resourceParams);// TODO specify port
//        Discoverer discoverer = discovererFactory.createDiscoverer(resource);
//        final String deviceName = discoverer.getDeviceName(resource);
//        logger.debug("hostname:"+hostName+", community: "+community+", found deviceName:"+deviceName);
//        return deviceName;
//    }
//
//    public static String getSymbolByOid(String mibName, String oid) throws Exception {
//        SimulSnmpWalker discoverer = (SimulSnmpWalker) discovererFactory.createDiscoverer(null);
//        return discoverer.getSymbolByOid(mibName, oid);
//    }
//
//    public static String getByOid(String hostName, String oid, String community) throws Exception {
//        Map<String, String> resourceParams = new HashMap<String, String>();
//        resourceParams.put("community", community);
//        resourceParams.put("version", "1");
//        Resource resource = new Resource(hostName,null, resourceParams);// TODO specify port
//        SimulSnmpWalker discoverer = (SimulSnmpWalker) discovererFactory.createDiscoverer(resource);
//        final String deviceName = discoverer.getByOid(resource, oid);
//        logger.debug("hostname:"+hostName+", community: "+community+", found deviceName:"+deviceName);
//        return deviceName;
//    }
//    public static String getIPbyName(String hostname){
//        try
//        {
//            java.net.InetAddress inetAdd = java.net.InetAddress.getByName(hostname);
//            return inetAdd.getHostAddress();
//        }
//        catch(java.net.UnknownHostException uhe)
//        {
//            //handle exception
//            return "";
//        }
//
//    }
//
//}
