package net.itransformers.idiscover.v2.core.node_discoverers.subnet_discoverer;

import net.itransformers.idiscover.v2.core.NodeDiscoverer;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.utils.CIDRUtils;

/**
 * Created by Vasil Yordanov on 22-Jun-16.
 */
public class SubnetDiscoverer implements NodeDiscoverer {
    CIDRUtils cidrUtils;

    @Override
    public NodeDiscoveryResult discover(ConnectionDetails connectionDetails) {


        String protocolType = connectionDetails.getParam("protocolType");
        String subnetIpAddress = connectionDetails.getParam("ipAddress");
        String subnetMask = connectionDetails.getParam("subnetMask");



        NodeDiscoveryResult nodeDiscoveryResult = new NodeDiscoveryResult(subnetIpAddress, null);
        nodeDiscoveryResult.setDiscoveredData("subnetIpAddress", subnetIpAddress);
        nodeDiscoveryResult.setDiscoveredData("protocolType", protocolType);
        nodeDiscoveryResult.setDiscoveredData("subnetMask", subnetMask);

        if (protocolType.equals("IPv4")) {
            if (bogonSubnetIdentifier(subnetIpAddress + "/" + subnetMask)) ;
            nodeDiscoveryResult.setDiscoveredData("bogon", true);
            if (privateSubnetIdentifier(subnetIpAddress + "/" + subnetMask)) ;
            nodeDiscoveryResult.setDiscoveredData("private", true);
        }

        return nodeDiscoveryResult;
    }

    private boolean bogonSubnetIdentifier(String subnetPrefix) {


        switch (subnetPrefix) {
            case "0.0.0.0/8":
                return true;
            case "127.0.0.0/8":
                return true;
            case "128.0.0.0/8":
                return true;
            case "169.254.0.0/16":
                return true;
            case "192.0.0.0/24":
                return true;
            case "192.0.2.0/24":
                return true;
            case "224.0.0.0/4":
                return true;
            case "240.0.0.0/4":
                return true;
            case "255.255.255.255/32":
                return true;

            default:
                return false;
        }


    }

    private boolean privateSubnetIdentifier(String subnetPrefix) {


        switch (subnetPrefix) {
            case "192.168.0.0/16":
                return true;
            case "172.16.0.0/12":
                return true;
            case "10.0.0.0/8":
                return true;
            default:
                return false;
        }

    }
}
