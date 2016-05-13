/*
 * CLIReportViewer.java
 *
 * This work is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * This work is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * Copyright (c) 2010-2016 iTransformers Labs. All rights reserved.
 */

package net.itransformers.topologyviewer.rightclick.impl;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;
import net.itransformers.topologyviewer.gui.GraphViewerPanel;
import net.itransformers.topologyviewer.gui.MyVisualizationViewer;
import net.itransformers.topologyviewer.gui.TopologyManagerFrame;
import net.itransformers.topologyviewer.rightclick.RightClickHandler;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Map;

public class NodeStatisticsRightClickHandler implements RightClickHandler {
    public <G> void handleRightClick(JFrame parent, String v,
                                     Map<String, String> graphMLParams,
                                     Map<String, String> rightClickParams,
                                     File projectPath,
                                     File versionDir) throws Exception {
        Logger logger = Logger.getLogger(NodeStatisticsRightClickHandler.class);

        JFrame frame = new JFrame(" report for " + v + " ");
        frame.setSize(300, 200);
        frame.getContentPane().setLayout(new BorderLayout());
        JTextPane text = new JTextPane();
        text.setEditable(true);
        text.setContentType("text/html");
        StringBuilder sb = new StringBuilder();
        TopologyManagerFrame viewer = (TopologyManagerFrame) parent;
        final GraphViewerPanel viewerPanel = (GraphViewerPanel) viewer.getTabbedPane().getSelectedComponent();
        final MyVisualizationViewer vv = (MyVisualizationViewer) viewerPanel.getVisualizationViewer();
        Graph currentGraph = viewerPanel.getCurrentGraph();
        int inDegree = currentGraph.inDegree(v);
        int outDegree = currentGraph.outDegree(v);
        sb.append("Number of incoming edges: ").append(inDegree);
        sb.append("\n");
        sb.append("Number of outgoing edges: ").append(outDegree);
        text.setText(sb.toString());
        JScrollPane scrollPane = new JScrollPane(text);
        frame.getContentPane().add("Center", scrollPane);
        frame.setVisible(true);

    }

}
