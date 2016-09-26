package net.itransformers.idiscover.api.models.graphml;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by niau on 8/17/16.
 */
public class GraphmlNode {

    private String id;
    private String label;

    private List<GraphmlNodeData> graphmlNodeDataList;

    private List<GraphmlPort> graphmlNodePorts;


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

    public List<GraphmlPort> getGraphmlNodePorts() {
        return graphmlNodePorts;
    }

    public void setGraphmlNodePorts(List<GraphmlPort> graphmlNodePorts) {
        this.graphmlNodePorts = graphmlNodePorts;
    }

    public List<GraphmlNodeData> getGraphmlNodeDataList() {
        return graphmlNodeDataList;
    }

    public void setGraphmlNodeDataList(List<GraphmlNodeData> graphmlNodeDataList) {
        this.graphmlNodeDataList = graphmlNodeDataList;
    }
}
