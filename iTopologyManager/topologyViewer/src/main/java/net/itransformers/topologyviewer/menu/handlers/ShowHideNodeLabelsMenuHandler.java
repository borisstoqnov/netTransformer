/*
 * ShowHideNodeLabelsMenuHandler.java
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
public class ShowHideNodeLabelsMenuHandler implements ActionListener {

    private TopologyManagerFrame frame;

    public ShowHideNodeLabelsMenuHandler(TopologyManagerFrame frame) throws HeadlessException {

        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        GraphViewerPanel viewerPanel = (GraphViewerPanel) frame.getTabbedPane().getSelectedComponent();
        if(viewerPanel.isVertexLabel()){
            viewerPanel.getVisualizationViewer().getRenderContext().setVertexLabelTransformer(new ConstantTransformer(null));
            viewerPanel.getVisualizationViewer().repaint();
            viewerPanel.setVertexLabel(false);
        }
        else{
            viewerPanel.getVisualizationViewer().getRenderContext().setVertexLabelTransformer(new ToStringLabeller<String>());
            viewerPanel.getVisualizationViewer().repaint();
            viewerPanel.setVertexLabel(true);

        }
    }
}
