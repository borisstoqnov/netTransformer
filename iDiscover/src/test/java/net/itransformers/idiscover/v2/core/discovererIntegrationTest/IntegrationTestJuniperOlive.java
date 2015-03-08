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

// This juit test case creates a dummy discoverer and transforms raw-data from file to device-data.
// It allows testing of the custom tags part in xslt transformer.
// It has been done as a test for issue https://sourceforge.net/p/itransformer/tickets/8/


package net.itransformers.idiscover.v2.core.discovererIntegrationTest;

import net.itransformers.idiscover.core.DiscoveryHelper;
import net.itransformers.idiscover.core.DiscoveryTypes;
import net.itransformers.idiscover.core.RawDeviceData;
import net.itransformers.idiscover.core.Resource;
import net.itransformers.idiscover.discoverers.DefaultDiscovererFactory;
import net.itransformers.idiscover.discoverers.SnmpWalker;
import net.itransformers.idiscover.discoveryhelpers.xml.XmlDiscoveryHelperFactory;
import net.itransformers.idiscover.discoverylisteners.DeviceFileLogger;
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
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class IntegrationTestJuniperOlive {
    private SnmpWalker walker;
    private String[] discoveryTypes = new String[5];
    private DiscoveryHelper discoveryHelper;
    private Resource resource;
    private RawDeviceData rawdata = new RawDeviceData(null);
    private final String baseDir = (String) System.getProperties().get("user.dir");
    private DeviceFileLogger deviceLogger;
 //   private XmlTopologyDeviceLogger xmlTopologyDeviceLogger;


    @Before
    public void setUp() throws Exception {

        File IntegrationTestJuniperOlive = new File(baseDir + File.separator + "iDiscover/src/test/resources/test/IntegrationTestJuniperOlive");
        if (IntegrationTestJuniperOlive.exists()){
            try {
                FileUtils.deleteDirectory(new File(baseDir + File.separator + "iDiscover/src/test/resources/test/IntegrationTestJuniperOlive"));
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

        XmlDiscoveryHelperFactory discoveryHelperFactory = null;
        try {
            Map<String, String> params1 = new HashMap<String, String>();
            params1.put("fileName", new File(baseDir,"iDiscover/conf/xml/discoveryParameters.xml").getAbsolutePath());
            discoveryHelperFactory = new XmlDiscoveryHelperFactory(params1);
        } catch (JAXBException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        try {
            Map<String, String> params1 = new HashMap<String, String>();
            String baseDir = (String) System.getProperties().get("basedir");
            params1.put("fileName", new File(baseDir,"iDiscover/conf/xml/discoveryParameters.xml").getAbsolutePath());
            discoveryHelperFactory = new XmlDiscoveryHelperFactory(params1);
        } catch (JAXBException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        discoveryTypes[0] = DiscoveryTypes.ADDITIONAL;
        Map<String, String> resourceParams = new HashMap<String, String>();
        resourceParams.put("community", "netTransformer-r");
        resourceParams.put("community2", "netTransformer-rw");
        resourceParams.put("version", "1");
        resourceParams.put("mibDir", "snmptoolkit/mibs");
        resource = new Resource("R1", "10.17.1.13", resourceParams);
        resource.setDeviceType("JUNIPER");
        walker = (SnmpWalker) new DefaultDiscovererFactory().createDiscoverer(resource);
        discoveryHelper = discoveryHelperFactory.createDiscoveryHelper("JUNIPER");



        Map<String, String> params1 = new HashMap<String, String>();
        params1.put("path","iDiscover/src/test/resources/test");
        params1.put("device-data-logging-path","device-hierarchical");
        params1.put("raw-data-logging-path","raw-data");
        params1.put("device-centric-logging-path","device-centric");
        params1.put("network-centric-logging-path","undirected");
        params1.put("xslt","iDiscover/conf/xslt/transformator-undirected2.xslt");



        deviceLogger = new DeviceFileLogger(params1,new File(baseDir),"IntegrationTestJuniperOlive");
     //   xmlTopologyDeviceLogger = new XmlTopologyDeviceLogger(params1,new File(baseDir),"IntegrationTestJuniperOlive");



        FileInputStream is = new FileInputStream("iDiscover/src/test/resources/raw-data-juniper-olive.xml");
        byte[] data = new byte[is.available()];
        is.read(data);
        rawdata.setData(data);
    }
    @Test
    public void testDoTransform() throws TransformerException, IOException, SAXException, ParserConfigurationException {
        new NodeDiscoverer(){
            @Override
            public String probe(ConnectionDetails connectionDetails) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public NodeDiscoveryResult discover(ConnectionDetails connectionDetails) {



                NodeDiscoveryResult result = new NodeDiscoveryResult();
                //String devName = walker.getDeviceName(resource);
                result.setNodeId(resource.getHost());
                result.setDiscoveredData("rawData", rawdata.getData());
                DiscoveredDeviceData discoveredDeviceData = discoveryHelper.parseDeviceRawData(rawdata, discoveryTypes, resource);
                result.setDiscoveredData("deviceData", discoveredDeviceData);

                int discoveredInterfaceCounter = 0;

                int discoveredNeighboursCounter = 0;
                HashMap<String,Integer> neighbourTypeCounts = new HashMap<String, Integer>();

                List<ObjectType> objects = discoveredDeviceData.getObject();
                for (ObjectType object : objects) {

                    if(object.getObjectType().equals("Discovery Interface"))
                        discoveredInterfaceCounter++;

                    System.out.println(object.getName());

                    List<ObjectType> interfaceObjects = object.getObject();

                    for (ObjectType interfaceObject : interfaceObjects) {

                            System.out.println("\t"+interfaceObject.getName());
                            if(interfaceObject.getObjectType().equals("Discovered Neighbor")){

                                discoveredNeighboursCounter++;
                            }

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


                deviceLogger.handleDevice(result.getNodeId(),rawdata,discoveredDeviceData,resource);
               // xmlTopologyDeviceLogger.handleDevice(result.getNodeId(),rawdata,discoveredDeviceData,resource);

                ObjectType ipv6Interface = objects.get(13);
                List<ObjectType> interfaceObjects = ipv6Interface.getObject();
                ObjectType ipv6Address = interfaceObjects.get(1);
                String ipv6Prefix = ipv6Address.getName();

//                try {
//                     System.out.println("Deleting" + baseDir + File.separator + "iDiscover/src/test/resources/test/IntegrationTestJuniperOlive");

//                  //  FileUtils.deleteDirectory(new File(baseDir + File.separator + "iDiscover/src/test/resources/test/IntegrationTestJuniperOlive"));
//                } catch (IOException e) {
//                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//                }

                Assert.assertEquals(19, discoveredInterfaceCounter);

                Assert.assertEquals(1,discoveredNeighboursCounter);
                Assert.assertEquals(ipv6Prefix, "FE.80.0.0.0.0.0.0.A.0.27.FF.FE.C1.B7.3B/128");
                System.out.println(neighbourTypeCounts);

//                Assert.assertEquals((Object) 4,neighbourTypeCounts.get("CDP"));
//                Assert.assertEquals((Object) 4,neighbourTypeCounts.get("r_OSPF"));

                Assert.assertEquals((Object) 1,neighbourTypeCounts.get("ARP"));

                return result;
            }
        }.discover(null);
    }

}

