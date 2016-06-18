package net.itransformers.idiscover.v2.core.parallel;

import net.itransformers.idiscover.v2.core.NodeDiscoverer;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.idiscover.v2.core.ipnetwork.IPNetConnectionDetails;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.idiscover.v2.core.model.Node;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.RecursiveAction;

/**
 * Created by Vasil Yordanov on 1/9/2016.
 */
public class DiscoveryWorker extends RecursiveAction {
    static Logger logger = Logger.getLogger(DiscoveryWorker.class);
    private ConnectionDetails connectionDetails;
    private Node parentNode;
    private int level;
    private final DiscoveryWorkerContext context;

    public DiscoveryWorker(ConnectionDetails connectionDetails, Node parentNode, int level, DiscoveryWorkerContext context) {
        // clone connection details to avoid problems due to later modification of connection details in match or discover methods invoked in discovery worker
        this.connectionDetails = new IPNetConnectionDetails(connectionDetails.getConnectionType(), connectionDetails.getParams());

        this.parentNode = parentNode;
        this.level = level;
        this.context = context;
    }


    @Override
    protected void compute() {
        logger.debug("Discovery worker: " + Thread.currentThread().getName() + " started. connectionDetails = " + connectionDetails);
        String connectionType = connectionDetails.getConnectionType();
        NodeDiscoverer nodeDiscoverer = context.getNodeDiscoverer(connectionType);

        if (context.getNotDiscoveredConnectionDetails().contains(connectionDetails)){
            logger.debug("No node could be discovered for connDetails: " + connectionDetails);
            context.fireNodeNotDiscoveredEvent(connectionDetails);
            return;
        }

        NodeDiscoveryResult discoveryResult = nodeDiscoverer.discover(connectionDetails);
        if (discoveryResult == null) {
            logger.debug("No node is discovered for connDetails: " + connectionDetails);
            logger.debug("Discovery worker: " + Thread.currentThread().getName() + " finished. connectionDetails = " + connectionDetails);
            context.fireNodeNotDiscoveredEvent(connectionDetails);
            return;
        }

        String nodeId = discoveryResult.getNodeId();
        Node currentNode = new Node(nodeId);
        synchronized (context) {
            if (context.getNodes().containsKey(nodeId)) {
                logger.debug("Node is already discovered, nodeId=" + nodeId);
                logger.debug("Discovery worker: " + Thread.currentThread().getName() + " finished. connectionDetails = " + connectionDetails);
                return;
            }
            logger.info("New node is discovered, nodeId=" + nodeId);
            if (parentNode != null) {
                parentNode.addNeighbour(currentNode);
            }
            context.getNodes().put(nodeId, currentNode);
        }
        context.fireNodeDiscoveredEvent(this.connectionDetails, discoveryResult);
        Set<ConnectionDetails> neighboursConnectionDetails = discoveryResult.getNeighboursConnectionDetails();
        logger.debug("Found " + neighboursConnectionDetails.size() + " neighbour nodes of node '" + nodeId + "', connection details: " + neighboursConnectionDetails);
        List<DiscoveryWorker> discoveryWorkerList = new ArrayList<DiscoveryWorker>();
        for (ConnectionDetails connDetails : neighboursConnectionDetails) {
            synchronized (context) {
                Set<ConnectionDetails> discoveringConnectionDetails = context.getDiscoveringConnectionDetails();
                Set<ConnectionDetails> discoveredConnectionDetails = context.getDiscoveredConnectionDetails();
                Set<ConnectionDetails> notDiscoveredConnectionDetails = context.getNotDiscoveredConnectionDetails();
                if (!discoveringConnectionDetails.contains(connDetails) ||
                        !discoveredConnectionDetails.contains(connDetails) ||
                        !notDiscoveredConnectionDetails.contains(connDetails)) {
                    discoveringConnectionDetails.add(connDetails);
                    discoveryWorkerList.add(new DiscoveryWorker(connDetails, currentNode, level, context));
                }
            }
        }
        invokeAll(discoveryWorkerList);
        logger.debug("Discovery worker: " + Thread.currentThread().getName() + " finished. connectionDetails = " + connectionDetails);
    }
}
