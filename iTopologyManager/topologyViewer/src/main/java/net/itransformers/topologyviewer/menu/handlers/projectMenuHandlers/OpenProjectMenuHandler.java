/*
 * iTransformer is an open source tool able to discover and transform
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

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class OpenProjectMenuHandler implements ActionListener {

    private TopologyManagerFrame frame;

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
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return  (f.isFile() && f.getName().endsWith(".pfl") || f.isDirectory());
            }

            @Override
            public String getDescription() {
                return "(Project Files) *.pfl";
            }
        });

        chooser.setMultiSelectionEnabled(false);
        int result = chooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            if(chooser.getSelectedFile().getName().equals("bgpPeeringMap.pfl")){
                frame.setProjectType("bgpPeeringMap");
                frame.setName("iTransformer - bgpPeeringMap");
                frame.setViewerConfig(new File(chooser.getSelectedFile().getParentFile()+ File.separator+"iTopologyManager/topologyViewer/conf/xml/bgpPeeringMap/viewer-config.xml"));
                frame.getRootPane().getJMenuBar().getMenu(1).getMenuComponent(0).setEnabled(false);
                frame.getRootPane().getJMenuBar().getMenu(1).getMenuComponent(1).setEnabled(true);

            } else if(chooser.getSelectedFile().getName().equals("itransformer.pfl"))    {
                frame.setProjectType("iTransformer");
                frame.setViewerConfig(new File(chooser.getSelectedFile().getParentFile()+File.separator+ "iTopologyManager/topologyViewer/conf/xml/viewer-config.xml"));
                //
                frame.getRootPane().getJMenuBar().getMenu(1).getMenuComponent(0).setEnabled(true);
                frame.getRootPane().getJMenuBar().getMenu(1).getMenuComponent(1).setEnabled(false);

            }  else{
                JOptionPane.showMessageDialog(frame, "Unknown project type");
                return;
            }
            frame.doOpenProject(chooser.getSelectedFile().getParentFile());
            frame.getRootPane().getJMenuBar().getMenu(1).setEnabled(true);
            frame.getRootPane().getJMenuBar().getMenu(2).setEnabled(true);
            frame.getRootPane().getJMenuBar().getMenu(3).setEnabled(true);
            frame.getRootPane().getJMenuBar().getMenu(4).setEnabled(true);
            frame.getRootPane().getJMenuBar().getMenu(0).getMenuComponent(4).setEnabled(true);
            frame.getRootPane().getJMenuBar().getMenu(0).getMenuComponent(5).setEnabled(true);
            frame.getRootPane().getJMenuBar().getMenu(0).getMenuComponent(6).setEnabled(true);
            frame.getRootPane().getJMenuBar().getMenu(0).getMenuComponent(7).setEnabled(true);
            frame.getRootPane().getJMenuBar().getMenu(0).getMenuComponent(8).setEnabled(true);
            frame.getRootPane().getJMenuBar().getMenu(0).getMenuComponent(9).setEnabled(true);

        }

    }

}
