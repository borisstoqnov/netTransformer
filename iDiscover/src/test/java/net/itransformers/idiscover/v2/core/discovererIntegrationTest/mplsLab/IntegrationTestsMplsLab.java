/*
 * IntegrationTestsMplsLab.java
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

package net.itransformers.idiscover.v2.core.discovererIntegrationTest.mplsLab;

import net.itransformers.idiscover.core.DiscoveryHelper;
import net.itransformers.idiscover.core.DiscoveryTypes;
import net.itransformers.idiscover.core.RawDeviceData;
import net.itransformers.idiscover.core.Resource;
import net.itransformers.idiscover.discoverers.DefaultDiscovererFactory;
import net.itransformers.idiscover.discoverers.SnmpWalker;
import net.itransformers.idiscover.discoveryhelpers.xml.XmlDiscoveryHelperFactory;
import net.itransformers.idiscover.discoverylisteners.DeviceFileLogger;
import net.itransformers.idiscover.discoverylisteners.XmlTopologyDeviceLogger;
import net.itransformers.idiscover.networkmodel.DiscoveredDeviceData;
import net.itransformers.idiscover.networkmodel.ObjectType;
import net.itransformers.idiscover.networkmodel.ParameterType;
import net.itransformers.idiscover.v2.core.NodeDiscoverer;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: niau
 * Date: 3/8/15
 * Time: 1:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class IntegrationTestsMplsLab {
    private SnmpWalker walker;
    private String[] discoveryTypes = new String[5];
    private DiscoveryHelper discoveryHelper;
    private Resource resource;
    private RawDeviceData rawdata = new RawDeviceData(null);
    private DeviceFileLogger deviceLogger;
    private XmlTopologyDeviceLogger xmlTopologyDeviceLogger;
    private final String baseDir = (String) System.getProperties().get("user.dir");
    private    Map<String, String> params1 = new HashMap<String, String>();
    Map<String, String> resourceParams = new HashMap<String, String>();


    @Before
    public void setUp() throws Exception {


        File IntegrationTestCiscoASR1000 = new File(baseDir + File.separator + "iDiscover/src/test/resources/test/mpls-lab");
        if (IntegrationTestCiscoASR1000.exists()){
            try {
                FileUtils.deleteDirectory(new File(baseDir + File.separator + "iDiscover/src/test/resources/test/mpls-lab"));
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }




        params1.put("path","iDiscover/src/test/resources/test");
        params1.put("device-data-logging-path","device-hierarchical");
        params1.put("raw-data-logging-path","raw-data");
        params1.put("device-centric-logging-path","device-centric");
        params1.put("network-centric-logging-path","undirected");
        // params1.put("xslt","iDiscover/conf/xslt/transformator-undirected2.xslt");

        XmlDiscoveryHelperFactory discoveryHelperFactory = null;
        try {
            Map<String, String> params1 = new HashMap<String, String>();
            params1.put("fileName", new File(baseDir,"iDiscover/conf/xml/discoveryParameters.xml").getAbsolutePath());
            discoveryHelperFactory = new XmlDiscoveryHelperFactory(params1);
        } catch (JAXBException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        discoveryHelper = discoveryHelperFactory.createDiscoveryHelper("CISCO");
        discoveryTypes[0] = DiscoveryTypes.ADDITIONAL;
        resourceParams.put("community", "netTransformer-r");
        resourceParams.put("version", "2c");
        resourceParams.put("retries", "1");
        resourceParams.put("timeout", "100");

        resourceParams.put("mibDir", "snmptoolkit/mibs");

        deviceLogger = new DeviceFileLogger(params1,new File(baseDir),"mplsLab");
        //    xmlTopologyDeviceLogger = new XmlTopologyDeviceLogger(params1,new File(baseDir),"CiscoASR1000Test");



    }
    @Test
    public void testR1() throws TransformerException, IOException, SAXException, ParserConfigurationException {
        new NodeDiscoverer(){

            @Override
            public NodeDiscoveryResult discover(ConnectionDetails connectionDetails) {
                resource = new Resource("R1", "10.17.1.13", resourceParams);
                resource.setDeviceType("CISCO");


                try {
                    walker = (SnmpWalker) new DefaultDiscovererFactory().createDiscoverer(resource);
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                FileInputStream is = null;
                try {
                    is = new FileInputStream("iDiscover/src/test/resources/raw-data-mpls-lab/raw-data-R1.xml");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                byte[] data = new byte[0];
                try {
                    data = new byte[is.available()];
                    is.read(data);

                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                rawdata.setData(data);




                NodeDiscoveryResult result = new NodeDiscoveryResult();
                //String devName = walker.getDeviceName(resource);
                result.setNodeId(resource.getHost());
                result.setDiscoveredData("rawData", rawdata.getData());
                DiscoveredDeviceData discoveredDeviceData = discoveryHelper.parseDeviceRawData(rawdata, discoveryTypes, resource);
                result.setDiscoveredData("deviceData", discoveredDeviceData);


                Map<String,Integer> neighbourTypeCounts = fillInNeighbourTree(discoveredDeviceData.getObject());




                deviceLogger.handleDevice(result.getNodeId(),rawdata,discoveredDeviceData,resource);
                // xmlTopologyDeviceLogger.handleDevice(result.getNodeId(),rawdata,discoveredDeviceData,resource);

                System.out.println(neighbourTypeCounts);
                Assert.assertEquals((Object) 3,neighbourTypeCounts.get("CDP"));
                Assert.assertEquals((Object) 1,neighbourTypeCounts.get("r_ISIS"));
                Assert.assertEquals((Object) 1,neighbourTypeCounts.get("Slash31"));
                Assert.assertEquals((Object) 1,neighbourTypeCounts.get("Slash30"));
                Assert.assertEquals((Object) 1,neighbourTypeCounts.get("ARP"));




                return result;
            }
        }.discover(null);
    }
    @Test
    public void testR2() throws TransformerException, IOException, SAXException, ParserConfigurationException {
        new NodeDiscoverer(){

            @Override
            public NodeDiscoveryResult discover(ConnectionDetails connectionDetails) {

                resource = new Resource("R2", "10.17.1.13", resourceParams);
                resource.setDeviceType("CISCO");


                try {
                        walker = (SnmpWalker) new DefaultDiscovererFactory().createDiscoverer(resource);
                    } catch (Exception e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }

                    FileInputStream is = null;
                    try {
                        is = new FileInputStream("iDiscover/src/test/resources/raw-data-mpls-lab/raw-data-R2.xml");
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }
                    byte[] data = new byte[0];
                    try {
                        data = new byte[is.available()];
                        is.read(data);

                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    }

                    rawdata.setData(data);

                NodeDiscoveryResult result = new NodeDiscoveryResult();
                //String devName = walker.getDeviceName(resource);
                result.setNodeId(resource.getHost());
                result.setDiscoveredData("rawData", rawdata.getData());
                DiscoveredDeviceData discoveredDeviceData = discoveryHelper.parseDeviceRawData(rawdata, discoveryTypes, resource);
                result.setDiscoveredData("deviceData", discoveredDeviceData);

                Map<String,Integer> neighbourTypeCounts = fillInNeighbourTree(discoveredDeviceData.getObject());
                deviceLogger.handleDevice(result.getNodeId(),rawdata,discoveredDeviceData,resource);
                // xmlTopologyDeviceLogger.handleDevice(result.getNodeId(),rawdata,discoveredDeviceData,resource);

                System.out.println(neighbourTypeCounts);

                Assert.assertEquals((Object) 3,neighbourTypeCounts.get("CDP"));
                Assert.assertEquals((Object) 2,neighbourTypeCounts.get("r_ISIS"));
                Assert.assertEquals((Object) 1,neighbourTypeCounts.get("r_OSPF"));

                Assert.assertEquals((Object) 2,neighbourTypeCounts.get("Slash30"));


                return result;
            }
        }.discover(null);
    }
    public void testR3() throws TransformerException, IOException, SAXException, ParserConfigurationException {
        new NodeDiscoverer(){

            @Override
            public NodeDiscoveryResult discover(ConnectionDetails connectionDetails) {

                resource = new Resource("R3", "10.17.1.13", resourceParams);
                resource.setDeviceType("CISCO");


                try {
                    walker = (SnmpWalker) new DefaultDiscovererFactory().createDiscoverer(resource);
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                FileInputStream is = null;
                try {
                    is = new FileInputStream("iDiscover/src/test/resources/raw-data-mpls-lab/raw-data-R3.xml");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                byte[] data = new byte[0];
                try {
                    data = new byte[is.available()];
                    is.read(data);

                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                rawdata.setData(data);


                NodeDiscoveryResult result = new NodeDiscoveryResult();
                //String devName = walker.getDeviceName(resource);
                result.setNodeId(resource.getHost());
                result.setDiscoveredData("rawData", rawdata.getData());
                DiscoveredDeviceData discoveredDeviceData = discoveryHelper.parseDeviceRawData(rawdata, discoveryTypes, resource);
                result.setDiscoveredData("deviceData", discoveredDeviceData);

                Map<String,Integer> neighbourTypeCounts = fillInNeighbourTree(discoveredDeviceData.getObject());
                deviceLogger.handleDevice(result.getNodeId(),rawdata,discoveredDeviceData,resource);
                // xmlTopologyDeviceLogger.handleDevice(result.getNodeId(),rawdata,discoveredDeviceData,resource);

                System.out.println(neighbourTypeCounts);

                Assert.assertEquals((Object) 4,neighbourTypeCounts.get("CDP"));
                Assert.assertEquals((Object) 4,neighbourTypeCounts.get("r_OSPF"));

                Assert.assertEquals((Object) 1,neighbourTypeCounts.get("ARP"));



                return result;
            }
        }.discover(null);
    }
    public void testR4() throws TransformerException, IOException, SAXException, ParserConfigurationException {
        new NodeDiscoverer(){
            @Override
            public NodeDiscoveryResult discover(ConnectionDetails connectionDetails) {

                resource = new Resource("R4", "10.17.1.13", resourceParams);
                resource.setDeviceType("CISCO");

                try {
                    walker = (SnmpWalker) new DefaultDiscovererFactory().createDiscoverer(resource);
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                FileInputStream is = null;
                try {
                    is = new FileInputStream("iDiscover/src/test/resources/raw-data-mpls-lab/raw-data-R4.xml");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                byte[] data = new byte[0];
                try {
                    data = new byte[is.available()];
                    is.read(data);

                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                rawdata.setData(data);


                NodeDiscoveryResult result = new NodeDiscoveryResult();
                //String devName = walker.getDeviceName(resource);
                result.setNodeId(resource.getHost());
                result.setDiscoveredData("rawData", rawdata.getData());
                DiscoveredDeviceData discoveredDeviceData = discoveryHelper.parseDeviceRawData(rawdata, discoveryTypes, resource);
                result.setDiscoveredData("deviceData", discoveredDeviceData);

                Map<String,Integer> neighbourTypeCounts = fillInNeighbourTree(discoveredDeviceData.getObject());
                deviceLogger.handleDevice(result.getNodeId(),rawdata,discoveredDeviceData,resource);
                // xmlTopologyDeviceLogger.handleDevice(result.getNodeId(),rawdata,discoveredDeviceData,resource);

                System.out.println(neighbourTypeCounts);

                Assert.assertEquals((Object) 3,neighbourTypeCounts.get("CDP"));
                Assert.assertEquals((Object) 1,neighbourTypeCounts.get("r_OSPF"));
                Assert.assertEquals((Object) 2,neighbourTypeCounts.get("r_ISIS"));
                Assert.assertEquals((Object) 2,neighbourTypeCounts.get("Slash30"));


                return result;
            }
        }.discover(null);
    }
    public void testR5() throws TransformerException, IOException, SAXException, ParserConfigurationException {
        new NodeDiscoverer(){
            @Override
            public NodeDiscoveryResult discover(ConnectionDetails connectionDetails) {
                resource = new Resource("R5", "10.17.1.13", resourceParams);
                resource.setDeviceType("CISCO");


                try {
                    walker = (SnmpWalker) new DefaultDiscovererFactory().createDiscoverer(resource);
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                FileInputStream is = null;
                try {
                    is = new FileInputStream("iDiscover/src/test/resources/raw-data-mpls-lab/raw-data-R5.xml");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                byte[] data = new byte[0];
                try {
                    data = new byte[is.available()];
                    is.read(data);

                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }

                rawdata.setData(data);


                NodeDiscoveryResult result = new NodeDiscoveryResult();
                //String devName = walker.getDeviceName(resource);
                result.setNodeId(resource.getHost());
                result.setDiscoveredData("rawData", rawdata.getData());
                DiscoveredDeviceData discoveredDeviceData = discoveryHelper.parseDeviceRawData(rawdata, discoveryTypes, resource);
                result.setDiscoveredData("deviceData", discoveredDeviceData);

                Map<String,Integer> neighbourTypeCounts = fillInNeighbourTree(discoveredDeviceData.getObject());
                deviceLogger.handleDevice(result.getNodeId(),rawdata,discoveredDeviceData,resource);
                // xmlTopologyDeviceLogger.handleDevice(result.getNodeId(),rawdata,discoveredDeviceData,resource);

                System.out.println(neighbourTypeCounts);

                Assert.assertEquals((Object) 3,neighbourTypeCounts.get("CDP"));
                Assert.assertEquals((Object) 1,neighbourTypeCounts.get("r_OSPF"));
                Assert.assertEquals((Object) 2,neighbourTypeCounts.get("r_ISIS"));
                Assert.assertEquals((Object) 1,neighbourTypeCounts.get("Slash30"));

                Assert.assertEquals((Object) 1,neighbourTypeCounts.get("ARP"));


                return result;
            }
        }.discover(null);
    }
    HashMap<String, Integer> fillInNeighbourTree(List<ObjectType> objects){
        HashMap<String, Integer>  neighbourTypeCounts  = new HashMap<String, Integer>();
        for (ObjectType object : objects) {
            if(object.getObjectType().equals("Discovery Interface")){
                System.out.println(object.getName());
                List<ObjectType> interfaceObjects = object.getObject();
                for (ObjectType interfaceObject : interfaceObjects) {

                    System.out.println("\t"+interfaceObject.getName());
                    System.out.println("\t"+interfaceObject.getObjectType());

                    if (interfaceObject.getObjectType().equals("Discovered Neighbor")){
                        System.out.println("\t"+interfaceObject.getObjectType());


                        System.out.println("\t"+interfaceObject.getName());


                        List<ParameterType> parameters =  interfaceObject.getParameters().getParameter();
                        for (ParameterType parameterType : parameters) {
                            if(parameterType.getName().equals("Discovery Method")){
                                System.out.println("\t\t"+parameterType.getValue());
                                String [] methods = parameterType.getValue().split(",");

                                for (String method : methods) {
                                    if (neighbourTypeCounts.get(method)==null){
                                        neighbourTypeCounts.put(method,1);
                                    }else {
                                        int currentCount=neighbourTypeCounts.get(method);
                                        neighbourTypeCounts.put(method,++currentCount);
                                    }
                                }


                            }
                            if(parameterType.getName().equals("Neighbor IP Address")){
                                System.out.println("\t\t"+parameterType.getValue());
                            }

                        }
                    }
                }

            }
        }
        return neighbourTypeCounts;
    }

}
