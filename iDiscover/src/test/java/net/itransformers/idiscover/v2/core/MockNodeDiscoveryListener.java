package net.itransformers.idiscover.v2.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vasko on 08.01.16.
 */
public class MockNodeDiscoveryListener implements NodeDiscoveryListener {
    private List<NodeDiscoveryResult> nodeDiscoveryResult = new ArrayList<NodeDiscoveryResult>();
    @Override
    public void nodeDiscovered(NodeDiscoveryResult discoveryResult) {
        nodeDiscoveryResult.add(discoveryResult);
    }

    public List<NodeDiscoveryResult> getNodeDiscoveryResult() {
        return nodeDiscoveryResult;
    }
}
