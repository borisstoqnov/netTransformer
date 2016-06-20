package net.itransformers.idiscover.v2.core.parallel;

import net.itransformers.idiscover.v2.core.NodeDiscoverer;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.idiscover.v2.core.factory.NodeFactory;
import net.itransformers.idiscover.v2.core.ipnetwork.IPNetConnectionDetails;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.idiscover.v2.core.model.Node;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private NodeFactory nodeFactory;

    public DiscoveryWorker(NodeFactory nodeFactory, ConnectionDetails connectionDetails, Node parentNode, int level, DiscoveryWorkerContext context) {
        this.nodeFactory = nodeFactory;
        // clone connection details to avoid problems due to later modification of connection details in match or discover methods invoked in discovery worker
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

        if (nodeDiscoverer == null) {
            logger.debug("No node discoverer can be found for connectionType: " + connectionDetails);
            logger.debug("Discovery worker: " + Thread.currentThread().getName() + " finished. connectionDetails = " + connectionDetails);
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
        Node currentNode = nodeFactory.createNode(nodeId);
        synchronized (context) {
            Map<String, Node> nodes = context.getNodes();
            if (nodes.containsKey(nodeId)) {
                logger.debug("Node is already discovered, nodeId=" + nodeId);
                logger.debug("Discovery worker: " + Thread.currentThread().getName() + " finished. connectionDetails = " + connectionDetails);
                return;
            }
            logger.info("New node is discovered, nodeId=" + nodeId);
            if (parentNode != null) {
                parentNode.addNeighbour(currentNode);
            }
            nodes.put(nodeId, currentNode);
        }
        context.fireNodeDiscoveredEvent(this.connectionDetails, discoveryResult);
        spawnNewWorkers(discoveryResult, nodeId, currentNode);
    }

    private void spawnNewWorkers(NodeDiscoveryResult discoveryResult, String nodeId, Node currentNode) {
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
                    discoveryWorkerList.add(new DiscoveryWorker(nodeFactory, connDetails, currentNode, level, context));
                }
            }
        }
        invokeAll(discoveryWorkerList);
        logger.debug("Discovery worker: " + Thread.currentThread().getName() + " finished. connectionDetails = " + connectionDetails);
    }
}
