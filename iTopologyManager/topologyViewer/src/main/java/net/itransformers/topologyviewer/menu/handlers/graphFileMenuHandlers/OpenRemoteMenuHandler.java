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

package net.itransformers.topologyviewer.menu.handlers.graphFileMenuHandlers;

import net.itransformers.topologyviewer.gui.TopologyManagerFrame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * Date: 12-4-27
 * Time: 23:30
 * To change this template use File | Settings | File Templates.
 */
public class OpenRemoteMenuHandler implements ActionListener {

    private TopologyManagerFrame frame;

    public OpenRemoteMenuHandler(TopologyManagerFrame frame) throws HeadlessException {

        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO obsolete code. remove it
//        String remoteURL = JOptionPane.showInputDialog(frame,"Enter Remote Address","Open Remote Address", JOptionPane.QUESTION_MESSAGE);
//        if (remoteURL != null && !remoteURL.trim().equals("")) {
//            System.out.println(remoteURL);
//            try {
//                remoteURL = remoteURL.trim();
//                if (remoteURL.endsWith("/")) {
//                    remoteURL = remoteURL.substring(0,remoteURL.length()-1);
//                }
//                int slashIndex = remoteURL.lastIndexOf("/");
//                File path = new URL(remoteURL.substring(0,slashIndex+1));
//                frame.setPath(path);
//                String graphmlRelDir = remoteURL.substring(slashIndex+1);
//                frame.setGraphmlRelDir(graphmlRelDir);
//            } catch (MalformedURLException e1) {
//                e1.printStackTrace();
//                JOptionPane.showMessageDialog(frame, "Can not create view. Error: " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//                return;
//            }
//            try {
//                frame.init();
//            } catch (Exception e1) {
//                e1.printStackTrace();
//                JOptionPane.showMessageDialog(frame, "Can not create view. Error: " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
//                return;
//            }
//            frame.createAndAddViewerPanel();
//        }
    }
}
