package net.itransformers.idiscover.v2.core.listeners.neighbor.device;

import net.itransformers.idiscover.core.Subnet;
import net.itransformers.idiscover.networkmodelv2.DiscoveredDevice;
import net.itransformers.idiscover.v2.core.listeners.graphmlRenderer.model.GraphmlEdge;
import net.itransformers.idiscover.v2.core.listeners.graphmlRenderer.model.GraphmlEdgeData;
import net.itransformers.idiscover.v2.core.listeners.graphmlRenderer.model.GraphmlNode;
import net.itransformers.idiscover.v2.core.listeners.graphmlRenderer.model.GraphmlNodeData;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by niau on 9/2/16.
 */
public class DeviceToGraphml {
    DiscoveredDevice device;
    Map<String, ConnectionDetails> neighbourDiscoveredConnectionDetails;


    public DeviceToGraphml(DiscoveredDevice device, Map<String, ConnectionDetails> neighbourDiscoveredConnectionDetails) {
        this.device = device;
        this.neighbourDiscoveredConnectionDetails = neighbourDiscoveredConnectionDetails;
    }


    public List<GraphmlNode> getGraphmlSubnetNodes(){

        List<Subnet> subnetList =  device.getDeviceSubnetsFromActiveInterfaces();

        List<GraphmlNode> graphmlSubnetNodes     = new ArrayList<>();
        for (Subnet subnet : subnetList) {
            GraphmlNode subnetNode = new GraphmlNode();
            subnetNode.setId(subnet.getName());
            subnetNode.setLabel(subnet.getName());
            subnetNode.setGraphmlNodeDataList(getGraphmlSubnetNodeMetaData(subnet));
        }

        return graphmlSubnetNodes;
    }

    public List<GraphmlEdge> getGraphmlSubnetEdges(){
        List <GraphmlEdge> graphmlEdges = new ArrayList<>();

        List<Subnet> subnetList =  device.getDeviceSubnetsFromActiveInterfaces();
        for (Subnet subnet : subnetList) {
            GraphmlEdge graphmlEdge = new GraphmlEdge();
            String edgeId = generateEdgeId(device.getId(), subnet.getName());
            graphmlEdge.setId(edgeId);
            graphmlEdge.setLabel(edgeId);
            graphmlEdge.setGraphmlEdgeDataList(getGraphmlSubnetEdgeMetaData(subnet));

        }
        return null;

    }
//    <data key="ipAddress">10.20.1.0</data>
//    <data key="protocolType">IPv4</data>
//    <data key="subnetPrefixMask">24</data>
//    <data key="subnetMask">255.255.255.0</data>
//    <data key="ipv4SubnetBroadcast">10.20.1.255</data>
//



    private List<GraphmlNodeData> getGraphmlSubnetNodeMetaData(Subnet subnet){

        List<GraphmlNodeData> graphmlNodeDatas = new ArrayList<>();

        GraphmlNodeData ipAddress = new GraphmlNodeData("ipAddress",subnet.getIpAddress());
        graphmlNodeDatas.add(ipAddress);
        GraphmlNodeData protocolType = new GraphmlNodeData("protocolType",subnet.getSubnetProtocolType());
        graphmlNodeDatas.add(protocolType);
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
        GraphmlEdgeData ipLink  = new GraphmlEdgeData("ipLink","true");
        graphmlEdgeDatas.add(ipLink);

        GraphmlEdgeData dataLink  = new GraphmlEdgeData("dataLink","true");
        graphmlEdgeDatas.add(dataLink);

        GraphmlEdgeData ipv4Forwarding  = new GraphmlEdgeData("ipv4Forwarding",subnet.getSubnetProtocolType());
        graphmlEdgeDatas.add(ipv4Forwarding);



        GraphmlEdgeData ipv6Forwarding  = new GraphmlEdgeData("ipv6Forwarding",subnet.getSubnetProtocolType());
        graphmlEdgeDatas.add(ipv6Forwarding);


        GraphmlEdgeData mplsLink = new GraphmlEdgeData("MPLS","false");
        graphmlEdgeDatas.add(mplsLink);



        GraphmlEdgeData linkDiscoveryMethod  = new GraphmlEdgeData("discoveryMethod",subnet.getSubnetProtocolType());
        graphmlEdgeDatas.add(linkDiscoveryMethod);

        return graphmlEdgeDatas;

    }
    private String generateEdgeId(String deviceId,String neighbourId){

        return null;

    }
}
