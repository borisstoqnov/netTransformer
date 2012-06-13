/*
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

package net.itransformers.idiscover.discoverylisteners;

import net.itransformers.idiscover.core.DiscoveryListener;
import net.itransformers.idiscover.core.RawDeviceData;
import net.itransformers.idiscover.core.Resource;
import net.itransformers.idiscover.networkmodel.DiscoveredDeviceData;
import net.itransformers.idiscover.util.JaxbMarshalar;
import net.itransformers.utils.XsltTransformer;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class XmlTopologyDeviceLogger implements DiscoveryListener{
    static Logger logger = Logger.getLogger(XmlTopologyDeviceLogger.class);
    private String path;
    private File xsltFileName;

    public XmlTopologyDeviceLogger(Map<String, String> params) {
        this.path = params.get("path");
        this.xsltFileName = new File(System.getProperty("base.dir"),params.get("xslt"));
    }

    //    public XmlTopologyDeviceLogger(String path, File xsltFileName) {
//        this.path = path;
//        this.xsltFileName = xsltFileName;
//    }

    public void handleDevice(String deviceName, RawDeviceData rawData, DiscoveredDeviceData discoveredDeviceData, Resource resource) {
        ByteArrayOutputStream graphMLOutputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream os  = new ByteArrayOutputStream();
        try {
            JaxbMarshalar.marshal(discoveredDeviceData, os, "DiscoveredDevice");
        } catch (JAXBException e) {
            logger.error(e.getMessage(),e);
        }
        XsltTransformer transformer = new XsltTransformer();
        try {
            transformer.transformXML(new ByteArrayInputStream(os.toByteArray()), xsltFileName,graphMLOutputStream, null,null);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        try {
            final String fileName = "node-" + deviceName + ".graphml";
            String fullFileName = path + File.separator + fileName;
            final File nodeFile = new File(System.getProperty("base.dir"),fullFileName);
            FileUtils.writeStringToFile(nodeFile, new String(graphMLOutputStream.toByteArray()));
            FileWriter writer = new FileWriter(new File(System.getProperty("base.dir"),path + File.separator +"nodes-file-list.txt"),true);
            writer.append(String.valueOf(fileName)).append("\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, JAXBException {
        String path = "devices_and_models\\lab\\undirected";
        File dir = new File (path);
        String[] files = dir.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return (name.endsWith(".hml"));
            }
        });
        Map<String, String> params = new HashMap<String, String >();
        params.put("path", path);
        params.put("conf/xslt", "iDiscover\\conf\\xslt\\transformator3.xslt");
        XmlTopologyDeviceLogger logger = new XmlTopologyDeviceLogger(params);

        for (String fileName: files){
            FileInputStream is = new FileInputStream(path + File.separator + fileName);
            DiscoveredDeviceData discoveredDeviceData;
            try {
                discoveredDeviceData = JaxbMarshalar.unmarshal(DiscoveredDeviceData.class, is);
            } finally {
                is.close();
            }
            String deviceName = fileName.substring("device-".length(),fileName.length()-".xml".length());
            logger.handleDevice(deviceName, null, discoveredDeviceData, null);
        }

    }

}
