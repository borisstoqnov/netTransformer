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
import net.itransformers.utils.XsltTransformer;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class sdnDeviceXmlFileLogDiscoveryListener implements NodeDiscoveryListener {
    static Logger logger = Logger.getLogger(sdnDeviceXmlFileLogDiscoveryListener.class);
    String deviceXmlDataDirName;
    String baseDirName;
    String deviceXmlXsltTransformator;



    // walker = (JsonDiscoverer) new DefaultDiscovererFactory().createDiscoverer(resource);
    @Override
    public void nodeDiscovered(NodeDiscoveryResult discoveryResult) {

        File baseDir = new File(baseDirName);
        if (!baseDir.exists()) baseDir.mkdir();

        File deviceXmlXslt = new File(System.getProperty("base.dir"), deviceXmlXsltTransformator);

        File deviceXmlDataDir = new File(deviceXmlDataDirName);

        if (!deviceXmlDataDir.exists()) deviceXmlDataDir.mkdir();

        String deviceName = discoveryResult.getNodeId();


        String rawDataXml = new String((byte[]) discoveryResult.getDiscoveredData("rawData"));

        try {
            //Create DeviceXml File
            ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
            ByteArrayInputStream inputStream;
            XsltTransformer transformer = new XsltTransformer();

            inputStream = new ByteArrayInputStream(rawDataXml.getBytes());

            transformer.transformXML(inputStream, deviceXmlXslt, outputStream1, null, null);
            logger.info("Transforming raw-data to DeviceXml for "+deviceName);
            logger.debug("Raw Data \n" + rawDataXml.toString());

            File deviceXmlFile = new File(deviceXmlDataDir, "node"+"-"+"floodLight"+"-"+deviceName+".xml");
            FileUtils.writeStringToFile(deviceXmlFile, outputStream1.toString());
            logger.info("Raw-data transformed to device-xml for "+deviceName);

            logger.debug("Device Data \n" + outputStream1.toString());

        } catch (IOException e) {
            logger.error(e);
        } catch (ParserConfigurationException e) {
            logger.error(e); //To change body of catch statement use File | Settings | File Templates.
        } catch (SAXException e) {
            logger.error(e);  //To change body of catch statement use File | Settings | File Templates.
        } catch (TransformerException e) {
            logger.error(e);  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public String getBaseDirName() {
        return baseDirName;
    }

    public void setBaseDirName(String baseDirName) {
        this.baseDirName = baseDirName;
    }
    public String getdeviceXmlDataDirName() {
        return deviceXmlDataDirName;
    }

    public void setdeviceXmlDataDirName(String deviceXmlDataDirName) {
        this.deviceXmlDataDirName = deviceXmlDataDirName;
    }

    public String getdeviceXmlXsltTransformator() {
        return deviceXmlXsltTransformator;
    }

    public void setdeviceXmlXsltTransformator(String deviceXmlXsltTransformator) {
        this.deviceXmlXsltTransformator = deviceXmlXsltTransformator;
    }

}
