import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.impls.tg.TinkerGraph;
import net.itransformers.idiscover.api.DiscoveryResult;
import net.itransformers.idiscover.api.models.graphml.*;
import net.itransformers.idiscover.api.models.node_data.DiscoveredDeviceData;
import net.itransformers.utils.ProjectConstants;
import net.itransformers.utils.blueprints_patch.MyGraphMLReader;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by niau on 9/26/16.
 */
public class DeviceDataXmlProvider implements DiscoveryResult {

    String projectPath;
    File networkPath;



    public DeviceDataXmlProvider(String projectPath, String graphType) {
        this.projectPath = projectPath;
        networkPath = new File(projectPath+File.separator+ ProjectConstants.networkDirName);
    }



    @Override
    public String[] getDiscoveredVersions() {
        return networkPath.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return new File(dir,name).isDirectory();
            }
        });
    }

    @Override
    public String getCreationDate(String version) {
        return null;
    }

    @Override
    public GraphmlGraph getVersionNetworkCentricModel(String version, String nodeId) {
        File versionDir = new File(networkPath+File.separator+version);
        if (versionDir.exists()){
            File graphmlFile = new File(versionDir+File.separator+ProjectConstants.undirectedGraphmlDirName+File.separator+ProjectConstants.graphmlDataPrefix+ProjectConstants.networkGraphmlFileName);

            if (graphmlFile.exists()){

                return loadGraphml(graphmlFile.getAbsolutePath());

            }   else {
                return null;
            }

        }   else {
            return null;
        }
    }

    @Override
    public GraphmlGraph getNodeCentricNetworkModel(String version, String nodeId) {
        File versionDir = new File(networkPath+File.separator+version);
        if (versionDir.exists()){
            File graphmlFile = new File(versionDir+File.separator+ProjectConstants.undirectedGraphmlDirName+File.separator+ProjectConstants.graphmlDataPrefix+ProjectConstants.graphmlDataPrefix+nodeId+".graphml");

            if (graphmlFile.exists()){

                return loadGraphml(graphmlFile.getAbsolutePath());

            }   else {
                return null;
            }

        }   else {
            return null;
        }
        }

    @Override
    public DiscoveredDeviceData getDeviceHierarchicalModel(String version, String nodeId) {
        return null;
    }


    private GraphmlGraph loadGraphml(String filePath){


        GraphmlGraph graphmlGraph = new GraphmlGraph();

        List<GraphmlEdge> graphmlEdges = new ArrayList<>();
        List<GraphmlNode> graphmlNodes = new ArrayList<>();

        Graph graph = new TinkerGraph();
        MyGraphMLReader reader2 = new MyGraphMLReader(graph);
        try {
            reader2.inputGraph(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Vertex vertex: graph.getVertices()){
            GraphmlNode node = new GraphmlNode();
            List<GraphmlNodeData> graphmlNodeDataList = new ArrayList<>();
            node.setId(vertex.getId().toString());
            Set<String> keys = vertex.getPropertyKeys();
            for (String key : keys) {
                graphmlNodeDataList.add(new GraphmlNodeData(key,vertex.getProperty(key)));
            }
            node.setGraphmlNodeDataList(graphmlNodeDataList);
            graphmlNodes.add(node);

        }
        for (Edge edge: graph.getEdges()){

            GraphmlEdge graphmlEdge = new GraphmlEdge();
            List<GraphmlEdgeData> graphmlEdgeDataList = new ArrayList<>();
            graphmlEdge.setId(edge.getId().toString());

            Vertex vertexIn = edge.getVertex(Direction.IN);

            graphmlEdge.setFromNode(vertexIn.getId().toString());
            Vertex vertexOut = edge.getVertex(Direction.OUT);

            graphmlEdge.setToNode(vertexOut.getId().toString());


            Set<String> keys = edge.getPropertyKeys();
            for (String key : keys) {
                graphmlEdgeDataList.add(new GraphmlEdgeData(key,edge.getProperty(key)));
            }
            graphmlEdge.setGraphmlEdgeDataList(graphmlEdgeDataList);
            graphmlEdges.add(graphmlEdge);


        }

        graphmlGraph.setGraphmlEdges(graphmlEdges);
        graphmlGraph.setGraphmlNodes(graphmlNodes);
        return graphmlGraph;

    }




}
