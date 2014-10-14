package net.itransformers.topologyviewer.gui;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.io.GraphMLMetadata;

import java.util.Map;

/**
 * Created by vasko on 10/13/14.
 */
public interface VertexIncluder {

    public boolean hasToInclude(String vertexName, final Map<String, GraphMLMetadata<String>> vertexMetadata, final Graph<String, String> graph1);
}
