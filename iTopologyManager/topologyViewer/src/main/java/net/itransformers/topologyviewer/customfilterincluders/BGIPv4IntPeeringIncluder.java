package net.itransformers.topologyviewer.customfilterincluders;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.io.GraphMLMetadata;
import net.itransformers.topologyviewer.gui.VertexIncluder;

import java.util.Collection;
import java.util.Map;

/**
 * Created by vasko on 10/13/14.
 */
public class BGIPv4IntPeeringIncluder implements VertexIncluder {
    @Override
    public boolean hasToInclude(String vertexName, Map<String, GraphMLMetadata<String>> vertexMetadata, Graph<String, String> graph1) {
        String country = vertexMetadata.get("Country").transformer.transform(vertexName);
        String ipv4Flag = vertexMetadata.get("IPv4Flag").transformer.transform(vertexName);
            if("BG".equals(country)&"TRUE".equals(ipv4Flag)){
                return true;
            }

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
                String oppositeIPv6Flag = vertexMetadata.get("IPv4Flag").transformer.transform(oppositeVertex);

                if ("BG".equals(oppositeCountry)&&"TRUE".equals(oppositeIPv6Flag)) {
//                   System.out.println("BGAndIntBGPeeringIncluder returns "+vertexName);
                    return true;
                }
           }
        }
        return false;
    }
}
