package net.itransformers.idiscover.networkmodelv2;

import net.itransformers.idiscover.v2.core.IPv4BogonIdentitifier;

import java.util.HashMap;

/**
 * Created by niau on 8/25/16.
 */
public class DiscoveredIPv4Address {
    private String name;
    private HashMap<String,String> params;
    private String id;
    private boolean bogon;


    public DiscoveredIPv4Address(String name, HashMap<String, String> params) {
        this.name = name;
        this.params = params;
        this.bogon = determineBogon(params.get("IPv4Address"));
    }

    private boolean determineBogon(String ipAddress){
        IPv4BogonIdentitifier iPv4BogonIdentitifier = new IPv4BogonIdentitifier(ipAddress);
        return iPv4BogonIdentitifier.identifyBogon();
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
