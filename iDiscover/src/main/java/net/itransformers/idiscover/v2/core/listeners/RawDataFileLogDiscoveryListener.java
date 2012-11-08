/*
 * iTransformer is an open source tool able to discover and transform
 *  IP network infrastructures.
 *  Copyright (C) 2012  http://itransformers.net
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.itransformers.idiscover.v2.core.listeners;/*
 * iTransformer is an open source tool able to discover IP networks
 * and to perform dynamic data data population into a xml based inventory system.
 * Copyright (C) 2010  http://itransformers.net
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import net.itransformers.idiscover.v2.core.NodeDiscoveryListener;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;

public class RawDataFileLogDiscoveryListener implements NodeDiscoveryListener {
    static Logger logger = Logger.getLogger(RawDataFileLogDiscoveryListener.class);
    String baseDirName;

    @Override
    public void nodeDiscovered(NodeDiscoveryResult discoveryResult) {
        File baseDir = new File(baseDirName);
        File rawDataDir = new File(baseDir, "raw-data");

        String deviceName = discoveryResult.getNodeId();
        File file = new File(rawDataDir, deviceName);
        String rawDataXml = new String((byte[]) discoveryResult.getDiscoveredData("rawData"));
        try {
            FileUtils.writeStringToFile(file,rawDataXml);
        } catch (IOException e) {
            logger.error(e);
        }
    }

    public String getBaseDirName() {
        return baseDirName;
    }

    public void setBaseDirName(String baseDirName) {
        this.baseDirName = baseDirName;
    }
}
