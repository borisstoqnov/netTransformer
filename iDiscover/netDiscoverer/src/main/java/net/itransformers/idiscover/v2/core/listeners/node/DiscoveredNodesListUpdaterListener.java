package net.itransformers.idiscover.v2.core.listeners.node;


import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetails;
import net.itransformers.idiscover.api.NodeDiscoveryListener;
import net.itransformers.idiscover.api.NodeDiscoveryResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

/**
 * Created by niau on 9/27/16.
 */
public class DiscoveredNodesListUpdaterListener implements NodeDiscoveryListener {
    protected String projectPath;
    protected String labelDirName;

    public DiscoveredNodesListUpdaterListener(String projectPath, String labelDirName) {
        this.projectPath = projectPath;
        this.labelDirName = labelDirName;
    }

    @Override
    public void nodeDiscovered(NodeDiscoveryResult discoveryResult) {
        String nodeId = discoveryResult.getNodeId();


        if (nodeId!=null && discoveryResult.getDiscoveredData()!=null) try {
            updatelist(nodeId, discoveryResult.getDiscoveryConnectionDetails(),discoveryResult.getOwnConnectionDetails(), discoveryResult.getNeighboursConnectionDetails());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void updatelist(String nodeId, ConnectionDetails connectionDetails, Set<ConnectionDetails> ownConnectionDetails, Set<ConnectionDetails> neighbourConnectionDetails) throws IOException {
        File parent = new File(projectPath, labelDirName);
        FileWriter writer = new FileWriter(new File(parent, "nodes.lst"),true);
            writer.append(String.valueOf(nodeId)).append("\n");
            writer.close();
    }
}
