package net.itransformers.idiscover.v2.core.node_discoverers.subnet_discoverer;

import net.itransformers.idiscover.v2.core.NodeDiscoverer;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.utils.CIDRUtils;

import java.net.UnknownHostException;

/**
 * Created by Vasil Yordanov on 22-Jun-16.
 */
public class SubnetDiscoverer implements NodeDiscoverer {

    @Override
    public NodeDiscoveryResult discover(ConnectionDetails connectionDetails) {


        String protocolType = connectionDetails.getParam("protocolType");
        String subnetIpAddress = connectionDetails.getParam("ipAddress");
        String subnetMask = connectionDetails.getParam("subnetMask");

        NodeDiscoveryResult nodeDiscoveryResult = new NodeDiscoveryResult(subnetIpAddress+"/"+subnetMask,null);
        nodeDiscoveryResult.setDiscoveredData("subnetDetails", connectionDetails.getParams());


        if (protocolType.equals("IPv4")) {
            if (bogonSubnetIdentifier(subnetIpAddress)) {
                nodeDiscoveryResult.setDiscoveredData("bogon", true);
            }    
            if (privateSubnetIdentifier(subnetIpAddress)) {
                nodeDiscoveryResult.setDiscoveredData("private", true);
            }    
        }

        return nodeDiscoveryResult;
    }

    private boolean bogonSubnetIdentifier(String subnetIpAddress) {

        CIDRUtils cidrUtils = null;
        try {
        

            cidrUtils = new CIDRUtils("0.0.0.0/8");

            if (cidrUtils.isInRange(subnetIpAddress)) {
                return true;
            }
            cidrUtils = new CIDRUtils("127.0.0.0/8");
            if (cidrUtils.isInRange(subnetIpAddress)) {
                return true;
            }
            cidrUtils = new CIDRUtils("128.0.0.0/8");
            if (cidrUtils.isInRange(subnetIpAddress)) {
                return true;
            }
            cidrUtils = new CIDRUtils("169.254.0.0/16");
            if (cidrUtils.isInRange(subnetIpAddress)) {
                return true;
            }
            cidrUtils = new CIDRUtils("192.0.0.0/24");
            if (cidrUtils.isInRange(subnetIpAddress)) {
                return true;
            }
            cidrUtils = new CIDRUtils("192.0.2.0/24");
            if (cidrUtils.isInRange(subnetIpAddress)) {
                return true;
            }
            cidrUtils = new CIDRUtils("224.0.0.0/4");
            if (cidrUtils.isInRange(subnetIpAddress)) {
                return true;
            }
            cidrUtils = new CIDRUtils("240.0.0.0/4");
            if (cidrUtils.isInRange(subnetIpAddress)) {
                return true;
            }
            cidrUtils = new CIDRUtils("255.255.255.255/32");
            if (cidrUtils.isInRange(subnetIpAddress)) {
                return true;
            }
        
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return false;

    }

    private boolean privateSubnetIdentifier(String ipv4Address) {

        CIDRUtils cidrUtils = null;
        try {


            cidrUtils = new CIDRUtils("192.168.0.0/16");
            if (cidrUtils.isInRange(ipv4Address)) {
                return true;
            }
            cidrUtils = new CIDRUtils("172.16.0.0/12");
            if (cidrUtils.isInRange(ipv4Address)) {
                return true;
            }
            cidrUtils = new CIDRUtils("10.0.0.0/8");
            if (cidrUtils.isInRange(ipv4Address)) {
                return true;
            }
        }catch (UnknownHostException ex){
             ex.printStackTrace();
        }
        return false;

    }
}
