

/*
 * DiscoveryManagerThread.java
 *
 * This work is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * This work is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * Copyright (c) 2010-2016 iTransformers Labs. All rights reserved.
 */

package net.itransformers.idiscover.v2.core;

import net.itransformers.idiscover.core.DiscoveryManager;
import net.itransformers.idiscover.core.DiscoveryManagerStatus;
import net.itransformers.idiscover.core.Resource;
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

    public DiscoveryManagerThread(DiscoveryManager manager, Resource resource, String s, String[] discoveryTypes) {

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
