package net.itransformers.idiscover.v2.core.factory;

import net.itransformers.idiscover.v2.core.model.Node;

/**
 * Created by vasko on 20.06.16.
 */
public class NodeFactory {
    public Node createNode(String nodeId){
        return new Node(nodeId);
    }
}
