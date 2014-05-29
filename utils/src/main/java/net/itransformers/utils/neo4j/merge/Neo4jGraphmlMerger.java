package net.itransformers.utils.neo4j.merge;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import net.itransformers.utils.graphmlmerge.DefaultMergeConflictResolver;
import net.itransformers.utils.graphmlmerge.MergeConflictResolver;
import net.itransformers.utils.blueprints_patch.MyGraphMLReader;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.index.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Neo4jGraphmlMerger {
    private Map<String, MergeConflictResolver> edgeConflictResolverMap;
    private Map<String, MergeConflictResolver> vertexConflictResolverMap;
    private DefaultMergeConflictResolver defaultMergeConflictResolver = new DefaultMergeConflictResolver();

    public Neo4jGraphmlMerger() {
        edgeConflictResolverMap = new HashMap<String, MergeConflictResolver>();
        vertexConflictResolverMap = new HashMap<String, MergeConflictResolver>();
    }

    public Neo4jGraphmlMerger(Map<String, MergeConflictResolver> edgeConflictResolverMap, Map<String, MergeConflictResolver> vertexConflictResolverMap) {
        this.edgeConflictResolverMap = edgeConflictResolverMap;
        this.vertexConflictResolverMap = vertexConflictResolverMap;
    }

    public void merge(GraphDatabaseService dbService, File[] files) throws IOException {
        for (File file : files) {
            merge(dbService, file);
        }
    }


    public void merge(GraphDatabaseService dbService, File file) throws IOException {
        Graph graph2 = new TinkerGraph();
        FileInputStream in2 = new FileInputStream(file);
        MyGraphMLReader reader2 = new MyGraphMLReader(graph2);
        reader2.inputGraph(in2);
        merge(dbService, graph2);
    }

    public void merge(GraphDatabaseService dbService, Graph graph2) {
        mergeGraphVertexes(dbService, graph2);
        mergeGraphEdges(dbService, graph2);
    }

    private void mergeGraphEdges(GraphDatabaseService dbService, Graph graph2) {
        for (Edge edge2 : graph2.getEdges()) {
            mergeEdge(dbService, edge2);
        }
    }

    private void mergeEdge(GraphDatabaseService dbService, Edge edge2) {
        Relationship edge1 = getEdgeById(dbService,edge2.getId());
        if (edge1 == null) {
            Node outVertex1 = getNodeById(dbService, edge2.getVertex(Direction.OUT).getId());
            Node outVertex2 = getNodeById(dbService, edge2.getVertex(Direction.IN).getId());
            edge1 = outVertex1.createRelationshipTo(outVertex2, DynamicRelationshipType.withName(edge2.getLabel()));//dbService.addEdge(edge2.getId(), outVertex1, outVertex2, edge2.getLabel());
            edge1.setProperty("id",edge2.getId());
            for (String key2 : edge2.getPropertyKeys()) {
                edge1.setProperty(key2,edge2.getProperty(key2));
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

    private void mergeGraphVertexes(GraphDatabaseService dbService, Graph graph2) {
        for (Vertex vertex2 : graph2.getVertices()) {
            mergeVertex(dbService, vertex2);
        }
    }

    private Node mergeVertex(GraphDatabaseService dbService, Vertex vertex2) {
        Node vertex1 = getNodeById(dbService, vertex2.getId());
        if (vertex1 == null) {
            vertex1 = dbService.createNode();
            vertex1.setProperty("id", vertex2.getId());
            Set<String> keys2 = vertex2.getPropertyKeys();
            for (String key2 : keys2) {
                vertex1.setProperty(key2,vertex2.getProperty(key2));
            }
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

    private Relationship getEdgeById(GraphDatabaseService dbService, Object id){
        ReadableRelationshipIndex index = dbService.index().getRelationshipAutoIndexer().getAutoIndex();
        IndexHits<Relationship> result = index.get("id", id);
        if (result.hasNext()) {
            return result.getSingle();
        } else {
            return null;
        }

    }
    private Node getNodeById(GraphDatabaseService dbService, Object id){
        ReadableIndex<Node> index = dbService.index().getNodeAutoIndexer().getAutoIndex();
        IndexHits<Node> result = index.get("id", id);
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
