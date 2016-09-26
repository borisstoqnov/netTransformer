package net.itransformers.idiscover.api.models.graphml;

/**
 * Created by niau on 8/17/16.
 */
public class GraphmlNodeData {
    private  String key;
    private String value;

    public GraphmlNodeData(String key, String value) {
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
