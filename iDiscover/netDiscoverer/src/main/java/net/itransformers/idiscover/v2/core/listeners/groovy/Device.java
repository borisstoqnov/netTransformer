/*
 * Device.java
 *
 * This work is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * This work is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * Copyright (c) 2010-2016 iTransformers Labs. All rights reserved.
 */

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
