package net.itransformers.idiscover.v2.core.node_discoverers.subnet_discoverer;

import net.itransformers.idiscover.v2.core.NodeDiscoverer;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;

/**
 * Created by Vasil Yordanov on 22-Jun-16.
 */
public class SubnetDiscoverer implements NodeDiscoverer {
    @Override
    public NodeDiscoveryResult discover(ConnectionDetails connectionDetails) {


        String protocolType = connectionDetails.getParam("protocolType");
        String subnetIpAddress = connectionDetails.getParam("ipAddress");
        String subnetMask = connectionDetails.getParam("subnetMask");


        NodeDiscoveryResult nodeDiscoveryResult = new NodeDiscoveryResult(subnetIpAddress+"/"+subnetMask, null);
        nodeDiscoveryResult.setDiscoveredData("subnetIpAddress", subnetIpAddress);
        nodeDiscoveryResult.setDiscoveredData("protocolType", protocolType);
        nodeDiscoveryResult.setDiscoveredData("subnetMask",subnetMask);
        return nodeDiscoveryResult;
    }
}
