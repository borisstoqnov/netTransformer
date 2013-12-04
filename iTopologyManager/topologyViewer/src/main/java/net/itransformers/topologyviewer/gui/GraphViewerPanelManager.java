/*
 * netTransformer is an open source tool able to discover and transform
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
    private String initialNode;
    private File projectPath;
    private File viewerConfigFile;
    private GraphType graphType;
    private IconMapLoader iconMapLoader;
    private EdgeStrokeMapLoader edgeStrokeMapLoader;
    private EdgeColorMapLoader edgeColorMapLoader;
    private File graphmlDir;
    private Factory<G> factory;
    private JTabbedPane tabbedPane;
    private TopologyViewerConfType viewerConfig;
    private JFrame frame;
    private final File versionDir;
    private String layout;

    public GraphViewerPanelManager(JFrame frame, String projectType, File projectPath, File viewerConfigFile, File graphmlFile, Factory<G> factory, JTabbedPane tabbedPane, GraphType graphType) throws Exception {
        this.frame = frame;
        this.projectPath = projectPath;
        this.graphType = graphType;
        this.viewerConfigFile = viewerConfigFile;
        versionDir = new File(graphmlFile.getParent());
        this.graphmlDir = graphmlFile;
        this.factory = factory;
        this.tabbedPane = tabbedPane;
        entireGraph = factory.create();
        viewerConfig = ViewerConfigLoader.loadViewerConfig(this.viewerConfigFile);
        this.layout="FRLayout";
        init();
}

    public Map<String, GraphMLMetadata<String>> getVertexMetadatas() {
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
        graphmlLoader = new GraphmlLoader<G>(entireGraph, factory);
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
    public GraphType getGraphType(){
        return graphType;
    }
}
