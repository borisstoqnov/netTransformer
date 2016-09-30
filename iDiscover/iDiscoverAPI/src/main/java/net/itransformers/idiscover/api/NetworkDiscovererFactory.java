package net.itransformers.idiscover.api;

/**
 * Created by vasko on 9/30/2016.
 */
public interface NetworkDiscovererFactory {
    NetworkDiscoverer createNetworkDiscoverer(String projectPath, String version);
    NetworkDiscoverer createNetworkDiscoverer(String projectPath, String version, int initialNumberOfThreads, int maxNumberOfThreads);
}
