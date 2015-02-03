package net.itransformers.idiscover.v2.core.listeners.groovy;

import java.util.HashMap;

/**
 * Created by vasko on 03.02.15.
 */
public class Device {
    String name;
    HashMap<String, Subnet> subnets = new HashMap<String, Subnet>();

    public Device(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addSubnet(Subnet subnet){
        subnets.put(subnet.getName(), subnet);
    }
    public HashMap<String, Subnet> getSubnets() {
        return subnets;
    }

}
