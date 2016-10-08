package net.itransformers.idiscover.v2.core.listeners.neighbor;

import net.itransformers.idiscover.networkmodelv2.DiscoveredDevice;
import net.itransformers.idiscover.api.NodeDiscoveryResult;
import net.itransformers.idiscover.api.NodeNeighboursDiscoveryListener;
import net.itransformers.idiscover.v2.core.listeners.graphmlRenderer.GraphmlRenderer;
import net.itransformers.idiscover.api.models.graphml.GraphmlEdge;
import net.itransformers.idiscover.api.models.graphml.GraphmlNode;
import net.itransformers.idiscover.api.models.graphml.GraphmlNodeData;
import net.itransformers.idiscover.v2.core.listeners.neighbor.device.DeviceToGraphml;
import net.itransformers.idiscover.api.models.network.Node;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vasko on 16.06.16.
 */


public class SnmpNodeNeighbourDiscoveryListener implements NodeNeighboursDiscoveryListener {
    static Logger logger = Logger.getLogger(SnmpNodeNeighbourDiscoveryListener.class);
    String labelDirName;
    String graphmlDirName;
    String projectPath;
    String velocityTemplate;
    GraphmlRenderer graphmlRenderer;


    public SnmpNodeNeighbourDiscoveryListener(){
          graphmlRenderer = new GraphmlRenderer();
    }

    public SnmpNodeNeighbourDiscoveryListener(String labelDirName, String graphmlDirName, String projectPath, String velocityTemplate) {
        this.labelDirName = labelDirName;
        this.graphmlDirName = graphmlDirName;
        this.projectPath = projectPath;
        this.velocityTemplate = velocityTemplate;
        this.graphmlRenderer = new GraphmlRenderer();
    }

    @Override
    public void handleNodeNeighboursDiscovered(Node node, NodeDiscoveryResult nodeDiscoveryResult) {

        File baseDir = new File(projectPath,labelDirName);
        File graphmlDir = new File(baseDir, graphmlDirName);
        if (!graphmlDir.exists()) graphmlDir.mkdir();

        String nodeFileName = node.getId();

        String discoveredIPv4Address = (String) nodeDiscoveryResult.getDiscoveredData().get("discoveredIPv4Address");


        DiscoveredDevice discoveredDevice = (DiscoveredDevice) nodeDiscoveryResult.getDiscoveredData().get("DiscoveredDevice");

        Map<String, String> subnetDetails = (Map<String, String>) nodeDiscoveryResult.getDiscoveredData().get("subnetDetails");

        String icmpStatus = (String) nodeDiscoveryResult.getDiscoveredData().get("icmpStatus");




        String dnsFQDN =   (String) nodeDiscoveryResult.getDiscoveredData().get("FQDN");
        String dnsPQDN =   (String) nodeDiscoveryResult.getDiscoveredData().get("PQDN");



        HashMap<String, Object> params = new HashMap<>();
        ArrayList<GraphmlNode> graphmlNodes = new ArrayList<>();
        List<GraphmlEdge> graphmlEdges = new ArrayList<>();
        GraphmlNode mainNode = new GraphmlNode(node.getId(), node.getId());
        List<GraphmlNodeData> mainNodeGraphmlDatas =  new ArrayList<>();




        if(icmpStatus!=null){
            //We got an icmpDevice

            GraphmlNodeData icmpNodeData = new GraphmlNodeData("icmpStatus",icmpStatus);
            mainNodeGraphmlDatas.add(icmpNodeData);
            GraphmlNodeData discoveredIPv4AddressNodeData = new GraphmlNodeData("discoveredIPv4Address",discoveredIPv4Address);
            mainNodeGraphmlDatas.add(discoveredIPv4AddressNodeData);

        }

        if (discoveredDevice!=null) {

            //We got an Snmp Device
            GraphmlNodeData snmpStatus = new GraphmlNodeData("snmpStatus","REACHABLE");
            GraphmlNodeData discoveredIPv4AddressNodeData = new GraphmlNodeData("discoveredIPv4Address",discoveredIPv4Address);
            mainNodeGraphmlDatas.add(discoveredIPv4AddressNodeData);
            mainNodeGraphmlDatas.add(snmpStatus);
            List<GraphmlNodeData> snmpNodeData = getSnmpMainNode(discoveredDevice);
            mainNodeGraphmlDatas.addAll(snmpNodeData);
            DeviceToGraphml deviceToGraphml = new DeviceToGraphml(node, discoveredDevice);
            graphmlNodes.addAll(deviceToGraphml.getSubnetNodes());
            graphmlNodes.addAll(deviceToGraphml.getNonSubnetNeighbours());
            graphmlEdges = deviceToGraphml.getSubnetEdgesToMainNode();
            graphmlEdges.addAll(deviceToGraphml.getEdgesToNeighbours());


        }
        if (subnetDetails!=null){

            List<GraphmlNodeData> subnetNodeData = getSubnetMainNode(subnetDetails);
            String bogonSubnetMarker =  (String) nodeDiscoveryResult.getDiscoveredData().get("bogon");
            String privateSubnetMarker = (String) nodeDiscoveryResult.getDiscoveredData().get("private");

//            System.out.println("BOGON: "+bogonSubnetMarker.toString());
//            System.out.println("PRIVATE: "+privateSubnetMarker.toString());

            if (bogonSubnetMarker!=null && bogonSubnetMarker.equals("YES"))
                subnetNodeData.add(new GraphmlNodeData("bogon","YES"));
            else
                subnetNodeData.add(new GraphmlNodeData("bogon","NO"));

            if (privateSubnetMarker!=null && privateSubnetMarker.equals("YES"))
                subnetNodeData.add(new GraphmlNodeData("private","YES"));
            else
                subnetNodeData.add(new GraphmlNodeData("private", "NO"));


            mainNodeGraphmlDatas.addAll(subnetNodeData);


//            List<GraphmlNode> subnetNeighbourNodes = new ArrayList<>();
//
//            List<GraphmlEdge> subnetNeighbourEdges = new ArrayList<>();

//            for (Node subnetNeighbour : node.getNeighbours()) {
//                GraphmlNode subnetNeighbourNode = new GraphmlNode(subnetNeighbour.getId(),subnetNeighbour.getId());
//              //  subnetNeighbourNodes.add(subnetNeighbourNode);
//                //TODO this edge has to be with id from the subnet ipAddress and the neighbour ip address in that subnet
//
//
//                EdgeIdGenerator edgeIdGenerator = new EdgeIdGenerator(subnetNeighbour.getId(),node.getId(),subnetNeighbour.getId(),node.getId());
//
//                GraphmlEdge subnetNeighbourEdge = edgeIdGenerator.createEdge();
//                subnetNeighbourEdges.add(subnetNeighbourEdge);
//            }

//            graphmlNodes.addAll(subnetNeighbourNodes);
//            graphmlEdges.addAll(subnetNeighbourEdges);
            String subnetIpAddress = subnetDetails.get("ipAddress");
            String subnetPrefixMask = subnetDetails.get("subnetPrefixMask");

            nodeFileName = subnetIpAddress+"--"+subnetPrefixMask;



        }



        if (dnsFQDN!=null){
            GraphmlNodeData fqdn =   new GraphmlNodeData("fqdn",dnsFQDN);
            mainNodeGraphmlDatas.add(fqdn);
            GraphmlNodeData pqdn =   new GraphmlNodeData("pqdn",dnsPQDN);
            mainNodeGraphmlDatas.add(pqdn);
        }

        if (icmpStatus==null && dnsFQDN==null && discoveredDevice ==null && subnetDetails==null)
            return;

        mainNode.setGraphmlNodeDataList(mainNodeGraphmlDatas);
        graphmlNodes.add(mainNode);
        params.put("nodes", graphmlNodes);
        params.put("graphDirection", "undirected");
        params.put("edges", graphmlEdges);

        try {
            String projectName = new File(projectPath).getName();
            if (projectName.equals("."))
                projectName = new File(new File(projectPath).getParent()).getName();
            params.put("project",projectName);
            params.put("version",baseDir.getCanonicalFile().getName());
        } catch (IOException e) {
            logger.error(e);
        }

        String graphml = null;
        try {
            logger.info("Starting to render graphml for node "+ node.getId());
            graphml = graphmlRenderer.render(velocityTemplate, params);
            logger.info("Finishing to render graphml for node " + node.getId());

        } catch (Exception e) {

            logger.trace(e.getMessage());
        }
        logger.trace(graphml);



        final String fileName = "node-" + nodeFileName+ ".graphml";
        final File nodeFile = new File(graphmlDir,fileName);


        try {
            FileUtils.writeStringToFile(nodeFile, graphml);

            File undirectedGraphmls = new File(graphmlDir.getParent(),"undirected"+".graphmls");
            if (!undirectedGraphmls.exists()){

                undirectedGraphmls.createNewFile();

            }
            FileWriter writer = new FileWriter(undirectedGraphmls,true);


            writer.append(String.valueOf(fileName)).append("\n");

            writer.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private List<GraphmlNodeData> getSubnetMainNode(Map<String,String> subnetParams) {
        List<GraphmlNodeData> graphmlNodeData = new ArrayList<>();


        String procotocolType = subnetParams.get("protocolType");
        GraphmlNodeData ipv4ForwardingNodeData;
        GraphmlNodeData ipv6ForwardingNodeData;
        if (procotocolType.equals("IPv4")) {
            ipv4ForwardingNodeData = new GraphmlNodeData("ipv4Forwarding", "YES");
            ipv6ForwardingNodeData = new GraphmlNodeData("ipv6Forwarding", "NO");

        }else {
             ipv4ForwardingNodeData = new GraphmlNodeData("ipv4Forwarding", "NO");
             ipv6ForwardingNodeData = new GraphmlNodeData("ipv6Forwarding", "YES");
        }
        graphmlNodeData.add(ipv4ForwardingNodeData);
        graphmlNodeData.add(ipv6ForwardingNodeData);


        String ipAddress = subnetParams.get("ipAddress");
        GraphmlNodeData ipAddressNodeData = new GraphmlNodeData("ipAddress",ipAddress);

        graphmlNodeData.add(ipAddressNodeData);

        String subnetPrefixMask = subnetParams.get("subnetPrefixMask");
        GraphmlNodeData subnetPrefixMaskNodeData = new GraphmlNodeData("subnetPrefixMask",subnetPrefixMask);
        graphmlNodeData.add(subnetPrefixMaskNodeData);


        return graphmlNodeData;
    }


    private List<GraphmlNodeData> getSnmpMainNode(DiscoveredDevice discoveredDevice) {

        List<GraphmlNodeData> graphmlNodeMetaDatas = new ArrayList<>();

        for (Map.Entry<String, String> entry : discoveredDevice.getParams().entrySet()) {
            logger.trace("MainNodeParm: " + entry.getKey() + "|" + entry.getValue());

            graphmlNodeMetaDatas.add(new GraphmlNodeData(entry.getKey(), entry.getValue()));

        }
        return graphmlNodeMetaDatas;
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
