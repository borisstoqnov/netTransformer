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

package net.itransformers.topologyviewer.rightclick.impl;

import net.itransformers.topologyviewer.gui.MyVisualizationViewer;
import net.itransformers.topologyviewer.gui.TopologyViewer;
import net.itransformers.topologyviewer.gui.ViewerPanel;
import net.itransformers.topologyviewer.rightclick.RightClickHandler;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

public class ShortestPath implements RightClickHandler {
    public <G> void handleRightClick(JFrame parent, String v,
                                     Map<String, String> graphMLParams,
                                     Map<String, String> rightClickParams,
                                     java.io.File deviceDataXmlFileName) throws Exception {

        TopologyViewer viewer = (TopologyViewer) parent;
        final ViewerPanel viewerPanel = (ViewerPanel) viewer.getTabbedPane().getSelectedComponent();
        final MyVisualizationViewer vv = (MyVisualizationViewer) viewerPanel.getVisualizationViewer();
        Object [] test = viewerPanel.getCurrentGraph().getVertices().toArray();
        Arrays.sort(test);

        final String mTo = (String) JOptionPane.showInputDialog(parent, "Choose B Node", "B Node", JOptionPane.PLAIN_MESSAGE, null, test, test[0]);

        final Graph<String, String> mGraph = viewerPanel.getCurrentGraph();
        final Set<String> mPred = viewerPanel.findShortest(v, mTo, mGraph);
        if(mPred == null) {
            JOptionPane.showMessageDialog(parent,String.format("Shortest path between %s,%s is not found",v,mTo),"Message",JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        final Layout<String,String> layout = vv.getGraphLayout();
        for (final String edge : layout.getGraph().getEdges()) {
            Pair<String> endpoints = mGraph.getEndpoints(edge);
            String v1= endpoints.getFirst()	;

            String v2= endpoints.getSecond() ;
            if (!v1.equals(v2) && mPred.contains(v1) && mPred.contains(v2)){
                vv.setEdgeStroke(edge, new BasicStroke(4f));

            }
        }

        viewerPanel.SetPickedStates(mPred);
       viewerPanel.repaint();
        }

}
