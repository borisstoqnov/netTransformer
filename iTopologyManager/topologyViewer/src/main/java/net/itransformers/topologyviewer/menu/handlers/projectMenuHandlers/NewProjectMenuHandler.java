/*
 * NewProjectMenuHandler.java
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

import net.itransformers.topologyviewer.dialogs.NewProjectDialog;
import net.itransformers.topologyviewer.gui.TopologyManagerFrame;
import net.itransformers.utils.RecursiveCopy;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by IntelliJ IDEA.
 * Date: 12-4-27
 * Time: 23:30
 * To change this template use File | Settings | File Templates.
 */
public class NewProjectMenuHandler implements ActionListener {

    private TopologyManagerFrame frame;
    static Logger logger = Logger.getLogger(NewProjectMenuHandler.class);

    public NewProjectMenuHandler(TopologyManagerFrame frame) throws HeadlessException {

        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        NewProjectDialog dialog = new NewProjectDialog(frame);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
        File file;
        logger.info("Selected project file is:"+dialog.getProjectType());

        if (!dialog.isOkPressed()) {
            return;
        } else if (dialog.getProjectType().equals("BGP Peering Map")) {
            file = new File("bgpPeeringMap.pfl");
            frame.setProjectType("bgpPeeringMap");
            frame.setViewerConfig(new File(dialog.getProjectDir() +File.separator +"iTopologyManager/topologyViewer/conf/xml/bgpPeeringMap/viewer-config.xml"));
            frame.getRootPane().getJMenuBar().getMenu(1).getMenuComponent(0).setEnabled(false);
            frame.getRootPane().getJMenuBar().getMenu(1).getMenuComponent(1).setEnabled(true);
            frame.getRootPane().getJMenuBar().getMenu(7).getMenuComponent(4).setEnabled(true);


        }else if (dialog.getProjectType().equals("Free Graph")) {
            file = new File("freeGraph.pfl");
            frame.setProjectType("freeGraph");
            frame.setViewerConfig(new File(dialog.getProjectDir() +File.separator +"iTopologyManager/topologyViewer/conf/xml/freeGraph/viewer-config.xml"));
            frame.getRootPane().getJMenuBar().getMenu(1).getMenuComponent(0).setEnabled(false);
            frame.getRootPane().getJMenuBar().getMenu(1).getMenuComponent(1).setEnabled(false);
            frame.getRootPane().getJMenuBar().getMenu(7).getMenuComponent(4).setEnabled(true);


        }

        else {
            file = new File("netTransformer.pfl");
            frame.setProjectType("IPNetworkDiscoverer");
            frame.setViewerConfig(new File(dialog.getProjectDir() +File.separator +"iTopologyManager/topologyViewer/conf/xml/viewer-config.xml"));
            frame.getRootPane().getJMenuBar().getMenu(1).getMenuComponent(0).setEnabled(true);
            frame.getRootPane().getJMenuBar().getMenu(1).getMenuComponent(1).setEnabled(true);
            frame.getRootPane().getJMenuBar().getMenu(7).getMenuComponent(3).setEnabled(true);

        }
        Scanner s = null;
        try {
            try {
                s = new Scanner(file);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(this.frame, "Can not find file:" + file.getAbsolutePath());
                return;
            }
            try {
                RecursiveCopy.copyFile(file, dialog.getProjectDir());
                while (s.hasNextLine()) {
                    String text = s.nextLine();
                    if (text.startsWith("#") || text.trim().equals("")) continue;
                    if (System.getProperty("base.dir") == null) System.setProperty("base.dir", ".");
                    String workDirName = System.getProperty("base.dir");
                    File workDir = new File(workDirName);
                    File srcDir = new File(workDir, text);
                    File destDir = new File(dialog.getProjectDir(), text).getParentFile();
                    destDir.mkdirs();
                    RecursiveCopy.copyDir(srcDir, destDir);

                }

            } catch (IOException e1) {
                JOptionPane.showMessageDialog(frame, "Unable to create project the reason is:" + e1.getMessage());
                e1.printStackTrace();
            }
            frame.setPath(dialog.getProjectDir());
            frame.getRootPane().getJMenuBar().getMenu(1).setEnabled(true);
            frame.getRootPane().getJMenuBar().getMenu(2).setEnabled(true);
            frame.getRootPane().getJMenuBar().getMenu(3).setEnabled(true);
            frame.getRootPane().getJMenuBar().getMenu(4).setEnabled(true);
            frame.getRootPane().getJMenuBar().getMenu(5).setEnabled(true);
            frame.getRootPane().getJMenuBar().getMenu(6).setEnabled(true);
            frame.getRootPane().getJMenuBar().getMenu(7).setEnabled(true);
            frame.getRootPane().getJMenuBar().getMenu(0).getMenuComponent(4).setEnabled(true);
            frame.getRootPane().getJMenuBar().getMenu(0).getMenuComponent(5).setEnabled(true);
            frame.getRootPane().getJMenuBar().getMenu(0).getMenuComponent(6).setEnabled(true);
            frame.getRootPane().getJMenuBar().getMenu(0).getMenuComponent(7).setEnabled(true);
            frame.getRootPane().getJMenuBar().getMenu(0).getMenuComponent(8).setEnabled(true);
            frame.getRootPane().getJMenuBar().getMenu(0).getMenuComponent(9).setEnabled(true);

        } finally {
            if (s != null) s.close();
        }

    }


}
