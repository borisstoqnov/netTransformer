package net.itransformers.idiscover.v2.core.listeners.groovy;

import java.util.HashMap;

/**
 * Created by vasko on 03.02.15.
 */
public class Network {
    String name;
    HashMap<String, DeviceNeighbour> neighbours = new HashMap<String, DeviceNeighbour>();
    String IPv4Address;
    String prefix;
    String ipv4SubnetMaskPrefix;

    public String getIpv4SubnetMaskPrefix() {
        return ipv4SubnetMaskPrefix;
    }

    public void setIpv4SubnetMaskPrefix(String ipv4SubnetMaskPrefix) {
        this.ipv4SubnetMaskPrefix = ipv4SubnetMaskPrefix;
    }

    public String getIPv4Address() {
        return IPv4Address;
    }

    public void setIPv4Address(String IPv4Address) {
        this.IPv4Address = IPv4Address;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public Network(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap<String, DeviceNeighbour> getNeighbours() {
        return neighbours;
    }

    public void addNeighbour(DeviceNeighbour neighbour){
        neighbours.put(neighbour.getName(), neighbour);
    }

    public void setNeighbours(HashMap<String, DeviceNeighbour> neighbours) {
        this.neighbours = neighbours;
    }

    @Override
    public String toString() {
        return "Subnet{" +
                "name='" + name + '\'' +
                '}';
    }
}
