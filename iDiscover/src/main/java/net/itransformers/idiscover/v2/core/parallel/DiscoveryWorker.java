package net.itransformers.idiscover.v2.core.parallel;

import net.itransformers.idiscover.v2.core.NodeDiscoverer;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.idiscover.v2.core.factory.NodeFactory;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by Vasil Yordanov on 1/9/2016.
 */
public class DiscoveryWorker implements Callable<NodeDiscoveryResult> {
    static Logger logger = Logger.getLogger(DiscoveryWorker.class);
    private Map<String, NodeDiscoverer> discoverers;
    private ConnectionDetails connectionDetails;
    private String parentId;

    public DiscoveryWorker(Map<String, NodeDiscoverer> discoverers, ConnectionDetails connectionDetails, String parentId) {
        this.discoverers = discoverers;
        this.connectionDetails = connectionDetails;
        this.parentId = parentId;
    }

    @Override
    public NodeDiscoveryResult call() throws Exception {
        logger.debug("Discovery worker: " + Thread.currentThread().getName() + " started. connectionDetails = " + connectionDetails);

        String connectionType = connectionDetails.getConnectionType();
        NodeDiscoverer nodeDiscoverer = discoverers.get(connectionType);

        if (nodeDiscoverer == null) {
            logger.debug("No node discoverer can be found for connectionType: " + connectionDetails);
            logger.debug("Discovery worker: " + Thread.currentThread().getName() + " finished. connectionDetails = " + connectionDetails);
            NodeDiscoveryResult nodeDiscoveryResult = new NodeDiscoveryResult();
            nodeDiscoveryResult.setParentId(parentId);
            return nodeDiscoveryResult;
        }
        NodeDiscoveryResult discoveryResult = nodeDiscoverer.discover((ConnectionDetails) connectionDetails.clone());
        discoveryResult.setParentId(parentId);
        return discoveryResult;
    }


}
