package net.itransformers.utils.neo4j.merge;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import net.itransformers.utils.blueprints_patch.MyGraphMLReader;
import net.itransformers.utils.graphmlmerge.DefaultMergeConflictResolver;
import net.itransformers.utils.graphmlmerge.MergeConflictResolver;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.ReadableIndex;
import org.neo4j.graphdb.index.ReadableRelationshipIndex;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.neo4j.rest.graphdb.util.QueryResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Neo4jGraphmlMerger {
    private GraphDatabaseService dbService;
    private final String version;
    private Map<String, MergeConflictResolver> edgeConflictResolverMap;
    private Map<String, MergeConflictResolver> vertexConflictResolverMap;
    private DefaultMergeConflictResolver defaultMergeConflictResolver = new DefaultMergeConflictResolver();
    private RestCypherQueryEngine engine;

    public Neo4jGraphmlMerger(GraphDatabaseService dbService, String version) {
        this.version = version;
        edgeConflictResolverMap = new HashMap<String, MergeConflictResolver>();
        vertexConflictResolverMap = new HashMap<String, MergeConflictResolver>();
        this.dbService = dbService;
        // ugly but fact
        this.engine = new RestCypherQueryEngine(((RestGraphDatabase)dbService).getRestAPI());
    }

    public Neo4jGraphmlMerger(GraphDatabaseService dbService, Map<String, MergeConflictResolver> edgeConflictResolverMap, Map<String, MergeConflictResolver> vertexConflictResolverMap,
                              String version) {
        this.edgeConflictResolverMap = edgeConflictResolverMap;
        this.vertexConflictResolverMap = vertexConflictResolverMap;
        this.version = version;
        this.dbService = dbService;
        this.engine = new RestCypherQueryEngine(((RestGraphDatabase)dbService).getRestAPI());

    }

//    public void merge(GraphDatabaseService dbService, File[] files) throws IOException {
//        for (File file : files) {
//            merge(dbService, file);
//        }
//    }


    public void merge(File file) throws IOException {
        Graph graph2 = new TinkerGraph();
        FileInputStream in2 = new FileInputStream(file);
        MyGraphMLReader reader2 = new MyGraphMLReader(graph2);
        reader2.inputGraph(in2);
        merge(graph2);
    }

    public void merge(Graph graph2) {
        mergeGraphVertexes(graph2);
        mergeGraphEdges(graph2);
    }

    private void mergeGraphEdges(Graph graph2) {
        for (Edge edge2 : graph2.getEdges()) {
            mergeEdge(edge2);
        }
    }

    private void mergeEdge(Edge edge2) {
        Relationship edge1 = getEdgeById(edge2.getId());
        if (edge1 == null) {
//            Node outVertex1 = getNodeById(edge2.getVertex(Direction.OUT).getId());
//            Node outVertex2 = getNodeById(edge2.getVertex(Direction.IN).getId());
//            DynamicRelationshipType dynamicRelationshipType = DynamicRelationshipType.withName(edge2.getLabel());
//            edge1 = outVertex1.createRelationshipTo(outVertex2, dynamicRelationshipType);
//            edge1.setProperty("__id",edge2.getId());
//            edge1.setProperty("__version",version);
//            for (String key2 : edge2.getPropertyKeys()) {
//                edge1.setProperty(key2,edge2.getProperty(key2));
//            }
            HashMap<String, Object> props = new HashMap<String, Object>();
            props.put("__id", edge2.getId());
            props.put("__version", version);
            Set<String> keys2 = edge2.getPropertyKeys();
            for (String key2 : keys2) {
                props.put(key2, edge2.getProperty(key2));
            }
            String propsJson = propertiesToJson(props);

            String query = "MATCH " +
                   " (a { __id: '"+edge2.getVertex(Direction.OUT).getId()+"', __version: '"+version+"'})," +
                   " (b { __id: '"+edge2.getVertex(Direction.IN).getId()+"', __version: '"+version+"'}) \n" +
                   "CREATE (a)-[r:RELTYPE "+propsJson+"]->(b)\n" +
                   "RETURN r";
            QueryResult<Map<String, Object>> result = engine.query(query, null);
            if (result.iterator().hasNext()){
                edge1 = (Relationship) result.iterator().next().get("r");
            } else {
                System.out.println("can not create edge");
            }
        } else {
            Iterable<String> keys1Iter = edge1.getPropertyKeys();
            final Set<String> keys1 = new HashSet<String>();
            for (String key : keys1Iter) keys1.add(key);
            Set<String> keys2 = edge2.getPropertyKeys();
            for (String key2 : keys2) {
                if (keys1.contains(key2)) {
                    MergeConflictResolver conflictResolver = getEdgeConflictResolver(key2);
                    Object merge =  conflictResolver.resolveConflict(edge1.getProperty(key2), edge2.getProperty(key2));
                    edge1.setProperty(key2, merge);
                } else {
                    edge1.setProperty(key2, edge2.getProperty(key2));
                }
            }
        }
    }

    private void mergeGraphVertexes(Graph graph2) {
        for (Vertex vertex2 : graph2.getVertices()) {
            mergeVertex(vertex2);
        }
    }

    private Node mergeVertex(Vertex vertex2) {
        Node vertex1 = getNodeById(vertex2.getId());
        if (vertex1 == null) {

//            vertex1 = dbService.createNode();
//            vertex1.setProperty("__id", vertex2.getId());
//            vertex1.setProperty("__version", version);
//            Set<String> keys2 = vertex2.getPropertyKeys();
//            for (String key2 : keys2) {
//                vertex1.setProperty(key2,vertex2.getProperty(key2));
//            }
            HashMap<String, Object> props = new HashMap<String, Object>();
            props.put("__id", vertex2.getId());
            props.put("__version", version);
            Set<String> keys2 = vertex2.getPropertyKeys();
            for (String key2 : keys2) {
                props.put(key2, vertex2.getProperty(key2));
            }
            String propsJson = propertiesToJson(props);
            String query = "CREATE (n " + propsJson + ") return n";
            QueryResult<Map<String, Object>> result = engine.query(query, null);
            vertex1 = (Node) result.iterator().next().get("n");
        } else {
            Iterable<String> keys1Iter = vertex1.getPropertyKeys();
            final Set<String> keys1 = new HashSet<String>();
            for (String key : keys1Iter) keys1.add(key);
            Set<String> keys2 = vertex2.getPropertyKeys();
            for (String key2 : keys2) {
                if (keys1.contains(key2)) {
                    MergeConflictResolver conflictResolver = getVertexConflictResolver(key2);
                    Object merge = conflictResolver.resolveConflict(vertex1.getProperty(key2), vertex2.getProperty(key2));
                    vertex1.setProperty(key2,merge);
                } else {
                    vertex1.setProperty(key2,vertex2.getProperty(key2));
                }
            }
        }
        return vertex1;
    }

    private String propertiesToJson(HashMap<String, Object> props) {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean isFirst = true;
        for (String key : props.keySet()) {
            if (!isFirst) {
                sb.append(", ");
            }
            Object val = props.get(key);
            if (val instanceof String && ((String)val).contains("'")) {
                val = ((String)val).replaceAll("'","\\\\'");
            }
            sb.append(key).append(":").append("'").append(val).append("'");
            isFirst = false;
        }
        sb.append("}");
        return sb.toString();
    }

    private Relationship getEdgeById( Object id){
        ReadableRelationshipIndex index = dbService.index().getRelationshipAutoIndexer().getAutoIndex();
        IndexHits<Relationship> result = index.query(String.format("__id:\"%s\" AND __version:\"%s\"", id, version));
        if (result.hasNext()) {
            return result.getSingle();
        } else {
            return null;
        }

    }
    private Node getNodeById(Object id){
        ReadableIndex<Node> index = dbService.index().getNodeAutoIndexer().getAutoIndex();
        IndexHits<Node> result = index.query(String.format("__id:\"%s\" AND __version:\"%s\"", id, version));
        if (result.hasNext()) {
            return result.getSingle();
        } else {
            return null;
        }
    }

    private MergeConflictResolver getVertexConflictResolver(String key){
        MergeConflictResolver conflictResolver = vertexConflictResolverMap.get(key);
        if (conflictResolver == null) {
            return defaultMergeConflictResolver;
        } else {
            return conflictResolver;
        }
    }
    private MergeConflictResolver getEdgeConflictResolver(String key){
        MergeConflictResolver conflictResolver = edgeConflictResolverMap.get(key);
        if (conflictResolver == null) {
            return defaultMergeConflictResolver;
        } else {
            return conflictResolver;
        }
    }

}
