package net.itransformers.idiscover.v2.core.listeners;

import net.itransformers.idiscover.v2.core.NetworkDiscoveryListener;
import net.itransformers.idiscover.v2.core.NetworkDiscoveryResult;
import net.itransformers.utils.graphmlmerge.GrahmlMerge;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SnmpNetworkDiscoveryListener implements NetworkDiscoveryListener {

    String graphmlDataDirName;

    String labelDirName;



    public String getGraphmlDataDirName() {
        return graphmlDataDirName;
    }

    public void setGraphmlDataDirName(String graphmlDataDirName) {
        this.graphmlDataDirName = graphmlDataDirName;
    }

    public String getLabelDirName() {
        return labelDirName;
    }

    public void setLabelDirName(String labelDirName) {
        this.labelDirName = labelDirName;
    }
    public static void main1(String[] args){
        File outFile = new File("/Users/niau/DemoLarge1/network/version10/undirected/network.graphml");
        if(!outFile.exists()){
            try {
                outFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        File dir = new File("/Users/niau/DemoLarge1/network/version3/undirected");
        File[] files = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".graphml");
            }
        });

        try {
            Map<String, String> edgesTypes = new HashMap<String, String>();
            edgesTypes.put("name","string");
            edgesTypes.put("method","string");
            edgesTypes.put("dataLink","string");
            edgesTypes.put("ipLink","string");
            edgesTypes.put("IPv4Forwarding","string");
            edgesTypes.put("IPv6Forwarding","string");
            edgesTypes.put("InterfaceNameA","string");
            edgesTypes.put("InterfaceNameB","string");
            edgesTypes.put("IPv4AddressA","string");
            edgesTypes.put("IPv4AddressB","string");
            edgesTypes.put("edgeTooltip","string");
            edgesTypes.put("diff","string");
            edgesTypes.put("diffs","string");

            Map<String, String> vertexTypes = new HashMap<String, String>();
            vertexTypes.put("deviceModel","string");
            vertexTypes.put("deviceType","string");
            vertexTypes.put("nodeInfo","string");
            vertexTypes.put("hostname","string");
            vertexTypes.put("deviceStatus","string");
            vertexTypes.put("ManagementIPAddress","string");
            vertexTypes.put("geoCoordinates","string");
            vertexTypes.put("site","string");
            vertexTypes.put("diff","string");
            vertexTypes.put("diffs","string");
            vertexTypes.put("diffs","string");
            vertexTypes.put("IPv6Forwarding","string");
            vertexTypes.put("IPv4Forwarding","string");

            new GrahmlMerge().merge(files,outFile,vertexTypes, edgesTypes, "undirected");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
    public static void main(String[] args){
        File outFile = new File("/Users/niau/DemoMedium1/network/version22/undirected/network.graphml");
        if(!outFile.exists()){
            try {
                outFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        File dir = new File("/Users/niau/DemoMedium1/network/version2/undirected");
        File[] files = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".graphml");
            }
        });
        try {
            Map<String, String> edgesTypes = new HashMap<String, String>();
            edgesTypes.put("name","string");
            edgesTypes.put("method","string");
            edgesTypes.put("dataLink","string");
            edgesTypes.put("ipLink","string");
            edgesTypes.put("IPv4Forwarding","string");
            edgesTypes.put("IPv6Forwarding","string");
            edgesTypes.put("InterfaceNameA","string");
            edgesTypes.put("InterfaceNameB","string");
            edgesTypes.put("IPv4AddressA","string");
            edgesTypes.put("IPv4AddressB","string");
            edgesTypes.put("edgeTooltip","string");
            edgesTypes.put("diff","string");
            edgesTypes.put("diffs","string");

            Map<String, String> vertexTypes = new HashMap<String, String>();
            vertexTypes.put("deviceModel","string");
            vertexTypes.put("deviceType","string");
            vertexTypes.put("nodeInfo","string");
            vertexTypes.put("hostname","string");
            vertexTypes.put("deviceStatus","string");
            vertexTypes.put("ManagementIPAddress","string");
            vertexTypes.put("geoCoordinates","string");
            vertexTypes.put("site","string");
            vertexTypes.put("diff","string");
            vertexTypes.put("diffs","string");
            vertexTypes.put("diffs","string");
            vertexTypes.put("IPv6Forwarding","string");
            vertexTypes.put("IPv4Forwarding","string");

            new GrahmlMerge().merge(files,outFile,vertexTypes, edgesTypes, "undirected");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    @Override
    public void networkDiscovered(NetworkDiscoveryResult result) {
        File outFile = new File(labelDirName+File.separator+graphmlDataDirName+File.separator+"entire-network.graphml");
        File dir = new File(labelDirName+File.separator+graphmlDataDirName);
        File[] files = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".graphml");
            }
        });

        try {
            Map<String, String> edgesTypes = new HashMap<String, String>();
            edgesTypes.put("name","string");
            edgesTypes.put("method","string");
            edgesTypes.put("dataLink","string");
            edgesTypes.put("ipLink","string");
            edgesTypes.put("IPv4Forwarding","string");
            edgesTypes.put("IPv6Forwarding","string");
            edgesTypes.put("InterfaceNameA","string");
            edgesTypes.put("InterfaceNameB","string");
            edgesTypes.put("IPv4AddressA","string");
            edgesTypes.put("IPv4AddressB","string");
            edgesTypes.put("edgeTooltip","string");
            edgesTypes.put("diff","string");
            edgesTypes.put("diffs","string");
            edgesTypes.put("bgpAutonomousSystemA","string");
            edgesTypes.put("bgpAutonomousSystemB","string");

            Map<String, String> vertexTypes = new HashMap<String, String>();
            vertexTypes.put("deviceModel","string");
            vertexTypes.put("deviceType","string");
            vertexTypes.put("nodeInfo","string");
            vertexTypes.put("hostname","string");
            vertexTypes.put("deviceStatus","string");
            vertexTypes.put("ManagementIPAddress","string");
            vertexTypes.put("geoCoordinates","string");
            vertexTypes.put("site","string");
            vertexTypes.put("diff","string");
            vertexTypes.put("diffs","string");
            vertexTypes.put("diffs","string");
            vertexTypes.put("IPv6Forwarding","string");
            vertexTypes.put("IPv4Forwarding","string");
            vertexTypes.put("bgpLocalAS","string");

            new GrahmlMerge().merge(files,outFile,vertexTypes, edgesTypes, "undirected");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
