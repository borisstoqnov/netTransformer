package net.itransformers.bgpPeeringMap;

import org.apache.log4j.Logger;


public class BgpPeeringMapManagerThread extends Thread {
    static Logger logger = Logger.getLogger(BgpPeeringMapManagerThread.class);

    private BgpPeeringMap manager;

    public BgpPeeringMapManagerThread(BgpPeeringMap manager) {
        logger.debug("Thread created");
        this.manager = manager;
    }

    @Override
    public void run() {
        try {
            logger.debug("Thread started");
             manager.discover();
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }

    public void stopDiscovery(){
        logger.info("stopping discovery");
        manager.stop();
    }
    public void pauseDiscovery(){
        logger.info("pausing discovery");
        manager.pause();
    }
    public void resumeDiscovery() {
        logger.info("resuming discovery");
        manager.resume();
    }
    public BgpDiscoveryManagerStatus getStatus(){
        BgpDiscoveryManagerStatus status = null;

        if (manager.isPaused()) {
            status =  BgpDiscoveryManagerStatus.PAUSED;
        } else if (manager.isRunning()) {
            status = BgpDiscoveryManagerStatus.RUNNING;
        } else if (manager.isStopped()) {
            status = BgpDiscoveryManagerStatus.STOPPED;
        } else {
            status = BgpDiscoveryManagerStatus.CONFIGURED;
        }
        logger.info("getting status. Status="+status);
        return status;
    }
}

