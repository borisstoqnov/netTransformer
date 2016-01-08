package net.itransformers.idiscover.v2.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vasko on 08.01.16.
 */
public class MockNetworkDiscoveryListener implements NetworkDiscoveryListener {
    List<NetworkDiscoveryResult> networkDiscoveryResults = new ArrayList<NetworkDiscoveryResult>();

    @Override
    public void networkDiscovered(NetworkDiscoveryResult result) {
        networkDiscoveryResults.add(result);
    }

    public List<NetworkDiscoveryResult> getNetworkDiscoveryResults() {
        return networkDiscoveryResults;
    }
}
