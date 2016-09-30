package net.itransformers.idiscover.api;

import net.itransformers.idiscover.api.models.graphml.GraphmlGraph;
import net.itransformers.idiscover.api.models.node_data.DiscoveredDeviceData;
import net.itransformers.idiscover.api.models.node_data.RawDeviceData;

import java.util.List;
import java.util.Set;

/**
 * Created by vasko on 9/18/2016.
 */
public interface DiscoveryResult {
    List<String> getDiscoveredVersions();
    Set<String> getDiscoveredNodes(String version);

    String getCreationDate(String version);
    GraphmlGraph getNetwork(String version);
    GraphmlGraph getNetwork(String version, String nodeId,int hops);
    RawDeviceData getRawData(String version, String nodeId);

    DiscoveredDeviceData getDiscoverdNodeData(String version, String nodeId);
}
