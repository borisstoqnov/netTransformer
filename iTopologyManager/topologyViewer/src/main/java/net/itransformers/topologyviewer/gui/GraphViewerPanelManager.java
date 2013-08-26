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

package net.itransformers.topologyviewer.gui;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.io.GraphMLMetadata;
import net.itransformers.topologyviewer.config.TopologyViewerConfType;
import org.apache.commons.collections15.Factory;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class GraphViewerPanelManager<G extends Graph<String, String>> {
    private G entireGraph;
    private GraphmlLoader<G> graphmlLoader;
    private Neo4jLoader<G> neo4jLoader;
    private String initialNode;
    private File projectPath;
    private GraphType graphType;
    private IconMapLoader iconMapLoader;
    private EdgeStrokeMapLoader edgeStrokeMapLoader;
    private EdgeColorMapLoader edgeColorMapLoader;
    private File graphmlDir;
    private Factory<G> factory;
    private JTabbedPane tabbedPane;
    private TopologyViewerConfType viewerConfig;
    private JFrame frame;
    private File graphmlsFile;
    private final File versionDir;
    private String layout;

    public GraphViewerPanelManager(JFrame frame, File projectPath, File graphmlsFile, Factory<G> factory, JTabbedPane tabbedPane, GraphType graphType) throws Exception {
        this.frame = frame;
        this.projectPath = projectPath;
        this.graphType = graphType;
        this.graphmlsFile = graphmlsFile;
        versionDir = new File(graphmlsFile.getParent());
        if (graphType==GraphType.DIRECTED){
            this.graphmlDir = new File(versionDir,"directed");
        }else{
            this.graphmlDir = new File(versionDir,"undirected");
        }
        this.factory = factory;
        this.tabbedPane = tabbedPane;
        entireGraph = factory.create();
        String fName;
        fName = new File("iTopologyManager/topologyViewer/conf/xml/viewer-config.xml").toString();
        viewerConfig = ViewerConfigLoader.loadViewerConfig(new File(this.projectPath, fName));
        this.layout="FRLayout";
        init();

//        this.configFile = configFile;
//        if (configFile == null) {                         /
//            String fName = preferences.getProperty(PreferencesKeys.CONFIG_FILE_NAME.name());
//    }
//        try {
//            viewerConfig = ViewerConfigLoader.loadViewerConfig(configURI);
//        } catch (JAXBException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        } catch (IOException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }

}

    public Map<String, Map<String, GraphMLMetadata<String>>> getVertexMetadatas() {
        return graphmlLoader.getVertexMetadatas();
    }

    public void setInitialNode(String initialNode) {
        this.initialNode = initialNode;
    }

    private GraphViewerPanel createViewerPanel() {
        return new GraphViewerPanel<G>(frame, viewerConfig, graphmlLoader, iconMapLoader, edgeStrokeMapLoader, edgeColorMapLoader, entireGraph, projectPath,versionDir, graphmlDir, initialNode,layout);
    }

    public void init() throws JAXBException, ParserConfigurationException, SAXException, IOException {
        iconMapLoader = new IconMapLoader(viewerConfig);
        edgeStrokeMapLoader = new EdgeStrokeMapLoader(viewerConfig);
        edgeColorMapLoader = new EdgeColorMapLoader(viewerConfig);
        neo4jLoader = new Neo4jLoader<G>(entireGraph, factory, "http://localhost:7474/db/data/");
//        String NetworkId=neo4jLoader.getLatestNetwork();

//        try {
//            neo4jLoader.getVertexes(NetworkId);
//            neo4jLoader.getNeighbourVertexes(NetworkId);
//        } catch (ParseException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        }

        graphmlLoader = new GraphmlLoader<G>(viewerConfig, entireGraph, factory, neo4jLoader.getVertexMetadatas());
        graphmlLoader.addGraphmlLoaderListener(iconMapLoader);
        graphmlLoader.addGraphmlLoaderListener(edgeStrokeMapLoader);
        graphmlLoader.addGraphmlLoaderListener(edgeColorMapLoader);
        graphmlLoader.loadGraphml(graphmlDir);
    }

    public void createAndAddViewerPanel() {
        GraphViewerPanel panel = createViewerPanel();
        String tabName = this.graphmlDir.getParentFile().getName();
        tabbedPane.addTab(tabName, null, panel, graphmlDir.getAbsolutePath());
    }
    public File getVersionDir(){
        return versionDir;
    }
}
