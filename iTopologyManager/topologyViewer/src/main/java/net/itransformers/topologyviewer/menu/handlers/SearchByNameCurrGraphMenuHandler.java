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

package net.itransformers.topologyviewer.menu.handlers;

import net.itransformers.topologyviewer.gui.GraphViewerPanel;
import net.itransformers.topologyviewer.gui.TopologyManagerFrame;
import edu.uci.ics.jung.visualization.VisualizationViewer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * Date: 12-4-27
 * Time: 23:30
 * To change this template use File | Settings | File Templates.
 */
public class SearchByNameCurrGraphMenuHandler implements ActionListener {

    private TopologyManagerFrame frame;

    public SearchByNameCurrGraphMenuHandler(TopologyManagerFrame frame) throws HeadlessException {

        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        GraphViewerPanel viewerPanel = (GraphViewerPanel) frame.getTabbedPane().getSelectedComponent();
        final VisualizationViewer vv = viewerPanel.getVisualizationViewer();
        Collection<String> vertices = viewerPanel.getCurrentGraph().getVertices();
        String [] test = vertices.toArray(new String[0]);
        Arrays.sort(test);
        String vertex = (String) JOptionPane.showInputDialog(frame, "Choose Node Name", "Node", JOptionPane.PLAIN_MESSAGE, null, test, test[0]);

        if (viewerPanel.FindNodeByIDCurrentGraph(vertex)){
            viewerPanel.SetPickedState(vertex);
            viewerPanel.Animator(vertex);
        }   else {
            JOptionPane.showMessageDialog(frame, "Can not find node:" + vertex, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
