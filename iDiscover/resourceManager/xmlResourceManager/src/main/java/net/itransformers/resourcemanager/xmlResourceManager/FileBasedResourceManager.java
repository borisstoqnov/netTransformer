package net.itransformers.resourcemanager.xmlResourceManager;

import net.itransformers.resourcemanager.ResourceManager;
import net.itransformers.resourcemanager.ResourceManagerException;
import net.itransformers.resourcemanager.config.ConnectionParamsType;
import net.itransformers.resourcemanager.config.ParamType;
import net.itransformers.resourcemanager.config.ResourceType;
import net.itransformers.resourcemanager.config.ResourcesType;
import net.itransformers.resourcemanager.xmlResourceManager.util.JaxbMarshalar;
import net.itransformers.utils.CIDRUtils;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by vasko on 14.09.16.
 */
public class FileBasedResourceManager implements ResourceManager {
    static Logger logger = Logger.getLogger(ResourceManager.class);
    private ResourcesType resource;
    private String file;

    public FileBasedResourceManager(String file) {
        this.file = file;
    }

    public void load() throws IOException, JAXBException {
        FileInputStream is = null;
        try {
            logger.info("Reading resource config from: "+new File(file).getAbsoluteFile());
            is = new FileInputStream(file);
            ResourcesType resourcesType;
            resourcesType = JaxbMarshalar.unmarshal(ResourcesType.class, is);
            this.resource = resourcesType;
        } finally {
            if (is != null) is.close();
        }
    }

    public void save() {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            JaxbMarshalar.marshal(this.resource, os, "resources");
        } catch (FileNotFoundException e) {
            throw new ResourceManagerException(e.getMessage(), e);
        } catch (JAXBException e) {
            e.printStackTrace();
        } finally {
            if (os != null) try {
                os.close();
            } catch (IOException e) {}
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
                        if (!deviceParams.get(key).equalsIgnoreCase(map.get(key))) {
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


    public ResourceType findFirstResourceBy(Map<String, String> deviceParams) {
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
                        if (!deviceParams.get(key).equalsIgnoreCase(map.get(key))) {
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

    @Override
    public List<ResourceType> findResourceBy(Map<String, String> deviceParams) {
        throw new UnsupportedOperationException();
    }


    public ArrayList<ResourceType> findResourcesByConnectionType(String connectionParametersType) {
        logger.debug("finding resource with connection parameters type: "+connectionParametersType);
        List<ResourceType> list = resource.getResource();
        ArrayList<ResourceType> resourceTypes = new ArrayList<ResourceType>();


        for (ResourceType currResourceType : list) {

            List <ConnectionParamsType> connectionParamsTypes = currResourceType.getConnectionParams();

            for (ConnectionParamsType connectionParamsType : connectionParamsTypes) {
                if(connectionParamsType.getConnectionType().equalsIgnoreCase(connectionParametersType)) {
                    resourceTypes.add(currResourceType);
                    break;
                }
            }


        }

        return resourceTypes;
    }

    @Override
    public void createResource(ResourceType resourceType) {
        String resourceName = resourceType.getName();
        if (resourceName == null) {
            throw new ResourceManagerException("Invalid resource name");
        }
        ResourceType existingResourceType = getResource(resourceName);
        if (existingResourceType != null) {
            throw new ResourceManagerException("Resource Type with name " + resourceName + " already exists");
        }
        resource.getResource().add(resourceType);
        save();
    }

    @Override
    public void createResource(String resourceName) {
        ResourceType resourceType = getResource(resourceName);
        if (resourceType != null) {
            throw new ResourceManagerException("Resource with name already exists");
        }
        resourceType = new ResourceType();
        resourceType.setName(resourceName);
        resource.getResource().add(resourceType);
        save();
    }

    @Override
    public void updateResource(String resourceName, String newResourceName) {
        ResourceType resourceType = getResource(resourceName);
        if (resourceType == null) {
            throw new ResourceManagerException("Resource with name does not exist");
        }
        resourceType.setName(newResourceName);
        save();
    }

    @Override
    public void deleteResource(String resourceName) {
        List<ResourceType> list = resource.getResource();
        for (ResourceType currResourceType : list) {
            if (currResourceType.getName().equalsIgnoreCase(resourceName)){
                list.remove(currResourceType);
                break;
            }
        }
        save();
    }

    @Override
    public List<String> getResources() {
        List<String> list = new ArrayList<>();
        for (ResourceType resourceType : this.resource.getResource()) {
            list.add(resourceType.getName());
        }
        return list;
    }

    @Override
    public void createSelectionParam(String resourceName, String paramName, String paramValue) {
        ParamType resourceParamType = new ParamType();
        resourceParamType.setName(paramName);
        resourceParamType.setValue(paramValue);
        ResourceType resourceType = getResource(resourceName);
        if (resourceType == null) {
            throw new ResourceManagerException("Resource with name does not exist");
        }
        List<ParamType> params = resourceType.getParam();
        createParam(resourceParamType, params);
        save();
    }

    private void createParam(ParamType resourceParamType, List<ParamType> params) {
        if (getParam(resourceParamType.getName(), params) != null) {
            throw new ResourceManagerException(String.format("Selection Parameter with name %s already exists", resourceParamType.getName()));
        }
        params.add(resourceParamType);
    }

    private ParamType getParam(String paramName, List<ParamType> params) {
        for (ParamType param : params) {
            if (param.getName().equals(paramName)) {
                return param;
            }
        }
        return null;
    }


    @Override
    public void updateSelectionParam(String resourceName, String paramName, String paramValue) {
        ParamType resourceParamType = new ParamType();
        resourceParamType.setName(paramName);
        resourceParamType.setValue(paramValue);
        ResourceType resourceType = getResource(resourceName);
        if (resourceType == null) {
            throw new ResourceManagerException("Resource with name does not exist");
        }
        List<ParamType> params = resourceType.getParam();
        updateParam(resourceParamType, params);
        save();
    }

    private void updateParam(ParamType resourceParamType, List<ParamType> params) {
        boolean isFound = false;
        for (ParamType param : params) {
            if (param.getName().equals(resourceParamType.getName())) {
                isFound = true;
                param.setValue(resourceParamType.getValue());
                break;
            }
        }
        if (!isFound) {
            throw new ResourceManagerException(String.format("Parameter with name %s can not be found",resourceParamType.getName()));
        }
        save();
    }

    @Override
    public void deleteSelectionParam(String resourceName, String paramName) {
        ResourceType resourceType = getResource(resourceName);
        if (resourceType == null) {
            throw new ResourceManagerException("Resource with name does not exist");
        }
        List<ParamType> params = resourceType.getParam();
        deleteParam(paramName, params);
        save();
    }

    private void deleteParam(String paramName, List<ParamType> params) {
        boolean isFound = false;
        for (ParamType param : params) {
            if (param.getName().equals(paramName)) {
                isFound = true;
                params.remove(param);
                break;
            }
        }
        if (!isFound) {
            throw new ResourceManagerException(String.format("Parameter with name %s can not be found",paramName));
        }
    }

    @Override
    public List<ParamType> getSelectionParams(String resourceName) {
        ResourceType resourceType = getResource(resourceName);
        if (resourceType == null) {
            throw new ResourceManagerException("Resource with name does not exist");
        }
        List<ParamType> params = resourceType.getParam();
        return params;
    }

    @Override
    public void createConnectionParam(String resourceName, String connectionType, String paramName, String paramValue) {
        ParamType connectionParamsType = new ParamType();
        connectionParamsType.setName(paramName);
        connectionParamsType.setValue(paramValue);
        ResourceType resourceType = getResource(resourceName);
        if (resourceType == null) {
            throw new ResourceManagerException("Resource with name does not exist");
        }
        List<ParamType> params = getConnetionParam(connectionType, resourceType);
        createParam(connectionParamsType, params);
        save();
    }

    private List<ParamType> getConnetionParam(String connParamType, ResourceType resourceType) {
        List<ConnectionParamsType> params = resourceType.getConnectionParams();
        for (ConnectionParamsType param : params) {
            if (param.getConnectionType().equals(connParamType)) {
                return param.getParam();
            }
        }
        return null;
    }

    @Override
    public void updateConnectionParams(String resourceName, String connectionType, String paramName, String paramValue) {
        ParamType resourceParamType = new ParamType();
        resourceParamType.setName(paramName);
        resourceParamType.setValue(paramValue);
        ResourceType resourceType = getResource(resourceName);
        List<ParamType> params = getConnetionParam(connectionType, resourceType);
        updateParam(resourceParamType, params);
        save();
    }

    @Override
    public void deleteConnectionParams(String resourceName, String connectionType, String paramName) {
        ResourceType resourceType = getResource(resourceName);
        List<ParamType> params = getConnetionParam(connectionType, resourceType);
        deleteParam(paramName, params);
        save();
    }

    @Override
    public List<String> getConnections(String resourceName) {
        ResourceType resourceType = getResource(resourceName);
        if (resourceType == null) {
            throw new ResourceManagerException("Resource with name does not exist");
        }
        List<String> connectionTypes = new ArrayList<>();
        List<ConnectionParamsType> params = resourceType.getConnectionParams();
        for (ConnectionParamsType param : params) {
            connectionTypes.add(param.getConnectionType());
        }
        return connectionTypes;
    }

    @Override
    public void createConnection(String resourceName, String connType) {
        ResourceType resourceType = getResource(resourceName);
        if (resourceType == null) {
            throw new ResourceManagerException("Resource with name does not exist");
        }
        List<ConnectionParamsType> params = resourceType.getConnectionParams();
        for (ConnectionParamsType param : params) {
            if (param.getConnectionType() != null && param.getConnectionType().equals(connType)){
                throw new ResourceManagerException("Connection type already exists");
            }
        }
        ConnectionParamsType connParams = new ConnectionParamsType();
        connParams.setConnectionType(connType);
        resourceType.getConnectionParams().add(connParams);
        save();
    }

    @Override
    public List<ParamType> getConnectionParams(String resourceName, String connectionType) {
        ResourceType resourceType = getResource(resourceName);
        if (resourceType == null) {
            throw new ResourceManagerException("Resource with name does not exist");
        }
        List<ParamType> params = getConnetionParam(connectionType, resourceType);
        return params;
    }

    @Override
    public ResourceType getResource(String resourceName) {
        List<ResourceType> resourceTypeList = resource.getResource();
        for (ResourceType resourceType : resourceTypeList) {
            if (resourceType.getName().equals(resourceName)) {
                return resourceType;
            }
        }
        return null;
    }

    public ResourceType findResourceByName(String resourceName){
        List<ResourceType> list = resource.getResource();
        for (ResourceType currResourceType : list) {
            if (currResourceType.getName().equalsIgnoreCase(resourceName)){
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
