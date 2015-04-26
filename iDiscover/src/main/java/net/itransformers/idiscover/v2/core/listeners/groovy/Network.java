package net.itransformers.idiscover.v2.core.listeners.groovy;

import java.util.HashMap;

/**
 * Created by vasko on 03.02.15.
 */
public class Network {
    String name;
    HashMap<String, DeviceNeighbour> neighbours = new HashMap<String, DeviceNeighbour>();
    String ipAddress;
    String prefix;
    String subnetPrefix;
    String localInterface;
    String subnetProtocolType;
    String subnetType;

    public String getSubnetPrefix() {
        return subnetPrefix;
    }

    public void setSubnetPrefix(String subnetPrefix) {
        this.subnetPrefix = subnetPrefix;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
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

    public String getLocalInterface() {
        return localInterface;
    }

    public void setLocalInterface(String localInterface) {
        this.localInterface = localInterface;
    }

    public String getSubnetProtocolType() {
        return subnetProtocolType;
    }

    public void setSubnetProtocolType(String subnetProtocolType) {
        this.subnetProtocolType = subnetProtocolType;
    }

    public String getSubnetType() {
        return subnetType;
    }

    public void setSubnetType(String subnetType) {
        this.subnetType = subnetType;
    }

    @Override
    public String toString() {
        return "Subnet{" +
                "name='" + name + '\'' +
                '}';
    }
}
