package net.itransformers.idiscover.api;

/**
 * Created by vasko on 9/18/2016.
 */
public interface DiscoveryProcess {
    void startDiscovery();
    void stopDiscovery();
    void pauseDiscovery();
    void resumeDiscovery();
}
