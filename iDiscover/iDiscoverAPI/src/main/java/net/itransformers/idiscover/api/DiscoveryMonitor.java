package net.itransformers.idiscover.api;

/**
 * Created by vasko on 9/18/2016.
 */
public interface DiscoveryMonitor {
    int getNumberOfActiveDiscoveryThreads();
    int getNumberOfQueuedDiscoveryThreads();
    int getNumberOfDiscoveredDevices();
}
