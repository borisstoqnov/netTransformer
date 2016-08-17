package net.itransformers.idiscover.v2.core.listeners.graphmlRenderer.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by niau on 8/17/16.
 */
public class GraphmlNode {

    private String id;
    private String label;

    private List<GraphmlNodeData> graphmlNodeDataList;


    public GraphmlNode(String id, String label) {
        this.id = id;
        this.label = label;
        graphmlNodeDataList = new ArrayList<>();
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

    public GraphmlNode(List<GraphmlNodeData> graphmlNodeDataList) {
        this.graphmlNodeDataList = graphmlNodeDataList;
    }
    public GraphmlNode() {
        this.graphmlNodeDataList = new ArrayList<>();
    }


    public List<GraphmlNodeData> getGraphmlNodeDataList() {
        return graphmlNodeDataList;
    }

    public void setGraphmlNodeDataList(List<GraphmlNodeData> graphmlNodeDataList) {
        this.graphmlNodeDataList = graphmlNodeDataList;
    }
}
