package net.itransformers.idiscover.v2.core.listeners;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.neo4j.rest.graphdb.util.QueryResult;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: niau
 * Date: 11/24/14
 * Time: 2:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class Neo4jTester {
    public static void main(String[] args) {
        GraphDatabaseService graphdb = new org.neo4j.rest.graphdb.RestGraphDatabase("http://54.69.21.203:7474/db/data");
        RestCypherQueryEngine engine = new RestCypherQueryEngine(((RestGraphDatabase)graphdb).getRestAPI());
        Transaction tx = graphdb.beginTx();
        try {
            QueryResult<Map<String, Object>> result = engine.query("CREATE (n { name : 'And\'res', title : 'Developer' }) return n", null);
            org.neo4j.graphdb.Node n = (Node) result.iterator().next().get("n");
            System.out.println(n);
            tx.success();
        } catch (Exception e) {
            if (tx != null){
                tx.failure();
            }
        }
    }
}
