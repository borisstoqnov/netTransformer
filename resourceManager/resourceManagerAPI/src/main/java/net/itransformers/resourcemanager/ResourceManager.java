/*
 * ResourceManager.java
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

package net.itransformers.resourcemanager;


import net.itransformers.resourcemanager.config.ConnectionParamsType;
import net.itransformers.resourcemanager.config.ParamType;
import net.itransformers.resourcemanager.config.ResourceType;
import java.util.*;

public interface ResourceManager {

    ResourceType findFirstResourceBy(Map<String, String> deviceParams);
    List<ResourceType> findResourceBy(Map<String, String> deviceParams);
    List<ResourceType> findResourcesByConnectionType(String connectionParametersType);


    void createResource(String resourceName);
    void updateResource(String resourceName, String newResourceName);
    void deleteResource(String resourceName);
    List<String> getResources();

    void createSelectionParam(String resourceName, ParamType resourceParamType);
    void updateSelectionParam(String resourceName, ParamType resourceParamType);
    void deleteSelectionParam(String resourceName, String paramName);
    List<ParamType> getSelectionParams(String resourceName);

    void createConnectionParam(String resourceName, String connParamType, ParamType connectionParamsType);
    void updateConnectionParams(String resourceName, String connectionType, ParamType resourceParamType);
    void deleteConnectionParams(String resourceName, String connectionType, String paramName);
    List<ParamType> getConnectionParams(String resourceName , String connectionType);


}
