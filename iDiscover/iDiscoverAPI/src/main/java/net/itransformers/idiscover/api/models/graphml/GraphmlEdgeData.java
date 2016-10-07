package net.itransformers.idiscover.api.models.graphml;

/**
 * Created by niau on 8/17/16.
 */
public class GraphmlEdgeData {
    private  String key;
    private String value;
    public GraphmlEdgeData() {
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GraphmlEdgeData that = (GraphmlEdgeData) o;

        if (!key.equals(that.key)) return false;
        return value.equals(that.value);

    }

    @Override
    public int hashCode() {
        int result = key.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }
}
