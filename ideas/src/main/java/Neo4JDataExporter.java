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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;


public class Neo4JDataExporter {


    private static String SERVER_ROOT_URI = "http://193.19.172.133:7474/db/data/";

    public static void main(String[] args) throws Exception {
        try {

//            URI firstNode = createNode();
//            addProperty(firstNode, "name", "Joe Strummer");
//            URI secondNode = createNode();
//            addProperty(secondNode, "band", "The Clash");
//
//            URI relationshipUri = addRelationship(firstNode, secondNode, "singer",
//                    "{ \"from\" : \"1976\", \"until\" : \"1986\" }");
//            addMetadataToProperty(relationshipUri, "stars", "5");

            String query = "start device=node:node_auto_index(objectType='Node') match device - [parent] -> interface --> neighbour where device.objectType='Node' and interface.objectType='Discovery Interface' and neighbour.objectType = 'Discovered Neighbor' return device.name, interface.name, neighbour.name";
            String params = "";
            String output = executeCypherQuery(query, params);
            JSONObject json = (JSONObject)new JSONParser().parse(output);
            System.out.println("columns=" + json.get("columns"));
            System.out.println("data=" + json.get("data"));
            HashMap map = new HashMap();

        } catch (Exception e) {

            e.printStackTrace();

        }
    }


    public static URI createNode() {
        final String nodeEntryPointUri = SERVER_ROOT_URI + "node";
// http://localhost:7474/db/data/node

        WebResource resource = Client.create()
                .resource(nodeEntryPointUri);
// POST {} to the node entry point URI
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .entity("{}")
                .post(ClientResponse.class);

        final URI location = response.getLocation();
        System.out.println(String.format(
                "POST to [%s], status code [%d], location header [%s]",
                nodeEntryPointUri, response.getStatus(), location.toString()));
        response.close();

        return location;
    }

    public static String toJsonCollection(String query, String params) {
        StringBuilder sb = new StringBuilder();
        sb.append("{ ");
        sb.append(" \"query\" : \"" + query + "\"");
        sb.append(", \"params\" :");
        sb.append("{ ");
        sb.append(params);
        sb.append(" }");
        sb.append("} ");
        return sb.toString();
    }

    public static String executeCypherQuery(String query, String params) {
        final String nodeEntryPointUri = SERVER_ROOT_URI + "cypher/";
// http://localhost:7474/db/data/cypher

        System.out.println(toJsonCollection(query, params));

        WebResource resource = Client.create()
                .resource(nodeEntryPointUri);
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .entity(toJsonCollection(query, params))
                .post(ClientResponse.class);
        if (response.getStatus() != 200) {
            throw new RuntimeException("Failed : HTTP error code : "+ response.getStatus());
        }
        String output = response.getEntity(String.class);
       // JSONObject json = (JSONObject)new JSONParser().parse(output);

        System.out.println("Output from Server .... \n");
        System.out.println(output);

        response.close();

        return output;
    }

    public static void addProperty(URI nodeUri, String propertyName, String propertyValue) {

        String propertyUri = nodeUri.toString() + "/properties/" + propertyName;
// http://localhost:7474/db/data/node/{node_id}/properties/{property_name}

        WebResource resource = Client.create()
                .resource(propertyUri);
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .entity("\"" + propertyValue + "\"")
                .put(ClientResponse.class);

        System.out.println(String.format("PUT to [%s], status code [%d]",
                propertyUri, response.getStatus()));
        response.close();
    }

    private static URI addRelationship(URI startNode, URI endNode,
                                       String relationshipType, String jsonAttributes)
            throws URISyntaxException {
        URI fromUri = new URI(startNode.toString() + "/relationships");
        String relationshipJson = generateJsonRelationship(endNode,
                relationshipType, jsonAttributes);

        WebResource resource = Client.create()
                .resource(fromUri);
        // POST JSON to the relationships URI
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .entity(relationshipJson)
                .post(ClientResponse.class);

        final URI location = response.getLocation();
        System.out.println(String.format(
                "POST to [%s], status code [%d], location header [%s]",
                fromUri, response.getStatus(), location.toString()));

        response.close();
        return location;
    }

    private static String generateJsonRelationship(URI endNode,
                                                   String relationshipType, String... jsonAttributes) {
        StringBuilder sb = new StringBuilder();
        sb.append("{ \"to\" : \"");
        sb.append(endNode.toString());
        sb.append("\", ");

        sb.append("\"type\" : \"");
        sb.append(relationshipType);
        if (jsonAttributes == null || jsonAttributes.length < 1) {
            sb.append("\"");
        } else {
            sb.append("\", \"data\" : ");
            for (int i = 0; i < jsonAttributes.length; i++) {
                sb.append(jsonAttributes[i]);
                if (i < jsonAttributes.length - 1) { // Miss off the final comma
                    sb.append(", ");
                }
            }
        }

        sb.append(" }");
        return sb.toString();
    }

    private static void addMetadataToProperty(URI relationshipUri,
                                              String name, String value) throws URISyntaxException {
        URI propertyUri = new URI(relationshipUri.toString() + "/properties");
        String entity = toJsonNameValuePairCollection(name, value);
        WebResource resource = Client.create()
                .resource(propertyUri);
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .entity(entity)
                .put(ClientResponse.class);

        System.out.println(String.format(
                "PUT [%s] to [%s], status code [%d]", entity, propertyUri,
                response.getStatus()));
        response.close();
    }

    private static String toJsonNameValuePairCollection(String name,
                                                        String value) {
        return String.format("{ \"%s\" : \"%s\" }", name, value);
    }

    private static void doTraversal(URI startNode)
            throws URISyntaxException {
        // START SNIPPET: traversalDesc
        // TraversalDescription turns into JSON to send to the Server
        TraversalDescription t = new TraversalDescription();
        t.setOrder(TraversalDescription.DEPTH_FIRST);
        t.setUniqueness(TraversalDescription.NODE);
        t.setMaxDepth(10);
        t.setReturnFilter(TraversalDescription.ALL);
        t.setRelationships(new Relationship("singer", Relationship.OUT));
        // END SNIPPET: traversalDesc

        // START SNIPPET: traverse
        URI traverserUri = new URI(startNode.toString() + "/traverse/node");
        WebResource resource = Client.create()
                .resource(traverserUri);
        String jsonTraverserPayload = t.toJson();
        ClientResponse response = resource.accept(MediaType.APPLICATION_JSON)
                .type(MediaType.APPLICATION_JSON)
                .entity(jsonTraverserPayload)
                .post(ClientResponse.class);

        System.out.println(String.format(
                "POST [%s] to [%s], status code [%d], returned data: "
                        + System.getProperty("line.separator") + "%s",
                jsonTraverserPayload, traverserUri, response.getStatus(),
                response.getEntity(String.class)));
        response.close();
        // END SNIPPET: traverse
    }
}