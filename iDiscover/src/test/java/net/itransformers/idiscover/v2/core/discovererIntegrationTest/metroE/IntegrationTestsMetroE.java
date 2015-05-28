package net.itransformers.idiscover.v2.core.discovererIntegrationTest.metroE;

import net.itransformers.idiscover.core.*;
import net.itransformers.idiscover.discoverers.DefaultDiscovererFactory;
import net.itransformers.idiscover.discoverers.SnmpWalker;
import net.itransformers.idiscover.discoveryhelpers.xml.SnmpForXslt;
import net.itransformers.idiscover.discoveryhelpers.xml.XmlDiscoveryHelperFactory;
import net.itransformers.idiscover.networkmodel.DiscoveredDeviceData;
import net.itransformers.idiscover.networkmodel.ObjectType;
import net.itransformers.idiscover.networkmodel.ParameterType;
import net.itransformers.idiscover.util.JaxbMarshalar;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.idiscover.v2.core.snmpdiscoverer.SnmpNodeDiscoverer;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.ArrayList;
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
public class IntegrationTestsMetroE {
    private SnmpNodeDiscoverer snmpNodeDiscoverer;
    private String[] discoveryTypes = new String[5];
    private final String baseDir = (String) System.getProperties().get("user.dir");
    private    Map<String, String> params1 = new HashMap<String, String>();
    private RawDeviceData rawDeviceData = new RawDeviceData(null);
    XmlDiscoveryHelperFactory discoveryHelperFactory = null;
    DiscoveryResourceManager discoveryResource;
    SnmpWalker walker;


    @Before
    public void setUp() throws Exception {


        File IntegrationTestCiscoASR1000 = new File(baseDir + File.separator + "iDiscover/src/test/resources/test/metroE");
        if (IntegrationTestCiscoASR1000.exists()){
            try {
                FileUtils.deleteDirectory(new File(baseDir + File.separator + "iDiscover/src/test/resources/test/metroE"));
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }



        try {
            Map<String, String> params1 = new HashMap<String, String>();
            params1.put("fileName", new File(baseDir,"iDiscover/conf/xml/discoveryParameters.xml").getAbsolutePath());
            discoveryHelperFactory = new XmlDiscoveryHelperFactory(params1);
        } catch (JAXBException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }
    @Test
    public void testM38(){
        Map<String, String> resourceParams = new HashMap<String, String>();
        Resource resource = new Resource("R1", "10.17.1.13", resourceParams);
        resource.setDeviceType("CISCO");

        FileInputStream is = null;
        try {
            is = new FileInputStream("iDiscover/src/test/resources/raw-data-metroE/raw-data-M-238.xml");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        byte[] data = null;
        try {
            data = new byte[is.available()];
            is.read(data);

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        rawDeviceData.setData(data);
        DiscoveryHelper discoveryHelper = discoveryHelperFactory.createDiscoveryHelper("CISCO");
        discoveryHelper.setDryRun(true);
        try {
            walker = (SnmpWalker) new DefaultDiscovererFactory().createDiscoverer(resource);
        } catch (Exception e) {
            e.printStackTrace();
        }


        DiscoveredDeviceData discoveredDeviceData = discoveryHelper.parseDeviceRawData(rawDeviceData, discoveryTypes, resource);
        Map<String, HashMap<String, String>> discoveredDevices = new HashMap<String, HashMap<String, String>>();


        SnmpForXslt.setDiscoveredDevices(discoveredDevices);

        discoveryHelper.setDryRun(false);
        discoveredDeviceData = discoveryHelper.parseDeviceRawData(rawDeviceData, discoveryTypes, resource);
        Map<String, Integer> neighbourTypeCounts = fillInNeighbourTree(discoveredDeviceData.getObject());
        Assert.assertEquals((Object) 2, neighbourTypeCounts.get("CDP"));
        Assert.assertEquals((Object) 1,neighbourTypeCounts.get("MAC"));
        Assert.assertEquals((Object) 3,neighbourTypeCounts.get("UNKNOWN"));



    }
//    @Test
//    public void testM238() throws TransformerException, IOException, SAXException, ParserConfigurationException {
//        new NodeDiscoverer(){
//
//
//            @Override
//            public NodeDiscoveryResult discover(ConnectionDetails connectionDetails) {
//                resource = new Resource("M238", "10.17.1.13", resourceParams);
//                resource.setDeviceType("CISCO");
//
//
//                try {
//                    walker = (SnmpWalker) new DefaultDiscovererFactory().createDiscoverer(resource);
//                } catch (Exception e) {
//                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                }
//
//                FileInputStream is = null;
//                try {
//                    is = new FileInputStream("iDiscover/src/test/resources/raw-data-metroE/raw-data-M-238.xml");
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                }
//                byte[] data = new byte[0];
//                try {
//                    data = new byte[is.available()];
//                    is.read(data);
//
//                } catch (IOException e) {
//                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                }
//
//                rawdata.setData(data);
//
//
//
//
//                NodeDiscoveryResult result = new NodeDiscoveryResult();
//                //String devName = walker.getDeviceName(resource);
//                result.setNodeId(resource.getHost());
//                result.setDiscoveredData("rawData", rawdata.getData());
//                DiscoveredDeviceData discoveredDeviceData = discoveryHelper.parseDeviceRawData(rawdata, discoveryTypes, resource);
//                result.setDiscoveredData("deviceData", discoveredDeviceData);
//
//
//                Map<String,Integer> neighbourTypeCounts = fillInNeighbourTree(discoveredDeviceData.getObject());
//
//
//
//
//                deviceLogger.handleDevice(result.getNodeId(), rawdata, discoveredDeviceData, resource);
//                // xmlTopologyDeviceLogger.handleDevice(result.getNodeId(),rawdata,discoveredDeviceData,resource);
//
//                System.out.println(neighbourTypeCounts);
//                Assert.assertEquals((Object) 2,neighbourTypeCounts.get("CDP"));
//                Assert.assertEquals((Object) 1,neighbourTypeCounts.get("MAC"));
//
//
//                return result;
//            }
//        }.discover(null);
//    }
    @Test
    public void testR112()  {

        Map<String, String> resourceParams = new HashMap<String, String>();
        Resource resource = new Resource("R1", "10.17.1.13", resourceParams);
        resource.setDeviceType("CISCO");

        FileInputStream is = null;
        try {
            is = new FileInputStream("iDiscover/src/test/resources/raw-data-metroE/raw-data-R-112.xml");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        byte[] data = null;
        try {
            data = new byte[is.available()];
            is.read(data);

        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        rawDeviceData.setData(data);
        DiscoveryHelper discoveryHelper = discoveryHelperFactory.createDiscoveryHelper("CISCO");
        discoveryHelper.setDryRun(true);
        try {
            walker = (SnmpWalker) new DefaultDiscovererFactory().createDiscoverer(resource);
        } catch (Exception e) {
            e.printStackTrace();
        }


        DiscoveredDeviceData discoveredDeviceData = discoveryHelper.parseDeviceRawData(rawDeviceData, discoveryTypes, resource);



        Map<String, HashMap<String, String>> discoveredDevices = new HashMap<String, HashMap<String, String>>();
        HashMap<String,String> s11218 =  new HashMap<String, String>();
        s11218.put("snmp","S-112-18");
        s11218.put("deviceType","CISCO");
        discoveredDevices.put("10.32.249.87", s11218);

        HashMap<String,String> s11227  = new HashMap<String, String>();
        s11227.put("snmp","S-112-27");
        s11227.put("deviceType","CISCO");
        discoveredDevices.put("10.32.249.119", s11227);

        HashMap<String,String> s1120  = new HashMap<String, String>();
        s1120.put("snmp","S-112-0");
        s1120.put("deviceType","CISCO");
        discoveredDevices.put("10.32.250.51", s1120);
       // S-112-3
        HashMap<String,String> s1123  = new HashMap<String, String>();
        s1123.put("snmp","S-112-3");
        s1123.put("deviceType","CISCO");
        discoveredDevices.put("10.32.250.56", s1123);
       //M-321
        HashMap<String,String> m321  = new HashMap<String, String>();
        m321.put("snmp","M-321");
        m321.put("deviceType","CISCO");
        discoveredDevices.put("10.32.219.53", m321);
       //172.16.2.98
        HashMap<String,String> n17216298  = new HashMap<String, String>();
        n17216298.put("snmp","");
        n17216298.put("deviceType","UNKNOWN");
        discoveredDevices.put("172.16.2.98", n17216298);
        //212.248.1.126


        //SnmpForXslt.resolveIPAddresses(discoveryResource,);
        SnmpForXslt.setDiscoveredDevices(discoveredDevices);


        discoveryHelper.setDryRun(false);
        OutputStream os  = null;

        discoveredDeviceData = discoveryHelper.parseDeviceRawData(rawDeviceData, discoveryTypes, resource);

        try {
            os = new ByteArrayOutputStream();
            JaxbMarshalar.marshal(discoveredDeviceData, os, "DiscoveredDevice");
            String str = os.toString();
            System.out.println(str);
        } catch (JAXBException e) {
            e.printStackTrace();
        } finally {
            if (os != null) try {os.close();} catch (IOException e) {}
        }


        Map<String, Integer> neighbourTypeCounts = fillInNeighbourTree(discoveredDeviceData.getObject());
        System.out.println(neighbourTypeCounts);
        //Assert.assertEquals((Object) 53,neighbourTypeCounts.get("c_OSPF"));
        Assert.assertEquals((Object) 7,neighbourTypeCounts.get("CDP"));
        Assert.assertEquals((Object) 6,neighbourTypeCounts.get("Slash30"));
        Assert.assertEquals((Object) 7,neighbourTypeCounts.get("c_OSPF"));
        Assert.assertEquals((Object) 1,neighbourTypeCounts.get("Slash31"));
        Assert.assertEquals((Object) 16,neighbourTypeCounts.get("UNKNOWN"));
        Assert.assertEquals((Object) 5,neighbourTypeCounts.get("CISCO"));



    }



//
//    @Test
//    public void testMAGRTI0() throws TransformerException, IOException, SAXException, ParserConfigurationException {
//        new NodeDiscoverer(){
//
//
//            @Override
//            public NodeDiscoveryResult discover(ConnectionDetails connectionDetails) {
//                resource = new Resource("MAG-RTI-0", "10.17.1.13", resourceParams);
//                resource.setDeviceType("CISCO");
//
//
//                try {
//                    walker = (SnmpWalker) new DefaultDiscovererFactory().createDiscoverer(resource);
//                } catch (Exception e) {
//                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                }
//
//                FileInputStream is = null;
//                try {
//                    is = new FileInputStream("iDiscover/src/test/resources/raw-data-metroE/raw-data-MAG-RTI-0.xml");
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                }
//                byte[] data = new byte[0];
//                try {
//                    data = new byte[is.available()];
//                    is.read(data);
//
//                } catch (IOException e) {
//                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                }
//
//                rawdata.setData(data);
//
//
//
//
//                NodeDiscoveryResult result = new NodeDiscoveryResult();
//                //String devName = walker.getDeviceName(resource);
//                result.setNodeId(resource.getHost());
//                result.setDiscoveredData("rawData", rawdata.getData());
//                DiscoveredDeviceData discoveredDeviceData = discoveryHelper.parseDeviceRawData(rawdata, discoveryTypes, resource);
//                result.setDiscoveredData("deviceData", discoveredDeviceData);
//
//
//                Map<String,Integer> neighbourTypeCounts = fillInNeighbourTree(discoveredDeviceData.getObject());
//
//
//
//
//                deviceLogger.handleDevice(result.getNodeId(),rawdata,discoveredDeviceData,resource);
//                // xmlTopologyDeviceLogger.handleDevice(result.getNodeId(),rawdata,discoveredDeviceData,resource);
//
//                System.out.println(neighbourTypeCounts);
//                Assert.assertEquals((Object) 2, neighbourTypeCounts.get("c_OSPF"));
//                Assert.assertEquals((Object) 1,neighbourTypeCounts.get("c_STATIC_ROUTE"));
//                Assert.assertEquals((Object) 10,neighbourTypeCounts.get("ARP"));
//                Assert.assertEquals((Object) 4,neighbourTypeCounts.get("Slash30"));
//
//
//
//                return result;
//            }
//        }.discover(null);
//    }
//
//    @Test
//    public void testR112() throws TransformerException, IOException, SAXException, ParserConfigurationException {
//        new NodeDiscoverer(){
//
//
//            @Override
//            public NodeDiscoveryResult discover(ConnectionDetails connectionDetails) {
//                resource = new Resource("R-112", "10.17.1.13", resourceParams);
//                resource.setDeviceType("CISCO");
//
//
//                try {
//                    walker = (SnmpWalker) new DefaultDiscovererFactory().createDiscoverer(resource);
//                } catch (Exception e) {
//                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                }
//
//                FileInputStream is = null;
//                try {
//                    is = new FileInputStream("iDiscover/src/test/resources/raw-data-metroE/raw-data-R-112.xml");
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                }
//                byte[] data = new byte[0];
//                try {
//                    data = new byte[is.available()];
//                    is.read(data);
//
//                } catch (IOException e) {
//                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                }
//
//                rawdata.setData(data);
//
//
//
//
//                NodeDiscoveryResult result = new NodeDiscoveryResult();
//                //String devName = walker.getDeviceName(resource);
//                result.setNodeId(resource.getHost());
//                result.setDiscoveredData("rawData", rawdata.getData());
//                DiscoveredDeviceData discoveredDeviceData = discoveryHelper.parseDeviceRawData(rawdata, discoveryTypes, resource);
//                result.setDiscoveredData("deviceData", discoveredDeviceData);
//
//
//                Map<String,Integer> neighbourTypeCounts = fillInNeighbourTree(discoveredDeviceData.getObject());
//
//
//
//
//                deviceLogger.handleDevice(result.getNodeId(), rawdata, discoveredDeviceData, resource);
//                // xmlTopologyDeviceLogger.handleDevice(result.getNodeId(),rawdata,discoveredDeviceData,resource);
//
//                System.out.println(neighbourTypeCounts);
//                Assert.assertEquals((Object) 53,neighbourTypeCounts.get("c_OSPF"));
//                Assert.assertEquals((Object) 11,neighbourTypeCounts.get("CDP"));
//                Assert.assertEquals((Object) 78,neighbourTypeCounts.get("Slash30"));
//
//                return result;
//            }
//        }.discover(null);
//    }
//
//    @Test
//    public void testR248() throws TransformerException, IOException, SAXException, ParserConfigurationException {
//        new NodeDiscoverer(){
//
//
//            @Override
//            public NodeDiscoveryResult discover(ConnectionDetails connectionDetails) {
//                resource = new Resource("R-248", "10.17.1.13", resourceParams);
//                resource.setDeviceType("CISCO");
//
//
//                try {
//                    walker = (SnmpWalker) new DefaultDiscovererFactory().createDiscoverer(resource);
//                } catch (Exception e) {
//                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                }
//
//                FileInputStream is = null;
//                try {
//                    is = new FileInputStream("iDiscover/src/test/resources/raw-data-metroE/raw-data-R-248.xml");
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                }
//                byte[] data = new byte[0];
//                try {
//                    data = new byte[is.available()];
//                    is.read(data);
//
//                } catch (IOException e) {
//                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                }
//
//                rawdata.setData(data);
//
//
//
//
//                NodeDiscoveryResult result = new NodeDiscoveryResult();
//                //String devName = walker.getDeviceName(resource);
//                result.setNodeId(resource.getHost());
//                result.setDiscoveredData("rawData", rawdata.getData());
//                DiscoveredDeviceData discoveredDeviceData = discoveryHelper.parseDeviceRawData(rawdata, discoveryTypes, resource);
//                result.setDiscoveredData("deviceData", discoveredDeviceData);
//
//
//                Map<String,Integer> neighbourTypeCounts = fillInNeighbourTree(discoveredDeviceData.getObject());
//
//
//
//
//                deviceLogger.handleDevice(result.getNodeId(),rawdata,discoveredDeviceData,resource);
//                // xmlTopologyDeviceLogger.handleDevice(result.getNodeId(),rawdata,discoveredDeviceData,resource);
//
//                System.out.println(neighbourTypeCounts);
//                Assert.assertEquals((Object) 52,neighbourTypeCounts.get("c_OSPF"));
//                Assert.assertEquals((Object) 8,neighbourTypeCounts.get("CDP"));
//                Assert.assertEquals((Object) 79,neighbourTypeCounts.get("Slash30"));
//
//                return result;
//            }
//        }.discover(null);
//    }
//
//    @Test
//    public void testRRRTI() throws TransformerException, IOException, SAXException, ParserConfigurationException {
//        new NodeDiscoverer(){
//
//
//            @Override
//            public NodeDiscoveryResult discover(ConnectionDetails connectionDetails) {
//                resource = new Resource("RR-RTI", "10.17.1.13", resourceParams);
//                resource.setDeviceType("CISCO");
//
//
//                try {
//                    walker = (SnmpWalker) new DefaultDiscovererFactory().createDiscoverer(resource);
//                } catch (Exception e) {
//                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                }
//
//                FileInputStream is = null;
//                try {
//                    is = new FileInputStream("iDiscover/src/test/resources/raw-data-metroE/raw-data-RR-RTI.xml");
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                }
//                byte[] data = new byte[0];
//                try {
//                    data = new byte[is.available()];
//                    is.read(data);
//
//                } catch (IOException e) {
//                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                }
//
//                rawdata.setData(data);
//
//
//
//
//                NodeDiscoveryResult result = new NodeDiscoveryResult();
//                //String devName = walker.getDeviceName(resource);
//                result.setNodeId(resource.getHost());
//                result.setDiscoveredData("rawData", rawdata.getData());
//                DiscoveredDeviceData discoveredDeviceData = discoveryHelper.parseDeviceRawData(rawdata, discoveryTypes, resource);
//                result.setDiscoveredData("deviceData", discoveredDeviceData);
//
//
//                Map<String,Integer> neighbourTypeCounts = fillInNeighbourTree(discoveredDeviceData.getObject());
//
//
//
//
//                deviceLogger.handleDevice(result.getNodeId(),rawdata,discoveredDeviceData,resource);
//                // xmlTopologyDeviceLogger.handleDevice(result.getNodeId(),rawdata,discoveredDeviceData,resource);
//
//                System.out.println(neighbourTypeCounts);
//                Assert.assertEquals((Object) 2, neighbourTypeCounts.get("c_OSPF"));
//                Assert.assertEquals((Object) 3, neighbourTypeCounts.get("CDP"));
//                Assert.assertEquals((Object) 3,neighbourTypeCounts.get("Slash30"));
//
//                return result;
//            }
//        }.discover(null);
//    }
//
//    @Test
//    public void testS2097() throws TransformerException, IOException, SAXException, ParserConfigurationException {
//        new NodeDiscoverer(){
//
//
//            @Override
//            public NodeDiscoveryResult discover(ConnectionDetails connectionDetails) {
//                resource = new Resource("S-209-7", "10.17.1.13", resourceParams);
//                resource.setDeviceType("CISCO");
//
//
//                try {
//                    walker = (SnmpWalker) new DefaultDiscovererFactory().createDiscoverer(resource);
//                } catch (Exception e) {
//                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                }
//
//                FileInputStream is = null;
//                try {
//                    is = new FileInputStream("iDiscover/src/test/resources/raw-data-metroE/raw-data-S-209-7.xml");
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                }
//                byte[] data = new byte[0];
//                try {
//                    data = new byte[is.available()];
//                    is.read(data);
//
//                } catch (IOException e) {
//                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                }
//
//                rawdata.setData(data);
//
//
//
//
//                NodeDiscoveryResult result = new NodeDiscoveryResult();
//                //String devName = walker.getDeviceName(resource);
//                result.setNodeId(resource.getHost());
//                result.setDiscoveredData("rawData", rawdata.getData());
//                DiscoveredDeviceData discoveredDeviceData = discoveryHelper.parseDeviceRawData(rawdata, discoveryTypes, resource);
//                result.setDiscoveredData("deviceData", discoveredDeviceData);
//
//
//                Map<String,Integer> neighbourTypeCounts = fillInNeighbourTree(discoveredDeviceData.getObject());
//
//
//
//
//                deviceLogger.handleDevice(result.getNodeId(), rawdata, discoveredDeviceData, resource);
//                // xmlTopologyDeviceLogger.handleDevice(result.getNodeId(),rawdata,discoveredDeviceData,resource);
//
//                System.out.println(neighbourTypeCounts);
//                Assert.assertEquals((Object) 1,neighbourTypeCounts.get("CDP"));
//
//                return result;
//            }
//        }.discover(null);
//    }
//
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
                        System.out.println("\t"+interfaceObject.getObjectType()+" "+interfaceObject.getName());




                        List<ParameterType> parameters =  interfaceObject.getParameters().getParameter();
                        for (ParameterType parameterType : parameters) {
                            if(parameterType.getName().equals("Discovery Method")){
                                System.out.println("\t\t"+"Discovery Method: "+parameterType.getValue());
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
                                System.out.println("\t\t"+"Neighbor IP Address: "+parameterType.getValue());

                            }
                            if(parameterType.getName().equals("Neighbor Device Type")){
                                System.out.println("\t\t" + "Neighbor Device Type: " + parameterType.getValue());
                                if (neighbourTypeCounts.get(parameterType.getValue())==null) {
                                    neighbourTypeCounts.put(parameterType.getValue(), 1);

                                }else{
                                    int currentCount = neighbourTypeCounts.get(parameterType.getValue());

                                    neighbourTypeCounts.put(parameterType.getValue(), ++currentCount);
                                }
                            }

                        }
                    }
                }

            }
        }
        return neighbourTypeCounts;
    }




    private List<ConnectionDetails> createNeighbourConnectionDetails(List<DeviceNeighbour> neighbours) {
        List<ConnectionDetails> neighboursConnDetails = new ArrayList<ConnectionDetails>();
        for (DeviceNeighbour neighbour : neighbours) {
            ConnectionDetails neighbourConnectionDetails = new ConnectionDetails();
            neighbourConnectionDetails.put("deviceType",neighbour.getDeviceType());
            if (neighbour.getStatus()){ // if reachable
                neighbourConnectionDetails.put("deviceName",neighbour.getHostName());
                neighbourConnectionDetails.put("ipAddress",neighbour.getIpAddress().getIpAddress());
                neighbourConnectionDetails.setConnectionType("snmp");
                neighboursConnDetails.add(neighbourConnectionDetails);
            }
        }
        return neighboursConnDetails;
    }



}