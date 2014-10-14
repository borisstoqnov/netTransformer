package net.itransformers.topologyviewer.gui;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.io.GraphMLMetadata;

import java.util.Collection;
import java.util.Map;

/**
 * Created by vasko on 10/13/14.
 */
public class BGAndIntBGPeeringIncluder implements VertexIncluder {
    @Override
    public boolean hasToInclude(String vertexName, Map<String, GraphMLMetadata<String>> vertexMetadata, Graph<String, String> graph1) {
        String country = vertexMetadata.get("Country").transformer.transform(vertexName);
        if (!"BG".equals(country)) {
            Collection<String> inEdges = graph1.getInEdges(vertexName);
            for (String inEdge : inEdges) {
                Pair<String> endpoints = graph1.getEndpoints(inEdge);
                String oppositeVertex;
                if (endpoints.getFirst().equals(vertexName)){
                    oppositeVertex = endpoints.getSecond();
                } else {
                    oppositeVertex = endpoints.getFirst();
                }
                String oppositeCountry = vertexMetadata.get("Country").transformer.transform(oppositeVertex);
                if ("BG".equals(oppositeCountry)) {
                    return true;
                }
           }
        }
        return false;
    }
}
