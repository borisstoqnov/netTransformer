package net.itransformers.idiscover.networkmodelv2;

import java.util.HashMap;

/**
 * Created by niau on 8/25/16.
 */
public class DiscoveredIPv6Address {
    private String name;
    private HashMap<String,String> params;
    private String id;
    private boolean bogon;

    public DiscoveredIPv6Address(String name, HashMap<String, String> params) {
        this.name = name;
        this.params = params;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }

    public boolean isBogon() {
        return bogon;
    }

    public void setBogon(boolean bogon) {
        this.bogon = bogon;
    }
}
