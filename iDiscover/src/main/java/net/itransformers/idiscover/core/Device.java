

/*
 * iTransformer is an open source tool able to discover IP networks
 * and to perform dynamic data data population into a xml based inventory system.
 * Copyright (C) 2010  http://itransformers.net
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.itransformers.idiscover.core;

import java.util.List;
/*
* 
*/
public class Device {

    private String name;

    private List<DeviceNeighbour> deviceNeighbours;

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

    public void setDeviceNeighbours(List<DeviceNeighbour> deviceNeighbours) {
        this.deviceNeighbours = deviceNeighbours;
    }

}
