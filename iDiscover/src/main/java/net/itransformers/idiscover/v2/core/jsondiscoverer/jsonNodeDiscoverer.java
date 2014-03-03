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

package net.itransformers.idiscover.v2.core.jsondiscoverer;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import net.itransformers.idiscover.v2.core.NodeDiscoverer;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.utils.XsltTransformer;
import net.sf.json.JSONArray;
import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class jsonNodeDiscoverer implements NodeDiscoverer {
    static Logger logger = Logger.getLogger(jsonNodeDiscoverer.class);
    String switchesUrl =null;
    String devicesUrl =null;
    String linksUrl=null;
    File floodlightDeviceXmlXslt = null;
    File floodlightGraphmlXslt = null;


    public jsonNodeDiscoverer(Map<String, String> attributes) throws Exception {

        switchesUrl=attributes.get("switches");
        devicesUrl=attributes.get("devices");
        linksUrl = attributes.get("links");

        floodlightDeviceXmlXslt = new File(System.getProperty("base.dir"),  attributes.get("floodlightDeviceXmlXslt"));
        floodlightGraphmlXslt = new File(System.getProperty("base.dir"),  attributes.get("floodlightGraphmlXslt"));
       // walker = (JsonDiscoverer) new DefaultDiscovererFactory().createDiscoverer(resource);
    }


    @Override
    public String probe(ConnectionDetails connectionDetails) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public NodeDiscoveryResult discover(ConnectionDetails connectionDetails) {
        NodeDiscoveryResult result = new NodeDiscoveryResult();
       //Assemble EntryUri
        final String EntryPointUri = connectionDetails.getParam("protocol") +"://" + connectionDetails.getParam("ipAddress")  + ":" + connectionDetails.getParam("port");
       //Query switches
        String xmlSwitches = serializeJsonToXml(getJsonDetails(EntryPointUri+switchesUrl),"switches");
        xmlSwitches = xmlSwitches.substring(xmlSwitches.indexOf('\n')+1);
       //Query links
        String xmlLinks =    serializeJsonToXml(getJsonDetails(EntryPointUri+linksUrl),"links");
        xmlLinks = xmlLinks.substring(xmlLinks.indexOf('\n')+1);
       //Query the rest of the devices
        String xmlDevices = serializeJsonToXml(getJsonDetails(EntryPointUri+devicesUrl),"devices");
        xmlDevices= xmlDevices.substring(xmlDevices.indexOf('\n')+1);
       //Assemble raw data
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" ?>\n");
        sb.append("<root>\n");
        sb.append(xmlSwitches);
        sb.append(xmlDevices);
        sb.append(xmlLinks);
        sb.append("</root>");

        logger.debug ("Raw Data \n" + sb.toString());
        result.setNodeId(connectionDetails.getParam("ipAddress"));
        result.setDiscoveredData("rawData",sb.toString().getBytes());
        return result;
    }

 private ByteArrayOutputStream xsltTranform(File xsltTransformator, String rawData, Map settings) throws TransformerException, IOException, SAXException, ParserConfigurationException {
     XsltTransformer transformer = new XsltTransformer();

     ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
     ByteArrayInputStream inputStream;
     inputStream = new ByteArrayInputStream(rawData.getBytes());
     transformer.transformXML(inputStream, xsltTransformator, outputStream1, settings, null);
     return outputStream1;

 }
 private String serializeJsonToXml(JSONArray json, String rootElement){
     XMLSerializer serializer = new XMLSerializer();
     serializer.setForceTopLevelObject(true);
     serializer.setTypeHintsEnabled(false);
     serializer.setRootName(rootElement);
     return serializer.write(json);
 }
  private JSONArray getJsonDetails(String url) {
        WebResource resource = Client.create()
                .resource(url);
        ClientResponse response = resource.accept("application/json")
                .get(ClientResponse.class);

        if (response.getStatus() != 200) {
            logger.error("Failed : HTTP error code : " + response.getStatus());
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());

        }
        String output = response.getEntity(String.class);
        response.close();
        JSONArray json = (JSONArray) JSONSerializer.toJSON(output);
      return  json;
  }
}
