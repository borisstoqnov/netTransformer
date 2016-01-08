package net.itransformers.idiscover.v2.core;

import net.itransformers.idiscover.v2.core.model.ConnectionDetails;

import java.util.Map;

/**
 * Created by vasko on 08.01.16.
 */
public class MockNodeDiscoverer implements NodeDiscoverer {

    Map<String, NodeDiscoveryResult> discoveryResultMap;

    public MockNodeDiscoverer(Map<String, NodeDiscoveryResult> discoveryResultMap) {
        this.discoveryResultMap = discoveryResultMap;
    }

    @Override
    public NodeDiscoveryResult discover(ConnectionDetails connectionDetails) {
        if ("mock".equals(connectionDetails.getConnectionType())) {
            String keyConnParam = connectionDetails.getParam("key");
            return discoveryResultMap.get(keyConnParam);
        }
        else {
            NodeDiscoveryResult emptyNodeDiscoveryResult = new NodeDiscoveryResult();
            return emptyNodeDiscoveryResult;
        }
    }
}
