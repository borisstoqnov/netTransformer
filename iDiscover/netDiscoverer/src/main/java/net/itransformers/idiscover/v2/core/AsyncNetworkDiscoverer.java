package net.itransformers.idiscover.v2.core;

import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetails;
import net.itransformers.idiscover.api.NetworkDiscoverer;
import net.itransformers.idiscover.api.NetworkDiscoveryListener;

import java.util.List;
import java.util.Set;

/**
 * Created by vasko on 9/30/2016.
 */
public class AsyncNetworkDiscoverer implements NetworkDiscoverer {
    protected NetworkDiscoverer networkDiscoverer;

    public AsyncNetworkDiscoverer(NetworkDiscoverer networkDiscoverer) {
        this.networkDiscoverer = networkDiscoverer;
    }

    @Override
    public void startDiscovery(Set<ConnectionDetails> connectionDetailsList) {
        new Thread(() -> networkDiscoverer.startDiscovery(connectionDetailsList)).start();
    }

    @Override
    public void stopDiscovery() {
        networkDiscoverer.stopDiscovery();
    }

    @Override
    public void pauseDiscovery() {
        networkDiscoverer.pauseDiscovery();
    }

    @Override
    public void resumeDiscovery() {
        networkDiscoverer.resumeDiscovery();
    }

    @Override
    public void addNetworkDiscoveryListeners(NetworkDiscoveryListener networkDiscoveryListeners) {
        networkDiscoverer.addNetworkDiscoveryListeners(networkDiscoveryListeners);
    }

    @Override
    public void removeNetworkDiscoveryListeners(NetworkDiscoveryListener networkDiscoveryListeners) {
        networkDiscoverer.removeNetworkDiscoveryListeners(networkDiscoveryListeners);
    }

}
