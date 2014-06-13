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

import com.tinkerpop.blueprints.impls.neo4j.Neo4jGraph;
import net.itransformers.idiscover.networkmodel.DiscoveredDeviceData;
import net.itransformers.idiscover.v2.core.NodeDiscoveryListener;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.utils.XmlFormatter;
import net.itransformers.utils.blueprints_patch.MyGraphMLWriter;
import net.itransformers.utils.neo4j.merge.Neo4jGraphmlMerger;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.test.TestGraphDatabaseFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

//import org.neo4j.graphdb.DynamicRelationshipType;
//import org.neo4j.graphdb.GraphDatabaseService;
//import org.neo4j.graphdb.Node;
//import org.neo4j.graphdb.Transaction;
//import javax.ws.rs.core.UriBuilder;

public class Neo4JGraphmlLoggerLogDiscoveryListener implements NodeDiscoveryListener {
    static Logger logger = Logger.getLogger(Neo4JGraphmlLoggerLogDiscoveryListener.class);
    File baseDir;
    File xsltFile;
    File graphmlDir;
    String labelDirName;

    private GraphDatabaseService graphdb;


    public Neo4JGraphmlLoggerLogDiscoveryListener(String labelDirName,String graphmDirName, String xsltFileName) {
        this.labelDirName = labelDirName;
        baseDir = new File(labelDirName);
        xsltFile = new File(xsltFileName);
        graphmlDir = new File(baseDir,graphmDirName);
        if (!graphmlDir.exists()) graphmlDir.mkdir();

        graphdb = new TestGraphDatabaseFactory().newImpermanentDatabaseBuilder()
                .setConfig(GraphDatabaseSettings.nodestore_mapped_memory_size, "10M")
                .setConfig(GraphDatabaseSettings.string_block_size, "60")
                .setConfig( GraphDatabaseSettings.array_block_size, "300" )
                .setConfig( GraphDatabaseSettings.node_auto_indexing, "true" )
                .setConfig( GraphDatabaseSettings.node_keys_indexable, "true" )
                .setConfig(GraphDatabaseSettings.relationship_auto_indexing, "true")
                .setConfig( GraphDatabaseSettings.relationship_keys_indexable, "true" )
                .setConfig( GraphDatabaseSettings.node_keys_indexable, "id" )
                .setConfig( GraphDatabaseSettings.relationship_keys_indexable, "id" )
                .newGraphDatabase();
    }

    @Override
    public void nodeDiscovered(NodeDiscoveryResult discoveryResult) {

        String deviceName = discoveryResult.getNodeId();
        DiscoveredDeviceData discoveredDeviceData = (DiscoveredDeviceData) discoveryResult.getDiscoveredData("deviceData");
        ByteArrayOutputStream graphMLOutputStream = new ByteArrayOutputStream();


        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            final String fileName = "node-" + deviceName + ".graphml";
//            String fullFileName = path + File.separator + fileName;
            final File nodeFile = new File(graphmlDir,fileName);
//            System.out.println(new String(graphMLOutputStream.toByteArray()));
            String graphml = new XmlFormatter().format(new String(graphMLOutputStream.toByteArray()));
            FileUtils.writeStringToFile(nodeFile, graphml);
            FileWriter writer = new FileWriter(new File(labelDirName,"undirected"+".graphmls"),true);
            writer.append(String.valueOf(fileName)).append("\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            Neo4jGraphmlMerger neo4jMerger = new Neo4jGraphmlMerger();
            Transaction tx = graphdb.beginTx();
            File file = new File("src/test/java/net/itransformers/utils/neo4j/merge/1.graphml");

            neo4jMerger.merge(graphdb, file);

        Neo4jGraph neo4jGraph = new Neo4jGraph(graphdb);
            neo4jGraph.removeVertex(neo4jGraph.getVertex(0));
            MyGraphMLWriter writer = new MyGraphMLWriter(neo4jGraph);
            writer.setNormalize(true);
            writer.setVertexIdKey("id");
            writer.setEdgeIdKey("id");
            String expectedResult = FileUtils.readFileToString(file);
            ByteArrayOutputStream os = new ByteArrayOutputStream(expectedResult.length());
            writer.outputGraph(os, "undirected");
            tx.success();
            Assert.assertEquals(expectedResult.replaceAll("\r", ""), os.toString().replaceAll("\r", ""));


        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }



}
