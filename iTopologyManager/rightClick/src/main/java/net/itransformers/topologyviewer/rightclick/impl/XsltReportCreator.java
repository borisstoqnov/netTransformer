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
        if(rightClickParams.get("type").equals("deviceXml")){
             xmlSourcePath = versionDir.getAbsolutePath()+File.separator+path+File.separator+"device-data-"+v+".xml";
            String deviceXmlDir =  versionDir+File.separator+path;
        }else {
            xmlSourcePath = versionDir+File.separator+path+File.separator+"node-"+v+".graphml";
            String graphmlDir =  versionDir + File.separator+path;

        }
        logger.info("XML source path: " + xmlSourcePath + " xsltFile: " + xsltFile + "xsltTableFile: " + xsltTableFile);
        if (!xsltTableFile.equals("")) {
            XsltReport testReport = new XsltReport(new File(projectPath, xsltFile), new File(projectPath, xsltTableFile), new File(xmlSourcePath));
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
