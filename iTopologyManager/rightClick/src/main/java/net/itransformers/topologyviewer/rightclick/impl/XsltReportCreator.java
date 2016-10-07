/*
 * XsltReportCreator.java
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

package net.itransformers.topologyviewer.rightclick.impl;

import net.itransformers.topologyviewer.rightclick.RightClickHandler;
import net.itransformers.utils.*;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Map;

public class XsltReportCreator implements RightClickHandler {
    public <G> void handleRightClick(JFrame parent, String v,
                                     Map<String, String> graphMLParams,
                                     Map<String, String> rightClickParams,
                                     File projectPath,
                                     File versionDir) throws Exception {

        JFrame frame = new JFrame(" report for " + v + " ");
        frame.setSize(600, 400);
        frame.getContentPane().setLayout(new BorderLayout());
        JTextPane text = new JTextPane();
        text.setEditable(true);
        text.setContentType("text/html");
        String xsltFile = rightClickParams.get("xsl_transformator");
        String xsltTableFile = rightClickParams.get("table_transformator");
        Logger logger = Logger.getLogger(XsltReportCreator.class);

        String path =  rightClickParams.get("path");
        String xmlSourcePath = null;
        File deviceXmlFileDir;
        File xmlSourceFile = null;
        if(rightClickParams.get("type").equals("deviceXml")){
            xmlSourcePath = versionDir.getAbsolutePath() + File.separator + path + File.separator + ProjectConstants.deviceDataPrefix + v + ".xml";
            String deviceXmlDir =  versionDir+File.separator+path;
            deviceXmlFileDir = new File(deviceXmlDir);
            if (!deviceXmlFileDir.exists()){
                JOptionPane.showMessageDialog(frame,"Could not find folder: "+deviceXmlDir,"Error",JOptionPane.ERROR_MESSAGE);
                return;
            }
            xmlSourceFile=new File(xmlSourcePath);
            if (!xmlSourceFile.exists()){
                JOptionPane.showMessageDialog(frame,"Could not find folder: "+xmlSourceFile,"Error",JOptionPane.ERROR_MESSAGE);
                return;
            }

        }


        logger.info("XML source path: " + xmlSourcePath + " xsltFile: " + xsltFile + "xsltTableFile: " + xsltTableFile);
        if (!xsltTableFile.equals("")) {
            XsltReport testReport = new XsltReport(new File(projectPath, xsltFile), new File(projectPath, xsltTableFile), xmlSourceFile);
            try {

                String report = testReport.doubleTransformer().toString();
                text.setText(report);
                logger.debug(report);

            } catch (Exception ex) {
                testReport.handleException(ex);
            }
        } else {

            XsltReport testReport = new XsltReport(new File(projectPath, xsltFile), versionDir);
            String report = testReport.doubleTransformer().toString() ;

            logger.debug(report);
            try {
                text.setText(report);
            } catch (Exception ex) {
                testReport.handleException(ex);
            }
        }
        JScrollPane scrollPane = new JScrollPane(text);
        frame.getContentPane().add("Center", scrollPane);
        frame.setVisible(true);
    }

}
