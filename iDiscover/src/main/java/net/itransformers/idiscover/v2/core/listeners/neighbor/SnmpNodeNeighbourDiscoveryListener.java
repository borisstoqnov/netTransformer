package net.itransformers.idiscover.v2.core.listeners.neighbor;

import net.itransformers.idiscover.networkmodelv2.DiscoveredDevice;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.idiscover.v2.core.NodeNeighboursDiscoveryListener;
import net.itransformers.idiscover.v2.core.listeners.graphmlRenderer.GraphmlRenderer;
import net.itransformers.idiscover.v2.core.listeners.graphmlRenderer.model.*;
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


public class SnmpNodeNeighbourDiscoveryListener implements NodeNeighboursDiscoveryListener {
    static Logger logger = Logger.getLogger(SnmpNodeNeighbourDiscoveryListener.class);
    String labelDirName;
    String graphmlDirName;
    String projectPath;
    String velocityTemplate;
    Map<String, String> edgesTypes;
    Map<String, String> vertexTypes;
    public SnmpNodeNeighbourDiscoveryListener(){

    }

    public SnmpNodeNeighbourDiscoveryListener(String labelDirName, String graphmlDirName, String projectPath, String velocityTemplate) {
        this.labelDirName = labelDirName;
        this.graphmlDirName = graphmlDirName;
        this.projectPath = projectPath;
        this.velocityTemplate = velocityTemplate;
    }

    @Override
    public void handleNodeNeighboursDiscovered(Node node, NodeDiscoveryResult nodeDiscoveryResult, Map<String, ConnectionDetails> neighbourDiscoveredConnectionDetails) {

        File baseDir = new File(projectPath,labelDirName);
        File graphmlDir = new File(baseDir, graphmlDirName);
        if (!graphmlDir.exists()) graphmlDir.mkdir();


        GraphmlRenderer graphmlRenderer = new GraphmlRenderer();

        DiscoveredDevice discoveredDevice = (DiscoveredDevice) nodeDiscoveryResult.getDiscoveredData().get("DiscoveredDevice");
        getMainNode(discoveredDevice);
        HashMap<String,Object> params = new HashMap<>();
        GraphmlNode mainNode = getMainNode(discoveredDevice);

        ArrayList<GraphmlNode> graphmlNodes = getNeighbourNodes(node,neighbourDiscoveredConnectionDetails);
        graphmlNodes.add(mainNode);
////
        ArrayList<GraphmlEdge> graphmlEdges = getEdges(node, nodeDiscoveryResult, neighbourDiscoveredConnectionDetails);
////
//        graphmlEdges.addAll(graphmlEdges);



        params.put("nodes", graphmlNodes);
        params.put("graphDirection", "undirected");
        params.put("edges",graphmlEdges);

        String graphml = null;
        try {
            graphml = graphmlRenderer.render(velocityTemplate, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.trace(graphml);


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


    private GraphmlNode getMainNode(DiscoveredDevice discoveredDevice) {

        GraphmlNode graphmlNode = new GraphmlNode(discoveredDevice.getName(), discoveredDevice.getName());
        List<GraphmlNodeData> graphmlNodeMetaDatas = new ArrayList<>();

        for (Map.Entry<String, String> entry : discoveredDevice.getParams().entrySet()) {
            logger.trace("MainNodeParm: " + entry.getKey() + "|" + entry.getValue());

            graphmlNodeMetaDatas.add(new GraphmlNodeData(entry.getKey(), entry.getValue()));
            graphmlNode.setGraphmlNodeDataList(graphmlNodeMetaDatas);
        }
        return graphmlNode;
    }


    private  ArrayList<GraphmlEdge> getEdges(Node mainNode,NodeDiscoveryResult nodeDiscoveryResult, Map<String, ConnectionDetails> neighbourDiscoveredConnectionDetails) {

        ArrayList<GraphmlEdge> graphmlEdges = new ArrayList<>();
        String mainNodeId = mainNode.getId();
        Iterator<Node> nodeIterator = mainNode.getNeighbours().iterator();
        while (nodeIterator.hasNext()){

            Node node = nodeIterator.next();
            ConnectionDetails connectionDetails = neighbourDiscoveredConnectionDetails.get(node.getId());

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

    private ArrayList<GraphmlNode> getNeighbourNodes(Node node, Map<String, ConnectionDetails> neighbourDiscoveredConnectionDetails) {

        ArrayList<GraphmlNode> graphmlNodes = new ArrayList<>();
        Set<Node> nodes = node.getNeighbours();

        for (Node neighbourNode : nodes) {
            String nodeId = neighbourNode.getId();
            GraphmlNode graphmlNode = new GraphmlNode(nodeId, nodeId);
            List<GraphmlNodeData> graphmlNodeMetaDatas = new ArrayList<>();
            List<GraphmlPort> graphmlPorts = new ArrayList<>();

            ConnectionDetails connectionDetails = neighbourDiscoveredConnectionDetails.get(nodeId);

            if (connectionDetails.getConnectionType().equals("subnet")) {
                for (Map.Entry<String, String> entry : connectionDetails.getParams().entrySet()) {
                    GraphmlNodeData graphmlNodeData = new GraphmlNodeData(entry.getKey(), entry.getValue());
                    if (!entry.getKey().equals("discoveryMethods") && !entry.getKey().equals("port")){
                        graphmlNodeMetaDatas.add(graphmlNodeData);
                        continue;

                    }
                    if (entry.getKey().equals("port")){
                        String portName =  node.getId()+"-"+entry.getValue();
                        GraphmlPort port = new GraphmlPort(portName);
                        graphmlPorts.add(port);
                    }
                }
            }
            graphmlNode.setGraphmlNodePorts(graphmlPorts);
            graphmlNode.setGraphmlNodeDataList(graphmlNodeMetaDatas);
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
