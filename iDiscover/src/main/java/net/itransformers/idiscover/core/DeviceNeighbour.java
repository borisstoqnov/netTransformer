

/*
 * iTransformer is an open source tool able to discover and transform
 *  IP network infrastructures.
 *  Copyright (C) 2012  http://itransformers.net
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.itransformers.idiscover.core;

public class DeviceNeighbour {
    private String hostName;
    private IPv4Address neighbourIpAddress;
    private String roCommunity;
    private boolean known;
    private String deviceType;
    private boolean Reachable;

    public DeviceNeighbour(String hostName, String deviceType,String snmpCommunity,boolean Reachable) {
        this.hostName = hostName;
        this.deviceType = deviceType;
        this.roCommunity =  snmpCommunity;
        this.Reachable = Reachable;
    }
    public DeviceNeighbour(String hostName,IPv4Address neighbourIpAddress,String deviceType, String snmpCommunity,boolean Reachable) {
        this.hostName = hostName;
        this.neighbourIpAddress = neighbourIpAddress;
        this.deviceType = deviceType;
        this.roCommunity = snmpCommunity;
        this.Reachable = Reachable;
    }
    public DeviceNeighbour(IPv4Address neighbourIpAddress,String deviceType,String snmpCommunity,boolean Reachable) {
        this.neighbourIpAddress = neighbourIpAddress;
        this.deviceType = deviceType;
        this.roCommunity =  snmpCommunity;
        this.Reachable = Reachable;

    }

    public IPv4Address getIpAddress(){
            return neighbourIpAddress;
    }

    public void setIpAddress(IPv4Address neighbourIpAddress){
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
