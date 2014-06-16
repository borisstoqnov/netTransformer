package net.itransformers.ideas.neo4j.importer;

import com.tinkerpop.blueprints.impls.neo4j.Neo4jGraph;
import net.itransformers.utils.blueprints_patch.MyGraphMLWriter;
import net.itransformers.utils.neo4j.merge.Neo4jGraphmlMerger;
import org.apache.commons.io.FileUtils;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Created by vasko on 6/16/14.
 */
public class Neo4jGraphmlImporter {
    public static void main(String[] args) throws IOException {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("neo4j-spring.xml");
        GraphDatabaseService graphdb = applicationContext.getBean("graphDbService", GraphDatabaseService.class);
//        graphdb.setConfig(GraphDatabaseSettings.string_block_size, "60")
//                .setConfig( GraphDatabaseSettings.array_block_size, "300" )
//                .setConfig( GraphDatabaseSettings.node_auto_indexing, "true" )
//                .setConfig( GraphDatabaseSettings.node_keys_indexable, "true" )
//                .setConfig(GraphDatabaseSettings.relationship_auto_indexing, "true")
//                .setConfig( GraphDatabaseSettings.relationship_keys_indexable, "true" )
//                .setConfig( GraphDatabaseSettings.node_keys_indexable, "id" )
//                .setConfig( GraphDatabaseSettings.relationship_keys_indexable, "id" )

        Neo4jGraphmlMerger neo4jMerger = new Neo4jGraphmlMerger();
        Transaction tx = graphdb.beginTx();

        File file = new File("src/main/resources/graphml/1.graphml");
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

    }
}
