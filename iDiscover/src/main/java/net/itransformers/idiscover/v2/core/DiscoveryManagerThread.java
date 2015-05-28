

package net.itransformers.idiscover.v2.core;

import net.itransformers.idiscover.core.DiscoveryManagerStatus;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;


public class DiscoveryManagerThread extends Thread {
    static Logger logger = Logger.getLogger(DiscoveryManagerThread.class);

    private NetworkNodeDiscovererImpl nodeDiscovererImpl;
    private int depth;
    LinkedHashMap<String, ConnectionDetails>  connectionDetails;

    private List<DiscoveryManagerListener> managerListeners = new ArrayList<DiscoveryManagerListener>();
    public DiscoveryManagerThread(NetworkNodeDiscovererImpl nodeDiscovererImpl, int depth, LinkedHashMap<String, ConnectionDetails> connectionDetails) {
        logger.debug("Thread created");
        this.nodeDiscovererImpl = nodeDiscovererImpl;
        this.depth = depth;
        this.connectionDetails = connectionDetails;
    }

    @Override
    public void run() {
        try {
            logger.debug("Thread started");
            fireEvent(DiscoveryManagerEvent.STARTED);
            nodeDiscovererImpl.discoverNetwork(new ArrayList<ConnectionDetails>(connectionDetails.values()), depth);
            fireEvent(DiscoveryManagerEvent.STOPPED);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }

    private void fireEvent(DiscoveryManagerEvent started) {
        for (DiscoveryManagerListener managerListener : managerListeners) {
            managerListener.handleEvent(started);
        }
    }

    public void stopDiscovery(){
        logger.info("stopping discovery");
        fireEvent(DiscoveryManagerEvent.STOPPING);
        nodeDiscovererImpl.stop();
    }
    public void pauseDiscovery(){
        logger.info("pausing discovery");
        nodeDiscovererImpl.pause();
        fireEvent(DiscoveryManagerEvent.PAUSED);
    }
    public void resumeDiscovery() {
        logger.info("resuming discovery");
        nodeDiscovererImpl.resume();
        fireEvent(DiscoveryManagerEvent.RESUMED);
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
        logger.info("getting status. Status=" + status);
        return status;
    }

    public void addDiscoveryManagerListener(DiscoveryManagerListener listener){
         managerListeners.add(listener);
    }


}
