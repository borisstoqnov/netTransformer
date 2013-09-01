/*
 * iTransformer is an open source tool able to discover IP networks
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
import net.itransformers.utils.XsltReport;
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
                                     File deviceDataXmlFileName) throws Exception {

//      JOptionPane.showMessageDialog(parent, "deviceDataXmlFileName: " + deviceDataXmlFileName );
        JFrame frame = new JFrame(" report for " + v + " ");
        frame.setSize(600, 400);
        frame.getContentPane().setLayout(new BorderLayout());
        JTextPane text = new JTextPane();
        text.setEditable(true);
        text.setContentType("text/html");
        String xsltFile = rightClickParams.get("xsl_transformator");
        String xsltTableFile = rightClickParams.get("table_transformator");
        Logger logger = Logger.getLogger(XsltReportCreator.class);

        logger.info("deviceDataXmlFileName: " + deviceDataXmlFileName + " xsltFile: " + xsltFile + "xsltTableFile: " + xsltTableFile);
        if (!xsltTableFile.equals("")) {
            XsltReport testReport = new XsltReport(new File(projectPath, xsltFile), new File(projectPath, xsltTableFile), deviceDataXmlFileName);
            try {

                String report = testReport.doubleTransformer().toString();
                text.setText(report);
                logger.debug(report);

            } catch (Exception ex) {
                testReport.handleException(ex);
            }
        } else {

            XsltReport testReport = new XsltReport(new File(projectPath, xsltFile), deviceDataXmlFileName);
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
