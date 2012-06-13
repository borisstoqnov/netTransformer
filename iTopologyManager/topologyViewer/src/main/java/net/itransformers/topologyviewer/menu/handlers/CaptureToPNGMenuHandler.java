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

import net.itransformers.topologyviewer.gui.PngFileFilter;
import net.itransformers.topologyviewer.gui.TopologyViewer;
import net.itransformers.topologyviewer.gui.ViewerPanel;
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

    private TopologyViewer frame;

    public CaptureToPNGMenuHandler(TopologyViewer frame) throws HeadlessException {

        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ViewerPanel viewerPanel = (ViewerPanel) frame.getTabbedPane().getSelectedComponent();
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
