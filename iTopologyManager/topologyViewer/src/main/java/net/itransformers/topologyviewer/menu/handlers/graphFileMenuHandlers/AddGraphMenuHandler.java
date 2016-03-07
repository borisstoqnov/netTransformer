/*
 * AddGraphMenuHandler.java
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
import net.itransformers.utils.ProjectConstants;
import org.jfree.ui.ExtensionFileFilter;
import org.springframework.util.FileCopyUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * Created by niau on 9/13/15.
 */
public class AddGraphMenuHandler implements ActionListener {

    private TopologyManagerFrame frame;
    public static final String VERSION_LABEL = ProjectConstants.labelDirName;


    public AddGraphMenuHandler(TopologyManagerFrame frame) {
        this.frame = frame;
    }





    @Override
    public void actionPerformed(ActionEvent e) {
        File projectPath = frame.getPath();

        if (projectPath == null) {
            JOptionPane.showMessageDialog(frame, "Can not open graph before project has been opened.");
            return;
        }
        final File networkPath = new File(projectPath + File.separator + ProjectConstants.networkDirName);
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select a graphml file to load into your project");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        chooser.setFileFilter(new ExtensionFileFilter("Graphml File chooser", "graphml"));

        chooser.setMultiSelectionEnabled(false);
        int result = chooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File fileResult = chooser.getSelectedFile();



            if(fileResult.exists()){
                System.out.println(fileResult);
                File version = autolabel(projectPath.getAbsolutePath());

                File graphmlDir = new File(version, ProjectConstants.undirectedGraphmlDirName);
                graphmlDir.mkdir();

                try {
                    File outputFile = new File(graphmlDir,"network.graphml");
                    FileCopyUtils.copy(fileResult,outputFile);
                    JOptionPane.showMessageDialog(frame, "Graph file successfully imported in "+ version.getName() + " of your current project!");

                    frame.doOpenGraph(outputFile);

                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        }

    }

    public static File autolabel(String projectPath){
        File networkPath = new File(projectPath, ProjectConstants.networkDirName);

        if (!networkPath.exists()) {
            networkPath.mkdir();

            File labelDir = new File(networkPath, VERSION_LABEL + "1");
            labelDir.mkdir();
            return labelDir;
        }
        String[] fileList = new File(projectPath, ProjectConstants.networkDirName).list();
        int max = 0;
        for (String fName : fileList) {
            if (fName.matches(VERSION_LABEL+"\\d+")){
                int curr = Integer.parseInt(fName.substring(VERSION_LABEL.length()));
                if (max < curr ) max = curr;
            }
        }
        File labelDir = new File(networkPath,VERSION_LABEL +(max+1));
        labelDir.mkdir();
        return labelDir;
    }

}

