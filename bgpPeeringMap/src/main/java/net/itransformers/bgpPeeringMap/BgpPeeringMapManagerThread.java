/*
 * BgpPeeringMapManagerThread.java
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

package net.itransformers.bgpPeeringMap;

import org.apache.log4j.Logger;

@Deprecated
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

