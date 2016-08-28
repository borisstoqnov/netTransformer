package net.itransformers.idiscover.networkmodelv2;

import java.util.HashMap;

/**
 * Created by niau on 8/25/16.
 */
public class DiscoveredIPv4Address {
    private String name;
    private HashMap<String,String> params;
    private String id;


    public DiscoveredIPv4Address(String name, HashMap<String, String> params) {
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
}
