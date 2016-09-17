package net.itransformers.ws.resourceManager;

import net.itransformers.resourcemanager.ResourceManager;
import net.itransformers.resourcemanager.config.ParamType;
import net.itransformers.resourcemanager.config.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@Controller
@RequestMapping(value="/resource")
public class ResourceManagerController {
    final Logger logger = LoggerFactory.getLogger(ResourceManagerController.class);

    @Resource(name="resourceManager")
    private ResourceManager resourceManager;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getResources() {
        return resourceManager.getResources();
    }

    @RequestMapping(value = "/{resourceName}", method=RequestMethod.GET)
    @ResponseBody
    public ResourceType getResource(@PathVariable String resourceName) {
        return resourceManager.getResource(resourceName);
    }

    @RequestMapping(value = "/", method=RequestMethod.POST)
    @ResponseBody
    public void createResource(@RequestBody String resourceName) {
        resourceManager.createResource(resourceName);
    }

    @RequestMapping(value = "/", method=RequestMethod.PATCH)
    @ResponseBody
    public void createResourceAll(@RequestBody ResourceType resourceType) {
        resourceManager.createResource(resourceType);
    }


    @RequestMapping(value = "/{resourceName}", method=RequestMethod.PUT)
    @ResponseBody
    public void updateResource(@PathVariable String resourceName, @RequestBody String newResourceName) {
        resourceManager.updateResource(resourceName, newResourceName);
    }

    @RequestMapping(value = "/{resourceName}", method=RequestMethod.DELETE)
    @ResponseBody
    public void deleteResource(@PathVariable String resourceName) {
        resourceManager.deleteResource(resourceName);
    }

    @RequestMapping(value = "/{resourceName}/connection", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getConnections(@PathVariable String resourceName) {
        return resourceManager.getConnections(resourceName);
    }

    @RequestMapping(value = "/{resourceName}/connection", method = RequestMethod.POST)
    @ResponseBody
    public void createConnections(@PathVariable String resourceName, @RequestBody String connType) {
        resourceManager.createConnection(resourceName, connType);
    }

    @RequestMapping(value = "/{resourceName}/connection/{connType}/param", method = RequestMethod.GET)
    @ResponseBody
    public List<ParamType> getConnectionParams(@PathVariable String resourceName, @PathVariable String connType) {
        return resourceManager.getConnectionParams(resourceName, connType);
    }

    @RequestMapping(value="/{resourceName}/connection/{connType}/param/{paramName}", method=RequestMethod.POST)
    @ResponseBody
    public void createConnectionParam(@PathVariable String resourceName, @PathVariable String connType, @PathVariable String paramName, @RequestBody String paramValue) {
        resourceManager.createConnectionParam(resourceName, connType, paramName, paramValue);
    }

    @RequestMapping(value="/{resourceName}/connection/{connType}/param/{paramName}", method=RequestMethod.PUT)
    @ResponseBody
    public void updateConnectionParam(@PathVariable String resourceName, @PathVariable String connType, @PathVariable String paramName, @RequestBody String paramValue) {
        resourceManager.updateConnectionParams(resourceName, connType, paramName, paramValue);
    }

    @RequestMapping(value="/{resourceName}/connection/{connType}/param/{paramName}", method=RequestMethod.DELETE)
    @ResponseBody
    public void deleteConnectionParam(@PathVariable String resourceName, @PathVariable String connType, @PathVariable String paramName) {
        resourceManager.deleteConnectionParams(resourceName, connType, paramName);
    }

    @RequestMapping(value = "/{resourceName}/selection/param", method = RequestMethod.GET)
    @ResponseBody
    public List<ParamType> getSelectionParams(@PathVariable String resourceName) {
        return resourceManager.getSelectionParams(resourceName);
    }

    @RequestMapping(value="/{resourceName}/selection/param/{paramName}", method=RequestMethod.POST)
    @ResponseBody
    public void createSelectionParam(@PathVariable String resourceName, @PathVariable String paramName, @RequestBody String paramValue) {
        resourceManager.createSelectionParam(resourceName, paramName, paramValue);
    }

    @RequestMapping(value="/{resourceName}/selection/param/{paramName}", method=RequestMethod.PUT)
    @ResponseBody
    public void updateSelectionParam(@PathVariable String resourceName, @PathVariable String paramName, @RequestBody String paramValue) {
        resourceManager.updateSelectionParam(resourceName, paramName, paramValue);
    }

    @RequestMapping(value="/{resourceName}/selection/param/{paramName}", method=RequestMethod.DELETE)
    @ResponseBody
    public void deleteSelectionParam(@PathVariable String resourceName, @PathVariable String paramName) {
        resourceManager.deleteSelectionParam(resourceName, paramName);
    }

}
