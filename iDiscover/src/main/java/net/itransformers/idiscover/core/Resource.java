
/*
 * Resource.java
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

import java.util.Map;
@Deprecated
public class Resource {
    private String host;
    private String deviceType;
    private IPv4Address ipAddress;
    private int snmpPort = 161;
    private Map<String, String> attributes;

    public Resource(IPv4Address iPv4Address, String deviceType, Map<String, String> attributes) {

        this.ipAddress = iPv4Address;
        this.attributes = attributes;
    }
    public Resource(IPv4Address iPv4Address, String deviceType, int port, Map<String, String> attributes) {
        this.ipAddress = iPv4Address;
        this.snmpPort = port;
        this.attributes = attributes;
    }
    public Resource(String host, IPv4Address iPv4Address, String deviceType, int port, Map<String, String> attributes) {
        this.ipAddress = iPv4Address;
        this.host = host;
        this.deviceType = deviceType;
        this.snmpPort = port;
        this.attributes = attributes;
    }
    public Resource(String host, String deviceType, Map<String, String> attributes) {
        this.host = host;
        this.attributes = attributes;
    }
    public Resource(String host,String deviceType, int port, Map<String, String> attributes) {
        this.host = host;
        this.snmpPort = port;
        this.attributes = attributes;
    }

    public void ChangeCommunity(){
        this.attributes.put("community",this.attributes.get("community2"));
    }

    public void setHost(String host) {
        this.host = host;
    }
    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
    public String getHost() {
        return host;
    }
    public String getDeviceType() {
        return deviceType;
    }
    public Map<String, String> getAttributes() {
        return attributes;
    }
    public void setAttributes(Map<String, String> attributes){
        this.attributes = attributes;
    }
    public IPv4Address getIpAddress() {
        return ipAddress;
    }
    public int getPort() {
        return snmpPort;
    }

    public String getAddress(){
        if (ipAddress != null) {
            return ipAddress.getIpAddress()+"/"+snmpPort;
        } else if (host != null){
            return host + "/" +snmpPort;
        } else {
            return null;
        }
    }
    public Resource clone(String hostName){
        return new Resource(hostName,this.deviceType, this.snmpPort, this.attributes);
    }
    public Resource clone(String hostName,IPv4Address ipAddress, String deviceType){
        return new Resource(hostName, ipAddress, deviceType, this.snmpPort, this.attributes);
    }
    public Resource clone(IPv4Address ipAddress){
        return new Resource(ipAddress,this.deviceType, this.snmpPort, this.attributes);
    }

}
