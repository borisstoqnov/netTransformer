

/*
 * DeviceNeighbour.java
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

import java.util.HashMap;

public class DeviceNeighbour {
    private String neighbourHostName;
    private String neighbourIpAddress;
    private HashMap<String,String> parameters;

    public DeviceNeighbour(String neighbourHostName, String neighbourIpAddress,HashMap<String,String> parameters ) {
        this.neighbourIpAddress = neighbourIpAddress;
        this.neighbourHostName = neighbourHostName;
        this.parameters = parameters;
    }




    public String getNeighbourIpAddress() {
        return neighbourIpAddress;
    }

    public void setNeighbourIpAddress(String neighbourIpAddress) {
        this.neighbourIpAddress = neighbourIpAddress;
    }

    public HashMap<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(HashMap<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getIpAddress() {
            return neighbourIpAddress;
    }

    public void setIpAddress(String neighbourIpAddress) {
        this.neighbourIpAddress = neighbourIpAddress;
    }

    public String getNeighbourHostName() {
        return neighbourHostName;
    }

    public void setNeighbourHostName(String neighbourHostName) {
        this.neighbourHostName = neighbourHostName;
    }

    @Override
    public String toString() {
        return "DeviceNeighbour{" +
                "hostName='" + neighbourHostName + '\'' +
                "ipAddress='" + neighbourIpAddress + '\'' +
                ", parameters='" + parameters +" }";

    }
}
