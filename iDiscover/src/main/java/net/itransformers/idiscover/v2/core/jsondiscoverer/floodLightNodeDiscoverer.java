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

//import com.sun.jersey.api.client.Client;
//import com.sun.jersey.api.client.ClientResponse;
//import com.sun.jersey.api.client.WebResource;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import net.itransformers.idiscover.v2.core.ANetworkDiscoverer;
import net.itransformers.idiscover.v2.core.NetworkDiscoveryResult;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.sf.json.JSONArray;
import net.sf.json.JSONSerializer;
import net.sf.json.xml.XMLSerializer;
import org.apache.log4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class floodLightNodeDiscoverer extends ANetworkDiscoverer {
    static Logger logger = Logger.getLogger(floodLightNodeDiscoverer.class);

    String switchesUrl =null;
    String devicesUrl =null;
    String linksUrl=null;
    File floodlightDeviceXmlXslt = null;
    File floodlightGraphmlXslt = null;


    public floodLightNodeDiscoverer(Map<String, String> attributes) throws Exception {

        switchesUrl=attributes.get("switches");
        devicesUrl=attributes.get("devices");
        linksUrl = attributes.get("links");

        floodlightDeviceXmlXslt = new File(System.getProperty("base.dir"),  attributes.get("floodlightDeviceXmlXslt"));
        floodlightGraphmlXslt = new File(System.getProperty("base.dir"),  attributes.get("floodlightGraphmlXslt"));
       // walker = (JsonDiscoverer) new DefaultDiscovererFactory().createDiscoverer(resource);
    }


//    public String probe(ConnectionDetails connectionDetails) {
//        final String URI = connectionDetails.getParam("protocol") +"://" + connectionDetails.getParam("ipAddress")  + ":" + connectionDetails.getParam("port")+"/wm/core/health/json";
//        WebResource resource = Client.create()
//                .resource(URI);
//        ClientResponse response = resource.accept("application/json")
//                .get(ClientResponse.class);
//
//        if (response.getStatus() != 200) {
//            logger.error("Failed : HTTP error code : " + response.getStatus());
//            throw new RuntimeException("Failed : HTTP error code : "
//                    + response.getStatus());
//
//        }
//        String output = response.getEntity(String.class);
//        response.close();
//        JSONObject json = (JSONObject) JSONSerializer.toJSON(output);
//        if (json!=null && "true".equals(json.get("healthy").toString())){
//            logger.debug("Controller with ip address"+ connectionDetails.getParam("ipAddress") +" is healthy!" );
//
//            return  connectionDetails.getParam("ipAddress");
//        } else{
//            logger.debug("Controller with ip address"+ connectionDetails.getParam("ipAddress") +" is not healthy!" );
//
//            return null;  //To change body of implemented methods use File | Settings | File Templates.
//
//        }
//
//    }

//    @Override
//    public NodeDiscoveryResult discover(ConnectionDetails connectionDetails) {
//    }

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
      //return null;
  }

    @Override
    public NetworkDiscoveryResult discoverNetwork(List<ConnectionDetails> connectionDetailsList, int depth) {
        NetworkDiscoveryResult result = new NetworkDiscoveryResult();



        for (ConnectionDetails connectionDetails : connectionDetailsList) {

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
            NodeDiscoveryResult result1= new NodeDiscoveryResult();
            result1.setDiscoveredData("rawData",sb.toString().getBytes());
            result1.setDiscoveredData("discoverer", "FloodLight");
            result.addDiscoveredData(connectionDetails.getParam("ipAddress"),result1);


            Map<String, ConnectionDetails> connectionDetailsMap = new HashMap<String,ConnectionDetails>();
            connectionDetailsMap.put(connectionDetails.getParam("ipAddress"),connectionDetails);
            result.setSourceConnectionDetails(connectionDetailsMap);
        }
        return result;

    }
}
