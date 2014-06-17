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

        GraphDatabaseService graphdb = new org.neo4j.rest.graphdb.RestGraphDatabase("http://193.19.172.133:7474/db/data");
        Neo4jGraphmlMerger neo4jMerger = new Neo4jGraphmlMerger();
        Transaction tx = null;
        try {
            tx = graphdb.beginTx();

            File file = new File("src/main/resources/graphml/1.graphml");
            neo4jMerger.merge(graphdb, file);
            tx.success();
        } catch (Exception e) {
            if (tx != null) tx.failure();
        } finally {
            if (tx != null) tx.finish();
        }

    }
}
