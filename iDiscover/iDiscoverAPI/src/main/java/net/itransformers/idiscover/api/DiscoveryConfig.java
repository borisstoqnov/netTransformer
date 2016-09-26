package net.itransformers.idiscover.api;

/**
 * Created by vasko on 9/18/2016.
 */
public interface DiscoveryConfig {
    void setMaxDiscoveryThreads(int numThreads);
    void setInitialDiscoveryThreads(int numThreads);
}
