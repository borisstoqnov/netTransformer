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

package net.itransformers.idiscover.discoverylisteners;

import net.itransformers.idiscover.core.*;
import net.itransformers.idiscover.networkmodel.DiscoveredDeviceData;
import net.itransformers.idiscover.networkmodel.ObjectType;
import net.itransformers.idiscover.networkmodel.ParameterType;
import net.itransformers.idiscover.networkmodel.ParametersType;
import net.itransformers.idiscover.util.JaxbMarshalar;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.*;

public class TopologyDeviceLogger implements DiscoveryListener{
    static Logger logger = Logger.getLogger(TopologyDeviceLogger.class);
    private String path;

    public TopologyDeviceLogger(String path) {
        this.path = path;
    }

    public void handleDevice(String deviceName, RawDeviceData rawData, DiscoveredDeviceData discoveredDeviceData, Resource resource) {
        StringBuffer buffer = new StringBuffer();
        String tabs = "\t";
        buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        buffer.append("<graphml>\n");
        buffer.append("<graph edgedefault=\"directed\">\n");
        buffer.append("<!-- data schema -->\n" +
                "<key id=\"deviceModel\" for=\"node\" attr.name=\"deviceModel\" attr.type=\"string\"/>\n" +
                "<key id=\"name\" for=\"edge\" attr.name=\"name\" attr.type=\"string\"/>\n" +
                "<key id=\"method\" for=\"edge\" attr.name=\"method\" attr.type=\"string\"/>\n");
        Map<String,Node> nodes = new HashMap<String, Node>();
        Map<String,Edge> edges = new HashMap<String,Edge>();
        StringBuffer nodesBuffer = new StringBuffer();
        StringBuffer edgesBuffer = new StringBuffer();
        edgesBuffer.append(String.format("%s<!-- edges -->\n",tabs));
        nodesBuffer.append(String.format("%s<!-- nodes -->\n",tabs));
        final String discoveredDeviceName = fillNodesAndEdges(discoveredDeviceData, resource, nodes, edges);

        for (Edge edge: edges.values()){
            edgesBuffer.append(String.format("%s<edge id=\"%s\" source=\"%s\" target=\"%s\">\n",
                    tabs, edge.id, discoveredDeviceName, edge.target));
            Set<String> methods = edge.params.get("method");
            StringBuffer sb = new StringBuffer();
            boolean first = true;
            for (String method : methods){
                if (!first) {
                    sb.append(",");
                } else {
                    first = false;
                }
                sb.append(method);
            }
            edgesBuffer.append(String.format("%s\t<data key=\"method\">%s</data>\n", tabs, sb.toString()));
            edgesBuffer.append(String.format("%s</edge>\n",tabs));

        }
        for (Node node : nodes.values()) {
            nodesBuffer.append(String.format("%s<node id=\"%s\">\n",tabs,node.id));
            if (node.params.get("deviceType") != null){
                final String deviceModel = node.params.get("deviceType").iterator().next();
                nodesBuffer.append(String.format("%s\t<data key=\"deviceModel\">%s</data>\n",tabs, deviceModel));
            }
            nodesBuffer.append(String.format("%s</node>\n",tabs));
        }
        buffer.append(nodesBuffer);
        buffer.append("\n");
        buffer.append(edgesBuffer);
        buffer.append("</graph>\n");
        buffer.append("</graphml>\n");
        System.out.println(buffer.toString());
        try {
            final File nodeFile = new File(System.getProperty("base.dir"),"node-" + deviceName + ".graphml");
            FileUtils.writeStringToFile(nodeFile, buffer.toString());
            FileWriter writer =new FileWriter(new File(System.getProperty("base.dir"),path + File.separator + "nodes-file-list.txt"),true);
            writer.append(String.valueOf(nodeFile)).append("\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String fillNodesAndEdges(DiscoveredDeviceData discoveredDeviceData, Resource resource, Map<String, Node> nodes, Map<String, Edge> edges) {
        final String discoveredDeviceName = discoveredDeviceData.getName();
        nodes.put(discoveredDeviceName, new Node(discoveredDeviceName));
        final ParametersType parameters = discoveredDeviceData.getParameters();
        for (ParameterType param1: parameters.getParameter()) {
            if (param1.getName().equals("deviceType")){
                Set<String> deviceType = new HashSet<String>();
                deviceType.add(param1.getValue());
                nodes.get(discoveredDeviceName).params.put("deviceType", deviceType);
            }
        }
        for (ObjectType obj : discoveredDeviceData.getObject()){
            if (obj.getObjectType().equals("Discovery Interface")){
                Map<String, List<String>> interfaceDetails = createInterfaceDetails(resource, obj);
                for (String neighbour : interfaceDetails.keySet()){
                    nodes.put(neighbour, new Node(neighbour));
                    final ParametersType parameters1 = discoveredDeviceData.getParameters();
                    for (ParameterType param1: parameters1.getParameter()) {
                        if (param1.getName().equals("deviceType")){
                            Set<String> deviceType = new HashSet<String>();
                            deviceType.add(param1.getValue());
                            nodes.get(discoveredDeviceName).params.put("deviceType", deviceType);
                        }
                    }

                    String edgeId = discoveredDeviceName + " " + neighbour;
//                    String edgeId = discoveredDeviceName.compareTo(neighbour) < 0 ?
//                            discoveredDeviceName + " " + neighbour : neighbour + " " +discoveredDeviceName;
                    if (!edges.containsKey(edgeId)){
                        Edge edge = new Edge(edgeId, discoveredDeviceName, neighbour);
                        edge.params.put("method", new HashSet<String>());
                        edges.put(edgeId, edge);
                    }
                    Edge edge = edges.get(edgeId);
                    Set<String> methods = (Set<String>) edge.params.get("method");
                    methods.addAll(interfaceDetails.get(neighbour));
                }
            }
        }
        return discoveredDeviceName;
    }

    private Map<String, List<String>> createInterfaceDetails(Resource resource, ObjectType obj) {
        Map<String, List<String>> interfaceDetails = new HashMap<String, List<String>>();
        for (ObjectType childObj : obj.getObject()){
            if (childObj.getObjectType().equals("Discovered Neighbor")){
                String neighbourName = null;
                ParametersType paramsType = childObj.getParameters();
                String neighbourHostName = null;
                String neighbourIPAddress = null;
                for (ParameterType param : paramsType.getParameter()){
                    if (param.getName().equals("Neighbor hostname")){
                        neighbourHostName = param.getValue();
                        if (!neighbourHostName.equals("")) {
                            neighbourName = neighbourHostName;
                        }
                        break;
                    }
                }
                if (neighbourName == null) {
                    for (ParameterType param : paramsType.getParameter()){
                        if (param.getName().equals("Neighbor IP Address")){
                            neighbourIPAddress = param.getValue();
                            if (!neighbourIPAddress.equals("")) {
                                neighbourName = neighbourIPAddress;
                            }
                            break;
                        }
                    }
                }
//                if (neighbourIPAddress != null) {
//                    if (resource != null){
//                        neighbourName =  discoverer.getDeviceName(new Resource(neighbourIPAddress+"/161",resource.getAttributes())); // TODO hardcoded port
//                    }
//                    if (neighbourName == null) {
////                                continue; // TODO display edge with unknown IP
//                          neighbourName = childObj.getName();
//                    }
//                } if (neighbourHostName == null && neighbourIPAddress == null) {
////                              neighbourName = childObj.getName();  // TODO search inventory for mac address and find neighbour details
//                        neighbourName = childObj.getName();
//                }


                List<String> methods = interfaceDetails.get(neighbourName);
                if (methods == null) {
                    methods = new ArrayList<String>();
                    interfaceDetails.put(neighbourName, methods);
                }
                paramsType = childObj.getParameters();
                for (ParameterType param : paramsType.getParameter()){
                    if (param.getName().equals("Discovery Method")){
                        String discoveryMethod = param.getValue();
                        methods.add(discoveryMethod);
                    }
                }
            }
        }
        return interfaceDetails;
    }
    static class Node {
        String id;
        Map<String,Set<String>> params = new HashMap<String,Set<String>>();

        public Node(String id) {
            this.id = id;
        }
    }
    static class Edge{
        String id;
        String source;
        String target;
        Map<String,Set<String>> params = new HashMap<String,Set<String>>();

        Edge(String id, String source, String target) {
            this.id = id;
            this.source = source;
            this.target = target;
        }
    }
    public static void main(String[] args) throws FileNotFoundException, JAXBException {
        String path = "tmp1";
        File dir = new File (path);
        String[] files = dir.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return (name.startsWith("device") && name.endsWith(".xml"));
            }
        });
        TopologyDeviceLogger logger = new TopologyDeviceLogger(path);
        String host = "10.33.0.5";
        String snmpROComm = "public";
        Map<String, String> resourceParams = new HashMap<String, String>();
        resourceParams.put("community", snmpROComm);
        resourceParams.put("version", "1");
        Resource resource = new Resource(host,null, resourceParams);

        for (String fileName: files){
            FileInputStream is = new FileInputStream(path + File.separator + fileName);
            DiscoveredDeviceData discoveredDeviceData = null;
            try {
                discoveredDeviceData = JaxbMarshalar.unmarshal(DiscoveredDeviceData.class, is);
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String deviceName = fileName.substring("device-".length(),fileName.length()-".xml".length());
            logger.handleDevice(deviceName, null, discoveredDeviceData, resource);
        }

    }

}
