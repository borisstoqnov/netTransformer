package net.itransformers.idiscover.api;

import net.itransformers.idiscover.api.models.graphml.GraphmlGraph;
import net.itransformers.idiscover.api.models.node_data.DiscoveredDeviceData;

/**
 * Created by vasko on 9/18/2016.
 */
public interface DiscoveryResult {
    GraphmlGraph getGraphmlData(String nodeId);
    DiscoveredDeviceData getNodeData(String nodeId);
}
