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

package net.itransformers.topologyviewer.gui;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.io.GraphMLMetadata;
import org.apache.commons.collections15.Factory;
import org.apache.commons.lang.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import edu.uci.ics.jung.algorithms.util.MapSettableTransformer;


import javax.ws.rs.core.MediaType;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Neo4jLoader<G extends Graph> {
    Factory<G> factory;
    private static String SERVER_ROOT_URI = "http://localhost:7474/db/data/";



    private G entireGraph;
    private HashMap<String, Map<String, GraphMLMetadata<String>>> graphMetadatas = new HashMap<String, Map<String, GraphMLMetadata<String>>>();
    private Map<String, Map<String, GraphMLMetadata<String>>> vertexMetadatas = new HashMap<String, Map<String, GraphMLMetadata<String>>>();

    public Map<String, Map<String, GraphMLMetadata<String>>> getEdgeMetadatas() {
        return edgeMetadatas;
    }

    public HashMap<String, Map<String, GraphMLMetadata<String>>> getGraphMetadatas() {
        return graphMetadatas;
    }

    private Map<String, Map<String, GraphMLMetadata<String>>> edgeMetadatas = new HashMap<String, Map<String, GraphMLMetadata<String>>>();

    public Map<String, Map<String, GraphMLMetadata<String>>> getVertexMetadatas() {
        return vertexMetadatas;
    }

    public Neo4jLoader( G entireGraph, Factory<G> factory,String SERVER_ROOT_URI) {
        this.entireGraph = entireGraph;
        this.factory = factory;
        this.SERVER_ROOT_URI = SERVER_ROOT_URI;

    }
    public void getNeighbourVertexes(String networkNodeId) {
        String query = "start network=node(" + networkNodeId + ") match network-->device-->interface-->neighbor where device.objectType='Device' and neighbor.objectType='DiscoveredNeighbor' return device.name, interface.name, neighbor.name, neighbor.Reachable?, neighbor.SNMPCommunity?,neighbor.DiscoveryMethod?,neighbor.LocalIPAddress?,neighbor.NeighborIPAddress?,neighbor.NeighborHostname?,neighbor.NeighborDeviceType?";
        String params = "";
        String output = executeCypherQuery(query, params);
        JSONObject json = null;
        try {
            json = (JSONObject) new JSONParser().parse(output);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JSONArray data = (JSONArray) json.get("data");
        JSONArray columns = (JSONArray) json.get("columns");

        HashMap<String, HashMap<String, String>> neighborMap = new HashMap<String, HashMap<String, String>>();

        Iterator dataIter = data.iterator();
        while (dataIter.hasNext()) {
            JSONArray temp = (JSONArray) dataIter.next();
            Iterator tempIter = temp.iterator();
            Iterator columnsIter = columns.iterator();
            String neighborName = null;
            String deviceName=null;
            String interfaceName=null;
            String edgeName = null;
            //        HashMap<String, GraphMLMetadata<String>> nodeMetaData = new HashMap<String, GraphMLMetadata<String>>();
            HashMap<String, GraphMLMetadata<String>> nodePropertiesMap = new HashMap<String, GraphMLMetadata<String>>();
            HashMap<String, GraphMLMetadata<String>> graphMLPropertiesMap = new HashMap<String, GraphMLMetadata<String>>();
            while (tempIter.hasNext() & columnsIter.hasNext()) {

                String column= columnsIter.next().toString();

                String keyNodeProperty = (String) tempIter.next();


                String columnsValue = null;
                if(column.contains("neighbor.")){
                    columnsValue = StringUtils.replace(StringUtils.substringAfter(column, "neighbor."), "%20", " ");
                } else{
                    columnsValue = column;
                }
                if(columnsValue.equals("device.name")){
                    deviceName=keyNodeProperty;

                }else  if(columnsValue.equals("interface.name")){
                      interfaceName=keyNodeProperty;
                }else if (columnsValue.equals("name")) {

                      neighborName = keyNodeProperty;
                      nodePropertiesMap.put(neighborName,null);

                }
                else {
                    if (keyNodeProperty==null){
                        keyNodeProperty = "";
                    }
                    else if (keyNodeProperty.equals("emptyValue")) {
                        keyNodeProperty = "";
                    }
                    HashMap<String,String> transformerValue = new HashMap<String,String>();
                    transformerValue.put(neighborName,keyNodeProperty);
                    MapSettableTransformer<String, String> transfrmer = new MapSettableTransformer<String, String>(transformerValue);

                    GraphMLMetadata<String> t = new GraphMLMetadata<String>(null,null,transfrmer);
                    nodePropertiesMap.put(columnsValue,t);

                }

            }
            String[] tempArray = {deviceName,neighborName};
            Arrays.sort(tempArray);
            StringBuilder builder = new  StringBuilder();

            for (int i = 0;i<tempArray.length;i++){
                 builder.append(tempArray[i]);
            }
            edgeName = builder.toString();
            Pair<String> endpoints = new Pair<String>(deviceName,neighborName);
            //Bogon filter
            if ((neighborName != null) && !entireGraph.containsVertex(neighborName) && (neighborName != "0.0.0.0") && !(StringUtils.contains(neighborName, "128.") || StringUtils.contains(neighborName, "127.") || StringUtils.contains(neighborName, "169.254.") || StringUtils.contains(neighborName, "0."))) {
                entireGraph.addVertex(neighborName);
                vertexMetadatas.put(neighborName,nodePropertiesMap);
                graphMetadatas.put(neighborName,graphMLPropertiesMap);

            }
            if(!entireGraph.containsEdge(edgeName)){

                entireGraph.addEdge(edgeName,endpoints);
            }

        }

    }


    public static String getLatestNetwork()  {
        String HighestNetworkID = null;
        String query = "start network=node:node_auto_index(name='network') return network";
        String params = "";
        String output = executeCypherQuery(query, params);
        JSONObject json = null;
        try {
            json = (JSONObject) new JSONParser().parse(output);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JSONArray jsonData = (JSONArray) json.get("data");

        JSONArray array = (JSONArray) jsonData.get(jsonData.size() - 1);

        JSONObject object1 = (JSONObject) array.get(array.size() - 1);
        String delims = "[/]";
        //Get the latest element of the URL e.g the node ID.
        String[] tokens = object1.get("self").toString().split(delims);
        HighestNetworkID = tokens[tokens.length - 1];
        return HighestNetworkID;

    }

    public void  getVertexes(String NetworkId) throws ParseException {
        String query = "start network=node(" + NetworkId + ") match network-->device where device.objectType='Device' return device.name, device.DeviceType,device.DeviceModel,device.ManagementIPAddress,device.YCoordinate,device.XCoordinate,device.siteID,device.ipv6Forwarding,device.BGPLocalASInfo,device.DeviceState";
        String params = "";
        String output = executeCypherQuery(query, params);
        JSONObject json = (JSONObject) new JSONParser().parse(output);
        JSONArray data = (JSONArray) json.get("data");
        JSONArray columns = (JSONArray) json.get("columns");
        Iterator dataIter = data.iterator();

        while (dataIter.hasNext()) {
            JSONArray temp = (JSONArray) dataIter.next();
            Iterator tempIter = temp.iterator();
            Iterator columnsIter = columns.iterator();
            String nodeName = null;
            HashMap<String, GraphMLMetadata<String>> nodePropertiesMap = new HashMap<String, GraphMLMetadata<String>>();
            HashMap<String, GraphMLMetadata<String>> graphMLPropertiesMap = new HashMap<String, GraphMLMetadata<String>>();

            while (tempIter.hasNext() & columnsIter.hasNext()) {

                String keyNodeProperty = tempIter.next().toString();
                String columnsValue = StringUtils.replace(StringUtils.substringAfter(columnsIter.next().toString(), "device."), "%20", " ");
                if (columnsValue.equals("name")) {

                    nodeName = keyNodeProperty;

                } else {
                    if (keyNodeProperty==null){
                        keyNodeProperty = "";
                    }
                    else if (keyNodeProperty.equals("emptyValue")) {
                        keyNodeProperty = "";
                    }
                    HashMap<String,String> transformerValue = new HashMap<String,String>();
                    transformerValue.put(nodeName,keyNodeProperty);
                    MapSettableTransformer<String, String> transfrmer = new MapSettableTransformer<String, String>(transformerValue);

                    GraphMLMetadata<String> t = new GraphMLMetadata<String>(null,null,transfrmer);
                    nodePropertiesMap.put(columnsValue,t);
                }

            }
            if (nodeName != null) {
                entireGraph.addVertex(nodeName);
                vertexMetadatas.put(nodeName, nodePropertiesMap);
                graphMetadatas.put(nodeName,graphMLPropertiesMap);
            }
        }


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
            throw new RuntimeException("Failed : HTTP error code : " + response.getStatus() + response.toString());
        }
        String output = response.getEntity(String.class);
        // JSONObject json = (JSONObject)new JSONParser().parse(output);

        System.out.println("Output from Server .... \n");
        System.out.println(output);

        response.close();

        return output;
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



    public void launch(){
        try {
            String networkNodeId = getLatestNetwork();
            System.out.println("NetworkId: " + networkNodeId);
            getVertexes(networkNodeId);
            //getNeighbourVertexes(networkNodeId);
            //  System.out.println(vertexes);
        } catch (Exception e) {

            e.printStackTrace();

        }
    }

//    public void loadGraphml(URL urlPath) throws ParserConfigurationException, SAXException, IOException {
////        File file = new File(dir + File.separator + "nodes-file-list.txt");
//        URL url2 = new URL(urlPath,"nodes-file-list.txt");
//        List<String> allFiles = readGraphmlFileNames(url2);//FileUtils.readLines(file);
//        List<String> files = allFiles.subList(loadedFiles.size(),allFiles.size());
//        for (String fileName : files){
//            if (fileName.startsWith("#")) continue;
//            boolean forUpdate = !loadedFiles.add(fileName);
//            final G graph = factory.create();
//            URL grahmlUrl = new URL(urlPath,fileName);
//            GraphMLReader gmlr = loadGraphmlInGraph(grahmlUrl, graph);
//            Collection<String> verteces = graph.getVertices();
//            for (String vertex :verteces){
//                entireGraph.addVertex(vertex);
//            }
//            Collection<String> edges = graph.getEdges();
//            for (String edge : edges){
//                Pair<String> endpoints = graph.getEndpoints(edge);
//                if (!entireGraph.containsEdge(edge)){
//                    entireGraph.addEdge(edge,endpoints);
//                }
//            }
//            graphMetadatas.put(fileName,gmlr.getGraphMetadata());
//            edgeMetadatas.put(fileName,gmlr.getEdgeMetadata());
//            vertexMetadatas.put(fileName,gmlr.getVertexMetadata());
//            notifyListeners(fileName, gmlr.getVertexMetadata(), gmlr.getEdgeMetadata(), graph);
//        }
//    }
//
//    static <G extends Graph<String,String>> GraphMLReader loadGraphmlInGraph(InputStream is, G graph) throws ParserConfigurationException, SAXException, IOException {
//        GraphMLReader gmlr = new GraphMLReader<G, String, String>(null, null);
//        BufferedReader in = new BufferedReader(new InputStreamReader(is));
//        gmlr.load(in, graph);
//        return gmlr;
//    }
//    static <G extends Graph<String,String>> GraphMLReader loadGraphmlInGraph(URL grahmlUrl, G graph) throws ParserConfigurationException, SAXException, IOException {
//        InputStream is = null;
//        try {
//            is = grahmlUrl.openStream();
//            return loadGraphmlInGraph(is,graph);
//        } catch (IOException e) {
//            System.out.println("Can not load graphml from url :"+grahmlUrl);
//            throw e;
//        } finally {
//            if (is != null) {
//                is.close();
//            }
//        }
//    }
//
//
//    public Map<String, Map<String, GraphMLMetadata<G>>> getGraphMetadatas() {
//        return graphMetadatas;
//    }
//
//    public Map<String, Map<String, GraphMLMetadata<String>>> getVertexMetadatas() {
//        return vertexMetadatas;
//    }
//
//    public Map<String, Map<String, GraphMLMetadata<String>>> getEdgeMetadatas() {
//        return edgeMetadatas;
//    }
//    public void addGraphmlLoaderListener(GraphmlLoaderListener listener){
//        this.listeners.add(listener);
//    }
//
//    private void notifyListeners(String fileName,
//                                 Map<String, GraphMLMetadata<String>> vertexMetadata,
//                                 Map<String, GraphMLMetadata<String>> edgeMetadata,
//                                 G graph){
//        for (GraphmlLoaderListener graphmlLoaderListener : listeners) {
//            graphmlLoaderListener.graphmlLoaded(fileName, vertexMetadata, edgeMetadata, graph);
//        }
//    }
}
