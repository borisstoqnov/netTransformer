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

import net.itransformers.topologyviewer.menu.MenuBuilder;
import edu.uci.ics.jung.graph.*;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.AccessControlException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Demonstrates loading (and visualizing) a graph from a GraphML file.
 * http://www.mesur.org/services/mesur4/mesur_test.html
 *
 */
public class TopologyManagerFrame extends JFrame{
    static Logger logger = Logger.getLogger(TopologyManagerFrame.class);
    public static final String VIEWER_PREFERENCES_PROPERTIES = "viewer-preferences.properties";
    private File path;
    private JTabbedPane tabbedPane;
    private Properties preferences = new Properties();
    Map<String, GraphViewerPanelManager> viewerPanelManagerMap = new HashMap<String, GraphViewerPanelManager>();

    public TopologyManagerFrame(final File path) throws Exception {
        super("iTopoManager");
        File prefsFile = new File(VIEWER_PREFERENCES_PROPERTIES);
        try {
            if (!prefsFile.exists()) {
                if (!prefsFile.createNewFile()){
                    logger.error("Can not create preferences file");
                }
            }
            preferences.load(new FileInputStream(prefsFile));
        } catch (AccessControlException e){
            logger.error(e.getMessage());
        }
        this.path = path;
        createFrame();
    }

    public File getPath() {
        return path;
    }

    public void setPath(File path) {
        this.path = path;
    }

    private void createFrame(){
        tabbedPane = new JTabbedPane();
        this.setExtendedState(Frame.MAXIMIZED_BOTH);
        try {
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } catch (java.security.AccessControlException e){
            e.printStackTrace();
        }
        final Container content = this.getContentPane();

        JMenuBar menuBar = new MenuBuilder().createMenuBar(this);

        content.add(tabbedPane);

        this.setJMenuBar(menuBar);
        this.setMinimumSize(new Dimension(640, 480));
        this.setVisible(true);
    }


    public JTabbedPane getTabbedPane() {
        return tabbedPane;
    }

    public Properties getPreferences() {
        return preferences;
    }

    public void doOpenGraph(File selectedFile) {
        try {
            if ("undirected".equals(selectedFile.getName()) || "diff-undirected".equals(selectedFile.getName())){
                GraphViewerPanelManager<UndirectedGraph<String, String>> viewerPanelManager = new GraphViewerPanelManager<UndirectedGraph<String, String>>(this, path, selectedFile, UndirectedSparseGraph.<String, String>getFactory(), tabbedPane);
                viewerPanelManagerMap.put(selectedFile.getAbsolutePath(),viewerPanelManager);
                viewerPanelManager.createAndAddViewerPanel();
            } else if ("directed".equals(selectedFile.getName()) || "diff-directed".equals(selectedFile.getName())){
                GraphViewerPanelManager<DirectedGraph<String, String>> viewerPanelManager = new GraphViewerPanelManager<DirectedGraph<String, String>>(this, path ,selectedFile, DirectedSparseMultigraph.<String, String>getFactory(), tabbedPane);
                viewerPanelManagerMap.put(selectedFile.getAbsolutePath(),viewerPanelManager);
                viewerPanelManager.createAndAddViewerPanel();
            } else {
                JOptionPane.showMessageDialog(this,String.format("Unknown graph type %s. Expected types are (directed, undirected, diff-undirected, diff-directed)",selectedFile.getName()));
            }
        } catch (Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,"Unknown create graph: "+e.getMessage());
        }
    }
    public void doCloseGraph() {
        GraphViewerPanel viewerPanel = (GraphViewerPanel)getTabbedPane().getSelectedComponent();
        if (viewerPanel == null){
            return;
        }
        String absolutePath = viewerPanel.getGraphmlDir().getAbsolutePath();
        viewerPanelManagerMap.remove(absolutePath);
        JTabbedPane tabbedPane = this.getTabbedPane();
        int count = tabbedPane.getTabCount() ;
        for (int j = count-1 ; j >= 0 ; j--) {
            GraphViewerPanel currentViewerPanel = (GraphViewerPanel) tabbedPane.getComponent(j);
            if (currentViewerPanel.getGraphmlDir().getAbsolutePath().equals(absolutePath)) tabbedPane.remove(j) ;
        }

    }

    public void doOpenProject(File selectedFile) {
        this.setPath(selectedFile);
        this.getPreferences().setProperty(PreferencesKeys.PATH.name(), this.getPath().toString());
        try {
            this.getPreferences().store(new FileOutputStream(TopologyManagerFrame.VIEWER_PREFERENCES_PROPERTIES), "");
        } catch (IOException e1) {
            e1.printStackTrace();
            JOptionPane.showMessageDialog(this, "Can not Store preferences: " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
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

    public void doCloseProject() {
        setPath(null);
        getTabbedPane().removeAll();
        viewerPanelManagerMap.clear();
    }

    public GraphViewerPanelManager getCurrentGraphViewerManager(){
        GraphViewerPanel viewerPanel = (GraphViewerPanel)getTabbedPane().getSelectedComponent();
        if (viewerPanel != null){
            return viewerPanelManagerMap.get(viewerPanel.getGraphmlDir().getAbsolutePath());
        } else {
            return null;
        }
    }
}

