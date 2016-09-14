package net.itransformers.idiscover.v2.core;

import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.idiscover.v2.core.model.Node;

import java.util.Map;

/**
 * Created by Vasil Yordanov on 26-May-16.
 */
public interface NodeNeighboursDiscoveryListener {
    void handleNodeNeighboursDiscovered(Node node, NodeDiscoveryResult nodeDiscoveryResult);
}
