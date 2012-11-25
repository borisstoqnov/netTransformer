/*
 * iTransformer is an open source tool able to discover IP networks
 * and to perform dynamic data data population into a xml based inventory system.
 * Copyright (C) 2010  http://itransformers.net
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.itransformers.topologyviewer.gui;

import net.itransformers.topologyviewer.config.TopologyViewerConfType;
import net.itransformers.topologyviewer.menu.MenuBuilder;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.io.GraphMLMetadata;
import org.apache.commons.collections15.Factory;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessControlException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;


/**
 * Demonstrates loading (and visualizing) a graph from a GraphML file.
 * http://www.mesur.org/services/mesur4/mesur_test.html
 *
 */
public class TopologyViewer<G extends Graph<String,String>> extends JFrame{
    static Logger logger = Logger.getLogger(TopologyViewer.class);
    public static final String VIEWER_PREFERENCES_PROPERTIES = "viewer-preferences.properties";
    /**
     * the visual component and renderer for the graph
     */
    private G entireGraph;
    private TopologyViewerConfType viewerConfig;
    private GraphmlLoader<G> graphmlLoader;
    private URL path;
    private String initialNode;
    private String graphmlRelDir;
    private URL configURI;
    private IconMapLoader iconMapLoader;
    private EdgeStrokeMapLoader edgeStrokeMapLoader;
    private EdgeColorMapLoader edgeColorMapLoader;

    public  Map<String, Map<String, GraphMLMetadata<String>>> getVertexMetadatas (){
        return graphmlLoader.getVertexMetadatas();
    }
    public GraphmlLoader<G> getGraphmlLoader() {
        return graphmlLoader;
    }
    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public Properties getPreferences() {
        return preferences;
    }

    public TopologyViewerConfType getViewerConfig() {
        return viewerConfig;
    }

    private JTabbedPane tabbedPane;
    private Factory<G> factory;
    private Properties preferences = new Properties();

//    private File currentDir;
//    private Map<String, IconsMapType> iconsMapTypeMap;

    /**
     * create an instance of a simple graph with controls to
     * demo the zoom features.
     * @throws org.xml.sax.SAXException
     * @throws javax.xml.parsers.ParserConfigurationException
     * @throws java.io.IOException
     *
     */
    public TopologyViewer(final URL path, String graphmlRelDir, Factory<G> factory, URL configFileURI, String initialNode) throws Exception {
        super("iTopoManager");
        File prefsFile = new File(VIEWER_PREFERENCES_PROPERTIES);
        try {
            if (!prefsFile.exists()) {
                if (!prefsFile.createNewFile()){
                    logger.error("Can not create prefs file");
                }
            }
            preferences.load(new FileInputStream(prefsFile));
        } catch (AccessControlException e){
            logger.error(e.getMessage());
        }
        this.path = path;
        this.graphmlRelDir = graphmlRelDir;
        this.configURI = configFileURI;
        this.initialNode = initialNode;
        createFrame();
        this.factory = factory;
        if (this.path == null) { // if path is null try to load from preferences
            final String pref_path = preferences.getProperty(PreferencesKeys.PATH.name());
            if (pref_path != null){
                this.path = new URL(pref_path);
            }
        }
        if (this.graphmlRelDir == null){ // if graphmlRelDir is null try to load from preferences
            this.graphmlRelDir = preferences.getProperty(PreferencesKeys.GRAPHML_REL_DIR.name());
        }
        if (configFileURI == null) {
            String fName = preferences.getProperty(PreferencesKeys.CONFIG_FILE_NAME.name());
            if (fName == null) { // use default
                fName = new File("iTopologyManager/topologyViewer/conf/xml/undirected.xml").toURI().toString();
            }
            this.configURI = new URL(fName);
        }
        init();

        if (path != null){
            createAndAddViewerPanel();
        }
    }

    public URL getPath() {
        return path;
    }

    public void setPath(URL path) {
        this.path = path;
    }

    public void setGraphmlRelDir(String graphmlRelDir) {
        this.graphmlRelDir = graphmlRelDir;
    }

    public String getGraphmlRelDir() {
        return graphmlRelDir;
    }

    public void init() throws JAXBException, ParserConfigurationException, SAXException, IOException {
        tabbedPane.removeAll();
        entireGraph = factory.create();
        viewerConfig = ViewerConfigLoader.loadViewerConfig(configURI);
        iconMapLoader = new IconMapLoader(viewerConfig);
        edgeStrokeMapLoader = new EdgeStrokeMapLoader(viewerConfig);
        edgeColorMapLoader = new EdgeColorMapLoader(viewerConfig);
        graphmlLoader = new GraphmlLoader<G>(viewerConfig, entireGraph, factory);
        graphmlLoader.addGraphmlLoaderListener(iconMapLoader);
        graphmlLoader.addGraphmlLoaderListener(edgeStrokeMapLoader);
        graphmlLoader.addGraphmlLoaderListener(edgeColorMapLoader);
        graphmlLoader.loadGraphml(new URL(path,graphmlRelDir+"/"));
    }

    private void createFrame(){
        // create a frome to hold the graph
//        final JFrame frame = new JFrame();
        tabbedPane = new JTabbedPane();
         JFrame frame = this;
//        frame.setBackground(Color.black);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        try {
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } catch (java.security.AccessControlException e){
            e.printStackTrace();
        }
        final Container content = frame.getContentPane();

        JMenuBar menuBar = new MenuBuilder().createMenuBar(this);

        content.add(tabbedPane);

        frame.setJMenuBar(menuBar);
        frame.setMinimumSize(new Dimension(640, 480));
        frame.setVisible(true);
//        return frame;
    }


    public void createAndAddViewerPanel() {
        ViewerPanel panel = createViewerPanel(this);
        tabbedPane.addTab("Network", panel);
    }

    public void doOpen(File selectedFile) {
        try {
            URL path = selectedFile.getParentFile().toURI().toURL();
            this.setPath(path);
            String graphmlRelDir = selectedFile.getName();
            this.setGraphmlRelDir(graphmlRelDir);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
            JOptionPane.showMessageDialog(this, "Can not create view. Error: " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        this.getPreferences().setProperty(PreferencesKeys.PATH.name(), this.getPath().toString());
        this.getPreferences().setProperty(PreferencesKeys.GRAPHML_REL_DIR.name(),this.getGraphmlRelDir());
        try {
            this.getPreferences().store(new FileOutputStream(TopologyViewer.VIEWER_PREFERENCES_PROPERTIES),"");
        } catch (IOException e1) {
            e1.printStackTrace();
            JOptionPane.showMessageDialog(this, "Can not Store preferences: " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
        try {
            this.init();
        } catch (Exception e1) {
            e1.printStackTrace();
            JOptionPane.showMessageDialog(this, "Can not create view. Error: " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        this.createAndAddViewerPanel();
    }

    public URL getConfigURI() {
        return configURI;
    }

    public void setConfigUri(URL configURI) {
        this.configURI = configURI;
    }

    public void setInitialNode(String initialNode) {
        this.initialNode = initialNode;
    }

    private ViewerPanel createViewerPanel(JFrame frame) {
        return new ViewerPanel(frame, viewerConfig, graphmlLoader, iconMapLoader, edgeStrokeMapLoader, edgeColorMapLoader, entireGraph,  path, graphmlRelDir,initialNode);
    }

//    private void applyNeighborFilter(final FilterType filter, final Integer hops, final Set<String> pickedVertexes) {
//        EdgePredicateFilter<String, String> KNeighborhoodFilter = createEdgeFilter(filter);
//        final Graph<String,String> graph1 = KNeighborhoodFilter.transform(entireGraph);
//        VertexPredicateFilter<String, String> filterV = createVertexFilter(filter, graph1);
//        edu.uci.ics.jung.algorithms.filters.KNeighborhoodFilter <String,String> FilterN = null;
//
//        G graph2 = (G) filterV.transform(graph1);
//        HashSet<String> set = new HashSet<String>(graph2.getVertices());
//        if (pickedVertexes != null){
//            set.retainAll(pickedVertexes);
//        }
//        KNeighborhoodFilter<String, String> f = new KNeighborhoodFilter<String,String>(set,hops, KNeighborhoodFilter.EdgeType.IN_OUT);
//        currentGraph = (G) f.transform(graph2);
//        vv.setGraphLayout(createLayout(currentGraph));
//    }

}

