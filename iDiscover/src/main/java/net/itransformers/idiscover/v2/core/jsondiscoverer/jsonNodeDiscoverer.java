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

package net.itransformers.idiscover.v2.core.jsondiscoverer;/*
 * iTransformer is an open source tool able to discover IP networks
 * and to perform dynamic data data population into a xml based inventory system.
 * Copyright (C) 2010  http://itransformers.net
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import net.itransformers.idiscover.core.*;
import net.itransformers.idiscover.discoverers.DefaultDiscovererFactory;
import net.itransformers.idiscover.discoverers.SnmpWalker;
import net.itransformers.idiscover.discoveryhelpers.xml.XmlDiscoveryHelperFactory;
import net.itransformers.idiscover.networkmodel.DiscoveredDeviceData;
import net.itransformers.idiscover.v2.core.NodeDiscoverer;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.resourcemanager.config.ResourceType;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.ws.rs.core.MediaType;
import java.util.*;

public class jsonNodeDiscoverer implements NodeDiscoverer {
    static Logger logger = Logger.getLogger(jsonNodeDiscoverer.class);
    String switchesUrl =null;
    String devicesUrl =null;
    String linksUrl=null;
    public jsonNodeDiscoverer(Map<String, String> attributes) throws Exception {

        switchesUrl=attributes.get("switches");
        devicesUrl=attributes.get("devices");
        linksUrl=attributes.get("links");
       // walker = (JsonDiscoverer) new DefaultDiscovererFactory().createDiscoverer(resource);
    }

    @Override
    public NodeDiscoveryResult discover(ConnectionDetails connectionDetails) {
        final String EntryPointUri = connectionDetails.getParam("protocol") +"://" + connectionDetails.getParam("ipAddress")  + ":" + connectionDetails.getParam("port");
        getSwitchDetails(EntryPointUri);
        getLinkDetails(EntryPointUri);
        getDeviceDetails(EntryPointUri);
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private List<ConnectionDetails> getSwitchDetails(String nodeEntryPointUri ) {
        final String switchesURL=nodeEntryPointUri+switchesUrl;
        WebResource resource = Client.create()
                .resource(switchesURL);
        ClientResponse response = resource.accept("application/json")
                .get(ClientResponse.class);

        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }
        String output = response.getEntity(String.class);
        System.out.println("Output from Server .... \n");
        //System.out.println(output);
        response.close();
        try {
            JSONArray json = (JSONArray)new JSONParser().parse(output);
            for (int i=0; i<json.size();i++){
                JSONObject temp = (JSONObject) json.get(i);
                for (int j=0; j<temp.size();j++){
                    System.out.println(json.get(i).toString() +"\n");

                }


            }

            return null;
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        }
    }

    private List<ConnectionDetails> getLinkDetails(String nodeEntryPointUri ) {
        final String linksURL=nodeEntryPointUri+ linksUrl;

        WebResource resource = Client.create()
                .resource(linksURL);
        ClientResponse response = resource.accept("application/json")
                .get(ClientResponse.class);

        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }
        String output = response.getEntity(String.class);
        System.out.println("Output from Server .... \n");
        System.out.println(output);
        response.close();
        try {
            JSONArray json = (JSONArray)new JSONParser().parse(output);
            for (int i=0; i<json.size();i++){
                System.out.println(json.get(i).toString());
            }
            //    JSONArray ports = (JSONArray) json.get("ports");
            //    JSONObject iAddress= (JSONObject)json.get("inetAddress");

            return null;
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        }
    }
    private List<ConnectionDetails> getDeviceDetails(String nodeEntryPointUri ) {
        final String linksURL=nodeEntryPointUri+ devicesUrl;

        WebResource resource = Client.create()
                .resource(linksURL);
        ClientResponse response = resource.accept("application/json")
                .get(ClientResponse.class);

        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus());
        }
        String output = response.getEntity(String.class);
        System.out.println("Output from Server .... \n");
        System.out.println(output);
        response.close();
        try {
            JSONArray json = (JSONArray)new JSONParser().parse(output);
            for (int i=0; i<json.size();i++){
                System.out.println(json.get(i).toString());
            }
            //    JSONArray ports = (JSONArray) json.get("ports");
            //    JSONObject iAddress= (JSONObject)json.get("inetAddress");

            return null;
        } catch (ParseException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return null;
        }
    }
}
