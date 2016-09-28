package net.itransformers.ws.iDiscover;

import net.itransformers.idiscover.api.models.graphml.GraphmlGraph;
import net.itransformers.idiscover.api.models.graphml.GraphmlNode;
import net.itransformers.idiscover.api.models.node_data.DiscoveredDeviceData;
import net.itransformers.xmlNodeDataProvider.XmlNodeDataProvider;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by niau on 9/27/16.
 */

@Controller
@RequestMapping(value="/discovery")
public class DiscoveryDataProvider {

    @Resource(name="xmlNodeDataProvider")
    private XmlNodeDataProvider xmlNodeDataProvider;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    @ResponseBody
    List<String> getDiscoveredVersions(){

       return xmlNodeDataProvider.getDiscoveredVersions();

    }
    @RequestMapping(value = "/{version}", method=RequestMethod.GET)
    @ResponseBody
    String getCreationDate(@PathVariable String version){
        return xmlNodeDataProvider.getCreationDate(version);
    }

    @RequestMapping(value = "/{version}/network", method=RequestMethod.GET)
    @ResponseBody
    GraphmlGraph getNetwork(@PathVariable String version){
        return xmlNodeDataProvider.getNetwork(version);
    }

    @RequestMapping(value = "/{version}/network?nodeId={nodeId}", method=RequestMethod.GET)
    @ResponseBody
    GraphmlNode getNode(@PathVariable String version, @RequestParam(value="nodeId", required=false) String nodeId){
        return xmlNodeDataProvider.getNode(version, nodeId);

    }

    @RequestMapping(value = "/{version}/nodes", method=RequestMethod.GET)
    @ResponseBody

    List<String> getDiscoveredNodes(@PathVariable String version){
        return xmlNodeDataProvider.getDiscoveredNodes(version);
    }

    @RequestMapping(value = "/{version}/nodes/{nodeId}/data", method=RequestMethod.GET)
    @ResponseBody

    DiscoveredDeviceData getDeviceHierarchicalModel(@PathVariable String version, @PathVariable String nodeId){
         return xmlNodeDataProvider.getDeviceHierarchicalModel(version, nodeId);
    }


    @RequestMapping(value = "/{version}/nodes/{nodeId}/raw", method=RequestMethod.GET)
    @ResponseBody
    String getRawData(@PathVariable String version, @PathVariable String nodeId){
        return new String(xmlNodeDataProvider.getRawData(version, nodeId).getData());
    }


}
