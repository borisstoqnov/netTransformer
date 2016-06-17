package net.itransformers.idiscover.v2.core.listeners.neighbor;

import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.idiscover.v2.core.NodeNeighboursDiscoveryListener;
import net.itransformers.idiscover.v2.core.model.Node;
import org.apache.log4j.Logger;

/**
 * Created by vasko on 16.06.16.
 */
public class SimpleNodeNeighbourDiscoveryListener implements NodeNeighboursDiscoveryListener {
    static Logger logger = Logger.getLogger(SimpleNodeNeighbourDiscoveryListener.class);
    @Override
    public void handleNodeNeighboursDiscovered(Node node, NodeDiscoveryResult nodeDiscoveryResult) {
        StringBuilder sb = new StringBuilder();

        sb.append("SimpleNodeNeighbourDiscoveryListener -> ");
        sb.append(String.format("nodeId=%s", node.getId()));
        sb.append("neighbours=(");
        for (Node neighbour : node.getNeighbours()) {
            sb.append(neighbour.getId());
            sb.append(",");
        }
        sb.append(")");
        logger.info(sb.toString());

    }
}
