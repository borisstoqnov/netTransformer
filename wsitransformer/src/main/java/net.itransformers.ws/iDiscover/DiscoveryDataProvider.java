package net.itransformers.ws.iDiscover;

import net.itransformers.idiscover.api.DiscoveryResult;
import net.itransformers.idiscover.api.models.graphml.GraphmlGraph;
import net.itransformers.idiscover.api.models.node_data.DiscoveredDeviceData;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by niau on 9/27/16.
 */

@Controller
@RequestMapping(value="/discovery")
public class DiscoveryDataProvider {

    @Resource(name="discoveryDataProvider")
    private DiscoveryResult discoveryResult;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    List<String> getDiscoveredVersions(){

       return discoveryResult.getDiscoveredVersions();

    }
    @RequestMapping(value = "/{version}", method=RequestMethod.GET)
    @ResponseBody
    String getCreationDate(@PathVariable String version){
        return discoveryResult.getCreationDate(version);
    }

    @RequestMapping(value = "/{version}/{nodeId}/networkcentric", method=RequestMethod.GET)
    @ResponseBody
    GraphmlGraph getVersionNetworkCentricModel(@PathVariable String version, @PathVariable String nodeId){
        return discoveryResult.getVersionNetworkCentricModel(version,nodeId);
    }
    @RequestMapping(value = "/{version}/{nodeId}/nodecentric", method=RequestMethod.GET)
    @ResponseBody
    GraphmlGraph getNodeCentricNetworkModel(@PathVariable String version, @PathVariable String nodeId){
        return discoveryResult.getNodeCentricNetworkModel(version, nodeId);

    }
    @RequestMapping(value = "/{version}/{nodeId}/devicehierarchical", method=RequestMethod.GET)
    @ResponseBody

    DiscoveredDeviceData getDeviceHierarchicalModel(@PathVariable String version, @PathVariable String nodeId){
         return discoveryResult.getDeviceHierarchicalModel(version,nodeId);
    }


}
