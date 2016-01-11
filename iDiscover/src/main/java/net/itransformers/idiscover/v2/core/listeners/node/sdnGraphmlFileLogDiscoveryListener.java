

package net.itransformers.idiscover.v2.core.listeners.node;


import net.itransformers.idiscover.v2.core.NodeDiscoveryListener;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.resourcemanager.ResourceManager;
import net.itransformers.resourcemanager.config.ConnectionParamsType;
import net.itransformers.resourcemanager.config.ParamType;
import net.itransformers.resourcemanager.config.ResourceType;
import net.itransformers.resourcemanager.config.ResourcesType;
import net.itransformers.utils.XsltTransformer;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class sdnGraphmlFileLogDiscoveryListener implements NodeDiscoveryListener {
    static Logger logger = Logger.getLogger(sdnGraphmlFileLogDiscoveryListener.class);
    String graphmlDataDirName;
    String labelDirName;
    String floodLighGraphmlXsltTransformator;
    private File projectPath;
    private String resourceManagerPath;



    // walker = (JsonDiscoverer) new DefaultDiscovererFactory().createDiscoverer(resource);
    @Override
    public void nodeDiscovered(NodeDiscoveryResult discoveryResult) {

        String xml = null;
        try {
            xml = FileUtils.readFileToString(new File(projectPath, resourceManagerPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream is1 = new ByteArrayInputStream(xml.getBytes());
        ResourcesType deviceGroupsType = null;

        try {
            deviceGroupsType = net.itransformers.resourcemanager.util.JaxbMarshalar.unmarshal(ResourcesType.class, is1);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        ResourceManager resourceManager = new ResourceManager(deviceGroupsType);
        final Map<String, String> params = new HashMap<String, String>();
        params.put("deviceName",discoveryResult.getNodeId());


        ResourceType resource =  resourceManager.findResource(params);



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




            if(resource!=null){
                List connectParameters = resource.getConnectionParams();

                for (int i = 0; i < connectParameters.size(); i++) {
                    ConnectionParamsType connParamsType = (ConnectionParamsType) connectParameters.get(i);

                    String connectionType = connParamsType.getConnectionType();
                    if (connectionType.equalsIgnoreCase("snmp")) {

                        for (ParamType param : connParamsType.getParam()) {
                            params.put(param.getName(), param.getValue());
                        }

                    }
                }


            }else {
                transformer.transformXML(inputStream, floodlightGraphmlXslt, outputStream1, params, null);
            }

            final  String graphmlFileName = "floodLight"+"-"+deviceName+".graphml";
            logger.info("Transforming raw-data to graphml for "+deviceName);
            logger.debug("Raw Data \n" + rawDataXml.toString());

            File graphmlFile = new File(graphmlDataDir,graphmlFileName );
            FileUtils.writeStringToFile(graphmlFile, outputStream1.toString());
            logger.info("Raw-data transformed to graphml for " + deviceName);

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