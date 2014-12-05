package net.itransformers.idiscover.v2.core;

import net.itransformers.idiscover.v2.core.model.ConnectionDetails;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: niau
 * Date: 12/2/14
 * Time: 11:52 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ANetworkDiscoverer implements NetworkDiscoverer  {
    protected List<NetworkDiscoveryListener> networkDiscoveryListeners;

    protected NodeDiscoverFilter nodeDiscoverFilter;

    public NetworkDiscoveryResult discoverNetwork(List<ConnectionDetails> connectionDetailsList) {
        return discoverNetwork(connectionDetailsList, -1);
    }

    public List<NetworkDiscoveryListener> getNetworkDiscoveryListeners() {
        return networkDiscoveryListeners;
    }

    public void setNetworkDiscoveryListeners(List<NetworkDiscoveryListener> networkDiscoveryListeners) {
        this.networkDiscoveryListeners = networkDiscoveryListeners;
    }
    protected void fireNetworkDiscoveredEvent(NetworkDiscoveryResult result) {
        if (networkDiscoveryListeners != null)
            for (NetworkDiscoveryListener networkDiscoveryListener : networkDiscoveryListeners) {
                networkDiscoveryListener.networkDiscovered(result);
            }
    }
    public void setNodeDiscoverFilter(NodeDiscoverFilter filter) {
        this.nodeDiscoverFilter = filter;
    }




}
