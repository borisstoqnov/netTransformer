

/*
 * DiscoveryHelper.java
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

package net.itransformers.idiscover.core;

import net.itransformers.idiscover.api.models.node_data.*;

import java.util.Map;

public interface DiscoveryHelper {

    DiscoveredDeviceData parseDeviceRawData(net.itransformers.idiscover.api.models.node_data.RawDeviceData rawData, String[] discoveryTypes, Map<String, String> params);

    Device createDevice(DiscoveredDeviceData discoveredDeviceData);

    String[] getRequestParams(String[] discoveryType);

}
