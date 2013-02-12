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

import net.itransformers.topologyviewer.gui.GraphType;
import net.itransformers.topologyviewer.gui.TopologyManagerFrame;

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
public class OpenGraphMenuHandler implements ActionListener {

    private TopologyManagerFrame frame;
    private GraphType graphType;

    public OpenGraphMenuHandler(TopologyManagerFrame frame, GraphType graphType) throws HeadlessException {

        this.frame = frame;
        this.graphType = graphType;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        File path = frame.getPath();
        if (path == null) {
            JOptionPane.showMessageDialog(frame, "Can not open graph before project path to be set.");
            return;
        }

        File networkDir = new File(path, "network");
        if (!networkDir.exists()) networkDir = path;
        JFileChooser chooser = new JFileChooser(networkDir);
        chooser.setDialogTitle("Choose Graph version");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        int result = chooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            frame.doOpenGraph(chooser.getSelectedFile(), graphType);
        }

    }

}
