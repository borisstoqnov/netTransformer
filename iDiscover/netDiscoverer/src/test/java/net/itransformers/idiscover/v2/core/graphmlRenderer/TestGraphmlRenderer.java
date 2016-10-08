package net.itransformers.idiscover.v2.core.graphmlRenderer;

import net.itransformers.idiscover.v2.core.listeners.graphmlRenderer.GraphmlRenderer;
import net.itransformers.idiscover.api.models.graphml.GraphmlNode;
import net.itransformers.idiscover.api.models.graphml.GraphmlNodeData;
import org.junit.Assert;
import org.junit.Test;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class TestGraphmlRenderer
{
    private final String baseDir = (String) System.getProperties().get("user.dir");
 @Test
    public void testRender( )
        throws Exception
    {

        GraphmlRenderer graphmlRenderer = new GraphmlRenderer();
        HashMap<String,Object> params = new HashMap<>();

        ArrayList<GraphmlNode> graphmlNodes = new ArrayList<>();
        params.put("nodes",graphmlNodes);
        params.put("graphDirection","undirected");
        params.put("project","test");
        params.put("version","version1");
        {
            GraphmlNode graphmlNode = new GraphmlNode("R1", "router1");

            List<GraphmlNodeData> graphmlNodeDataList = graphmlNode.getGraphmlNodeDataList();

            graphmlNodeDataList.add(new GraphmlNodeData("deviceName", "R1"));
            graphmlNodeDataList.add(new GraphmlNodeData("deviceModel", "cisco2911"));
            graphmlNodeDataList.add(new GraphmlNodeData("deviceType", "CISCO"));
            graphmlNodeDataList.add(new GraphmlNodeData("discoveredIPv4Address", "1.1.1.1"));
            graphmlNodeDataList.add(new GraphmlNodeData("ipv4Forwarding", "YES"));
            graphmlNodeDataList.add(new GraphmlNodeData("totalInterfaceCount", "27"));

            graphmlNodes.add(graphmlNode);
        }
        {
            GraphmlNode graphmlNode = new GraphmlNode("R2", "router2");

            List<GraphmlNodeData> graphmlNodeDataList = graphmlNode.getGraphmlNodeDataList();

            graphmlNodeDataList.add(new GraphmlNodeData("deviceName", "R2"));
            graphmlNodeDataList.add(new GraphmlNodeData("deviceModel", "cisco2911"));
            graphmlNodeDataList.add(new GraphmlNodeData("deviceType", "CISCO"));
            graphmlNodeDataList.add(new GraphmlNodeData("discoveredIPv4Address", "2.2.2.2"));
            graphmlNodeDataList.add(new GraphmlNodeData("ipv4Forwarding", "YES"));
            graphmlNodeDataList.add(new GraphmlNodeData("totalInterfaceCount", "12"));

            graphmlNodes.add(graphmlNode);
        }
        String graphml = graphmlRenderer.render("netDiscoverer/velocity/snmpGraphmlTemplate.vm",params);

       // FileUtils.writeStringToFile(new File(baseDir + "/" + "iDiscover/src/test/resources/expectedGraphml.graphml"), graphml);
        Assert.assertEquals(FileUtils.readFileToString(new File(baseDir + "/" + "iDiscover/netDiscoverer/src/test/resources/graphmlRenderer/expectedGraphml.graphml")), graphml);
    }
}