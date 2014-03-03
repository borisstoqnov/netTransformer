/*
 * iTransformer is an open source tool able to discover and transform
 *  IP network infrastructures.
 *  Copyright (C) 2012  http://itransformers.net
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.itransformers.idiscover.v2.core;

import net.itransformers.idiscover.core.DiscoveryManagerStatus;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import org.apache.log4j.Logger;

import java.util.Arrays;


public class DiscoveryManagerThread extends Thread {
    static Logger logger = Logger.getLogger(DiscoveryManagerThread.class);

    private NetworkDiscoverer discoverer;
    private int depth;
    ConnectionDetails connectionDetails;

    public DiscoveryManagerThread(NetworkDiscoverer discoverer, int depth, ConnectionDetails connectionDetails) {
        logger.debug("Thread created");
        this.discoverer = discoverer;
        this.depth = depth;
        this.connectionDetails = connectionDetails;
    }

    @Override
    public void run() {
        try {
            logger.debug("Thread started");
            discoverer.discoverNodes(Arrays.asList(connectionDetails), depth);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
        }
    }

    public void stopDiscovery(){
        logger.info("stopping discovery");
        discoverer.stop();
    }
    public void pauseDiscovery(){
        logger.info("pausing discovery");
        discoverer.pause();
    }
    public void resumeDiscovery() {
        logger.info("resuming discovery");
        discoverer.resume();
    }
    public DiscoveryManagerStatus getStatus(){
        DiscoveryManagerStatus status = null; 

        if (discoverer.isPaused()) {
            status =  DiscoveryManagerStatus.PAUSED;
        } else if (discoverer.isRunning()) {
            status = DiscoveryManagerStatus.RUNNING;
        } else if (discoverer.isStopped()) {
            status = DiscoveryManagerStatus.STOPPED;
        } else {
            status = DiscoveryManagerStatus.CONFIGURED;
        }
        logger.info("getting status. Status="+status);
        return status;
    }
}
