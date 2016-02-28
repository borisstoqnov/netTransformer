/*
 * ShortestPathMenuHandler.java
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

package net.itransformers.topologyviewer.menu.handlers.graphTools.shortherstPathMenuHandlers;

import net.itransformers.topologyviewer.gui.GraphViewerPanel;
import net.itransformers.topologyviewer.gui.MyVisualizationViewer;
import net.itransformers.topologyviewer.gui.TopologyManagerFrame;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * Date: 12-4-27
 * Time: 23:30
 * To change this template use File | Settings | File Templates.
 */
public class ShortestPathMenuHandler implements ActionListener {

    private TopologyManagerFrame frame;

    public ShortestPathMenuHandler(TopologyManagerFrame frame) throws HeadlessException {

        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final GraphViewerPanel viewerPanel = (GraphViewerPanel) frame.getTabbedPane().getSelectedComponent();
        final MyVisualizationViewer vv = (MyVisualizationViewer) viewerPanel.getVisualizationViewer();
        Collection<String> vertices = viewerPanel.getCurrentGraph().getVertices();
        String[] test = vertices.toArray(new String[0]);
        Arrays.sort(test);

        final String mFrom = (String) JOptionPane.showInputDialog(frame, "Choose A Node", "A Node", JOptionPane.PLAIN_MESSAGE, null, test, test[0]);
        final String mTo = (String) JOptionPane.showInputDialog(frame, "Choose B Node", "B Node", JOptionPane.PLAIN_MESSAGE, null, test, test[0]);

        final Graph<String, String> mGraph = viewerPanel.getCurrentGraph();
        final Set<String> mPred = viewerPanel.findShortest(mFrom, mTo, mGraph);
        if (mPred == null) {
            JOptionPane.showMessageDialog(frame, String.format("Shortest path between %s,%s is not found", mFrom, mTo), "Message", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        final Layout<String, String> layout = vv.getGraphLayout();
        for (final String edge : layout.getGraph().getEdges()) {
            Pair<String> endpoints = mGraph.getEndpoints(edge);
            String v1 = endpoints.getFirst();
            String v2 = endpoints.getSecond();
            if (!v1.equals(v2) && mPred.contains(v1) && mPred.contains(v2)) {
                vv.setEdgeStroke(edge, new BasicStroke(4f));
            }
        }


        viewerPanel.SetPickedStates(mPred);
        vv.repaint();
    }
}
