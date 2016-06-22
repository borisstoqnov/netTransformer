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

package net.itransformers.idiscover.core;

/**
 * Created by vasko on 03.02.15.
 */
public class Subnet {
    String name;
    String ipAddress;
    String subnetMask;
    String localInterface;
    String subnetProtocolType;

    public Subnet(String name, String ipAddress, String subnetMask, String localInterface, String subnetProtocolType) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.subnetMask = subnetMask;
        this.localInterface = localInterface;
        this.subnetProtocolType = subnetProtocolType;

    }


    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getsubnetMask() {
        return subnetMask;
    }

    public void setSubnetMask(String subnetMask) {
        this.subnetMask = subnetMask;
    }

    public Subnet(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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


    @Override
    public String toString() {
        return "Subnet{" +
                "name='" + name + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", subnetMask='" + subnetMask + '\'' +
                ", localInterface='" + localInterface + '\'' +
                ", subnetProtocolType='" + subnetProtocolType + '\'' +
                '}';
    }
}
