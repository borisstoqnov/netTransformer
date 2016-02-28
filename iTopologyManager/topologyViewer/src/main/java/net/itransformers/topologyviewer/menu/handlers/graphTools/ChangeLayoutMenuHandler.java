/*
 * ChangeLayoutMenuHandler.java
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

package net.itransformers.topologyviewer.menu.handlers.graphTools;

import net.itransformers.topologyviewer.gui.GraphViewerPanel;
import net.itransformers.topologyviewer.gui.TopologyManagerFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ChangeLayoutMenuHandler implements ActionListener {
    private TopologyManagerFrame frame;
    String layout;

    public ChangeLayoutMenuHandler(TopologyManagerFrame frame, String layout) throws HeadlessException {

        this.frame = frame;
        this.layout = layout;
    }

    @Override
    public void actionPerformed(ActionEvent e) {





        String attractionValue = JOptionPane.showInputDialog(frame, "Attraction factor", 0.75D);
        String repultionValue = JOptionPane.showInputDialog(frame, "Repultion factor", 0.75D);
        String maxIterationsValue = JOptionPane.showInputDialog(frame, "Max iterations", 700);


        final double repultion = new Double(repultionValue);
        final double attraction = new Double(attractionValue);
        final int maxIterations = new Integer(maxIterationsValue);

        final GraphViewerPanel viewerPanel = (GraphViewerPanel) frame.getTabbedPane().getSelectedComponent();
        viewerPanel.setLayout(layout);


        viewerPanel.changeLayout(repultion,attraction,maxIterations,viewerPanel.calculateNodeDensity());
    }
}
