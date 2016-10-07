

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

package net.itransformers.idiscover.core;

import java.util.List;
import java.util.Set;

/*
* 
*/
public class Device {

    private String name;
    private String uniqueIdentifier;
    private List<DeviceNeighbour> deviceNeighbours;
    private Set<Subnet> deviceSubnets;
    private List<MacAddress> deviceMacAddresses;

    public Device(String name) {
        this.name  = name;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DeviceNeighbour> getDeviceNeighbours() {
        return deviceNeighbours;
    }
    public Set<Subnet> getDeviceSubnets() {
        return deviceSubnets;
    }


    public void setDeviceNeighbours(List<DeviceNeighbour> deviceNeighbours) {
        this.deviceNeighbours = deviceNeighbours;
    }

    public void setDeviceSubnets(Set<Subnet> deviceSubnets) {
        this.deviceSubnets = deviceSubnets;
    }

    public String getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    public void setUniqueIdentifier(String uniqueIdentifier) {
        this.uniqueIdentifier = uniqueIdentifier;
    }

    public List<MacAddress> getDeviceMacAddresses() {
        return deviceMacAddresses;
    }

    public void setDeviceMacAddresses(List<MacAddress> deviceMacAddresses) {
        this.deviceMacAddresses = deviceMacAddresses;
    }
}
