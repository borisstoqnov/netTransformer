package net.itransformers.ws.iDiscover;

import net.itransformers.idiscover.api.models.graphml.GraphmlGraph;
import net.itransformers.idiscover.api.models.node_data.DiscoveredDeviceData;
import net.itransformers.xmlNodeDataProvider.XmlNodeDataProvider;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.List;
import java.util.Set;

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
    GraphmlGraph getNetwork(@PathVariable String version,
                            @RequestParam(value="nodeId", required=false) String nodeId,
                            @RequestParam(value="hops", required = false, defaultValue = "1") int hops){

        if (nodeId==null){
            return xmlNodeDataProvider.getNetwork(version);

        }else{
            return xmlNodeDataProvider.getNetwork(version, nodeId,hops);
        }
    }

//    @RequestMapping(value = "/{version}/network?nodeId={nodeId}", method=RequestMethod.GET)
//    @ResponseBody
//    GraphmlNode getNode(@PathVariable String version,  String nodeId){
//        return );
//
//    }

    @RequestMapping(value = "/{version}/nodes", method=RequestMethod.GET)
    @ResponseBody
    Set<String> getDiscoveredNodes(@PathVariable String version){
        return xmlNodeDataProvider.getDiscoveredNodes(version);
    }

    @RequestMapping(value = "/{version}/nodes/{nodeId}/data", method=RequestMethod.GET)
    @ResponseBody

    DiscoveredDeviceData getDeviceHierarchicalModel(@PathVariable String version, @PathVariable String nodeId){
         return xmlNodeDataProvider.getDiscoverdNodeData(version, nodeId);
    }


    @RequestMapping(value = "/{version}/nodes/{nodeId}/raw", method=RequestMethod.GET, produces={"application/xml","application/json"})
    @ResponseBody
    String getRawData(@PathVariable String version, @PathVariable String nodeId){
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        byte [] rawData = xmlNodeDataProvider.getRawData(version, nodeId).getData();
        return new String(rawData);

    }



}
