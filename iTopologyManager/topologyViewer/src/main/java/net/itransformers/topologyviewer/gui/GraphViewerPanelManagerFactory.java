package net.itransformers.topologyviewer.gui;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseMultigraph;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.File;

/**
 * Created by vasko on 9/14/2016.
 */
public class GraphViewerPanelManagerFactory {
    static Logger logger = Logger.getLogger(GraphViewerPanelManagerFactory.class);
    private TopologyManagerFrame topologyManagerFrame;
    protected GraphViewerPanelFactory graphViewerPanelFactory;
    public GraphViewerPanelManagerFactory(TopologyManagerFrame topologyManagerFrame, GraphViewerPanelFactory graphViewerPanelFactory) {
        this.topologyManagerFrame = topologyManagerFrame;
        this.graphViewerPanelFactory = graphViewerPanelFactory;
    }

    public GraphViewerPanelManager createGraphViewerPanelManager(String graphType,
                                                                 String projectType,
                                                                 File viewerConfig, File selectedFile, File path, JTabbedPane tabbedPane) throws Exception {
        if (graphType.equals("undirected")) {
            logger.info("Opening "+ projectType + " with viewer config" + viewerConfig + "and selected file" + selectedFile);
            return
                    new GraphViewerPanelManager<UndirectedGraph<String, String>>(
                            topologyManagerFrame,
                            projectType,
                            path,
                            viewerConfig,
                            selectedFile,
                            UndirectedSparseMultigraph.<String, String>getFactory(),
                            tabbedPane,
                            GraphType.UNDIRECTED,
                            graphViewerPanelFactory);
        } else if (selectedFile.getAbsolutePath().contains("directed")) {

            return
                    new GraphViewerPanelManager<DirectedGraph<String, String>>(
                            topologyManagerFrame,
                            projectType,
                            path,
                            viewerConfig,
                            selectedFile,
                            DirectedSparseMultigraph.<String, String>getFactory(),
                            tabbedPane,
                            GraphType.DIRECTED,
                            graphViewerPanelFactory);
        } else {
            throw new RuntimeException(String.format("Unknown graph type %s. Expected types are (directed, undirected)", selectedFile.getName()));
        }

    }
}
