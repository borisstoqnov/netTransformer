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
import java.util.Map;

public class DiscoveryResourceManager {
  private ResourceManager resourceManager;

    public DiscoveryResourceManager(String PathToXML) {
        String xml = null;
        try {
            xml = FileUtils.readFileToString(new File(System.getProperty("base.dir"),PathToXML));
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

    public ResourceType ReturnResourceByParam(Map params){
        ResourceType result = resourceManager.findResource(params);
        return result;
    }
    public ResourceType ReturnResourceByName(String name){
        ResourceType result = resourceManager.getResource(name);
        return result;
    }

    public Map<String, String> getParamMap(ResourceType resourceType) {
        Map<String, String> connParams = new HashMap<String, String>();
        ConnectionParamsType connParamsType = resourceType.getConnectionParams().get(0);
        for (ParamType param : connParamsType.getParam()) {
            connParams.put(param.getName(), param.getValue());
        }
        return connParams;
    }

    public static void main(String[] args){
        System.setProperty("base.dir", System.getProperty("user.dir"));
        DiscoveryResourceManager Discover = new DiscoveryResourceManager("iDiscover/conf/xml/discoveryResource.xml");
        ResourceManager Test = Discover.getResourceManager();
//        Get CLI parameters
        ResourceType CLI = Discover.ReturnResourceByName("CLI");

         Map<String, String> CLIconnParams = new HashMap<String, String>();
         CLIconnParams = Discover.getParamMap(CLI);

        Map<String,String> params = new HashMap<String, String>();
        params.put("protocol","SNMP");
        ResourceType SNMP = Discover.ReturnResourceByParam(params);
        Map<String, String> SNMPconnParams = new HashMap<String, String>();
        SNMPconnParams = Discover.getParamMap(SNMP);

    }
}
