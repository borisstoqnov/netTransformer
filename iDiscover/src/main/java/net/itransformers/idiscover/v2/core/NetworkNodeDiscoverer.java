package net.itransformers.idiscover.v2.core;

import net.itransformers.idiscover.v2.core.model.ConnectionDetails;

import java.util.List;
import java.util.Map;


public abstract class NetworkNodeDiscoverer implements NetworkDiscoverer {
   protected Map<String, NodeDiscoverer> nodeDiscoverers;
   protected List<NodeDiscoveryListener> nodeDiscoveryListeners;
   protected List<NetworkDiscoveryListener> networkDiscoveryListeners;
   protected NodeDiscoverFilter nodeDiscoverFilter;

    public NetworkDiscoveryResult discoverNetwork(List<ConnectionDetails> connectionDetailsList) {
        return discoverNetwork(connectionDetailsList, -1);
    }

    public void setNodeDiscoverFilter(NodeDiscoverFilter filter) {
        this.nodeDiscoverFilter = filter;
    }


    public void setNodeDiscoverers(Map<String, NodeDiscoverer> nodeDiscoverers) {
        this.nodeDiscoverers = nodeDiscoverers;
    }

    public List<NodeDiscoveryListener> getNodeDiscoveryListeners() {
        return nodeDiscoveryListeners;
    }

    public void setNodeDiscoveryListeners(List<NodeDiscoveryListener> nodeDiscoveryListeners) {
        this.nodeDiscoveryListeners = nodeDiscoveryListeners;
    }

    public List<NetworkDiscoveryListener> getNetworkDiscoveryListeners() {
        return networkDiscoveryListeners;
    }

    public void setNetworkDiscoveryListeners(List<NetworkDiscoveryListener> networkDiscoveryListeners) {
        this.networkDiscoveryListeners = networkDiscoveryListeners;
    }
    protected void fireNodeDiscoveredEvent(NodeDiscoveryResult discoveryResult) {
        if (nodeDiscoveryListeners != null)
            for (NodeDiscoveryListener nodeDiscoveryListener : nodeDiscoveryListeners) {
                nodeDiscoveryListener.nodeDiscovered(discoveryResult);
            }
    }

    protected void fireNetworkDiscoveredEvent(NetworkDiscoveryResult result) {
        if (networkDiscoveryListeners != null)
            for (NetworkDiscoveryListener networkDiscoveryListener : networkDiscoveryListeners) {
                networkDiscoveryListener.networkDiscovered(result);
            }
    }
}
