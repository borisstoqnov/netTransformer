package net.itransformers.idiscover.v2.core.listeners.neighbor;

import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.idiscover.v2.core.NodeNeighboursDiscoveryListener;
import net.itransformers.idiscover.v2.core.model.Node;

/**
 * Created by vasko on 16.06.16.
 */
public class SimpleNodeNeighbourDiscoveryListener implements NodeNeighboursDiscoveryListener {
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
        System.out.println(sb.toString());

    }
}
