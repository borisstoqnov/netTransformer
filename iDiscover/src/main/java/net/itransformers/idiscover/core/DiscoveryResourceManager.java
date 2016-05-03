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
import net.itransformers.resourcemanager.config.ResourcesType;
import net.itransformers.resourcemanager.util.JaxbMarshalar;
import org.apache.commons.io.FileUtils;

import javax.xml.bind.JAXBException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DiscoveryResourceManager {
  private ResourceManager resourceManager;

    public DiscoveryResourceManager(File projectDir, String label, String PathToXML) {
        String xml = null;
        try {
            xml = FileUtils.readFileToString(new File(projectDir,PathToXML));
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        InputStream is = new ByteArrayInputStream(xml.getBytes());
        ResourcesType deviceGroupsType = null;
        try {
            deviceGroupsType = JaxbMarshalar.unmarshal(ResourcesType.class, is);
        } catch (JAXBException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        this.resourceManager = new ResourceManager(deviceGroupsType);
    }

    public ResourceManager getResourceManager() {
        return resourceManager;
    }

    public ResourceType returnResourceByParam(Map params){
        return resourceManager.findResource(params);
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
