/*
 * OpenGraphMenuHandler.java
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

package net.itransformers.topologyviewer.menu.handlers.graphFileMenuHandlers;

import net.itransformers.topologyviewer.gui.TopologyManagerFrame;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
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
    File undirectedDir;
    File networkGraphml;
    public OpenGraphMenuHandler(TopologyManagerFrame frame) throws HeadlessException {

        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        File path = frame.getPath();

        if (path == null) {
            JOptionPane.showMessageDialog(frame, "Can not open graph before project has been opened.");
            return;
        }
        File networkPath = new File(path+File.separator+"network");
        JFileChooser chooser;
        if (!networkPath.exists()){
                chooser = new JFileChooser(path);
        } else{
            chooser = new JFileChooser(networkPath);
        }
        chooser.setDialogTitle("Choose Graph version");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);


        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {

                    if (f.exists() && f.isDirectory()){

                        undirectedDir = new File(f+File.separator);
                        networkGraphml = new File(undirectedDir + "network.graphml");
                        if (undirectedDir.exists() && networkGraphml.exists()){
                            return true;
                        }

                    }
                     return false;
            }

            @Override
            public String getDescription() {
                return "(Network version folders)";
            }
        });
        chooser.setMultiSelectionEnabled(false);
        int result = chooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File fileResult = new File(chooser.getSelectedFile() + File.separator + "graphml-undirected" + File.separator +"network.graphml");

            System.out.println(fileResult);

            if(fileResult.exists()){
                frame.doOpenGraph(fileResult);
            }else {
                JOptionPane.showMessageDialog(frame, "You are trying to open a version that does not contain a network graph!");

            }
        }

    }

}
