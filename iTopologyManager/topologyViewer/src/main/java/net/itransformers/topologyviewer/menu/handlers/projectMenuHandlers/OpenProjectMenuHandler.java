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
        File dir = new File(System.getProperty("user.home"));
        if (frame.getPath() != null){
            dir = frame.getPath();
        }

        JFileChooser chooser = new JFileChooser(dir);
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setFileView(new FileView() {
            ImageIcon imageIcon;
            {
                final URL resource = getClass().getResource("/images/metro_switch_medium_small.png");
                imageIcon = new ImageIcon(resource);

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
            if(selectedFile.getName().equals("bgpPeeringMap.pfl")){
                frame.setProjectType("netTransformer - BGP Peering Map");
                frame.setName("bgpPeeringMap");
                frame.setViewerConfig(new File(selectedFile.getParentFile()+ File.separator+"iTopologyManager/topologyViewer/conf/xml/bgpPeeringMap/viewer-config.xml"));
                frame.getRootPane().getJMenuBar().getMenu(1).getMenuComponent(0).setEnabled(false);
                frame.getRootPane().getJMenuBar().getMenu(1).getMenuComponent(1).setEnabled(true);
                frame.getRootPane().getJMenuBar().getMenu(6).getMenuComponent(4).setEnabled(true);



            } else if(selectedFile.getName().equals("netTransformer.pfl"))    {
                frame.setProjectType("netTransformer - SNMP Network Discoverer");
                frame.setViewerConfig(new File(selectedFile.getParentFile()+File.separator+ "iTopologyManager/topologyViewer/conf/xml/viewer-config.xml"));
                //
                frame.getRootPane().getJMenuBar().getMenu(1).getMenuComponent(0).setEnabled(true);
                frame.getRootPane().getJMenuBar().getMenu(1).getMenuComponent(1).setEnabled(false);
                frame.getRootPane().getJMenuBar().getMenu(6).getMenuComponent(3).setEnabled(true);

            }  else{
                JOptionPane.showMessageDialog(frame, "Unknown project type");
                return;
            }
            frame.doOpenProject(selectedFile.getParentFile());
            frame.getRootPane().getJMenuBar().getMenu(1).setEnabled(true);
            frame.getRootPane().getJMenuBar().getMenu(2).setEnabled(true);
            frame.getRootPane().getJMenuBar().getMenu(3).setEnabled(true);
            frame.getRootPane().getJMenuBar().getMenu(4).setEnabled(true);
            frame.getRootPane().getJMenuBar().getMenu(5).setEnabled(true);
            frame.getRootPane().getJMenuBar().getMenu(6).setEnabled(true);

            frame.getRootPane().getJMenuBar().getMenu(0).getMenuComponent(4).setEnabled(true);
            frame.getRootPane().getJMenuBar().getMenu(0).getMenuComponent(5).setEnabled(true);
            frame.getRootPane().getJMenuBar().getMenu(0).getMenuComponent(6).setEnabled(true);
            frame.getRootPane().getJMenuBar().getMenu(0).getMenuComponent(7).setEnabled(true);
            frame.getRootPane().getJMenuBar().getMenu(0).getMenuComponent(8).setEnabled(true);
            frame.getRootPane().getJMenuBar().getMenu(0).getMenuComponent(9).setEnabled(true);

        }

    }

}
