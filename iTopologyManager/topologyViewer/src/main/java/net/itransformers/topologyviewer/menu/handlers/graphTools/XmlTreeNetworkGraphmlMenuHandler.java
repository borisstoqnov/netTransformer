/*
 * netTransformer is an open source tool able to discover and transform
 *  IP network infrastructures.
 *  Copyright (C) 2012  http://itransformers.net
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.itransformers.topologyviewer.menu.handlers.graphTools;

import net.itransformers.topologyviewer.gui.TopologyManagerFrame;
import net.itransformers.utils.XMLTreeView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * Date: 12-4-27
 * Time: 23:30
 * To change this template use File | Settings | File Templates.
 */
public class XmlTreeNetworkGraphmlMenuHandler implements ActionListener {

    private TopologyManagerFrame frame;
    File pathToResource;

    public XmlTreeNetworkGraphmlMenuHandler(TopologyManagerFrame frame) throws HeadlessException {

        this.frame = frame;
        //TODO remove this hardCode with configuration
        this.pathToResource = null;

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (frame.getCurrentGraphViewerManager()==null) {
            JOptionPane.showMessageDialog(frame, "Please load the graph prior reviewing its model!");

        } else {
            pathToResource = new File(frame.getCurrentGraphViewerManager().getVersionDir() + File.separator + "undirected" + File.separator + "graphml/network.graphml");

            try {
            new XMLTreeView("Network Centric model",pathToResource);

            } catch (IOException e1) {
                e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }

    }


}
