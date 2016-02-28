/*
 * Neo4JGraphmlLoggerLogDiscoveryListener.java
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

import net.itransformers.idiscover.networkmodel.DiscoveredDeviceData;
import net.itransformers.idiscover.util.JaxbMarshalar;
import net.itransformers.idiscover.v2.core.NodeDiscoveryListener;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.utils.XmlFormatter;
import net.itransformers.utils.XsltTransformer;
import net.itransformers.utils.neo4j.merge.Neo4jGraphmlMerger;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;

//import org.neo4j.graphdb.GraphDatabaseService;


public class Neo4JGraphmlLoggerLogDiscoveryListener implements NodeDiscoveryListener {
    static Logger logger = Logger.getLogger(Neo4JGraphmlLoggerLogDiscoveryListener.class);
    String labelDirName;
    String graphmDirName;
    String xsltFileName;
    String graphDbUrl;
    String dataType;
    String projectPath;
    @Override
    public void nodeDiscovered(NodeDiscoveryResult discoveryResult) {
        File baseDir = new File(projectPath,labelDirName);
        File xsltFile = new File(xsltFileName);
        File graphmlDir = new File(baseDir,graphmDirName);

         GraphDatabaseService graphdb = new org.neo4j.rest.graphdb.RestGraphDatabase(graphDbUrl);

        if (!graphmlDir.exists()) graphmlDir.mkdir();

        String deviceName = discoveryResult.getNodeId();
        DiscoveredDeviceData discoveredDeviceData = (DiscoveredDeviceData) discoveryResult.getDiscoveredData(dataType);
        ByteArrayOutputStream graphMLOutputStream = new ByteArrayOutputStream();


        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            JaxbMarshalar.marshal(discoveredDeviceData, out, "DiscoveredDevice");
        } catch (JAXBException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        XsltTransformer transformer = new XsltTransformer();
        try {
            transformer.transformXML(new ByteArrayInputStream(out.toByteArray()), xsltFile,graphMLOutputStream, null,null);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }


            final String fileName = "node-" + deviceName + ".graphml";
            final File nodeFile = new File(graphmlDir,fileName);
        String unformatedGraphml = new String(graphMLOutputStream.toByteArray());

        try {
            String graphml = new XmlFormatter().format(unformatedGraphml);

            FileUtils.writeStringToFile(nodeFile, graphml);
            FileWriter writer = new FileWriter(new File(labelDirName,"undirected"+".graphmls"),true);
            writer.append(String.valueOf(fileName)).append("\n");
            writer.close();
        } catch (IOException e) {
            logger.debug("Unformated xml is not in correct format: \n"+unformatedGraphml);
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }


        Neo4jGraphmlMerger neo4jMerger = new Neo4jGraphmlMerger(graphdb, "v1");
        Transaction tx = graphdb.beginTx();

        try {
            tx = graphdb.beginTx();
            neo4jMerger.merge(nodeFile);
            tx.success();
        } catch (Exception e) {
            if (tx != null) tx.failure();
        } finally {
            if (tx != null) tx.finish();
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

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }
}
