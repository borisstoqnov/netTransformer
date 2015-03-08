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


package net.itransformers.idiscover.v2.core.discovererIntegrationTest.metroE;

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
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class IntegrationTestMetroR112 {
    private SnmpWalker walker;
    private String[] discoveryTypes = new String[5];
    private DiscoveryHelper discoveryHelper;
    private Resource resource;
    private RawDeviceData rawdata = new RawDeviceData(null);
    private DeviceFileLogger deviceLogger;
    private XmlTopologyDeviceLogger xmlTopologyDeviceLogger;
    private final String baseDir = (String) System.getProperties().get("user.dir");

    @Before
    public void setUp() throws Exception {


        File IntegrationTestCiscoASR1000 = new File(baseDir + File.separator + "iDiscover/src/test/resources/test/metroELab");
        if (IntegrationTestCiscoASR1000.exists()){
            try {
                FileUtils.deleteDirectory(new File(baseDir + File.separator + "iDiscover/src/test/resources/test/metroELab"));
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



            Map<String, String> params1 = new HashMap<String, String>();
            params1.put("path","iDiscover/src/test/resources/test");
            params1.put("device-data-logging-path","device-hierarchical");
            params1.put("raw-data-logging-path","raw-data");
            params1.put("device-centric-logging-path","device-centric");
            params1.put("network-centric-logging-path","undirected");
           // params1.put("xslt","iDiscover/conf/xslt/transformator-undirected2.xslt");



            deviceLogger = new DeviceFileLogger(params1,new File(baseDir),"raw-data-metroE-R-112");
        //    xmlTopologyDeviceLogger = new XmlTopologyDeviceLogger(params1,new File(baseDir),"CiscoASR1000Test");


        discoveryTypes[0] = DiscoveryTypes.ADDITIONAL;
        Map<String, String> resourceParams = new HashMap<String, String>();
        resourceParams.put("community", "netTransformer-r");
        resourceParams.put("version", "2c");
        resourceParams.put("retries", "1");
        resourceParams.put("timeout", "100");

        resourceParams.put("mibDir", "snmptoolkit/mibs");
        resource = new Resource("R-112", "10.17.1.13", resourceParams);
        resource.setDeviceType("CISCO");
        walker = (SnmpWalker) new DefaultDiscovererFactory().createDiscoverer(resource);
        discoveryHelper = discoveryHelperFactory.createDiscoveryHelper("CISCO");

        FileInputStream is = new FileInputStream("iDiscover/src/test/resources/raw-data-metroE/raw-data-R-112.xml");
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

                List<ObjectType> objects = discoveredDeviceData.getObject();
                int discoveredInterfaceCounter = 0;

                Map<String,Integer> neighbourTypeCounts = new HashMap<String, Integer>();

                for (ObjectType object : objects) {
                    if(object.getObjectType().equals("Discovery Interface")){
                        System.out.println(object.getName());

                        discoveredInterfaceCounter++;

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


                deviceLogger.handleDevice(result.getNodeId(),rawdata,discoveredDeviceData,resource);
               // xmlTopologyDeviceLogger.handleDevice(result.getNodeId(),rawdata,discoveredDeviceData,resource);

                System.out.println(neighbourTypeCounts);
                Assert.assertEquals(769,discoveredInterfaceCounter);

                Assert.assertEquals((Object) 53,neighbourTypeCounts.get("c_OSPF"));
                Assert.assertEquals((Object) 11,neighbourTypeCounts.get("CDP"));
                Assert.assertEquals((Object) 78,neighbourTypeCounts.get("Slash30"));




                return result;
            }
        }.discover(null);
    }

}

