/*
 * RawDataFileLogDiscoveryListener.java
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

package net.itransformers.idiscover.v2.core.listeners.node;

import net.itransformers.idiscover.v2.core.NodeDiscoveryListener;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class RawDataFileLogDiscoveryListener implements NodeDiscoveryListener {
    static Logger logger = Logger.getLogger(RawDataFileLogDiscoveryListener.class);
    String labelDirName;
    String rawDataDirName;
    String projectPath;


    @Override
    public void nodeDiscovered(NodeDiscoveryResult discoveryResult) {

        File baseDir = new File(projectPath, labelDirName);
        if (!baseDir.exists()) baseDir.mkdir();
        File rawDataDir = new File(baseDir, rawDataDirName);
        if (!rawDataDir.exists()) rawDataDir.mkdir();
        String deviceName = discoveryResult.getNodeId();
        File file = new File(rawDataDir, deviceName+".xml");
        String rawDataXml = new String((byte[]) discoveryResult.getDiscoveredData("rawData"));
        try {
            FileUtils.writeStringToFile(file,rawDataXml);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    public String getLabelDirName() {
        return labelDirName;
    }

    public void setLabelDirName(String labelDirName) {
        this.labelDirName = labelDirName;
    }

    public String getRawDataDirName() {
        return rawDataDirName;
    }

    public void setRawDataDirName(String rawDataDirName) {
        this.rawDataDirName = rawDataDirName;
    }

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }
}
