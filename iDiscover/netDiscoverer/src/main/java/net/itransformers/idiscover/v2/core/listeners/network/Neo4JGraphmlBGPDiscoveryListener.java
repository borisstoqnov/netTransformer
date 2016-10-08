

/*
 * Neo4JGraphmlBGPDiscoveryListener.java
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

package net.itransformers.idiscover.v2.core.listeners.network;

import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import net.itransformers.idiscover.api.NetworkDiscoveryListener;
import net.itransformers.idiscover.api.NetworkDiscoveryResult;
import net.itransformers.utils.blueprints_patch.MyGraphMLReader;
import net.itransformers.utils.neo4j.merge.Neo4jGraphmlMerger;
import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;

//import org.neo4j.graphdb.GraphDatabaseService;


public class Neo4JGraphmlBGPDiscoveryListener implements NetworkDiscoveryListener {
    static Logger logger = Logger.getLogger(Neo4JGraphmlBGPDiscoveryListener.class);
    String labelDirName;
    String graphmlDirName;
    String xsltFileName;
    String graphDbUrl;
    String dataType;
    String version;
    Date date;
    Map<String,String> params;



    public String getLabelDirName() {
        return labelDirName;
    }

    public void setLabelDirName(String labelDirName) {
        this.labelDirName = labelDirName;
    }

    public String getGraphmlDirName() {
        return graphmlDirName;
    }

    public void setGraphmlDirName(String graphmlDirName) {
        this.graphmlDirName = graphmlDirName;
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


    @Override
    public void networkDiscovered(NetworkDiscoveryResult result) {
        //To change body of implemented methods use File | Settings | File Templates.
        File baseDir = new File(labelDirName);
        File graphmlDir = new File(baseDir, graphmlDirName);
        logger.info("Starting Neo4JGraphml BGP Discovery Listener");

        GraphDatabaseService graphdb = new org.neo4j.rest.graphdb.RestGraphDatabase(graphDbUrl);


        if (!graphmlDir.exists()) graphmlDir.mkdir();

        version = (String)result.getDiscoveredData("version");
        byte[] discoveredDeviceData = (byte[]) result.getDiscoveredData("graphml");

        Graph graph2 = new TinkerGraph();
        ByteArrayInputStream in2 = new ByteArrayInputStream(discoveredDeviceData);

        MyGraphMLReader reader2 = new MyGraphMLReader(graph2);
        try {
            reader2.inputGraph(in2);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


        Neo4jGraphmlMerger neo4jMerger = new Neo4jGraphmlMerger(graphdb, version);
        Transaction tx = graphdb.beginTx();

        try {
            tx = graphdb.beginTx();
            neo4jMerger.merge(graph2);
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
}
