package net.itransformers.idiscover.v2.core.listeners.graphmlRenderer.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by niau on 8/22/16.
 */
public class GraphmlEdge {

        private String id;
        private String label;
        private String fromNode;
        private String toNode;

        private List<GraphmlEdgeData> graphmlEdgeDataList;


        public GraphmlEdge(String id, String label,String fromNode,String toNode) {
            this.id = id;
            this.label = label;
            this.fromNode = fromNode;
            this.toNode = toNode;
            graphmlEdgeDataList = new ArrayList<>();
        }

    public String getFromNode() {
        return fromNode;
    }

    public void setFromNode(String fromNode) {
        this.fromNode = fromNode;
    }

    public String getToNode() {
        return toNode;
    }

    public void setToNode(String toNode) {
        this.toNode = toNode;
    }

    public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public GraphmlEdge(List<GraphmlEdgeData> graphmlEdgeDataList) {
            this.graphmlEdgeDataList = graphmlEdgeDataList;
        }
        public GraphmlEdge() {
            this.graphmlEdgeDataList = new ArrayList<>();
        }


        public List<GraphmlEdgeData> getGraphmlEdgeDataList() {
            return graphmlEdgeDataList;
        }

        public void setGraphmlEdgeDataList(List<GraphmlEdgeData> graphmlEdgeDataList) {
            this.graphmlEdgeDataList = graphmlEdgeDataList;
        }

    @Override
    public String toString() {
        return "GraphmlEdge{" +
                "id='" + id + '\'' +
                ", label='" + label + '\'' +
                ", fromNode='" + fromNode + '\'' +
                ", toNode='" + toNode + '\'' +
                ", graphmlEdgeDataList=" + graphmlEdgeDataList +
                '}';
    }
}
