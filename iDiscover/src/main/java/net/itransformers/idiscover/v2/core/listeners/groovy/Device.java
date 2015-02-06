package net.itransformers.idiscover.v2.core.listeners.groovy;

import java.util.HashMap;

/**
 * Created by vasko on 03.02.15.
 */
public class Device {
    String name;
    HashMap<String, Network> subnets = new HashMap<String, Network>();

    public Device(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addSubnet(Network network){
        subnets.put(network.getName(), network);
    }
    public HashMap<String, Network> getSubnets() {
        return subnets;
    }

}
