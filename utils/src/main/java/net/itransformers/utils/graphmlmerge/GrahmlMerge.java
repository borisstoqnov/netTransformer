package net.itransformers.utils.graphmlmerge;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLReader;
import com.tinkerpop.blueprints.util.io.graphml.GraphMLWriter;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


public class GrahmlMerge {
   private Map<String, MergeConflictResolver> edgeConflictResolverMap;
   private Map<String, MergeConflictResolver> vertexConflictResolverMap;
   private DefaultMergeConflictResolver  defaultMergeConflictResolver = new DefaultMergeConflictResolver();

    public GrahmlMerge() {
        edgeConflictResolverMap = new HashMap<String, MergeConflictResolver>();
        vertexConflictResolverMap = new HashMap<String, MergeConflictResolver>();
    }

    public GrahmlMerge(Map<String, MergeConflictResolver> edgeConflictResolverMap, Map<String, MergeConflictResolver> vertexConflictResolverMap) {
        this.edgeConflictResolverMap = edgeConflictResolverMap;
        this.vertexConflictResolverMap = vertexConflictResolverMap;
    }

    public void merge(File inFile1, File inFile2, File outFile) throws IOException {
        Graph graph1 = new TinkerGraph();
        FileInputStream in1 = new FileInputStream(inFile1);
        GraphMLReader reader1 = new GraphMLReader(graph1);
        reader1.inputGraph(in1);

        Graph graph2 = new TinkerGraph();
        FileInputStream in2 = new FileInputStream(inFile2);
        GraphMLReader reader2 = new GraphMLReader(graph2);
        reader2.inputGraph(in2);

        mergeGraphs(graph1, graph2);

        FileOutputStream out = new FileOutputStream(outFile);
        GraphMLWriter writer = new GraphMLWriter(graph1);
        writer.setNormalize(true);
        writer.outputGraph(out);

    }


    public void merge(File[] files, File outFile) throws IOException {
        Graph graph = new TinkerGraph();
        for (File file : files) {
            graph = merge(graph, file);
        }

        FileOutputStream out = new FileOutputStream(outFile);
        GraphMLWriter writer = new GraphMLWriter(graph);
        writer.setNormalize(true);
        writer.outputGraph(out);
    }

    public Graph merge(File[] files) throws IOException {
        Graph graph = new TinkerGraph();
        for (File file : files) {
            merge(graph, file);
        }
        return graph;
    }

    public Graph merge(Graph graph, File file) throws IOException {
        Graph graph2 = new TinkerGraph();
        FileInputStream in2 = new FileInputStream(file);
        GraphMLReader reader2 = new GraphMLReader(graph2);
        reader2.inputGraph(in2);

        mergeGraphs(graph, graph2);

        return graph;
    }

    private void mergeGraphs(Graph graph1, Graph graph2) {
        mergeVertexes(graph1, graph2);
        mergeEdges(graph1, graph2);
    }

    private void mergeEdges(Graph graph1, Graph graph2) {
        for (Edge edge2 : graph2.getEdges()) {
            mergeEdge(graph1, edge2);
        }
    }

    private void mergeEdge(Graph graph1, Edge edge2) {
        Edge edge1 = graph1.getEdge(edge2.getId());
        if (edge1 == null) {
            Vertex outVertex1 = graph1.getVertex(edge2.getVertex(Direction.OUT).getId());
            Vertex outVertex2 = graph1.getVertex(edge2.getVertex(Direction.IN).getId());
            edge1 = graph1.addEdge(edge2.getId(), outVertex1, outVertex2, edge2.getLabel());
            for (String key2 : edge2.getPropertyKeys()) {
                edge1.setProperty(key2,edge2.getProperty(key2));
            }
        } else {
            Set<String> keys1 = edge1.getPropertyKeys();
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

    private void mergeVertexes(Graph graph1, Graph graph2) {
        for (Vertex vertex2 : graph2.getVertices()) {
            mergeVertex(graph1, vertex2);
        }
    }

    private Vertex mergeVertex(Graph graph1, Vertex vertex2) {
        Vertex vertex1 = graph1.getVertex(vertex2.getId());
        if (vertex1 == null) {
            vertex1 = graph1.addVertex(vertex2.getId());
            Set<String> keys2 = vertex2.getPropertyKeys();
            for (String key2 : keys2) {
                vertex1.setProperty(key2,vertex2.getProperty(key2));
            }
        } else {
            Set<String> keys1 = vertex1.getPropertyKeys();
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
