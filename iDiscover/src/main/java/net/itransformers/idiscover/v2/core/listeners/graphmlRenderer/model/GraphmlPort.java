package net.itransformers.idiscover.v2.core.listeners.graphmlRenderer.model;

import net.itransformers.utils.hash.HashGenerator;

/**
 * Created by niau on 8/26/16.
 */
public class GraphmlPort {
    String id;
    String name;

    public GraphmlPort( String name) {
        try {
            this.id = HashGenerator.generateMd5(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
