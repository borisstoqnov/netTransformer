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

package net.itransformers.topologyviewer.rightclick.impl;

import net.itransformers.resourcemanager.ResourceManager;
import net.itransformers.resourcemanager.config.ConnectionParamsType;
import net.itransformers.resourcemanager.config.ParamType;
import net.itransformers.resourcemanager.config.ResourceType;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ResourceResolver {
    public static Map<String, String> getResource(Map<String, String> graphMLParams,
                                                  File resourceFile) throws Exception {
        ResourceType resource;
        ResourceManager rc = new ResourceManager(resourceFile);
        resource = rc.findResource(graphMLParams);
        if (resource == null) {
            throw new RuntimeException("Resource not found");
        }

        return getConnectionParams(resource,graphMLParams);
    }
    public static Map<String, String> getConnectionParams(ResourceType resource, Map<String, String> graphMLParams){
        ConnectionParamsType connParamsType = resource.getConnectionParams().get(0);
        Map<String, String> connParams = new HashMap<String, String>();
        for (ParamType param : connParamsType.getParam()) {
            connParams.put(param.getName(), param.getValue());
        }
        // ask Niki how to remove this hardcode
        connParams.put("ManagementIPAddress",graphMLParams.get("ManagementIPAddress"));
        connParams.put("protocol", connParamsType.getConnectionType());
        return connParams;
    }
}
