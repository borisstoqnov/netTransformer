/*
 * NetworkDiscoverer.java
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

package net.itransformers.idiscover.api;


import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetails;

import java.util.List;
import java.util.Set;

public interface NetworkDiscoverer  {
    void startDiscovery(Set<ConnectionDetails> connectionDetailsList);
    void stopDiscovery();
    void pauseDiscovery();
    void resumeDiscovery();

    void addNetworkDiscoveryListeners(NetworkDiscoveryListener networkDiscoveryListeners);
    void removeNetworkDiscoveryListeners(NetworkDiscoveryListener networkDiscoveryListeners);

}
