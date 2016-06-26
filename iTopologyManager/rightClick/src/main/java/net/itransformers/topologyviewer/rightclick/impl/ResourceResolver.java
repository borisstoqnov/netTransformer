/*
 * ResourceResolver.java
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

package net.itransformers.topologyviewer.rightclick.impl;

import net.itransformers.resourcemanager.ResourceManager;
import net.itransformers.resourcemanager.config.ConnectionParamsType;
import net.itransformers.resourcemanager.config.ParamType;
import net.itransformers.resourcemanager.config.ResourceType;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResourceResolver {
    public static Map<String, String> getResource(Map<String, String> graphMLParams,
                                                  File resourceFile, String protocol) throws Exception {
        ResourceType resource;
        ResourceManager rc = new ResourceManager(resourceFile);
        resource = rc.findResource(graphMLParams);
        if (resource == null) {
            throw new RuntimeException("Resource not found");
        }

        return getConnectionParams(resource, graphMLParams, protocol);
    }

    public static Map<String, String> getConnectionParams(ResourceType resource, Map<String, String> graphMLParams, String protocol) {

        List<ConnectionParamsType> connParamsTypes = resource.getConnectionParams();

        for (ConnectionParamsType connParamsType : connParamsTypes) {

            if (connParamsType.getConnectionType().equals(protocol)){

                Map<String, String> connParams = new HashMap<String, String>();
                for (ParamType param : connParamsType.getParam()) {
                    connParams.put(param.getName(), param.getValue());
                }

                connParams.put("discoveredIPv4Address", graphMLParams.get("discoveredIPv4Address"));
                connParams.put("protocol", connParamsType.getConnectionType());
                return connParams;
            }
        }

        if(resource.getConnectionParams()!=null){
            ConnectionParamsType connParamsType = resource.getConnectionParams().get(0);
            Map<String, String> connParams = new HashMap<String, String>();
            for (ParamType param : connParamsType.getParam()) {
                connParams.put(param.getName(), param.getValue());
            }
            // ask Niki how to remove this hardcode
            connParams.put("discoveredIPv4Address",graphMLParams.get("discoveredIPv4Address"));
            connParams.put("protocol", connParamsType.getConnectionType());
            return connParams;

        }else {
             return null;
        }
    }


}
