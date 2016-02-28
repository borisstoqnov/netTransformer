/*
 * CaptureToPNGMenuHandler.java
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

import net.itransformers.topologyviewer.gui.GraphViewerPanel;
import net.itransformers.topologyviewer.gui.PngFileFilter;
import net.itransformers.topologyviewer.gui.TopologyManagerFrame;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.screencap.Dump;
import edu.uci.ics.screencap.PNGDump;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * Date: 12-4-27
 * Time: 23:30
 * To change this template use File | Settings | File Templates.
 */
public class CaptureToPNGMenuHandler implements ActionListener {

    private TopologyManagerFrame frame;

    public CaptureToPNGMenuHandler(TopologyManagerFrame frame) throws HeadlessException {

        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        GraphViewerPanel viewerPanel = (GraphViewerPanel) frame.getTabbedPane().getSelectedComponent();
        final VisualizationViewer vv = viewerPanel.getVisualizationViewer();
        try {
            File dir = new File(".");
            JFileChooser chooser = new JFileChooser(dir);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setMultiSelectionEnabled(false);
            chooser.setFileFilter(new PngFileFilter());
            int result = chooser.showSaveDialog(frame);
            if (result == JFileChooser.APPROVE_OPTION) {
                String absolutePath = chooser.getSelectedFile().getAbsolutePath();
                if (!absolutePath.endsWith(".png")) {
                    absolutePath += ".png";
                }
                Dump dumper = new PNGDump();
                dumper.dumpComponent(new File(absolutePath), vv);

            }
        } catch (Exception e1) {
            e1.printStackTrace();
            JOptionPane.showMessageDialog(frame,"Error saving image: "+e1.getMessage());
        }
    }
}
