/*
 * OpenProjectMenuHandler.java
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

package net.itransformers.topologyviewer.menu.handlers.projectMenuHandlers;

import net.itransformers.topologyviewer.gui.TopologyManagerFrame;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

public class OpenProjectMenuHandler implements ActionListener {

    private TopologyManagerFrame frame;

    static Logger logger = Logger.getLogger(OpenProjectMenuHandler.class);
    public OpenProjectMenuHandler(TopologyManagerFrame frame) throws HeadlessException {

        this.frame = frame;

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        File dir = new File(new File(System.getProperty("user.dir")).getParent()+File.separator+"Projects");
        if(!dir.exists()){
            dir.mkdir();
        }
        if (frame.getPath() != null){
            dir = frame.getPath();
        }

        JFileChooser chooser = new JFileChooser(dir);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setFileView(new FileView() {
            ImageIcon imageIcon;
            {
                final URL resource = getClass().getResource("/images/metro_switch_medium_small.png");
                if (resource!=null){
                    imageIcon = new ImageIcon(resource);
                }


            }
            @Override
            public String getName(File file) {
                return super.getName(file);
            }

            @Override
            public String getDescription(File file) {
                return super.getDescription(file);
            }

            @Override
            public String getTypeDescription(File file) {
                return super.getTypeDescription(file);
            }

            @Override
            public Icon getIcon(File f) {
                if (f.isDirectory()) {
                    File[] files = f.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            if (file.getName().endsWith(".pfl")) {
                                return imageIcon;
                            }
                        }
                    }
                }
                return super.getIcon(f);
            }

            @Override
            public Boolean isTraversable(File file) {
                return super.isTraversable(file);
            }
        });
        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isFile() && f.getName().endsWith(".pfl")) {
                    return true;
                } else if (f.isDirectory()) {
                    File[] files = f.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            if (file.getName().endsWith(".pfl")) {
                                return true;
                            }
                        }
                    }
                }
                return false;
            }

            @Override
            public String getDescription() {
                return "(Project Files) *.pfl";
            }
        });

        chooser.setMultiSelectionEnabled(false);
        int result = chooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            if (selectedFile.isDirectory()){
                File[] files = selectedFile.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.getName().endsWith(".pfl")) {
                            selectedFile = file;
                            break;
                        }
                    }
                }
            }
            logger.info("Selected project file is:"+selectedFile.getAbsolutePath());


            frame.doOpenProject(selectedFile);


        }

    }

}
