package net.itransformers.idiscover.v2.core.factory;

import net.itransformers.idiscover.v2.core.NodeDiscoverer;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.idiscover.v2.core.parallel.DiscoveryWorker;

import java.util.Map;

/**
 * Created by vasko on 20.06.16.
 */
public class DiscoveryWorkerFactory {
    public DiscoveryWorker createDiscoveryWorker(Map<String, NodeDiscoverer> nodeDiscoverers, ConnectionDetails connectionDetails, String parentNodeId) {
        return new DiscoveryWorker(nodeDiscoverers, connectionDetails, parentNodeId );
    }
}
