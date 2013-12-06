package net.itransformers.idiscover.v2.core.listeners;

//import net.itransformers.idiscover.v2.core.NetworkDiscoveryListener;
//import net.itransformers.idiscover.v2.core.model.Node;
//import net.itransformers.utils.GrahmlMerge;
//
//import java.io.File;
//import java.io.FileFilter;
//import java.io.IOException;
//import java.util.Map;

public class SnmpNetworkDiscoveryListener {//} implements NetworkDiscoveryListener {

//    String graphmlDataDirName;
//
//
//
//    String labelDirName;
//
//    @Override
//    public void networkDiscovered(Map<String, Node> network) {
//        File outFile = new File(labelDirName+File.separator+graphmlDataDirName+File.separator+"entire-network.graphml");
//        File dir = new File(labelDirName+File.separator+graphmlDataDirName);
//        File[] files = dir.listFiles(new FileFilter() {
//            @Override
//            public boolean accept(File pathname) {
//                return pathname.getName().endsWith(".graphml");
//            }
//        });
//
//        try {
//            new GrahmlMerge().merge(files, outFile);
//        } catch (IOException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }
//    }
//    public String getGraphmlDataDirName() {
//        return graphmlDataDirName;
//    }
//
//    public void setGraphmlDataDirName(String graphmlDataDirName) {
//        this.graphmlDataDirName = graphmlDataDirName;
//    }
//    public String getLabelDirName() {
//        return labelDirName;
//    }
//
//    public void setLabelDirName(String labelDirName) {
//        this.labelDirName = labelDirName;
//    }

}
