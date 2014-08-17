

package net.itransformers.idiscover.v2.core.listeners;


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

public class sdnGraphmlFileLogDiscoveryListener implements NodeDiscoveryListener {
    static Logger logger = Logger.getLogger(sdnGraphmlFileLogDiscoveryListener.class);
    String graphmlDataDirName;
    String labelDirName;
    String floodLighGraphmlXsltTransformator;



    // walker = (JsonDiscoverer) new DefaultDiscovererFactory().createDiscoverer(resource);
    @Override
    public void nodeDiscovered(NodeDiscoveryResult discoveryResult) {

        File baseDir = new File(labelDirName);
        if (!baseDir.exists()) baseDir.mkdir();

        File floodlightGraphmlXslt = new File(System.getProperty("base.dir"), floodLighGraphmlXsltTransformator);

        File graphmlDataDir = new File(graphmlDataDirName);

        if (!graphmlDataDir.exists()) graphmlDataDir.mkdir();

        String deviceName = discoveryResult.getNodeId();


        String rawDataXml = new String((byte[]) discoveryResult.getDiscoveredData("rawData"));

        try {
            //Create graphml File
            ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
            ByteArrayInputStream inputStream;
            XsltTransformer transformer = new XsltTransformer();

            inputStream = new ByteArrayInputStream(rawDataXml.getBytes());

            transformer.transformXML(inputStream, floodlightGraphmlXslt, outputStream1, null, null);

            final  String graphmlFileName = "node"+"-"+"floodLight"+"-"+deviceName+".graphml";
            logger.info("Transforming raw-data to graphml for "+deviceName);
            logger.debug("Raw Data \n" + rawDataXml.toString());

            File graphmlFile = new File(graphmlDataDir,graphmlFileName );
            FileUtils.writeStringToFile(graphmlFile, outputStream1.toString());
            logger.info("Raw-data transformed to graphml for "+deviceName);

            File nodesFileListFile = new File(labelDirName, "nodes.graphmls");
            FileUtils.writeStringToFile(nodesFileListFile, graphmlFileName);
            logger.debug("Graphml Data \n" + outputStream1.toString());

        } catch (IOException e) {
            logger.error(e);
        } catch (ParserConfigurationException e) {
            logger.error(e);  //To change body of catch statement use File | Settings | File Templates.
        } catch (SAXException e) {
            logger.error(e);  //To change body of catch statement use File | Settings | File Templates.
        } catch (TransformerException e) {
            logger.error(e);  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public String getLabelDirName() {
        return labelDirName;
    }

    public void setLabelDirName(String labelDirName) {
        this.labelDirName = labelDirName;
    }
    public String getGraphmlDataDirName() {
        return graphmlDataDirName;
    }

    public void setGraphmlDataDirName(String graphmlDataDirName) {
        this.graphmlDataDirName = graphmlDataDirName;
    }

    public String getfloodLighGraphmlXsltTransformator() {
        return floodLighGraphmlXsltTransformator;
    }

    public void setfloodLighGraphmlXsltTransformator(String floodLighGraphmlXsltTransformator) {
        this.floodLighGraphmlXsltTransformator = floodLighGraphmlXsltTransformator;
    }

//    public void setfloodlightGraphmlXsltName(String floodlightGraphmlXslt) {
//        this.floodlightGraphmlXslt = floodlightGraphmlXslt;
//    }
}
