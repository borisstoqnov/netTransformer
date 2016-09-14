package net.itransformers.topologyviewer.gui;

import edu.uci.ics.jung.graph.Graph;
import net.itransformers.topologyviewer.config.TopologyViewerConfType;
import net.itransformers.topologyviewer.gui.*;
import net.itransformers.topologyviewer.rightclick.RightClickInvoker;

import java.io.File;

/**
 * Created by vasko on 9/14/2016.
 */
public class GraphViewerPanelFactory {
    private TopologyManagerFrame topologyManagerFrame;
    private RightClickInvoker rightClickInvoker;

    public GraphViewerPanelFactory(TopologyManagerFrame topologyManagerFrame, RightClickInvoker rightClickInvoker ) {
        this.topologyManagerFrame = topologyManagerFrame;
        this.rightClickInvoker = rightClickInvoker;
    }

    public <G extends Graph<String, String>> GraphViewerPanel createGraphViewerPanel(TopologyViewerConfType viewerConfig, GraphmlLoader<G> graphmlLoader, IconMapLoader iconMapLoader, EdgeStrokeMapLoader edgeStrokeMapLoader, EdgeColorMapLoader edgeColorMapLoader, G entireGraph, File projectPath, File deviceXmlPath, File versionDir, File graphmlFileName, String initialNode, String layout){
        return new GraphViewerPanel<G>(topologyManagerFrame,
                viewerConfig,
                graphmlLoader,
                iconMapLoader,
                edgeStrokeMapLoader,
                edgeColorMapLoader,
                entireGraph,
                projectPath,
                deviceXmlPath,
                versionDir,
                graphmlFileName,
                initialNode,
                layout,
                rightClickInvoker);

    }
}
