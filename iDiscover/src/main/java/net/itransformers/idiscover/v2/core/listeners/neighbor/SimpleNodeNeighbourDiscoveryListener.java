package net.itransformers.idiscover.v2.core.listeners.neighbor;

import net.itransformers.idiscover.networkmodel.DiscoveredDeviceData;
import net.itransformers.idiscover.networkmodel.ParameterType;
import net.itransformers.idiscover.networkmodel.ParametersType;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.idiscover.v2.core.NodeNeighboursDiscoveryListener;
import net.itransformers.idiscover.v2.core.listeners.graphmlRenderer.GraphmlRenderer;
import net.itransformers.idiscover.v2.core.listeners.graphmlRenderer.model.GraphmlNode;
import net.itransformers.idiscover.v2.core.listeners.graphmlRenderer.model.GraphmlNodeData;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.idiscover.v2.core.model.Node;
import org.apache.log4j.Logger;

import java.util.*;

import java.util.List;

/**
 * Created by vasko on 16.06.16.
 */
public class SimpleNodeNeighbourDiscoveryListener implements NodeNeighboursDiscoveryListener {
    static Logger logger = Logger.getLogger(SimpleNodeNeighbourDiscoveryListener.class);


    @Override
    public void handleNodeNeighboursDiscovered(Node node, NodeDiscoveryResult nodeDiscoveryResult) {

        GraphmlRenderer graphmlRenderer = new GraphmlRenderer();

        DiscoveredDeviceData discoveredData = (DiscoveredDeviceData) nodeDiscoveryResult.getDiscoveredData().get("deviceData");
        Set<ConnectionDetails> connectionDetails = nodeDiscoveryResult.getNeighboursConnectionDetails();

        HashMap<String,Object> params = new HashMap<>();

        ArrayList<GraphmlNode> graphmlNodes = new ArrayList<>();

        graphmlNodes.add(setDiscoveredNodeParameters(discoveredData));

        graphmlNodes.addAll(setNeighbourNodeParameters(node.getNeighbours(),connectionDetails));


        params.put("nodes",graphmlNodes);
        params.put("graphDirection","undirected");



        try {
            String graphml = graphmlRenderer.render("iDiscover/conf/xml/snmpGraphmlTemplate.vm",params);
        } catch (Exception e) {
            e.printStackTrace();
        }


    public void handleNodeNeighboursDiscovered(Node node, NodeDiscoveryResult nodeDiscoveryResult, List<NodeDiscoveryResult> neighbourDiscoveryResults) {
        StringBuilder sb = new StringBuilder();


        sb.append("SimpleNodeNeighbourDiscoveryListener -> ");
        sb.append(String.format("nodeId=%s", node.getId()));
        sb.append(", neighbours=(");
        for (Node neighbour : node.getNeighbours()) {
            sb.append(neighbour.getId());
            sb.append(",");
        }
        sb.append(")");
        logger.info(sb.toString());

    }


    private GraphmlNode setDiscoveredNodeParameters(DiscoveredDeviceData discoveredData){
        GraphmlNode graphmlNode = new GraphmlNode(discoveredData.getName(),discoveredData.getName());
        List<GraphmlNodeData> graphmlNodeDataList = graphmlNode.getGraphmlNodeDataList();
        ParametersType parametersType = discoveredData.getParameters();
        List<ParameterType> parameterType = parametersType.getParameter();

        for (ParameterType param : parameterType) {
            String paramName = param.getName();
            String paramValue = param.getValue();
            GraphmlNodeData nodeData = new GraphmlNodeData(paramName, paramValue);

            graphmlNodeDataList.add(nodeData);
        }

        return graphmlNode;
    }

    private  List<GraphmlNode> setNeighbourNodeParameters(Set<Node> nodes, Set<ConnectionDetails> connectionDetailses) {

        List<GraphmlNode> graphmlNodes = new ArrayList<>();

        for (Node node : nodes) {
            node.getId();


            GraphmlNode graphmlNode = new GraphmlNode(node.getId(), node.getId());


            Set<Node> nodeNeighbours = node.getNeighbours();
            graphmlNodes.add(graphmlNode);
        }

//            if (connectionDetail.getConnectionType().equals("subnet")){
//                String subnetWithMask = params.get("ipAddress")+"/"+params.get("subnetMask");
//                List<GraphmlNodeData> graphmlNodeDataList = graphmlNode.getGraphmlNodeDataList();
//
//                for (Map.Entry<String,String> param : params.entrySet()) {
//                    GraphmlNodeData nodeData = new GraphmlNodeData(param.getKey(),param.getValue());
//                    graphmlNodeDataList.add(nodeData);
//
//                }
//            }

//            if (connectionDetail.getConnectionType().equals("snmp")){
//
//            }
//        }


        return graphmlNodes;

    }


}
