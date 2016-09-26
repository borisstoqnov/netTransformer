package net.itransformers.idiscover.api;

import net.itransformers.idiscover.api.models.graphml.GraphmlGraph;
import net.itransformers.idiscover.api.models.node_data.DiscoveredDeviceData;

/**
 * Created by vasko on 9/18/2016.
 */
public interface DiscoveryResult {
    String[] getDiscoveredVersions();
    String getCreationDate(String version);
    GraphmlGraph getVersionNetworkCentricModel(String version, String nodeId);
    GraphmlGraph getNodeCentricNetworkModel(String version, String nodeId);
    DiscoveredDeviceData getDeviceHierarchicalModel(String version, String nodeId);
}
