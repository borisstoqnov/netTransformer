package net.itransformers.idiscover.v2.core.listeners.groovy;

import java.util.HashMap;

/**
 * Created by vasko on 03.02.15.
 */
public class DeviceNeighbour {
    String name;
    HashMap<String, String> properties = new HashMap<String, String>();

    public DeviceNeighbour(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void put(String key, String value){
        properties.put(key, value);
    }
    public HashMap<String, String> getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return "DeviceNeighbour{" +
                "name='" + name + '\'' +
                ", properties=" + properties +
                '}';
    }
}
