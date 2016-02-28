/*
 * DeviceFileLogger.java
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

package net.itransformers.idiscover.discoverylisteners;

import net.itransformers.idiscover.core.DiscoveryListener;
import net.itransformers.idiscover.core.RawDeviceData;
import net.itransformers.idiscover.core.Resource;
import net.itransformers.idiscover.networkmodel.DiscoveredDeviceData;
import net.itransformers.idiscover.util.JaxbMarshalar;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.Map;

public class DeviceFileLogger implements DiscoveryListener{
    static Logger logger = Logger.getLogger(DeviceFileLogger.class);
    private File path;
    private File deviceDataPath;
    private File rawDataPath;

    public DeviceFileLogger(Map<String, String> params, File baseDir, String label) {
        File base1 = new File(baseDir, params.get("path"));
        if (!base1.exists()) {
            base1.mkdir();
        }
        this.path = new File(base1,label);
        if (!this.path.exists()) {
            this.path.mkdir();
        }
        deviceDataPath = new File(path, params.get("device-data-logging-path"));

        if (!this.deviceDataPath.exists()) {
            this.deviceDataPath.mkdir();
        }
        rawDataPath = new File(path, params.get("raw-data-logging-path"));

        if (!this.rawDataPath.exists()) {
            this.rawDataPath.mkdir();
        }
    }

    public void handleDevice(String deviceName, RawDeviceData rawData, DiscoveredDeviceData discoveredDeviceData, Resource resource) {
//        final String deviceFileName = path + File.separator + "device-data-" + deviceName + ".xml";
        final String deviceFileName = "device-data-" + deviceName + ".xml";
        OutputStream os = null;
        try {
            os = new FileOutputStream(new File(deviceDataPath,deviceFileName));
            JaxbMarshalar.marshal(discoveredDeviceData, os, "DiscoveredDevice");
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(),e);
        } catch (JAXBException e) {
            logger.error(e.getMessage(),e);
        } finally {
            if (os != null) try {os.close();} catch (IOException e) {}
        }
        byte[] data = rawData.getData();
//        final String rawDeviceName = path + File.separator + "raw-data-" + deviceName + ".xml";
        final String rawDeviceName = "raw-data-" + deviceName + ".xml";
        try {
            FileUtils.writeStringToFile(new File(rawDataPath,rawDeviceName), new String(data));
        } catch (IOException e) {
            logger.error("Can not log raw data file: " + rawDeviceName, e);
        }
    }

}
