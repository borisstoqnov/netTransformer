/*
 * iTransformer is an open source tool able to discover IP networks
 * and to perform dynamic data data population into a xml based inventory system.
 * Copyright (C) 2010  http://itransformers.net
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.itransformers.idiscover.core;

import net.itransformers.idiscover.networkmodel.NetworkType;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * Date: 12-1-26
 * Time: 13:12
 * To change this template use File | Settings | File Templates.
 */
public class DiscoveryManagerThread extends Thread {
    static Logger logger = Logger.getLogger(DiscoveryManagerThread.class);

    private DiscoveryManager manager;
    private Resource resource;
    private String mode;
    private String[] discoveryTypes;

    public DiscoveryManagerThread(DiscoveryManager manager, Resource resource, String mode, String[] discoveryTypes) {
        logger.debug("Thread created");
        this.manager = manager;
        this.resource = resource;
        this.mode = mode;
        this.discoveryTypes = discoveryTypes;
    }

    @Override
    public void run() {
        try {
            logger.debug("Thread started");
            NetworkType network = manager.discoverNetwork(resource, mode, discoveryTypes);
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
    public DiscoveryManagerStatus getStatus(){
        DiscoveryManagerStatus status = null; 

        if (manager.isPaused()) {
            status =  DiscoveryManagerStatus.PAUSED;
        } else if (manager.isRunning()) {
            status = DiscoveryManagerStatus.RUNNING;
        } else if (manager.isStopped()) {
            status = DiscoveryManagerStatus.STOPPED;
        } else {
            status = DiscoveryManagerStatus.CONFIGURED;
        }
        logger.info("getting status. Status="+status);
        return status;
    }
}
