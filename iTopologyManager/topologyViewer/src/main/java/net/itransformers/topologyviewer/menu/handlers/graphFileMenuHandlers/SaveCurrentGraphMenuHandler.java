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
public class SaveCurrentGraphMenuHandler implements ActionListener {

    private TopologyManagerFrame frame;
    File undirectedDir;
    File networkGraphml;
    public SaveCurrentGraphMenuHandler(TopologyManagerFrame frame) throws HeadlessException {

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
                        undirectedDir = new File(f+File.separator+"undirected");
                        networkGraphml = new File(undirectedDir+File.separator+ "network.graphml");
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
            File fileResult = new File(chooser.getSelectedFile()+File.separator+"undirected"+File.separator+ "network.graphml");
            System.out.println(fileResult);
            if(fileResult.exists()){
                frame.doOpenGraph(fileResult);
            }else {
                JOptionPane.showMessageDialog(frame, "You are trying to open a version that does not contain a network graph!");

            }
        }

    }

}
