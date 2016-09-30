package net.itransformers.ws.resourceManager;

import net.itransformers.resourcemanager.ResourceManager;
import net.itransformers.resourcemanager.ResourceManagerFactory;
import net.itransformers.resourcemanager.config.ParamType;
import net.itransformers.resourcemanager.config.ResourceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.ServletContextAware;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value="/resource")
public class ResourceManagerController implements ServletContextAware {
    final Logger logger = LoggerFactory.getLogger(ResourceManagerController.class);

    @Resource(name="resourceManagerFactory")
    private ResourceManagerFactory resourceManagerFactory;


    @Resource(name="projectPath")
    private String projectPath;

    private ServletContext context;

    public void setServletContext(ServletContext servletContext) {
        this.context = servletContext;
    }
    private ResourceManager getResourceManager(){
        ResourceManager resourceManager =
                (ResourceManager) context.getAttribute("resourceManager");
        if (resourceManager == null) {
            Map<String, String> props = new HashMap<>();
            props.put("projectPath", projectPath);
            resourceManager = resourceManagerFactory.createResourceManager("xml", props);
            context.setAttribute("resourceManager", resourceManager);
        }
        return resourceManager;
    }
    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getResources() {
        return getResourceManager().getResources();
    }

    @RequestMapping(value = "/{resourceName}", method=RequestMethod.GET)
    @ResponseBody
    public ResourceType getResource(@PathVariable String resourceName) {
        return getResourceManager().getResource(resourceName);
    }

    @RequestMapping(value = "/", method=RequestMethod.POST)
    @ResponseBody
    public void createResource(@RequestBody String resourceName) {
        getResourceManager().createResource(resourceName);
    }

    @RequestMapping(value = "/", method=RequestMethod.PATCH)
    @ResponseBody
    public void createResourceAll(@RequestBody ResourceType resourceType) {
        getResourceManager().createResource(resourceType);
    }


    @RequestMapping(value = "/{resourceName}", method=RequestMethod.PUT)
    @ResponseBody
    public void updateResource(@PathVariable String resourceName, @RequestBody String newResourceName) {
        getResourceManager().updateResource(resourceName, newResourceName);
    }

    @RequestMapping(value = "/{resourceName}", method=RequestMethod.DELETE)
    @ResponseBody
    public void deleteResource(@PathVariable String resourceName) {
        getResourceManager().deleteResource(resourceName);
    }

    @RequestMapping(value = "/{resourceName}/connection", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getConnections(@PathVariable String resourceName) {
        return getResourceManager().getConnections(resourceName);
    }

    @RequestMapping(value = "/{resourceName}/connection", method = RequestMethod.POST)
    @ResponseBody
    public void createConnections(@PathVariable String resourceName, @RequestBody String connType) {
        getResourceManager().createConnection(resourceName, connType);
    }

    @RequestMapping(value = "/{resourceName}/connection/{connType}/param", method = RequestMethod.GET)
    @ResponseBody
    public List<ParamType> getConnectionParams(@PathVariable String resourceName, @PathVariable String connType) {
        return getResourceManager().getConnectionParams(resourceName, connType);
    }

    @RequestMapping(value="/{resourceName}/connection/{connType}/param/{paramName}", method=RequestMethod.POST)
    @ResponseBody
    public void createConnectionParam(@PathVariable String resourceName, @PathVariable String connType, @PathVariable String paramName, @RequestBody String paramValue) {
        getResourceManager().createConnectionParam(resourceName, connType, paramName, paramValue);
    }

    @RequestMapping(value="/{resourceName}/connection/{connType}/param/{paramName}", method=RequestMethod.PUT)
    @ResponseBody
    public void updateConnectionParam(@PathVariable String resourceName, @PathVariable String connType, @PathVariable String paramName, @RequestBody String paramValue) {
        getResourceManager().updateConnectionParams(resourceName, connType, paramName, paramValue);
    }

    @RequestMapping(value="/{resourceName}/connection/{connType}/param/{paramName}", method=RequestMethod.DELETE)
    @ResponseBody
    public void deleteConnectionParam(@PathVariable String resourceName, @PathVariable String connType, @PathVariable String paramName) {
        getResourceManager().deleteConnectionParams(resourceName, connType, paramName);
    }

    @RequestMapping(value = "/{resourceName}/selection/param", method = RequestMethod.GET)
    @ResponseBody
    public List<ParamType> getSelectionParams(@PathVariable String resourceName) {
        return getResourceManager().getSelectionParams(resourceName);
    }

    @RequestMapping(value="/{resourceName}/selection/param/{paramName}", method=RequestMethod.POST)
    @ResponseBody
    public void createSelectionParam(@PathVariable String resourceName, @PathVariable String paramName, @RequestBody String paramValue) {
        getResourceManager().createSelectionParam(resourceName, paramName, paramValue);
    }

    @RequestMapping(value="/{resourceName}/selection/param/{paramName}", method=RequestMethod.PUT)
    @ResponseBody
    public void updateSelectionParam(@PathVariable String resourceName, @PathVariable String paramName, @RequestBody String paramValue) {
        getResourceManager().updateSelectionParam(resourceName, paramName, paramValue);
    }

    @RequestMapping(value="/{resourceName}/selection/param/{paramName}", method=RequestMethod.DELETE)
    @ResponseBody
    public void deleteSelectionParam(@PathVariable String resourceName, @PathVariable String paramName) {
        getResourceManager().deleteSelectionParam(resourceName, paramName);
    }

}
