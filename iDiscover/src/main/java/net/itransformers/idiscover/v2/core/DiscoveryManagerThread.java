

package net.itransformers.idiscover.v2.core;

import net.itransformers.idiscover.core.DiscoveryManagerStatus;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import org.apache.log4j.Logger;

import java.util.Arrays;


public class DiscoveryManagerThread extends Thread {
    static Logger logger = Logger.getLogger(DiscoveryManagerThread.class);

    private NetworkNodeDiscovererImpl nodeDiscovererImpl;
    private int depth;
    ConnectionDetails connectionDetails;

    public DiscoveryManagerThread(NetworkNodeDiscovererImpl nodeDiscovererImpl, int depth, ConnectionDetails connectionDetails) {
        logger.debug("Thread created");
        this.nodeDiscovererImpl = nodeDiscovererImpl;
        this.depth = depth;
        this.connectionDetails = connectionDetails;
    }

    @Override
    public void run() {
        try {
            logger.debug("Thread started");
            nodeDiscovererImpl.discoverNetwork(Arrays.asList(connectionDetails), depth);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }

    public void stopDiscovery(){
        logger.info("stopping discovery");
        nodeDiscovererImpl.stop();
    }
    public void pauseDiscovery(){
        logger.info("pausing discovery");
        nodeDiscovererImpl.pause();
    }
    public void resumeDiscovery() {
        logger.info("resuming discovery");
        nodeDiscovererImpl.resume();
    }
    public DiscoveryManagerStatus getStatus(){
        DiscoveryManagerStatus status = null; 

        if (nodeDiscovererImpl.isPaused()) {
            status =  DiscoveryManagerStatus.PAUSED;
        } else if (nodeDiscovererImpl.isRunning()) {
            status = DiscoveryManagerStatus.RUNNING;
        } else if (nodeDiscovererImpl.isStopped()) {
            status = DiscoveryManagerStatus.STOPPED;
        } else {
            status = DiscoveryManagerStatus.CONFIGURED;
        }
        logger.info("getting status. Status="+status);
        return status;
    }
}
