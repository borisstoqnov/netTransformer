package net.itransformers.idiscover.v2.core.listeners.graphmlRenderer.model;

/**
 * Created by niau on 8/17/16.
 */
public class GraphmlEdgeData {
    private  String key;
    private String value;

    public GraphmlEdgeData(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
