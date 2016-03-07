/*
 * SaveCurrentGraphMenuHandler.java
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

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import net.itransformers.topologyviewer.gui.GraphViewerPanel;
import net.itransformers.topologyviewer.gui.TopologyManagerFrame;
import net.itransformers.utils.MyGraphMLWriter;
import net.itransformers.utils.ProjectConstants;
import org.apache.commons.collections15.Transformer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * Date: 12-4-27
 * Time: 23:30
 * To change this template use File | Settings | File Templates.
 */
public class SaveCurrentGraphMenuHandler implements ActionListener {

    private TopologyManagerFrame frame;
    File graphmlDir;
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

        JTextField fileName = new JTextField();
        Object[] message = {"File name", fileName};
        String versionName = JOptionPane.showInputDialog("Enter output graph version");

        File networkPath = new File(path + File.separator + ProjectConstants.networkDirName);

        File versionPath = new File(networkPath,versionName);
        if (versionPath.exists()){
            JOptionPane.showMessageDialog(frame, "Version already exists" + versionPath.getAbsolutePath());

            return;
        }

        if (!versionPath.mkdir()){
            JOptionPane.showMessageDialog(frame, "Unable to create version path: " + versionPath.getAbsolutePath());
        }

        if ("undirected".equalsIgnoreCase(frame.getCurrentGraphViewerManager().getGraphType().toString())) {
            graphmlDir = new File(versionPath.getAbsolutePath(), ProjectConstants.undirectedGraphmlDirName);


        } else {
            graphmlDir = new File(versionPath.getAbsolutePath(), ProjectConstants.directedGraphmlDirName);

        }
        if (!graphmlDir.mkdir()) {
            JOptionPane.showMessageDialog(frame, "Unable to create version path:" + versionPath.getAbsolutePath(), "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }


        Writer fileWriter;
        try {
            fileWriter = new FileWriter(new File(graphmlDir, ProjectConstants.networkGraphmlFileName));
        } catch (IOException e1) {
            JOptionPane.showMessageDialog(frame, "Unable to create network.graphml file:", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }
        GraphViewerPanel viewerPanel = (GraphViewerPanel) frame.getTabbedPane().getSelectedComponent();
        final Graph<String, String> currentGraph = viewerPanel.getCurrentGraph();
        MyGraphMLWriter  writer = new MyGraphMLWriter();
        writer.setGraphData(viewerPanel.getGraphmlLoader().getGraphMetadatas());
        writer.setVertexData(viewerPanel.getGraphmlLoader().getVertexMetadatas());
        writer.setEdgeData(viewerPanel.getGraphmlLoader().getEdgeMetadatas());
        writer.setEdgeIDs(new Transformer<String, String>() {

            @Override
            public String transform(String s) {
                Pair<String> endpoints = currentGraph.getEndpoints(s);
                String[] endpointsArr =  new String[] {endpoints.getFirst(), endpoints.getSecond()};
                Arrays.sort(endpointsArr);
                return endpointsArr[0] + "_" + endpointsArr[1];
            }
        });
        boolean flag;
        try {
            writer.save(currentGraph, fileWriter);
            flag = true;

        } catch (IOException e1) {
            flag = false;
            JOptionPane.showMessageDialog(frame, "Unable to write graph file:" + e1.getMessage(), "Error", JOptionPane.WARNING_MESSAGE);

        }

    }

}
