package net.itransformers.ws.iDiscover;

import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetails;
import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetailsManager;
import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetailsManagerFactory;
import net.itransformers.idiscover.api.NetworkDiscoverer;
import net.itransformers.idiscover.api.NetworkDiscovererFactory;
import net.itransformers.idiscover.api.VersionManager;
import net.itransformers.idiscover.api.VersionManagerFactory;
import net.itransformers.resourcemanager.ResourceManager;
import net.itransformers.resourcemanager.config.ParamType;
import net.itransformers.resourcemanager.config.ResourceType;
import org.apache.commons.lang.enums.Enum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.ServletContextAware;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value="/discovery")
public class NetDiscovererController implements ServletContextAware {

    final Logger logger = LoggerFactory.getLogger(NetDiscovererController.class);

    @Resource(name="networkDiscoveryFactory")
    private NetworkDiscovererFactory networkDiscovererFactory;

    @Resource(name="versionManagerFactory")
    private VersionManagerFactory versionManagerFactory;

    @Resource(name="connectionManagerFactory")
    private ConnectionDetailsManagerFactory connectionManagerFactory;

    @Resource(name="projectPath")
    private String projectPath;

    private ServletContext context;

    public void setServletContext(ServletContext servletContext) {
        this.context = servletContext;
    }


    @RequestMapping(value = "/", method=RequestMethod.POST)
    @ResponseBody
    public String createVersion() {
        Map<String, String> props = new HashMap<>();
        props.put("projectPath",projectPath);
        VersionManager versionManager = versionManagerFactory.createVersionManager("dir", props);
        return versionManager.createVersion();
    }

    @RequestMapping(value = "/{version}", method=RequestMethod.DELETE)
    @ResponseBody
    public void deleteVersion(@PathVariable String version) {
        Map<String, String> props = new HashMap<>();
        props.put("projectPath",projectPath);
        VersionManager versionManager = versionManagerFactory.createVersionManager("dir", props);
        versionManager.deleteVersion(version);
    }

    @RequestMapping(value = "/{version}/discoverer", method=RequestMethod.POST)
    @ResponseBody
    public void createDiscoverer(@PathVariable String version) {
        Map<String, String> props = new HashMap<>();
        props.put("projectPath",projectPath);
        props.put("version",version);
        NetworkDiscoverer networkDiscoverer = networkDiscovererFactory.createNetworkDiscoverer("async_parallel", props);
        ConnectionDetailsManager connectionManager = connectionManagerFactory.createConnectionDetailsManager("xml", props);
        Map<String, ConnectionDetails> connDetails = connectionManager.getConnectionDetails();
        networkDiscoverer.startDiscovery(new HashSet<>(connDetails.values()));
        context.setAttribute(version, networkDiscoverer);
    }

    @RequestMapping(value = "/{version}/discoverer", method=RequestMethod.PUT)
    @ResponseBody
    public void modifyDiscoverer(@PathVariable String version, @RequestParam Operation operation) {
        if (operation == Operation.PAUSE){
            NetworkDiscoverer networkDiscoverer = (NetworkDiscoverer) context.getAttribute(version);
            networkDiscoverer.pauseDiscovery();
        } else {
            NetworkDiscoverer networkDiscoverer = (NetworkDiscoverer) context.getAttribute(version);
            networkDiscoverer.resumeDiscovery();
        }
    }

    @RequestMapping(value = "/{version}/discoverer", method=RequestMethod.DELETE)
    @ResponseBody
    public void deleteDiscoverer(@PathVariable String version) {
        NetworkDiscoverer networkDiscoverer = (NetworkDiscoverer) context.getAttribute(version);
        networkDiscoverer.stopDiscovery();
        context.removeAttribute(version);
    }
    enum Operation{
        PAUSE,
        RESUME
    }
}
