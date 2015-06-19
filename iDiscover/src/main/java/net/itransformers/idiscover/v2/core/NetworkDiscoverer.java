package net.itransformers.idiscover.v2.core;

import net.itransformers.idiscover.v2.core.model.ConnectionDetails;

import java.util.List;

public interface NetworkDiscoverer  {
    NetworkDiscoveryResult discoverNetwork(List<ConnectionDetails> connectionDetailsList);
    NetworkDiscoveryResult discoverNetwork(List<ConnectionDetails> connectionDetailsList, int depth);
    void setNetworkDiscoveryListeners(List<NetworkDiscoveryListener> networkDiscoveryListeners);
    }
