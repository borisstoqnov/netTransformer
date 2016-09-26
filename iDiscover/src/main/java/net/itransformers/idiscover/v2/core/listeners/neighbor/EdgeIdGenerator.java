package net.itransformers.idiscover.v2.core.listeners.neighbor;

import net.itransformers.idiscover.api.models.graphml.GraphmlEdge;

/**
 * Created by niau on 9/16/16.
 */
public class EdgeIdGenerator {
    String fromNodeId;
    String toNodeId;
    String fromNodeEdgeId;
    String toNodeEdgeId;


    public EdgeIdGenerator(String fromNodeId, String toNodeId,String fromNodeEdgeId, String toNodeEdgeId) {
        this.fromNodeId = fromNodeId;
        this.toNodeId = toNodeId;
        this.fromNodeEdgeId = fromNodeEdgeId;
        this.toNodeEdgeId = toNodeEdgeId;
    }

    public GraphmlEdge createEdge(){
        String graphmlEdgeId;
        String from;
        String to;
        int comparisson = fromNodeId.compareToIgnoreCase(toNodeId);
        if (comparisson <0){
            graphmlEdgeId =   fromNodeEdgeId+"-"+toNodeEdgeId;
            from=fromNodeId;
            to=toNodeId;
        }else{
            graphmlEdgeId =   toNodeEdgeId+"-"+fromNodeEdgeId;
            from=toNodeId;
            to=fromNodeId;
        }
        return new GraphmlEdge(graphmlEdgeId,graphmlEdgeId,from,to);
    }
}
