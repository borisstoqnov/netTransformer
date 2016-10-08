package net.itransformers.idiscover.api.models.graphml;

import java.util.List;

/**
 * Created by vasko on 26.09.16.
 */
public class GraphmlGraph {
    List<GraphmlKeyData> graphmlKeys;

    List<GraphmlEdge> graphmlEdges;
    List<GraphmlNode> graphmlNodes;

    public List<GraphmlEdge> getGraphmlEdges() {
        return graphmlEdges;
    }

    public void setGraphmlEdges(List<GraphmlEdge> graphmlEdges) {
        this.graphmlEdges = graphmlEdges;
    }

    public List<GraphmlNode> getGraphmlNodes() {
        return graphmlNodes;
    }

    public void setGraphmlNodes(List<GraphmlNode> graphmlNodes) {
        this.graphmlNodes = graphmlNodes;
    }
}
