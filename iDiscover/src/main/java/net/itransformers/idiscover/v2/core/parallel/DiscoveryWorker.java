package net.itransformers.idiscover.v2.core.parallel;

import net.itransformers.idiscover.v2.core.NodeDiscoverer;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.idiscover.v2.core.model.Node;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RecursiveAction;

/**
 * Created by Vasil Yordanov on 1/9/2016.
 */
public class DiscoveryWorker extends RecursiveAction {
    static Logger logger = Logger.getLogger(DiscoveryWorker.class);
    private ConnectionDetails connectionDetails;
    private Node parentNode;
    private int level;
    private DiscoveryWorkerContext context;

    public DiscoveryWorker(ConnectionDetails connectionDetails, Node parentNode, int level, DiscoveryWorkerContext context) {
        this.connectionDetails = connectionDetails;
        this.parentNode = parentNode;
        this.level = level;
        this.context = context;
    }


    @Override
    protected void compute() {
        logger.debug("Discovery worker: " + Thread.currentThread().getName() + " started. connectionDetails = " + connectionDetails);
        String connectionType = connectionDetails.getConnectionType();
        NodeDiscoverer nodeDiscoverer = context.getNodeDiscoverer(connectionType);

        NodeDiscoveryResult discoveryResult = nodeDiscoverer.discover(connectionDetails);
        if (discoveryResult == null) {
            logger.debug("No node is discovered for connDetails: " + connectionDetails);
            logger.debug("Discovery worker: "+Thread.currentThread().getName()+" finished. connectionDetails = "+connectionDetails);
            return;
        }

        String nodeId = discoveryResult.getNodeId();
        Node currentNode = new Node(nodeId, connectionDetails);
        synchronized (context.getNodes()) {
            if (context.getNodes().containsKey(nodeId)) {
                logger.debug("Node is already discovered, nodeId=" + nodeId);
                logger.debug("Discovery worker: "+Thread.currentThread().getName()+" finished. connectionDetails = "+connectionDetails);
                return;
            }
            logger.info("New node is discovered, nodeId=" + nodeId);
            if (parentNode != null) {
                parentNode.addNeighbour(currentNode);
            }
            context.getNodes().put(nodeId, currentNode);
        }
        context.fireNodeDiscoveredEvent(discoveryResult);
        Map<String, List<ConnectionDetails>> neighboursConnectionDetails = discoveryResult.getNeighboursConnectionDetails();
        logger.debug("Found "+neighboursConnectionDetails.keySet().size()+" neighbour nodes of node '"+nodeId+"', connection details: " + neighboursConnectionDetails);
        List<DiscoveryWorker> discoveryWorkerList = new ArrayList<DiscoveryWorker>();
        for (String key : neighboursConnectionDetails.keySet()) {
            List<ConnectionDetails> connDetails = neighboursConnectionDetails.get(key);
            for (ConnectionDetails connDetail : connDetails) {
                synchronized (context.getUsedConnectionDetails()) {
                    if (!context.getUsedConnectionDetails().contains(connDetail)) {
                        context.getUsedConnectionDetails().add(connDetail);
                        discoveryWorkerList.add(new DiscoveryWorker(connDetail, currentNode, level, context));
                    }
                }
            }
        }
        invokeAll(discoveryWorkerList);
        logger.debug("Discovery worker: "+Thread.currentThread().getName()+" finished. connectionDetails = "+connectionDetails);
    }
}
