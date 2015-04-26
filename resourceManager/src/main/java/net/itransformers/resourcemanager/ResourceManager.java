/*
 * iTransformer is an open source tool able to discover and transform
 *  IP network infrastructures.
 *  Copyright (C) 2012  http://itransformers.net
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.itransformers.resourcemanager;


import net.itransformers.resourcemanager.config.ParamType;
import net.itransformers.resourcemanager.config.ResourceType;
import net.itransformers.resourcemanager.config.ResourcesType;
import net.itransformers.resourcemanager.util.JaxbMarshalar;
import net.itransformers.utils.CIDRUtils;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResourceManager {
    static Logger logger = Logger.getLogger(ResourceManager.class);
    private ResourcesType resource;

    public ResourceManager(ResourcesType resource) {
        this.resource = resource;
    }
    public ResourceManager(File file) throws IOException, JAXBException {
        FileInputStream is = null;
        try {
            if (file == null) {
                throw new RuntimeException("No File Name is specified for loading resources");
            }
            is = new FileInputStream(file);
            ResourcesType resourcesType;
            resourcesType = JaxbMarshalar.unmarshal(ResourcesType.class, is);
            this.resource = resourcesType;
        } finally {
            if (is != null) is.close();
        }
    }



    public ResourceType findResourceByIPrange(Map<String, String> deviceParams) {
        logger.debug("finding resource for device params: "+deviceParams);
        List<ResourceType> list = resource.getResource();
        int matchCount = -1;
        ResourceType resourceType = null;
        for (ResourceType currResourceType : list) {
            HashMap<String, String> map = getParamMap(currResourceType);
            Set<String> paramSet = map.keySet();
            logger.debug("Trying resource: "+currResourceType.getName() +", param keySet: "+paramSet);

            if (map.get("ipv4Range")!=null){

                String ipv4Address = deviceParams.get("ipv4Address");

                try {
                    CIDRUtils cidrUtils = new CIDRUtils(map.get("ipv4Range"));

                    if (cidrUtils.isInRange(ipv4Address)){
                        matchCount++;
                    } else {

                    }

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }


            }

            if (map.get("ipv6Range")!=null){

                String ipv4Address = deviceParams.get("ipv4Address");

                try {
                    CIDRUtils cidrUtils = new CIDRUtils(map.get("ipv4Range"));

                    if (cidrUtils.isInRange(ipv4Address)){
                        matchCount++;
                    } else {

                    }

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }


            }

            if (map.get("ipv6Range")!=null){

                String ipv6Address = deviceParams.get("ipv46ddress");

            }

            if (deviceParams.keySet().containsAll(paramSet)) {
                boolean found = true;
                for (String key : paramSet) {
                    if(deviceParams.get(key)!=null){
                        if (!deviceParams.get(key).equals(map.get(key))) {
                            found = false;
                            break;
                        }

                    }   else{
                        found = false;
                        break;
                    }
                }
                if (found) {
                    logger.debug("device params contains all resource param key and match, match count="+matchCount+", param set size="+paramSet.size());
                    if (matchCount < paramSet.size()) {
                        matchCount = paramSet.size();
                        resourceType = currResourceType;
                    }
                } else {
                    logger.debug("device params contains all resource param key but does not match by values");
                }
            } else {
                logger.debug("device params does not contain all resource param key");
            }
        }
        if (resourceType != null) {
            logger.debug("match Resource=" + resourceType.getName() + ", device params: " + deviceParams);
        }
        return resourceType;
    }


    public ResourceType findResource(Map<String, String> deviceParams) {
        logger.debug("finding resource for device params: "+deviceParams);
        List<ResourceType> list = resource.getResource();
        int matchCount = -1;
        ResourceType resourceType = null;
        for (ResourceType currResourceType : list) {
            HashMap<String, String> map = getParamMap(currResourceType);
            Set<String> paramSet = map.keySet();
            logger.debug("Trying resource: "+currResourceType.getName() +", param keySet: "+paramSet);

            if (deviceParams.keySet().containsAll(paramSet)) {
                boolean found = true;
                for (String key : paramSet) {
                    if(deviceParams.get(key)!=null){
                        if (!deviceParams.get(key).equals(map.get(key))) {
                            found = false;
                            break;
                        }

                    }   else{
                        found = false;
                        break;
                    }
                }
                if (found) {
                    logger.debug("device params contains all resource param key and match, match count="+matchCount+", param set size="+paramSet.size());
                    if (matchCount < paramSet.size()) {
                        matchCount = paramSet.size();
                        resourceType = currResourceType;
                    }
                } else {
                    logger.debug("device params contains all resource param key but does not match by values");
                }
            } else {
                logger.debug("device params does not contain all resource param key");
            }
        }
        if (resourceType != null) {
            logger.debug("match Resource=" + resourceType.getName() + ", device params: " + deviceParams);
        }
        return resourceType;
    }

    public ResourceType getResource(String resourceName){
        List<ResourceType> list = resource.getResource();
        for (ResourceType currResourceType : list) {
            if (currResourceType.getName().equals(resourceName)){
                return currResourceType;
            }
        }
        return null;
    }
    private HashMap<String, String> getParamMap(ResourceType resourceType) {
        List<ParamType> params = resourceType.getParam();
        HashMap<String, String> map = new HashMap<String, String>();
        for (ParamType param : params) {
            map.put(param.getName(), param.getValue());
        }
        return map;
    }
}
