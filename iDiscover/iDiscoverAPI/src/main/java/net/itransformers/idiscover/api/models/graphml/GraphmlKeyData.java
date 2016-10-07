package net.itransformers.idiscover.api.models.graphml;

/**
 * Created by niau on 9/27/16.
 */
public class GraphmlKeyData {
    private  String key;
    private String value;

    public GraphmlKeyData(String key, String value) {
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
