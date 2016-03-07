

/*
 * BGPGraphmlFileLogDiscoveryListener.java
 *
 * This work is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * This work is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * Copyright (c) 2010-2016 iTransformers Labs. All rights reserved.
 */

package net.itransformers.idiscover.v2.core.listeners.network;

import net.itransformers.idiscover.v2.core.NetworkDiscoveryListener;
import net.itransformers.idiscover.v2.core.NetworkDiscoveryResult;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.utils.ProjectConstants;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class BGPGraphmlFileLogDiscoveryListener implements NetworkDiscoveryListener {
    static Logger logger = Logger.getLogger(BGPGraphmlFileLogDiscoveryListener.class);
    String labelDirName;
    String graphmDirName;

    @Override
    public void networkDiscovered(NetworkDiscoveryResult result) {
        File baseDir = new File(labelDirName);
        File graphmlDir = new File(baseDir,graphmDirName);
        if (!graphmlDir.exists()) graphmlDir.mkdir();


       Map<String, NodeDiscoveryResult> discoveryResultMap = result.getDiscoveredData();
        for (String node : discoveryResultMap.keySet()) {


            byte[] discoveredDeviceData = (byte []) discoveryResultMap.get(node).getDiscoveredData("graphml");


            try {
                final String fileName = ProjectConstants.networkGraphmlFileName;
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
