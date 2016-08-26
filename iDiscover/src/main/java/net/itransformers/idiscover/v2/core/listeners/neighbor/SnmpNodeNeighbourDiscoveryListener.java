package net.itransformers.idiscover.v2.core.listeners.neighbor;

import net.itransformers.idiscover.networkmodelv2.DiscoveredDevice;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.idiscover.v2.core.NodeNeighboursDiscoveryListener;
import net.itransformers.idiscover.v2.core.listeners.graphmlRenderer.GraphmlRenderer;
import net.itransformers.idiscover.v2.core.listeners.graphmlRenderer.model.GraphmlEdge;
import net.itransformers.idiscover.v2.core.listeners.graphmlRenderer.model.GraphmlEdgeData;
import net.itransformers.idiscover.v2.core.listeners.graphmlRenderer.model.GraphmlNode;
import net.itransformers.idiscover.v2.core.listeners.graphmlRenderer.model.GraphmlNodeData;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.idiscover.v2.core.model.Node;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by vasko on 16.06.16.
 */


//<key id="deviceModel" for="node" attr.name="deviceModel" attr.type="string" />
//        <key id="deviceStatus" for="node" attr.name="deviceStatus" attr.type="string" />
//        <key id="deviceType" for="node" attr.name="deviceType" attr.type="string" />
//        <key id="diff" for="node" attr.name="diff" attr.type="string" />
//        <key id="diffs" for="node" attr.name="diffs" attr.type="string" />
//        <key id="discoveredIPv4Address" for="node" attr.name="discoveredIPv4Address" attr.type="string" />
//        <key id="geoCoordinates" for="node" attr.name="geoCoordinates" attr.type="string" />
//        <key id="hostname" for="node" attr.name="hostname" attr.type="string" />
//        <key id="ipProtocolType" for="node" attr.name="ipProtocolType" attr.type="string" />
//        <key id="ipv4Forwarding" for="node" attr.name="ipv4Forwarding" attr.type="string" />
//        <key id="ipv6Forwarding" for="node" attr.name="ipv6Forwarding" attr.type="string" />
//        <key id="nodeInfo" for="node" attr.name="nodeInfo" attr.type="string" />
//        <key id="site" for="node" attr.name="site" attr.type="string" />
//        <key id="subnetPrefix" for="node" attr.name="subnetPrefix" attr.type="string" />
//        <key id="subnetRangeType" for="node" attr.name="subnetRangeType" attr.type="string" />
//        <key id="totalInterfaceCount" for="node" attr.name="totalInterfaceCount" attr.type="string" />
//        <key id="Discovery Method" for="edge" attr.name="Discovery Method" attr.type="string" />
//        <key id="Interface" for="edge" attr.name="Interface" attr.type="string" />
//        <key id="Neighbor Device Type" for="edge" attr.name="Neighbor Device Type" attr.type="string" />
//        <key id="Neighbor IP Address" for="edge" attr.name="Neighbor IP Address" attr.type="string" />
//        <key id="diff" for="edge" attr.name="diff" attr.type="string" />
//        <key id="diffs" for="edge" attr.name="diffs" attr.type="string" />
//        <key id="name" for="edge" attr.name="name" attr.type="string" />

public class SnmpNodeNeighbourDiscoveryListener implements NodeNeighboursDiscoveryListener {
    static Logger logger = Logger.getLogger(SnmpNodeNeighbourDiscoveryListener.class);
    String labelDirName;
    String graphmlDirName;
    String projectPath;
    String velocityTemplate;

    public SnmpNodeNeighbourDiscoveryListener(){

    }

    public SnmpNodeNeighbourDiscoveryListener(String labelDirName, String graphmlDirName, String projectPath, String velocityTemplate) {
        this.labelDirName = labelDirName;
        this.graphmlDirName = graphmlDirName;
        this.projectPath = projectPath;
        this.velocityTemplate = velocityTemplate;
    }

    @Override

    public void handleNodeNeighboursDiscovered(Node node, NodeDiscoveryResult nodeDiscoveryResult) {

        File baseDir = new File(projectPath,labelDirName);
        File graphmlDir = new File(baseDir, graphmlDirName);
        if (!graphmlDir.exists()) graphmlDir.mkdir();




        GraphmlRenderer graphmlRenderer = new GraphmlRenderer();

//        DiscoveredDevice discoveredDevice = (DiscoveredDevice) nodeDiscoveryResult.getDiscoveredData().get("DiscoveredDevice");

        HashMap<String, Object> params = new HashMap<>();

        ArrayList<GraphmlNode> graphmlNodes = getNeighbourNodes(node.getNeighbours(), nodeDiscoveryResult.getNeighboursConnectionDetails());

        ArrayList<GraphmlEdge> graphmlEdges = setEdges(node,node.getNeighbours(),nodeDiscoveryResult.getNeighboursConnectionDetails())

        graphmlEdges.addAll(graphmlEdges);



        params.put("nodes", graphmlNodes);
        params.put("graphDirection", "undirected");
        params.put("edges",graphmlEdges);

        String graphml = null;
        try {
            graphml = graphmlRenderer.render(velocityTemplate, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(graphml);


        final String fileName = "node-" + node.getId() + ".graphml";
        final File nodeFile = new File(graphmlDir,fileName);


        try {
            FileUtils.writeStringToFile(nodeFile, graphml);

        File undirectedGraphmls = new File(graphmlDir.getParent(),"undirected"+".graphmls");
        if (!undirectedGraphmls.exists()){

                undirectedGraphmls.createNewFile();

        }
        FileWriter writer = null;

            writer = new FileWriter(undirectedGraphmls,true);


            writer.append(String.valueOf(fileName)).append("\n");

            writer.close();
        } catch (IOException e) {
            System.out.printf(e.getMessage());
            logger.error(e.getMessage());
        }


    }


    private GraphmlNode setDiscoveredNodeParameters(Node node,DiscoveredDevice discoveredDevice){

        GraphmlNode graphmlNode = new GraphmlNode(node.getId(),node.getId());
        List<GraphmlNodeData> graphmlNodeDataList = graphmlNode.getGraphmlNodeDataList();
        HashMap<String,String> parameters = discoveredDevice.getParams();
        for (Map.Entry<String,String> entry    : parameters.entrySet()) {
            String paramName = entry.getKey();
            String paramValue = entry.getValue();
            GraphmlNodeData nodeData = new GraphmlNodeData(paramName, paramValue);
            graphmlNodeDataList.add(nodeData);
        }

        return graphmlNode;
    }

    private  List<GraphmlEdge> setEdges(Node mainNode,Set<Node> nodes, Set<ConnectionDetails> neighbourConnectionDetails) {
        List<GraphmlEdge> graphmlEdges = new ArrayList<>();

        String mainNodeId = mainNode.getId();
        Iterator<Node> nodeIterator = nodes.iterator();

        Iterator<ConnectionDetails> connectionDetailsIterator = neighbourConnectionDetails.iterator();

        while (nodeIterator.hasNext() && connectionDetailsIterator.hasNext()){

            Node node = nodeIterator.next();
            ConnectionDetails connectionDetails = connectionDetailsIterator.next();
            String nodeId = node.getId();
            String graphmlEdgeId;
            String from;
            String to;
            int comparisson = nodeId.compareToIgnoreCase(mainNodeId);
            if (comparisson <0){
               graphmlEdgeId =   nodeId+"-"+mainNodeId;
               from=nodeId;
               to=mainNodeId;
            }else{
                graphmlEdgeId =   mainNodeId+"-"+nodeId;
                from=mainNodeId;
                to=nodeId;

            }
            GraphmlEdge edge = new GraphmlEdge(graphmlEdgeId,graphmlEdgeId,from,to);
            List<GraphmlEdgeData> graphmlEdgeMetaDatas = new ArrayList<>();

            Map<String,String> connectionDetailsParams = connectionDetails.getParams();
            for (Map.Entry<String,String> entry : connectionDetailsParams.entrySet()) {
                if (entry.getKey().equals("DiscoveryMethod")){
                    GraphmlEdgeData graphmlEdgeData = new GraphmlEdgeData(entry.getKey(),entry.getValue());
                    graphmlEdgeMetaDatas.add(graphmlEdgeData);
                }

            }
            edge.setGraphmlEdgeDataList(graphmlEdgeMetaDatas);
            graphmlEdges.add(edge);
        }
        return graphmlEdges;

    }

    //<key id="deviceModel" for="node" attr.name="deviceModel" attr.type="string" />
//        <key id="deviceStatus" for="node" attr.name="deviceStatus" attr.type="string" />
//        <key id="deviceType" for="node" attr.name="deviceType" attr.type="string" />
//        <key id="diff" for="node" attr.name="diff" attr.type="string" />
//        <key id="diffs" for="node" attr.name="diffs" attr.type="string" />
//        <key id="discoveredIPv4Address" for="node" attr.name="discoveredIPv4Address" attr.type="string" />
//        <key id="geoCoordinates" for="node" attr.name="geoCoordinates" attr.type="string" />
//        <key id="hostname" for="node" attr.name="hostname" attr.type="string" />
//        <key id="ipProtocolType" for="node" attr.name="ipProtocolType" attr.type="string" />
//        <key id="ipv4Forwarding" for="node" attr.name="ipv4Forwarding" attr.type="string" />
//        <key id="ipv6Forwarding" for="node" attr.name="ipv6Forwarding" attr.type="string" />
//        <key id="nodeInfo" for="node" attr.name="nodeInfo" attr.type="string" />
//        <key id="site" for="node" attr.name="site" attr.type="string" />
//        <key id="subnetPrefix" for="node" attr.name="subnetPrefix" attr.type="string" />
//        <key id="subnetRangeType" for="node" attr.name="subnetRangeType" attr.type="string" />
//        <key id="totalInterfaceCount" for="node" attr.name="totalInterfaceCount" attr.type="string" />
//        <key id="Discovery Method" for="edge" attr.name="Discovery Method" attr.type="string" />
//        <key id="Interface" for="edge" attr.name="Interface" attr.type="string" />
//        <key id="Neighbor Device Type" for="edge" attr.name="Neighbor Device Type" attr.type="string" />
//        <key id="Neighbor IP Address" for="edge" attr.name="Neighbor IP Address" attr.type="string" />
//        <key id="diff" for="edge" attr.name="diff" attr.type="string" />
//        <key id="diffs" for="edge" attr.name="diffs" attr.type="string" />
//        <key id="name" for="edge" attr.name="name" attr.type="string" />
//    private List<GraphmlNode> setDiscoveredDeviceSubnets(List<Subnet> subnets){
//        List<GraphmlNode> graphmlNodes = new ArrayList<>();
//        for (Subnet subnet : subnets) {
//            GraphmlNode graphmlNode = new GraphmlNode();
//            graphmlNode.setId(subnet.getName());
//            graphmlNode.setLabel(subnet.getName());
//            List<GraphmlNodeData> graphmlNodeMetaDatas = new ArrayList<>();
//            graphmlNode.setGraphmlNodeDataList(graphmlNodeMetaDatas);
//            graphmlNodes.add(graphmlNode);
//        }
//        return graphmlNodes;
//
//    }

    private ArrayList<GraphmlNode> getNeighbourNodes(Set<Node> nodes, Set<ConnectionDetails> neighboursConnectionDetails) {

        List<GraphmlNode> graphmlNodes = new ArrayList<>();

        Iterator<Node> nodeIterator = nodes.iterator();

        Iterator<ConnectionDetails> connectionDetailsIterator = neighboursConnectionDetails.iterator();
        while (nodeIterator.hasNext() && connectionDetailsIterator.hasNext()) {
        }

            for (Node node : nodes) {
           String nodeId= node.getId();
            GraphmlNode graphmlNode = new GraphmlNode(nodeId, nodeId);
            List<GraphmlNodeData> graphmlNodeMetaDatas = new ArrayList<>();

            boolean doNotAdd=false;

                for (ConnectionDetails connectionDetails : neighboursConnectionDetails) {
                    String neighbourConnectionType = connectionDetails.getConnectionType();

                    if (neighbourConnectionType.equals("subnet")){

                    }

//                    if (neighbourNodeId.equals(nodeId)) {
//                        ConnectionDetails connectionDetails = nodeNeighbourDiscoveryResult.getDiscoveryConnectionDetails();
//                        if (connectionDetails.getConnectionType().equals("subnet")) {
//                            GraphmlNodeData graphmlNodeData = new GraphmlNodeData("nodeType", "subnet");
//                            graphmlNodeMetaDatas.add(graphmlNodeData);
//
//                            Map<String, Object> discoveredData = nodeNeighbourDiscoveryResult.getDiscoveredData();
//                            String subnetIP = nodeId;
//                            graphmlNode.setId(subnetIP);
//                            for (Map.Entry<String, Object> entry : discoveredData.entrySet()) {
//                                if (entry.getKey().equals("bogon") && entry.getValue().equals("true")){
//                                    doNotAdd=true;
//                                }
//                                GraphmlNodeData graphmlNodeData1 = new GraphmlNodeData(entry.getKey(), entry.getValue().toString());
//                                graphmlNodeMetaDatas.add(graphmlNodeData1);
//                            }
//                        } else {
//
//                            System.out.println("Adding an empty node "+neighbourNodeId);
//                            //Do nothing we will just add an empty node!!!
//                        }
//                        graphmlNode.setGraphmlNodeDataList(graphmlNodeMetaDatas);
//                        break;
//                    }

                }

            if (!doNotAdd)
                graphmlNodes.add(graphmlNode);
        }
        return graphmlNodes;

    }

    public String getLabelDirName() {
        return labelDirName;
    }

    public void setLabelDirName(String labelDirName) {
        this.labelDirName = labelDirName;
    }

    public String getGraphmlDirName() {
        return graphmlDirName;
    }

    public void setGraphmlDirName(String graphmlDirName) {
        this.graphmlDirName = graphmlDirName;
    }

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public String getVelocityTemplate() {
        return velocityTemplate;
    }

    public void setVelocityTemplate(String velocityTemplate) {
        this.velocityTemplate = velocityTemplate;
    }



}
