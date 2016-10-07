

/*
 * sdnDeviceXmlFileLogDiscoveryListener.java
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
import net.itransformers.idiscover.api.NodeDiscoveryListener;
import net.itransformers.idiscover.api.NodeDiscoveryResult;
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

public class sdnDeviceXmlFileLogDiscoveryListener implements NodeDiscoveryListener {
    static Logger logger = Logger.getLogger(sdnDeviceXmlFileLogDiscoveryListener.class);
    String deviceXmlDataDirName;
    String labelDirName;



    String deviceXmlXsltTransformator;



    // walker = (JsonDiscoverer) new DefaultDiscovererFactory().createDiscoverer(resource);
    @Override
    public void nodeDiscovered(NodeDiscoveryResult discoveryResult) {

        File baseDir = new File(labelDirName);
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

            transformer.transformXML(inputStream, deviceXmlXslt, outputStream1);
            logger.info("Transforming raw-data to DeviceXml for "+deviceName);
            logger.debug("Raw Data \n" + rawDataXml.toString());

            File deviceXmlFile = new File(deviceXmlDataDir, "node"+"-"+"floodLight"+"-"+deviceName+".xml");
            FileUtils.writeStringToFile(deviceXmlFile, outputStream1.toString());
            logger.info("Raw-data transformed to device-xml for "+deviceName);

            logger.debug("Node Data \n" + outputStream1.toString());

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
    public String getDeviceXmlDataDirName() {
        return deviceXmlDataDirName;
    }

    public void setDeviceXmlDataDirName(String deviceXmlDataDirName) {
        this.deviceXmlDataDirName = deviceXmlDataDirName;
    }

    public String getLabelDirName() {
        return labelDirName;
    }

    public void setLabelDirName(String labelDirName) {
        this.labelDirName = labelDirName;
    }

    public String getDeviceXmlXsltTransformator() {
        return deviceXmlXsltTransformator;
    }

    public void setDeviceXmlXsltTransformator(String deviceXmlXsltTransformator) {
        this.deviceXmlXsltTransformator = deviceXmlXsltTransformator;
    }


}
