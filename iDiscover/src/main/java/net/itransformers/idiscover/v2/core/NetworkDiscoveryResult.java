

package net.itransformers.idiscover.v2.core;

import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.idiscover.v2.core.model.Node;

import java.util.HashMap;
import java.util.Map;

public class NetworkDiscoveryResult {
    Map<String,NodeDiscoveryResult> discoveredData = new HashMap<String,  NodeDiscoveryResult>();

    Map<String,ConnectionDetails> sourceConnectionDetails = new HashMap<String,  ConnectionDetails>();
    Map<String, Node> nodes = new HashMap<String, Node>();

    public Object getDiscoveredData(String sourceId){
        return discoveredData.get(sourceId);
    }
    public void addDiscoveredData(String sourceId, NodeDiscoveryResult discoveredNodeData){

        discoveredData.put(sourceId,discoveredNodeData);
    }

    public Map<String,NodeDiscoveryResult> getDiscoveredData() {
        return discoveredData;
    }

    public void setDiscoveredData(Map<String,NodeDiscoveryResult> discoveredData) {
        this.discoveredData = discoveredData;
    }
    public Map<String, ConnectionDetails> getSourceConnectionDetails() {
        return sourceConnectionDetails;
    }

    public void setSourceConnectionDetails(Map<String, ConnectionDetails> sourceConnectionDetails) {
        this.sourceConnectionDetails = sourceConnectionDetails;
    }

    public Map<String, Node> getNodes() {
        return nodes;
    }

    public void setNodes(Map<String, Node> nodes) {
        this.nodes = nodes;
    }

    @Override
    public String toString() {
        return "NetworkDiscoveryResult{" +
                "discoveredData=" + discoveredData +
                '}';
    }
}
