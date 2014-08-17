

package net.itransformers.idiscover.v2.core.listeners;

import net.itransformers.idiscover.v2.core.NodeDiscoveryListener;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.utils.XmlFormatter;
import net.itransformers.utils.XsltTransformer;
import net.itransformers.utils.neo4j.merge.Neo4jGraphmlMerger;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import java.io.*;
import java.util.Date;
import java.util.Map;

//import org.neo4j.graphdb.GraphDatabaseService;


public class Neo4JGraphmlBGPLoggerLogDiscoveryListener implements NodeDiscoveryListener {
    static Logger logger = Logger.getLogger(Neo4JGraphmlBGPLoggerLogDiscoveryListener.class);
    String labelDirName;
    String graphmDirName;
    String xsltFileName;
    String graphDbUrl;
    String dataType;
    String version;
    Date date;
    Map<String,String> params;

    @Override
    public void nodeDiscovered(NodeDiscoveryResult discoveryResult) {
        File baseDir = new File(labelDirName);
        File xsltFile = new File(xsltFileName);
        File graphmlDir = new File(baseDir,graphmDirName);
        logger.info("Starting Neo4JGraphml BGP Discovery Listener");

        GraphDatabaseService graphdb = new org.neo4j.rest.graphdb.RestGraphDatabase(graphDbUrl);

        if (!graphmlDir.exists()) graphmlDir.mkdir();

        String deviceName = discoveryResult.getNodeId();
        version = (String)discoveryResult.getDiscoveredData("version");
        byte [] discoveredDeviceData = (byte[]) discoveryResult.getDiscoveredData(dataType);
        ByteArrayOutputStream graphMLOutputStream = new ByteArrayOutputStream();


        //ByteArrayOutputStream out = new ByteArrayOutputStream();

        logger.info("Starting xslt transformation");

        XsltTransformer transformer = new XsltTransformer();
        try {
            transformer.transformXML(new ByteArrayInputStream(discoveredDeviceData), xsltFile,graphMLOutputStream, params,null);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }


            final String fileName = "node-" + deviceName + ".graphml";
            final File nodeFile = new File(graphmlDir,fileName);
            String graphml = new XmlFormatter().format(new String(graphMLOutputStream.toByteArray()));
        try {
            FileUtils.writeStringToFile(nodeFile, graphml);
            FileWriter writer = new FileWriter(new File(labelDirName,"undirected"+".graphmls"),true);
            writer.append(String.valueOf(fileName)).append("\n");
            writer.close();
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
        logger.info("Xslt transformation finished");

        logger.info("Connecting to neo4j graph datatabase!");

        Neo4jGraphmlMerger neo4jMerger = new Neo4jGraphmlMerger(version);
        Transaction tx = graphdb.beginTx();

        try {
            tx = graphdb.beginTx();
            neo4jMerger.merge(graphdb, nodeFile);
            tx.success();
            logger.info("Successfully merged data into neo4jdb!!");

        } catch (Exception e) {
            if (tx != null){
                tx.failure();
                logger.info("neo4j transaction failed with message:");
                logger.error(e.getMessage(),e);
            }
        } finally {
            if (tx != null){
                logger.info("neo4j transaction finished!");
                tx.finish();
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

    public String getXsltFileName() {
        return xsltFileName;
    }

    public void setXsltFileName(String xsltFileName) {
        this.xsltFileName = xsltFileName;
    }

    public String getGraphDbUrl() {
        return graphDbUrl;
    }

    public void setGraphDbUrl(String graphDbUrl) {
        this.graphDbUrl = graphDbUrl;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
