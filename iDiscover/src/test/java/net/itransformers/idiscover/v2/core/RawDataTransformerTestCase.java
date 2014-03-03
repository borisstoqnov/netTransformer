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


package net.itransformers.idiscover.v2.core;

import net.itransformers.idiscover.core.DiscoveryHelper;
import net.itransformers.idiscover.core.DiscoveryTypes;
import net.itransformers.idiscover.core.RawDeviceData;
import net.itransformers.idiscover.core.Resource;
import net.itransformers.idiscover.discoverers.DefaultDiscovererFactory;
import net.itransformers.idiscover.discoverers.SnmpWalker;
import net.itransformers.idiscover.discoveryhelpers.xml.XmlDiscoveryHelperFactory;
import net.itransformers.idiscover.networkmodel.DiscoveredDeviceData;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
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
import java.util.Map;

public class RawDataTransformerTestCase {
    private SnmpWalker walker;
    private String[] discoveryTypes = new String[5];
    private DiscoveryHelper discoveryHelper;
    private Resource resource;
    private RawDeviceData rawdata = new RawDeviceData(null);
    @Before
    public void setUp() throws Exception {
        XmlDiscoveryHelperFactory discoveryHelperFactory = null;
        try {
            Map<String, String> params1 = new HashMap<String, String>();
            String baseDir = (String) System.getProperties().get("basedir");
            params1.put("fileName", new File(baseDir,"resourceManager/conf/xml/resource.xml").getAbsolutePath());
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
        resource.setDeviceType("CISCO");
        walker = (SnmpWalker) new DefaultDiscovererFactory().createDiscoverer(resource);
        discoveryHelper = discoveryHelperFactory.createDiscoveryHelper("CISCO");

        FileInputStream is = new FileInputStream("src/test/resources/raw-data-Juniper.xml");
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
                return result;
            }
        }.discover(null);
        //Assert.assertEquals("a", "a");
    }

}

