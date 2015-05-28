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

    String projectPath;
    String networkDir;


    public String getGraphmlDataDirName() {
        return graphmlDataDirName;
    }

    public void setGraphmlDataDirName(String graphmlDataDirName) {
        this.graphmlDataDirName = graphmlDataDirName;
    }

    public String getLabelDirName() {
        return labelDirName;
    }

    public String getNetworkDir() {
        return networkDir;
    }

    public void setNetworkDir(String networkDir) {
        this.networkDir = networkDir;
    }

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public void setLabelDirName(String labelDirName) {
        this.labelDirName = labelDirName;
    }
    public static void main(String[] args){

        File dir = new File("/Users/niau/Projects/Projects/nmSevV/network/version5/device-centric");
        File[] files = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".graphml");
            }
        });
        if (files == null) {
            // some log
            return;
        }


        File outFile = new File("/Users/niau/Projects/Projects/nmSevV/network/version5/device-centric/network.graphml");
        if(!outFile.exists()){
            try {
                outFile.createNewFile   ();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        try {
            Map<String, String> edgesTypes = new HashMap<String, String>();
            edgesTypes.put("name","string");
//            edgesTypes.put("IPv4Forwarding","string");
//            edgesTypes.put("IPv6Forwarding","string");
            edgesTypes.put("Interface","string");
            edgesTypes.put("Discovery Method","string");
            edgesTypes.put("Neighbor IP Address","string");
            edgesTypes.put("Neighbor Device Type","string");
            edgesTypes.put("diff","string");
            edgesTypes.put("diffs","string");


            Map<String, String> vertexTypes = new HashMap<String, String>();
            vertexTypes.put("deviceModel","string");
            vertexTypes.put("deviceType","string");
            vertexTypes.put("nodeInfo","string");
            vertexTypes.put("hostname","string");
            vertexTypes.put("deviceStatus","string");
            vertexTypes.put("discoveredIPv4Address","string");
            vertexTypes.put("geoCoordinates","string");
            vertexTypes.put("site","string");
            vertexTypes.put("diff","string");
            vertexTypes.put("diffs","string");
            vertexTypes.put("diffs","string");
            vertexTypes.put("ipv6Forwarding","string");
            vertexTypes.put("ipv4Forwarding","string");
            vertexTypes.put("subnetPrefix","string");
            vertexTypes.put("totalInterfaceCount","string");
            vertexTypes.put("ipProtocolType","string");
            vertexTypes.put("subnetRangeType","string");



            new GrahmlMerge().merge(files,outFile,vertexTypes, edgesTypes, "undirected");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
    public static void main1(String[] args){
        File outFile = new File("network/version1/undirected/network.graphml");
        if(!outFile.exists()){
            try {
                outFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        File dir = new File("version1/undirected");
        File[] files = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".graphml");
            }
        });
        try {
            Map<String, String> edgesTypes = new HashMap<String, String>();
            edgesTypes.put("name","string");
            edgesTypes.put("IPv4Forwarding","string");
            edgesTypes.put("IPv6Forwarding","string");
            edgesTypes.put("Interface","string");
            edgesTypes.put("Discovery Method","string");
            edgesTypes.put("Neighbor IP Address","string");
            edgesTypes.put("Neighbor Device Type","string");
            edgesTypes.put("diff","string");
            edgesTypes.put("diffs","string");


            Map<String, String> vertexTypes = new HashMap<String, String>();
            vertexTypes.put("deviceModel","string");
            vertexTypes.put("deviceType","string");
            vertexTypes.put("nodeInfo","string");
            vertexTypes.put("hostname","string");
            vertexTypes.put("deviceStatus","string");
            vertexTypes.put("DiscoveredIPv4Address","string");
            vertexTypes.put("geoCoordinates","string");
            vertexTypes.put("site","string");
            vertexTypes.put("diff","string");
            vertexTypes.put("diffs","string");
            vertexTypes.put("diffs","string");
            vertexTypes.put("IPv6Forwarding","string");
            vertexTypes.put("IPv4Forwarding","string");
            vertexTypes.put("SubnetPrefix","string");


            new GrahmlMerge().merge(files,outFile,vertexTypes, edgesTypes, "undirected");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    @Override
    public void networkDiscovered(NetworkDiscoveryResult result) {
        File outFile = new File(projectPath, labelDirName+File.separator+graphmlDataDirName+File.separator+"network.graphml");
        File dir = new File(projectPath, labelDirName+File.separator+graphmlDataDirName);
        File[] files = dir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".graphml");
            }
        });
        if (files == null) {
            // some log
            return;
        }
        try {
            Map<String, String> edgesTypes = new HashMap<String, String>();
            edgesTypes.put("name","string");
//            edgesTypes.put("IPv4Forwarding","string");
//            edgesTypes.put("IPv6Forwarding","string");
            edgesTypes.put("Interface","string");
            edgesTypes.put("Discovery Method","string");
            edgesTypes.put("Neighbor IP Address","string");
            edgesTypes.put("Neighbor Device Type","string");
            edgesTypes.put("diff","string");
            edgesTypes.put("diffs","string");


            Map<String, String> vertexTypes = new HashMap<String, String>();
            vertexTypes.put("deviceModel","string");
            vertexTypes.put("deviceType","string");
            vertexTypes.put("nodeInfo","string");
            vertexTypes.put("hostname","string");
            vertexTypes.put("deviceStatus","string");
            vertexTypes.put("discoveredIPv4Address","string");
            vertexTypes.put("geoCoordinates","string");
            vertexTypes.put("site","string");
            vertexTypes.put("diff","string");
            vertexTypes.put("diffs","string");
            vertexTypes.put("diffs","string");
            vertexTypes.put("ipv6Forwarding","string");
            vertexTypes.put("ipv4Forwarding","string");
            vertexTypes.put("subnetPrefix","string");
            vertexTypes.put("totalInterfaceCount","string");
            vertexTypes.put("ipProtocolType","string");
            vertexTypes.put("subnetRangeType","string");



            new GrahmlMerge().merge(files,outFile,vertexTypes, edgesTypes, "undirected");
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
