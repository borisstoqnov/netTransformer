/*
 * Network.java
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
public class Network {
    String name;
    HashMap<String, DeviceNeighbour> neighbours = new HashMap<String, DeviceNeighbour>();
    String ipAddress;
    String prefix;
    String subnetPrefix;
    String localInterface;
    String localMac;
    String subnetProtocolType;
    String subnetType;

    public Network(String name, HashMap<String, DeviceNeighbour> neighbours, String ipAddress, String prefix, String subnetPrefix, String localInterface, String localMac, String subnetProtocolType, String subnetType) {
        this.name = name;
        this.neighbours = neighbours;
        this.ipAddress = ipAddress;
        this.prefix = prefix;
        this.subnetPrefix = subnetPrefix;
        this.localInterface = localInterface;
        this.localMac = localMac;
        this.subnetProtocolType = subnetProtocolType;
        this.subnetType = subnetType;
    }

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

    public String getLocalMac() {
        return localMac;
    }

    public void setLocalMac(String localMac) {
        this.localMac = localMac;
    }

    @Override
    public String toString() {
        return "Subnet{" +
                "name='" + name + '\'' +
                '}';
    }
}
