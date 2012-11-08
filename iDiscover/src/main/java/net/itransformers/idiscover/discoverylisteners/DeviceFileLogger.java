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
    private String path;

    public DeviceFileLogger(Map<String, String> params) {
        this.path = params.get("path");
        this.path = path;
    }
//    public DeviceFileLogger(String path) {
//        this.path = path;
//    }

    public void handleDevice(String deviceName, RawDeviceData rawData, DiscoveredDeviceData discoveredDeviceData, Resource resource) {
        final String deviceFileName = path + File.separator + "device-data-" + deviceName + ".xml";
        OutputStream os = null;
        try {
            os = new FileOutputStream(new File(System.getProperty("base.dir"),deviceFileName));
            JaxbMarshalar.marshal(discoveredDeviceData, os, "DiscoveredDevice");
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage(),e);
        } catch (JAXBException e) {
            logger.error(e.getMessage(),e);
        } finally {
            if (os != null) try {os.close();} catch (IOException e) {}
        }
        byte[] data = rawData.getData();
        final String rawDeviceName = path + File.separator + "raw-data-" + deviceName + ".xml";
        try {
            FileUtils.writeStringToFile(new File(System.getProperty("base.dir"),rawDeviceName), new String(data));
        } catch (IOException e) {
            logger.error("Can not log raw data file: " + rawDeviceName, e);
        }
    }

}
