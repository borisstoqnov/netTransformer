

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
public class DeviceNeighbour {
    private String hostName;
    private String neighbourIpAddress;
    private String roCommunity;
    private boolean known;
    private String deviceType;
    private boolean Reachable;

    public DeviceNeighbour(String hostName, String deviceType, String snmpCommunity, boolean Reachable) {
        this.hostName = hostName;
        this.deviceType = deviceType;
        this.roCommunity =  snmpCommunity;
        this.Reachable = Reachable;
    }

    public DeviceNeighbour(String hostName, String neighbourIpAddress, String deviceType, String snmpCommunity, boolean Reachable) {
        this.hostName = hostName;
        this.neighbourIpAddress = neighbourIpAddress;
        this.deviceType = deviceType;
        this.roCommunity = snmpCommunity;
        this.Reachable = Reachable;
    }

    public DeviceNeighbour(String hostName, String neighbourIpAddress) {
        this.neighbourIpAddress = neighbourIpAddress;
        this.hostName = hostName;
    }

    public String getIpAddress() {
            return neighbourIpAddress;
    }

    public void setIpAddress(String neighbourIpAddress) {
        this.neighbourIpAddress = neighbourIpAddress;
    }

    public String getHostName(){
            return hostName;
        }



    public boolean getStatus(){
            return Reachable;
        }

    public String getDeviceType(){
        return deviceType;
    }
    public String getROCommunity(){
        return roCommunity;
    }
    public void setHostName(String hostName){
            this.hostName = hostName;
        }

    public boolean isKnown(){
            return known;
        }

    public void setKnown(boolean known){
            this.known = known;
        }

    @Override
    public String toString() {
        return "DeviceNeighbour{" +
                "hostName='" + hostName + '\'' +
                ", neighbourIpAddress='" + neighbourIpAddress + '\'' +
                ", known=" + known +
                '}';
    }
}
