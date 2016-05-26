package net.itransformers.idiscover.v2.core.parallel;

import net.itransformers.idiscover.v2.core.NodeDiscoverer;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.idiscover.v2.core.model.Node;

import java.util.Map;
import java.util.Set;

/**
 * Created by Vasil Yordanov on 1/9/2016.
 */
public interface DiscoveryWorkerContext {
    void fireNodeDiscoveredEvent(ConnectionDetails connectionDetails, NodeDiscoveryResult nodeDiscoveryResult);
    void fireNodeNotDiscoveredEvent(ConnectionDetails connectionDetails);

    Map<String, Node> getNodes();
    Set<ConnectionDetails> getUsedConnectionDetails();
    NodeDiscoverer getNodeDiscoverer(String connectionType);
}
