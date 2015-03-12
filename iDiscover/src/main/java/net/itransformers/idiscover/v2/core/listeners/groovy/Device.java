package net.itransformers.idiscover.v2.core.listeners.groovy;

import java.util.HashMap;

/**
 * Created by vasko on 03.02.15.
 */
public class Device {
    String name;
    HashMap<String, Network> subnets = new HashMap<String, Network>();
    HashMap<String, DeviceNeighbour> physicalNeighbours = new HashMap<String, DeviceNeighbour>();
    HashMap<String, DeviceNeighbour> logicalNeighbours = new HashMap<String, DeviceNeighbour>();


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

    public HashMap<String, DeviceNeighbour> getPhysicalNeighbours() {
        return physicalNeighbours;
    }

    public void setPhysicalNeighbours(HashMap<String, DeviceNeighbour> physicalNeighbours) {
        this.physicalNeighbours = physicalNeighbours;
    }
    public void addPhysicalNeighbour(DeviceNeighbour neighbour){
          physicalNeighbours.put(neighbour.getName(), neighbour);
    }

    public HashMap<String, DeviceNeighbour> getLogicalNeighbours() {
        return logicalNeighbours;
    }

    public void setLogicalNeighbours(HashMap<String, DeviceNeighbour> logicalNeighbours) {
        this.logicalNeighbours = logicalNeighbours;
    }

    public void addLogicalNeighbour(DeviceNeighbour neighbour){
        logicalNeighbours.put(neighbour.getName(), neighbour);
    }
}
