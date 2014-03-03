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

package net.itransformers.topologyviewer.rightclick.impl;

import net.itransformers.topologyviewer.rightclick.RightClickHandler;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Map;

public class CLIReportViewer implements RightClickHandler {
    public <G> void handleRightClick(JFrame parent, String v,
                                     Map<String, String> graphMLParams,
                                     Map<String, String> rightClickParams,
                                     File projectPath,
                                     File versionDir) throws Exception {
        Logger logger = Logger.getLogger(CLIReportViewer.class);

        JFrame frame = new JFrame(" report for " + v + " ");
        frame.setSize(600, 400);
        frame.getContentPane().setLayout(new BorderLayout());
        JTextPane text = new JTextPane();
        text.setEditable(true);
        text.setContentType("text/html");
        String postDiscoveryFolderPath = rightClickParams.get("post-discovery-file-path");
        String reportFileName =    rightClickParams.get("reportFileName");
        logger.info("CLI report executed in "+versionDir+" for "+File.separator+postDiscoveryFolderPath+File.separator+v+File.separator+reportFileName);

        File xmlReport = new File(versionDir.getAbsolutePath()+File.separator+postDiscoveryFolderPath+File.separator+v+File.separator+reportFileName);
        if (xmlReport.exists()){
            text.setText(FileUtils.readFileToString(xmlReport));
            JScrollPane scrollPane = new JScrollPane(text);
            frame.getContentPane().add("Center", scrollPane);
            frame.setVisible(true);


        }else{
            JOptionPane.showMessageDialog(parent,"Report does not exist! Please generate it first!");
        }
    }

}
