/*
 * DiscoveryResourceManager.java
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
import net.itransformers.resourcemanager.ResourceManager;
import net.itransformers.resourcemanager.config.ConnectionParamsType;
import net.itransformers.resourcemanager.config.ParamType;
import net.itransformers.resourcemanager.config.ResourceType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscoveryResourceManager {
  private ResourceManager resourceManager;

    public DiscoveryResourceManager(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    public List<ResourceType> returnResourcesByConnectionType(String connectionType){
        return resourceManager.findResourcesByConnectionType(connectionType);
    }
    public ResourceType returnResourceByParam(Map params){
        return resourceManager.findFirstResourceBy(params);
    }

    public Map<String, String> getParamMap(ResourceType resourceType,String connectionType) {
        Map<String, String> connParams = new HashMap<String, String>();
        List<ConnectionParamsType> connectionParams = resourceType.getConnectionParams();
        for (ConnectionParamsType connectionParam : connectionParams) {
            if (connectionParam.getConnectionType().equalsIgnoreCase(connectionType)) {
                for (ParamType param : connectionParam.getParam()) {
                    connParams.put(param.getName(), param.getValue());
                }
                return connParams;
            }
        }
        return null;
    }

}
