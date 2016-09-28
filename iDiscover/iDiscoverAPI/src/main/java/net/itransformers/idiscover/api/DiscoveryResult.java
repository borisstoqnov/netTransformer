package net.itransformers.idiscover.api;

import net.itransformers.idiscover.api.models.graphml.GraphmlGraph;
import net.itransformers.idiscover.api.models.graphml.GraphmlNode;
import net.itransformers.idiscover.api.models.node_data.DiscoveredDeviceData;
import net.itransformers.idiscover.api.models.node_data.RawDeviceData;

import java.util.List;

/**
 * Created by vasko on 9/18/2016.
 */
public interface DiscoveryResult {
    List<String> getDiscoveredVersions();
    List<String> getDiscoveredNodes(String version);

    String getCreationDate(String version);
    GraphmlGraph getNetwork(String version);
    GraphmlNode getNode(String version, String nodeId);
    RawDeviceData getRawData(String version, String nodeId);

    DiscoveredDeviceData getDeviceHierarchicalModel(String version, String nodeId);
}
