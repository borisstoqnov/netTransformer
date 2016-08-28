package net.itransformers.idiscover.v2.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by niau on 8/26/16.
 */


public class SnmpGraphmlMetadataProvider {


    private static Map<String, String> edgesMetadataTypes ;

    private static Map<String, String> vertexMetadataTypes;

    private static Map<String, String> graphMetadataTypes;

    public SnmpGraphmlMetadataProvider() {
        setGraphMetadataTypes();
        setVertexMetadataTypes();
        setEdgesMetadataTypes();
    }






    public static  Map<String, String> getGraphMetadataTypes() {
        return graphMetadataTypes;
    }

    public void setGraphMetadataTypes(){
        graphMetadataTypes = new HashMap<String, String>();
        graphMetadataTypes.put("networkName","string");
        graphMetadataTypes.put("networkDiscoveredTime","date");
    }

    public static Map<String, String> getVertexMetadataTypes() {
        return vertexMetadataTypes;
    }

//        <key id="hostname" for="node" attr.name="Device Hostname" attr.type="string"/>
//        <key id="deviceModel" for="node" attr.name="Device Model" attr.type="string"/>
//        <key id="deviceType" for="node" attr.name="Device Type" attr.type="string"/>
//        <key id="nodeInfo" for="node" attr.name="Node Information " attr.type="string"/>
//        <key id="discoveredIPv4Address" for="node" attr.name="Discovered IPv4 Address" attr.type="string"/>
//        <key id="discoveredState" for="node" attr.name="Discovered State" attr.type="string"/>
//        <key id="sysLocation" for="node" attr.name="Location by SNMP" attr.type="string"/>
//        <key id="site" for="node" attr.name="site" attr.type="string"/>
//        <key id="diff" for="node" attr.name="diff" attr.type="string"/>
//        <key id="diffs" for="node" attr.name="diffs" attr.type="string"/>
//        <key id="ipv6Forwarding" for="node" attr.name="IP v6 Forwarding" attr.type="string"/>
//        <key id="ipv4Forwarding" for="node" attr.name="IP v4 Forwarding" attr.type="string"/>
//        <key id="subnetPrefix" for="node" attr.name="Subnet Prefix" attr.type="string"/>
//        <key id="ipProtocolType" for="node" attr.name="IP Protocol Type" attr.type="string"/>
//        <key id="bgpAS" for="node" attr.name="BGP Autonomous system" attr.type="string"/>
//        <key id="totalInterfaceCount" for="node" attr.name="Total Interface Count" attr.type="string"/>


    public void setVertexMetadataTypes() {
        vertexMetadataTypes = new HashMap<String, String>();
        vertexMetadataTypes.put("hostname", "string");
        vertexMetadataTypes.put("deviceModel", "string");
        vertexMetadataTypes.put("deviceType", "string");
        vertexMetadataTypes.put("nodeInfo", "string");
        vertexMetadataTypes.put("discoveredState", "string");
        vertexMetadataTypes.put("discoveredIPv4Address", "string");
        vertexMetadataTypes.put("sysLocation", "string");
        vertexMetadataTypes.put("site", "string");
        vertexMetadataTypes.put("diff", "string");
        vertexMetadataTypes.put("diffs", "string");
        vertexMetadataTypes.put("ipv6Forwarding", "string");
        vertexMetadataTypes.put("ipv4Forwarding", "string");
        vertexMetadataTypes.put("subnetPrefix", "string");
        vertexMetadataTypes.put("ipProtocolType", "string");
        vertexMetadataTypes.put("bgpAS", "string");
        vertexMetadataTypes.put("subnetRangeType", "string");
        vertexMetadataTypes.put("totalInterfaceCount", "string");

    }


    public static  Map<String, String> getEdgesMetadataTypes() {
        return edgesMetadataTypes;
    }
//        <key id="discoveryMethod" for="edge" attr.name="Discovery Method" attr.type="string"/>
//        <key id="dataLink" for="edge" attr.name="dataLink" attr.type="string"/>
//        <key id="ipLink" for="edge" attr.name="IP Link" attr.type="string"/>
//        <key id="MPLS" for="edge" attr.name="MPLS" attr.type="string"/>
//        <key id="ipv6Forwarding" for="edge" attr.name="IP v6 Forwarding" attr.type="string"/>
//        <key id="ipv4Forwarding" for="edge" attr.name="IP v4 Forwarding" attr.type="string"/>
//        <key id="interface" for="edge" attr.name="interface" attr.type="string"/>
//        <key id="diff" for="edge" attr.name="diff" attr.type="string"/>
//        <key id="diffs" for="edge" attr.name="diffs" attr.type="string"/>
//        <key id="encapsulation" for="edge" attr.name="L2 encapsulation" attr.type="string"/>
//        <key id="speed" for="edge" attr.name="Port Speed" attr.type="string"/>


    public void setEdgesMetadataTypes() {
        edgesMetadataTypes = new HashMap<String, String>();
        edgesMetadataTypes.put("discoveryMethod","string");
        edgesMetadataTypes.put("dataLink","string");
        edgesMetadataTypes.put("ipLink","string");
        edgesMetadataTypes.put("MPLS","string");
        edgesMetadataTypes.put("ipv4Forwarding","string");
        edgesMetadataTypes.put("ipv6Forwarding","string");
        edgesMetadataTypes.put("interface","string");
        edgesMetadataTypes.put("diff","string");
        edgesMetadataTypes.put("diffs","string");
        edgesMetadataTypes.put("ifType","string");
        edgesMetadataTypes.put("ifSpeed","string");
    }
}
