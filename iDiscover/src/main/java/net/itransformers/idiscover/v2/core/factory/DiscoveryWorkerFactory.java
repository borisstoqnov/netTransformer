package net.itransformers.idiscover.v2.core.factory;

import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.idiscover.v2.core.model.Node;
import net.itransformers.idiscover.v2.core.parallel.DiscoveryWorker;
import net.itransformers.idiscover.v2.core.parallel.DiscoveryWorkerContext;

/**
 * Created by vasko on 20.06.16.
 */
public class DiscoveryWorkerFactory {
    public DiscoveryWorker createDiscoveryWorker(NodeFactory nodeFactory, ConnectionDetails connectionDetails, Node parentNode, int level, DiscoveryWorkerContext context){
        return new DiscoveryWorker(nodeFactory, connectionDetails, parentNode, level, context);
    }
}
