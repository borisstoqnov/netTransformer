package net.itransformers.idiscover.v2.core.listeners.node;

import net.itransformers.idiscover.v2.core.NodeDiscoveryListener;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by niau on 9/27/16.
 */
public class DiscoveredNodesListUpdaterListener implements NodeDiscoveryListener{
    String labelDirName;

    @Override
    public void nodeDiscovered(NodeDiscoveryResult discoveryResult) {
        String nodeId = discoveryResult.getNodeId();
        if (nodeId!=null) try {
            updatelist(nodeId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void updatelist(String nodeId) throws IOException {

            FileWriter writer = new FileWriter(new File(labelDirName, "nodes.lst"),true);
            writer.append(String.valueOf(nodeId)).append("\n");
            writer.close();

    }

    public String getLabelDirName() {
        return labelDirName;
    }

    public void setLabelDirName(String labelDirName) {
        this.labelDirName = labelDirName;
    }
}
