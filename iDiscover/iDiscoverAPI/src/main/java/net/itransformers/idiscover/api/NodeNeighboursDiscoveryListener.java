package net.itransformers.idiscover.api;

import net.itransformers.idiscover.api.models.network.Node;

/**
 * Created by Vasil Yordanov on 26-May-16.
 */
public interface NodeNeighboursDiscoveryListener {
    void handleNodeNeighboursDiscovered(Node node, NodeDiscoveryResult nodeDiscoveryResult);
}
