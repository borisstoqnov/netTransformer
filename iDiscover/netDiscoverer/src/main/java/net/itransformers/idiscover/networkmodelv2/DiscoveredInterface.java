package net.itransformers.idiscover.networkmodelv2;


import net.itransformers.utils.CIDRUtils;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by niau on 8/25/16.
 */
public class DiscoveredInterface {
    private String name;
    private String type;
    private HashMap<String,String> params;
    private List<DeviceNeighbour> neighbours;
    private List<DiscoveredIPv4Address> iPv4AddressList;
    private List<DiscoveredIPv6Address> ipv6AddressList;
    private String id;

    public DiscoveredInterface(String name, HashMap<String, String> params) {
        this.name = name;
        this.params = params;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }

    public List<DeviceNeighbour> getNeighbours() {
        return neighbours;
    }


    public DeviceNeighbour getDiscoveredNeighboursPerSubnet(String subnetName,String subnetProtocolType){


        return null;


    }

    public String getDiscoveryMethodsPerSubnet(String subnetName, String subnetProtocolType) throws UnknownHostException {

        StringBuilder discoveredMethods = new StringBuilder();

        for (DeviceNeighbour neighbour : getNeighbours()){

            String neighbourIPAddress = neighbour.getNeighbourIpAddress();

            if (subnetProtocolType.equals("IPv4")){

                for (DiscoveredIPv4Address ipv4Address : iPv4AddressList) {
                    HashMap<String, String> ipv4AddressParams = ipv4Address.getParams();
                    String ipv4Subnet = ipv4AddressParams.get("ipv4Subnet");
                    String ipv4SubnetPrefix = ipv4AddressParams.get("ipv4SubnetPrefix");
                    String ipv4SubnetName = ipv4Subnet + "/" + ipv4SubnetPrefix;
                    if (ipv4SubnetName.equals(subnetName)) {
                        CIDRUtils cidrUtils = new CIDRUtils(ipv4SubnetName);
                        cidrUtils.isInRange(neighbourIPAddress);
                        discoveredMethods.append(neighbour.getParameters().get("Discovery Method")+",");
                    }

                }
            }else{
                for (DiscoveredIPv6Address ipv6Address : ipv6AddressList) {
                    HashMap<String, String> ipv6AddressParams = ipv6Address.getParams();
                    String ipv6Subnet = ipv6AddressParams.get("ipv6Subnet");
                    String ipv6SubnetPrefix = ipv6AddressParams.get("ipv6SubnetPrefix");
                    String ipv6SubnetName = ipv6Subnet + "/" + ipv6SubnetPrefix;
                    if (ipv6SubnetName.equals(subnetName)) {
                        CIDRUtils cidrUtils = new CIDRUtils(ipv6SubnetPrefix);
                        cidrUtils.isInRange(neighbourIPAddress);
                        discoveredMethods.append(neighbour.getParameters().get("Discovery Method")+",");
                    }
                }
            }


        }
        return discoveredMethods.toString();
    }
    public void setNeighbours(List<DeviceNeighbour> neighbours) {
        this.neighbours = neighbours;
    }

    public List<DiscoveredIPv4Address> getiPv4AddressList() {
        return iPv4AddressList;
    }

    public void setiPv4AddressList(List<DiscoveredIPv4Address> iPv4AddressList) {
        this.iPv4AddressList = iPv4AddressList;
    }

    public List<DiscoveredIPv6Address> getIpv6AddressList() {
        return ipv6AddressList;
    }

    public void setIpv6AddressList(List<DiscoveredIPv6Address> ipv6AddressList) {
        this.ipv6AddressList = ipv6AddressList;
    }
}
