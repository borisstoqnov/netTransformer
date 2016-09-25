package net.itransformers.idiscover.api;

/**
 * Created by vasko on 9/18/2016.
 */
public interface DiscoveryProcess {
    /**
     *
     * @return Discovery process id (This is the version field in current impl)
     */
    String start();

    /**
     *
     * @param discoveryProcessId Discovery process id
     */
    void stop(String discoveryProcessId);
    /**
     *
     * @param discoveryProcessId Discovery process id
     */
    void pause(String discoveryProcessId);
    /**
     *
     * @param discoveryProcessId Discovery process id
     */
    void resume(String discoveryProcessId);
}
