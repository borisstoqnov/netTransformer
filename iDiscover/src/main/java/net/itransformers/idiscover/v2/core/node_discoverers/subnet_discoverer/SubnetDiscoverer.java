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
        // TODO implement better
        String val = connectionDetails.getParam("...");
        NodeDiscoveryResult nodeDiscoveryResult = new NodeDiscoveryResult("...TODO..", null);
        nodeDiscoveryResult.setDiscoveredData("some_key", "some_data");
        return nodeDiscoveryResult;
    }
}
