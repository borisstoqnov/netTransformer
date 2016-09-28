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
    String toMac;
    String fromMac;
    String toIpAddress;
    String fromIPAddress;



    public EdgeIdGenerator(String fromNodeId, String toNodeId,String fromNodeEdgeId, String toNodeEdgeId) {
        this.fromNodeId = fromNodeId;
        this.toNodeId = toNodeId;
        this.fromNodeEdgeId = fromNodeEdgeId;
        this.toNodeEdgeId = toNodeEdgeId;
    }


    public EdgeIdGenerator(String fromNodeId, String toNodeId,String fromNodeEdgeId, String toNodeEdgeId,String toMac,String fromMac) {
        this.fromNodeId = fromNodeId;
        this.toNodeId = toNodeId;
        this.fromNodeEdgeId = fromNodeEdgeId;
        this.toNodeEdgeId = toNodeEdgeId;
        this.toMac = toMac;
        this.fromMac = fromMac;
    }



    public GraphmlEdge createEdge(){
        String graphmlEdgeId;
        String from;
        String to;
        if (toMac!=null && fromMac!=null){
            int comparisson = fromNodeId.compareToIgnoreCase(toNodeId);
            if (comparisson <0){
                graphmlEdgeId =   fromNodeEdgeId+fromMac+"-"+toNodeEdgeId+toMac;
                from=fromNodeId;
                to=toNodeId;
            }else{
                graphmlEdgeId =   toNodeEdgeId+toMac+"-"+fromNodeEdgeId+fromMac;
                from=toNodeId;
                to=fromNodeId;
            }

        } else {
            int comparisson = fromNodeId.compareToIgnoreCase(toNodeId);
            if (comparisson < 0) {
                graphmlEdgeId = fromNodeEdgeId + "-" + toNodeEdgeId;
                from = fromNodeId;
                to = toNodeId;
            } else {
                graphmlEdgeId = toNodeEdgeId + "-" + fromNodeEdgeId;
                from = toNodeId;
                to = fromNodeId;
            }
        }
        return new GraphmlEdge(graphmlEdgeId,graphmlEdgeId,from,to);
    }
}
