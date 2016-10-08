/*
 * ANetworkDiscoverer.java
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


import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetails;
import net.itransformers.idiscover.api.NetworkDiscoverer;
import net.itransformers.idiscover.api.NetworkDiscoveryListener;
import net.itransformers.idiscover.api.NetworkDiscoveryResult;
import net.itransformers.idiscover.api.NodeDiscoverFilter;

import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: niau
 * Date: 12/2/14
 * Time: 11:52 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ANetworkDiscoverer implements NetworkDiscoverer {
    protected List<NetworkDiscoveryListener> networkDiscoveryListeners;

    protected NodeDiscoverFilter nodeDiscoverFilter;

    public List<NetworkDiscoveryListener> getNetworkDiscoveryListeners() {
        return networkDiscoveryListeners;
    }

    public void setNetworkDiscoveryListeners(List<NetworkDiscoveryListener> networkDiscoveryListeners) {
        this.networkDiscoveryListeners = networkDiscoveryListeners;
    }
    protected void fireNetworkDiscoveredEvent(NetworkDiscoveryResult result) {
        if (networkDiscoveryListeners != null)
            for (NetworkDiscoveryListener networkDiscoveryListener : networkDiscoveryListeners) {
                networkDiscoveryListener.networkDiscovered(result);
            }
    }
    public void setNodeDiscoverFilter(NodeDiscoverFilter filter) {
        this.nodeDiscoverFilter = filter;
    }



    @Override
    public void addNetworkDiscoveryListeners(NetworkDiscoveryListener networkDiscoveryListener) {
        this.networkDiscoveryListeners.add(networkDiscoveryListener);
    }

    @Override
    public void removeNetworkDiscoveryListeners(NetworkDiscoveryListener networkDiscoveryListener) {
        this.networkDiscoveryListeners.remove(networkDiscoveryListener);
    }

}
