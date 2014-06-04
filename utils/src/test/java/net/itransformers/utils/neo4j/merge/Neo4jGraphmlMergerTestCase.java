package net.itransformers.utils.neo4j.merge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.neo4j.Neo4jGraph;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import net.itransformers.utils.graphmlmerge.MergeConflictResolver;
import net.itransformers.utils.blueprints_patch.MyGraphMLWriter;
import net.itransformers.utils.neo4j.merge.Neo4jGraphmlMerger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.neo4j.graphdb.index.*;
import org.neo4j.test.TestGraphDatabaseFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by vasko on 5/27/14.
 */
public class Neo4jGraphmlMergerTestCase {
    private GraphDatabaseService graphdb;

    @Before
    public void setUp() throws Exception {

        graphdb = new TestGraphDatabaseFactory().newImpermanentDatabaseBuilder()
//                .setConfig(GraphDatabaseSettings.nodestore_mapped_memory_size, "10M")
                .setConfig(GraphDatabaseSettings.string_block_size, "60")
                .setConfig( GraphDatabaseSettings.array_block_size, "300" )
                .setConfig( GraphDatabaseSettings.node_auto_indexing, "true" )
                .setConfig( GraphDatabaseSettings.node_keys_indexable, "true" )
                .setConfig(GraphDatabaseSettings.relationship_auto_indexing, "true")
                .setConfig( GraphDatabaseSettings.relationship_keys_indexable, "true" )
                .setConfig( GraphDatabaseSettings.node_keys_indexable, "id" )
                .setConfig( GraphDatabaseSettings.relationship_keys_indexable, "id" )
                .newGraphDatabase();
//        Transaction tx = null;
//        try {
//            tx = graphdb.beginTx();
//            Schema schema = graphdb.schema();
//            schema.indexFor(DynamicLabel.label("Node")).on("id").create();
//            schema.indexFor(DynamicLabel.label("Edge")).on("id").create();
//            tx.success();
//        } catch (Exception e) {
//            if (tx != null) tx.failure();
//            throw e;
//        } finally {
//            if (tx != null) tx.close();
//        }
    }
    @Test
    public void testMerge() throws IOException {
        Neo4jGraphmlMerger neo4jMerger = new Neo4jGraphmlMerger();
        Transaction tx = graphdb.beginTx();
        File file = new File("src/test/java/net/itransformers/utils/neo4j/merge/1.graphml");
        neo4jMerger.merge(graphdb, file);
        Neo4jGraph neo4jGraph = new Neo4jGraph(graphdb);
        neo4jGraph.removeVertex(neo4jGraph.getVertex(0));
        MyGraphMLWriter  writer = new MyGraphMLWriter(neo4jGraph);
        writer.setNormalize(true);
        writer.setVertexIdKey("id");
        writer.setEdgeIdKey("id");
        String expectedResult = FileUtils.readFileToString(file);
        ByteArrayOutputStream os = new ByteArrayOutputStream(expectedResult.length());
        writer.outputGraph(os, "undirected");
        tx.success();
        Assert.assertEquals(expectedResult.replaceAll("\r",""), os.toString().replaceAll("\r",""));

    }
    @Test
    public void testMerge2() throws IOException {
        File file1 = new File("src/test/java/net/itransformers/utils/neo4j/merge/1.graphml");
        File file2 = new File("src/test/java/net/itransformers/utils/neo4j/merge/2.graphml");
        File file3 = new File("src/test/java/net/itransformers/utils/neo4j/merge/3.graphml");
        Neo4jGraphmlMerger neo4jMerger = new Neo4jGraphmlMerger();
        Transaction tx = graphdb.beginTx();

        neo4jMerger.merge(graphdb, file1);
        tx.success();

        tx = graphdb.beginTx();
        neo4jMerger.merge(graphdb, file2);
        tx.success();

        Neo4jGraph neo4jGraph = new Neo4jGraph(graphdb);
        neo4jGraph.removeVertex(neo4jGraph.getVertex(0));
        MyGraphMLWriter  writer = new MyGraphMLWriter(neo4jGraph);
        writer.setNormalize(true);
        writer.setVertexIdKey("id");
        writer.setEdgeIdKey("id");
        String expectedResult = FileUtils.readFileToString(file3);
        ByteArrayOutputStream os = new ByteArrayOutputStream(expectedResult.length());
        writer.outputGraph(os, "undirected");
        Assert.assertEquals(expectedResult.replaceAll("\r",""), os.toString().replaceAll("\r",""));

    }

    @Test
    public void testVertexMerge() throws IOException {
        File file1 = new File("src/test/java/net/itransformers/utils/neo4j/merge/1.graphml");
        File expectedFile = new File("src/test/java/net/itransformers/utils/neo4j/merge/vertex_merge1.graphml");
        Neo4jGraphmlMerger neo4jMerger = new Neo4jGraphmlMerger();
        Transaction tx = graphdb.beginTx();

        neo4jMerger.merge(graphdb, file1);
        tx.success();

        tx = graphdb.beginTx();

        Graph graph = new TinkerGraph();
        Vertex vertex = graph.addVertex("5");
        vertex.setProperty("lang","java2");
        vertex.setProperty("name","ripple2");
        neo4jMerger.merge(graphdb, graph);

        tx.success();

        Neo4jGraph neo4jGraph = new Neo4jGraph(graphdb);
        neo4jGraph.removeVertex(neo4jGraph.getVertex(0));
        MyGraphMLWriter  writer = new MyGraphMLWriter(neo4jGraph);
        writer.setNormalize(true);
        writer.setVertexIdKey("id");
        writer.setEdgeIdKey("id");
        String expectedResult = FileUtils.readFileToString(expectedFile);
        ByteArrayOutputStream os = new ByteArrayOutputStream(expectedResult.length());
        writer.outputGraph(os, "undirected");
        Assert.assertEquals(expectedResult.replaceAll("\r",""), os.toString().replaceAll("\r",""));

    }

//    @Test
    public void testMerge3() throws IOException {
        Transaction tx = graphdb.beginTx();

        File f1 = new File("src/test/java/net/itransformers/utils/graphmlmerge/version5/device-centric/node-R11.graphml");
        File f2 = new File("src/test/java/net/itransformers/utils/graphmlmerge/version5/device-centric/node-R2.graphml");
        File f3 = new File("src/test/java/net/itransformers/utils/graphmlmerge/version5/device-centric/expected.graphml");
        File f3Actual = File.createTempFile("graphml_merge",".xml");

        Map<String, MergeConflictResolver> vertexConflictResolver = new HashMap<String, MergeConflictResolver>();
        vertexConflictResolver.put("name", new MergeConflictResolver(){
            @Override
            public Object resolveConflict(Object srcValue, Object targetValue) {
                return srcValue + "-" + targetValue;
            }
        });
        Map<String, MergeConflictResolver> edgeConflictResolver = new HashMap<String, MergeConflictResolver>();
        edgeConflictResolver.put("weight", new MergeConflictResolver() {
            @Override
            public Object resolveConflict(Object srcValue1, Object targetValue) {
                if (srcValue1 instanceof Float && targetValue instanceof Float) {
                    return (Float)srcValue1 + (Float)targetValue;
                } else {
                    return srcValue1 + "#" + targetValue;
                }
            }
        });
        Map<String, String> edgesTypes = new HashMap<String, String>();
        edgesTypes.put("name","string");
        edgesTypes.put("method","string");
        edgesTypes.put("dataLink","string");
        edgesTypes.put("ipLink","string");
        edgesTypes.put("IPv4Forwarding","string");
        edgesTypes.put("IPv6Forwarding","string");
        edgesTypes.put("InterfaceNameA","string");
        edgesTypes.put("InterfaceNameB","string");
        edgesTypes.put("IPv4AddressA","string");
        edgesTypes.put("IPv4AddressB","string");
        edgesTypes.put("edgeTooltip","string");
        edgesTypes.put("diff","string");
        edgesTypes.put("diffs","string");

        Map<String, String> vertexTypes = new HashMap<String, String>();
        vertexTypes.put("deviceModel","string");
        vertexTypes.put("deviceType","string");
        vertexTypes.put("nodeInfo","string");
        vertexTypes.put("hostname","string");
        vertexTypes.put("deviceStatus","string");
        vertexTypes.put("ManagementIPAddress","string");
        vertexTypes.put("geoCoordinates","string");
        vertexTypes.put("site","string");
        vertexTypes.put("diff","string");
        vertexTypes.put("diffs","string");
        vertexTypes.put("diffs","string");
        vertexTypes.put("IPv6Forwarding","string");
        vertexTypes.put("IPv4Forwarding","string");
        new Neo4jGraphmlMerger(edgeConflictResolver, vertexConflictResolver).merge(graphdb,new File[]{f1, f2});
        tx.success();

        String xml3 = FileUtils.readFileToString(f3);
        String xml3Actual = FileUtils.readFileToString(f3Actual);
        Assert.assertEquals(xml3, xml3Actual);

        Neo4jGraph neo4jGraph = new Neo4jGraph(graphdb);
        neo4jGraph.removeVertex(neo4jGraph.getVertex(0));
        MyGraphMLWriter  writer = new MyGraphMLWriter(neo4jGraph);
        writer.setNormalize(true);
        writer.setVertexIdKey("id");
        writer.setEdgeIdKey("id");

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        writer.outputGraph(os, "undirected");
        Assert.assertEquals(os.toString().replaceAll("\r",""), os.toString().replaceAll("\r",""));

    }

    @Test
    public void testMerge4() throws IOException {
        Transaction tx = graphdb.beginTx();

        File f1 = new File("src/test/java/net/itransformers/utils/neo4j/merge/1.graphml");
        File f2 = new File("src/test/java/net/itransformers/utils/neo4j/merge/2.graphml");
        File f3 = new File("src/test/java/net/itransformers/utils/neo4j/merge/4.graphml");

        Map<String, MergeConflictResolver> vertexConflictResolver = new HashMap<String, MergeConflictResolver>();
        vertexConflictResolver.put("name", new MergeConflictResolver(){
            @Override
            public Object resolveConflict(Object srcValue, Object targetValue) {
                return srcValue + "-" + targetValue;
            }
        });
        Map<String, MergeConflictResolver> edgeConflictResolver = new HashMap<String, MergeConflictResolver>();
        edgeConflictResolver.put("weight", new MergeConflictResolver() {
            @Override
            public Object resolveConflict(Object srcValue1, Object targetValue) {
                if (srcValue1 instanceof Float && targetValue instanceof Float) {
                    return (Float)srcValue1 + (Float)targetValue;
                } else {
                    return srcValue1 + "#" + targetValue;
                }
            }
        });
        new Neo4jGraphmlMerger(edgeConflictResolver, vertexConflictResolver).merge(graphdb, new File[]{f1, f2});
        tx.success();

        Neo4jGraph neo4jGraph = new Neo4jGraph(graphdb);
        neo4jGraph.removeVertex(neo4jGraph.getVertex(0));
        MyGraphMLWriter  writer = new MyGraphMLWriter(neo4jGraph);
        writer.setNormalize(true);
        writer.setVertexIdKey("id");
        writer.setEdgeIdKey("id");

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        writer.outputGraph(os, "undirected");
        String expected = FileUtils.readFileToString(f3);
        Assert.assertEquals(expected.replaceAll("\r",""), os.toString().replaceAll("\r",""));

    }

    @Test
    public void testFindNode() throws IOException {
        Transaction tx = graphdb.beginTx();
        Node node = graphdb.createNode();
        node.setProperty("id","1234");
        ReadableIndex<Node> index = graphdb.index().getNodeAutoIndexer().getAutoIndex();
        Node foundNode = index.get("id", "1234").getSingle();
        Assert.assertEquals(node.getId(), foundNode.getId());
        tx.success();
    }

    @Test
    public void testFindRelation() throws IOException {
//        http://docs.neo4j.org/chunked/stable/auto-indexing.html
        Transaction tx = graphdb.beginTx();
        Node node1 = graphdb.createNode();
        Node node2 = graphdb.createNode();
        Relationship relationShip = node1.createRelationshipTo(node2, DynamicRelationshipType.withName("Edge"));
        relationShip.setProperty("id", "12345");
        ReadableRelationshipIndex index = graphdb.index().getRelationshipAutoIndexer().getAutoIndex();
        IndexHits<Relationship> result = index.get("id", "12345");
        Relationship foundRelationship = result.getSingle();
        Assert.assertEquals(relationShip.getId(), foundRelationship.getId());
        tx.success();

    }

    @After
    public void tearDown(){

    }
}
