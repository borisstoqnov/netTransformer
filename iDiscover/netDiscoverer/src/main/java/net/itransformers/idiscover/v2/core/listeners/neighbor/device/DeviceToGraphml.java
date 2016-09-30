package net.itransformers.idiscover.v2.core.listeners.neighbor.device;

import net.itransformers.idiscover.api.models.graphml.GraphmlEdge;
import net.itransformers.idiscover.api.models.graphml.GraphmlEdgeData;
import net.itransformers.idiscover.api.models.graphml.GraphmlNode;
import net.itransformers.idiscover.api.models.graphml.GraphmlNodeData;
import net.itransformers.idiscover.core.Subnet;
import net.itransformers.idiscover.networkmodelv2.DeviceNeighbour;
import net.itransformers.idiscover.networkmodelv2.DiscoveredDevice;
import net.itransformers.idiscover.networkmodelv2.DiscoveredInterface;
import net.itransformers.idiscover.v2.core.listeners.neighbor.EdgeIdGenerator;
import net.itransformers.idiscover.v2.core.model.Node;
import org.apache.log4j.Logger;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by niau on 9/2/16.
 */
public class DeviceToGraphml {
    static Logger logger = Logger.getLogger(DeviceToGraphml.class);

    DiscoveredDevice device;
    Node node;

    public DeviceToGraphml(Node node, DiscoveredDevice device) {
        this.device = device;
        this.node = node;
    }


    public List<GraphmlNode> getSubnetNodes(){
        List<Subnet> subnetList =  device.getDeviceSubnetsFromActiveInterfaces();
        List<GraphmlNode> graphmlSubnetNodes     = new ArrayList<>();
        for (Subnet subnet : subnetList) {
            GraphmlNode subnetNode = new GraphmlNode();
            subnetNode.setId(subnet.getName());
            subnetNode.setLabel(subnet.getName());
            subnetNode.setGraphmlNodeDataList(getGraphmlSubnetNodeMetaData(subnet));
            graphmlSubnetNodes.add(subnetNode);
        }
        return graphmlSubnetNodes;
    }

    public List<GraphmlNode> getNonSubnetNeighbours(){
        List<GraphmlNode> neighbourNodes     = new ArrayList<>();

        List<DeviceNeighbour> deviceNeighbours =  device.getDeviceNeighbours();
        for (DeviceNeighbour deviceNeighbour : deviceNeighbours) {


//            String neighbourName = deviceNeighbour.getId();
            String neighbourIpAddress = deviceNeighbour.getIpAddress();
            String neighbourHostName = deviceNeighbour.getNeighbourHostName();
            String neighbourMac = deviceNeighbour.getNeighbourMac();


            String neighbourId = getNeighbourIdFromAliases(neighbourHostName, neighbourIpAddress, neighbourMac);

            if (neighbourId==null) {
                logger.info("Can't find neighbour id for: " + deviceNeighbour);
                continue;
            }

            GraphmlNode graphmlNode = new GraphmlNode(neighbourId, neighbourId);

            neighbourNodes.add(graphmlNode);


        }

        return neighbourNodes;
    }




    public List<GraphmlEdge> getSubnetEdgesToMainNode(){
        List <GraphmlEdge> graphmlEdges = new ArrayList<>();

        List<Subnet> subnetList =  device.getDeviceSubnetsFromActiveInterfaces();
        for (Subnet subnet : subnetList) {
            //TODO this has to create edges with id from the local ip address and the subnet.
            EdgeIdGenerator edgeIdGenerator = new EdgeIdGenerator(device.getName(), subnet.getName(),device.getName(), subnet.getName());

            GraphmlEdge graphmlEdge = edgeIdGenerator.createEdge();
            graphmlEdge.setGraphmlEdgeDataList(getGraphmlSubnetEdgeMetaData(subnet));
            graphmlEdges.add(graphmlEdge);

        }
        return graphmlEdges;

    }


    public List<GraphmlEdge> getEdgesToNeighbours(){
        List <GraphmlEdge> graphmlEdges = new ArrayList<>();

        List<Subnet> subnetList =  device.getDeviceSubnetsFromActiveInterfaces();
        //List<DeviceNeighbour> deviceNeighbours = device.getDeviceNeighbours();


        for (DiscoveredInterface devInterface : device.getInterfaceList()){
            String localMac = devInterface.getParams().get("ifPhysAddress");


//            List<DiscoveredIPv4Address> discoveredIPv4Addresses = devInterface.getiPv4AddressList();


            for (DeviceNeighbour deviceNeighbour : devInterface.getNeighbours()) {


                String neighbourIpAddress = deviceNeighbour.getNeighbourIpAddress();
                String neighbourHostName = deviceNeighbour.getNeighbourHostName();
                String neighbourMac = deviceNeighbour.getNeighbourMac();

                String neighbourId = getNeighbourIdFromAliases(neighbourHostName, neighbourIpAddress, neighbourMac);

                if (neighbourId==null)
                    continue;

                boolean neighbourInSubnet = false;

                if(neighbourIpAddress!=null&& !neighbourIpAddress.isEmpty()) {
                    for (Subnet subnet : subnetList) {
                        if (subnet.contains(neighbourIpAddress)) {
                            EdgeIdGenerator edgeIdGenerator = new EdgeIdGenerator(neighbourId, subnet.getName(),neighbourIpAddress,subnet.getIpAddress());

                            GraphmlEdge graphmlEdge = edgeIdGenerator.createEdge();
                            graphmlEdge.setGraphmlEdgeDataList(getGraphmlDirectNeighbourEdgeMetaData(deviceNeighbour));

                            boolean edgeAlreadyDefined = false;
                            for (GraphmlEdge edge : graphmlEdges){
                                if (edge.getId().equals(graphmlEdge.getId())){
                                    edgeAlreadyDefined = true;
                                    int index = graphmlEdges.indexOf(edge);
                                    logger.info (graphmlEdge +"already exists" );


                                    List <GraphmlEdgeData> existingGraphmlEdgeDatas = edge.getGraphmlEdgeDataList();
                                    List <GraphmlEdgeData> newGraphmlEdgeDatas = graphmlEdge.getGraphmlEdgeDataList();

                                    existingGraphmlEdgeDatas.addAll(newGraphmlEdgeDatas);

                                    edge.setGraphmlEdgeDataList(existingGraphmlEdgeDatas);

                                    graphmlEdges.set(index,edge);

                                }
                            }
                            if (!edgeAlreadyDefined) {
                                graphmlEdges.add(graphmlEdge);
                            }


                            neighbourInSubnet = true;
                            break;
                        }

                    }
                }

                if (!neighbourInSubnet){
                    EdgeIdGenerator edgeIdGenerator = new EdgeIdGenerator(neighbourId, node.getId(),neighbourId,node.getId(),localMac,neighbourMac);

                    GraphmlEdge graphmlEdge = edgeIdGenerator.createEdge();
                    graphmlEdge.setGraphmlEdgeDataList(getGraphmlDirectNeighbourEdgeMetaData(deviceNeighbour));

                    boolean edgeAlreadyDefined = false;
                    for (GraphmlEdge edge : graphmlEdges){
                        if (edge.getId().equals(graphmlEdge.getId())){
                            edgeAlreadyDefined = true;
                            int index = graphmlEdges.indexOf(edge);
                            logger.info (graphmlEdge +"already exists" );


                            List <GraphmlEdgeData> existingGraphmlEdgeDatas = edge.getGraphmlEdgeDataList();
                            List <GraphmlEdgeData> newGraphmlEdgeDatas = graphmlEdge.getGraphmlEdgeDataList();

                            existingGraphmlEdgeDatas.addAll(newGraphmlEdgeDatas);

                            edge.setGraphmlEdgeDataList(existingGraphmlEdgeDatas);

                            graphmlEdges.set(index,edge);

                        }
                    }
                    if (!edgeAlreadyDefined) {
                        graphmlEdges.add(graphmlEdge);
                    }


                   // graphmlEdges.add(graphmlEdge);
                }


            }
            //TODO
           //ь device.getLogicalDeviceData().getDeviceNeighbourList()



//            for (DiscoveredIPv4Address discoveredIPv4Address : discoveredIPv4Addresses) {
//                  if (!discoveredIPv4Address.isBogon()) {
//
//                      HashMap<String,String> params = discoveredIPv4Address.getParams();
//                      String ipv4Subnet = params.get("ipv4Subnet");
//                      String subnetPrefixMask = params.get("ipv4SubnetPrefix");
//
//                  }
//            }



        }




        return graphmlEdges;

    }


    private String getNeighbourIdFromAliases(String neighbourHostName,String neighbourIpAddress, String neighbourMac){
       String neighbourId;

        if (neighbourIpAddress!=null && !neighbourIpAddress.isEmpty()){
            neighbourId  = findNeighbourFromAliases(neighbourIpAddress);
            if (neighbourId!=null) {
                return neighbourId;
            }
        } else if (neighbourHostName!=null && !neighbourHostName.isEmpty()){

            neighbourId = findNeighbourFromAliases(neighbourHostName);
            if (neighbourId!=null) {
                return neighbourId;
            }

        }  else if (neighbourMac!=null && !neighbourMac.isEmpty()){
            neighbourId = findNeighbourFromAliases(neighbourMac);
            if (neighbourId!=null) {
                return neighbourId;
            }

        } else {
            logger.info("Can't find neighbour id hostName,ipAddress and mac are all null or empty!!!");

        }

        if (neighbourHostName!=null&&!neighbourHostName.isEmpty()) {
            return neighbourHostName;

        }else if (neighbourIpAddress!=null && !neighbourIpAddress.isEmpty()){
                return neighbourIpAddress;
        }else if (neighbourMac!=null && !neighbourMac.isEmpty()){
                return neighbourMac;
        }



        return null;
    }


    private List<GraphmlEdgeData> getGraphmlDirectNeighbourEdgeMetaData(DeviceNeighbour neighbour ) {

        List<GraphmlEdgeData> graphmlEdgeDatas = new ArrayList<>();
        GraphmlEdgeData ipLink;
        if (neighbour.getNeighbourIpAddress()!=null && !neighbour.getNeighbourIpAddress().isEmpty())
            ipLink  = new GraphmlEdgeData("ipLink","YES");
        else
            ipLink  = new GraphmlEdgeData("ipLink","NO");

        graphmlEdgeDatas.add(ipLink);

       String neighbourIpAddress = neighbour.getIpAddress();

        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getByName(neighbourIpAddress);
        } catch (UnknownHostException e) {
            logger.error(e.getMessage(), e);
        }
        if (inetAddress instanceof Inet4Address) {
            GraphmlEdgeData ipv4Forwarding  = new GraphmlEdgeData("ipv4Forwarding","YES");
            graphmlEdgeDatas.add(ipv4Forwarding);
            GraphmlEdgeData ipv6Forwarding  = new GraphmlEdgeData("ipv6Forwarding","NO");
            graphmlEdgeDatas.add(ipv6Forwarding);

        }else{
            GraphmlEdgeData ipv6Forwarding  = new GraphmlEdgeData("ipv6Forwarding","YES");
            graphmlEdgeDatas.add(ipv6Forwarding);
            GraphmlEdgeData ipv4Forwarding  = new GraphmlEdgeData("ipv4Forwarding","NO");
            graphmlEdgeDatas.add(ipv4Forwarding);
        }
        String discoveryMethod= neighbour.getParameters().get("Discovery Method");
        GraphmlEdgeData linkDiscoveryMethod  = new GraphmlEdgeData("discoveryMethod",discoveryMethod);
        graphmlEdgeDatas.add(linkDiscoveryMethod);
        GraphmlEdgeData dataLink;

        if (discoveryMethod.equals("CDP")||discoveryMethod.equals("LLDP")||discoveryMethod.equals("MAC")){
            dataLink  = new GraphmlEdgeData("dataLink","YES");
        }    else {
            dataLink  = new GraphmlEdgeData("dataLink","NO");

        }
        graphmlEdgeDatas.add(dataLink);


        return graphmlEdgeDatas;

    }


//    private List<GraphmlEdgeData> getGraphmlSubnetNeighbourEdgeMetaData(Subnet subnet, DeviceNeighbour neighbour ) {
//
//        List<GraphmlEdgeData> graphmlEdgeDatas = new ArrayList<>();
//        GraphmlEdgeData ipLink;
//        if (neighbour.getNeighbourIpAddress()!=null && !neighbour.getNeighbourIpAddress().isEmpty())
//             ipLink  = new GraphmlEdgeData("ipLink","true");
//        else
//            ipLink  = new GraphmlEdgeData("ipLink","false");
//
//        graphmlEdgeDatas.add(ipLink);
//
//        GraphmlEdgeData ipv4Forwarding;
//        GraphmlEdgeData ipv6Forwarding;
//        switch (subnet.getSubnetProtocolType()) {
//            case "IPv4":
//                ipv4Forwarding  = new GraphmlEdgeData("ipv4Forwarding","YES");
//                graphmlEdgeDatas.add(ipv4Forwarding);
//                ipv6Forwarding  = new GraphmlEdgeData("ipv6Forwarding","NO");
//                graphmlEdgeDatas.add(ipv6Forwarding);
//                break;
//            case "IPv6":
//                ipv6Forwarding  = new GraphmlEdgeData("ipv6Forwarding","YES");
//                graphmlEdgeDatas.add(ipv6Forwarding);
//                ipv4Forwarding  = new GraphmlEdgeData("ipv4Forwarding","NO");
//                graphmlEdgeDatas.add(ipv4Forwarding);
//                break;
//        }
//        String discoveryMethod= neighbour.getParameters().get("Discovery Method");
//        GraphmlEdgeData linkDiscoveryMethod  = new GraphmlEdgeData("discoveryMethod",discoveryMethod);
//        graphmlEdgeDatas.add(linkDiscoveryMethod);
//        GraphmlEdgeData dataLink;
//
//        if (discoveryMethod.equals("CDP")||discoveryMethod.equals("LLDP")||discoveryMethod.equals("MAC")){
//             dataLink  = new GraphmlEdgeData("dataLink","true");
//        }    else {
//            dataLink  = new GraphmlEdgeData("dataLink","false");
//
//        }
//        graphmlEdgeDatas.add(dataLink);
//
//
//        return graphmlEdgeDatas;
//
//    }


    private List<GraphmlNodeData> getGraphmlSubnetNodeMetaData(Subnet subnet){

        List<GraphmlNodeData> graphmlNodeDatas = new ArrayList<>();

        GraphmlNodeData ipAddress = new GraphmlNodeData("ipAddress",subnet.getIpAddress());
        graphmlNodeDatas.add(ipAddress);
        GraphmlNodeData ipv4Forwarding;
        GraphmlNodeData ipv6Forwarding;
        switch (subnet.getSubnetProtocolType()){
            case "IPv4":
                ipv4Forwarding  = new GraphmlNodeData("ipv4Forwarding","YES");
                graphmlNodeDatas.add(ipv4Forwarding);
                ipv6Forwarding  = new GraphmlNodeData("ipv6Forwarding","NO");
                graphmlNodeDatas.add(ipv6Forwarding);
                break;
            case "IPv6":
                ipv6Forwarding  = new GraphmlNodeData("ipv6Forwarding","YES");
                graphmlNodeDatas.add(ipv6Forwarding);
                ipv4Forwarding  = new GraphmlNodeData("ipv4Forwarding","NO");
                graphmlNodeDatas.add(ipv4Forwarding);
                break;
        }


        GraphmlNodeData subnetPrefixMask = new GraphmlNodeData("subnetPrefixMask",subnet.getSubnetPrefixMask());
        graphmlNodeDatas.add(subnetPrefixMask);
        GraphmlNodeData subnetMask = new GraphmlNodeData("subnetMask",subnet.getsubnetMask());
        graphmlNodeDatas.add(subnetMask);
        GraphmlNodeData subnetDeviceType = new GraphmlNodeData("deviceType","subnet");
        graphmlNodeDatas.add(subnetDeviceType);
        GraphmlNodeData subnetPort = new GraphmlNodeData("port",subnet.getLocalInterface());
        graphmlNodeDatas.add(subnetPort);


        return graphmlNodeDatas;

    }

    private List<GraphmlEdgeData> getGraphmlSubnetEdgeMetaData(Subnet subnet){
        List<GraphmlEdgeData> graphmlEdgeDatas = new ArrayList<>();
        GraphmlEdgeData ipLink  = new GraphmlEdgeData("ipLink","YES");
        graphmlEdgeDatas.add(ipLink);

        GraphmlEdgeData dataLink  = new GraphmlEdgeData("dataLink","YES");
        graphmlEdgeDatas.add(dataLink);

        String protocolType= subnet.getSubnetProtocolType();
        GraphmlEdgeData ipv4Forwarding;
        GraphmlEdgeData ipv6Forwarding;

        switch (protocolType){
            case "IPv4":
                ipv4Forwarding  = new GraphmlEdgeData("ipv4Forwarding","YES");
                ipv6Forwarding  = new GraphmlEdgeData("ipv6Forwarding","NO");
                graphmlEdgeDatas.add(ipv4Forwarding);
                graphmlEdgeDatas.add(ipv6Forwarding);

                break;
            case "IPv6":
                ipv6Forwarding  = new GraphmlEdgeData("ipv6Forwarding","YES");
                ipv4Forwarding  = new GraphmlEdgeData("ipv4Forwarding","NO");
                graphmlEdgeDatas.add(ipv4Forwarding);
                graphmlEdgeDatas.add(ipv6Forwarding);
                break;


        }

        GraphmlEdgeData linkDiscoveryMethod  = new GraphmlEdgeData("discoveryMethod",subnet.getSubnetDiscoveryMethods());
        graphmlEdgeDatas.add(linkDiscoveryMethod);

        return graphmlEdgeDatas;

    }


    private String findNeighbourFromAliases(String key){


        for (Node neighbour : node.getNeighbours()){

            Set<String> neighbourAliases = neighbour.getAliases();
            if (neighbourAliases!=null && neighbourAliases.contains(key)){
                return neighbour.getId();
            }

        }
        return null;

    }

}
