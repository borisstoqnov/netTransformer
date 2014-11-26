

package net.itransformers.idiscover.v2.core.listeners;

import net.itransformers.idiscover.v2.core.NodeDiscoveryListener;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class BGPGraphmlFileLogDiscoveryListener implements NodeDiscoveryListener {
    static Logger logger = Logger.getLogger(BGPGraphmlFileLogDiscoveryListener.class);
    String labelDirName;
    String graphmDirName;

    @Override
    public void nodeDiscovered(NodeDiscoveryResult discoveryResult) {
        File baseDir = new File(labelDirName);
        File graphmlDir = new File(baseDir,graphmDirName);
        if (!graphmlDir.exists()) graphmlDir.mkdir();

        String deviceName = discoveryResult.getNodeId();
        byte[] discoveredDeviceData = (byte []) discoveryResult.getDiscoveredData("graphml");



        try {
            final String fileName = "node-" + deviceName + ".graphml";
            final File nodeFile = new File(graphmlDir,fileName);
            String graphml = new String(discoveredDeviceData);
            FileUtils.writeStringToFile(nodeFile, graphml);
            FileWriter writer = new FileWriter(new File(labelDirName,"undirected"+".graphmls"),true);
            writer.append(String.valueOf(fileName)).append("\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public String getLabelDirName() {
        return labelDirName;
    }

    public void setLabelDirName(String labelDirName) {
        this.labelDirName = labelDirName;
    }

    public String getGraphmDirName() {
        return graphmDirName;
    }

    public void setGraphmDirName(String graphmDirName) {
        this.graphmDirName = graphmDirName;
    }
}
