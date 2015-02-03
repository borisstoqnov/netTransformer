package net.itransformers.idiscover.v2.core.listeners.groovy;

import java.util.HashMap;

/**
 * Created by vasko on 03.02.15.
 */
public class Subnet {
    String name;
    HashMap<String, DeviceNeighbour> neighbours = new HashMap<String, DeviceNeighbour>();

    public Subnet(String name) {
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
