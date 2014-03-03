/*
 * netTransformer is an open source tool able to discover IP networks
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

import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import net.itransformers.topologyviewer.gui.GraphViewerPanel;
import net.itransformers.topologyviewer.gui.TopologyManagerFrame;
import org.apache.commons.collections15.functors.ConstantTransformer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * Date: 12-4-27
 * Time: 23:30
 * To change this template use File | Settings | File Templates.
 */
public class ShowHideEdgeLabelsMenuHandler implements ActionListener {

    private TopologyManagerFrame frame;

    public ShowHideEdgeLabelsMenuHandler(TopologyManagerFrame frame) throws HeadlessException {

        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        GraphViewerPanel viewerPanel = (GraphViewerPanel) frame.getTabbedPane().getSelectedComponent();
        if(viewerPanel.isEdgeLabel()){
            viewerPanel.getVisualizationViewer().getRenderContext().setEdgeLabelTransformer(new ConstantTransformer(null));
            viewerPanel.getVisualizationViewer().repaint();
            viewerPanel.setEdgeLabel(false);
        }
        else{
            viewerPanel.getVisualizationViewer().getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<String>());
            viewerPanel.getVisualizationViewer().repaint();
            viewerPanel.setEdgeLabel(true);

        }


    }
}
