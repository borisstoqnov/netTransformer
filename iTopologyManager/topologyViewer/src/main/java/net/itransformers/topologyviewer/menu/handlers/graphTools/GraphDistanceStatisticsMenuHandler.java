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

package net.itransformers.topologyviewer.menu.handlers.graphTools;

import edu.uci.ics.jung.algorithms.shortestpath.DistanceStatistics;
import edu.uci.ics.jung.algorithms.shortestpath.UnweightedShortestPath;
import net.itransformers.topologyviewer.gui.GraphViewerPanel;
import net.itransformers.topologyviewer.gui.MyVisualizationViewer;
import net.itransformers.topologyviewer.gui.TopologyManagerFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * Date: 12-4-27
 * Time: 23:30
 * To change this template use File | Settings | File Templates.
 */
public class GraphDistanceStatisticsMenuHandler implements ActionListener {

    private TopologyManagerFrame frame;


    public GraphDistanceStatisticsMenuHandler(TopologyManagerFrame frame) throws HeadlessException {

        this.frame = frame;


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final GraphViewerPanel viewerPanel = (GraphViewerPanel) frame.getTabbedPane().getSelectedComponent();
        final MyVisualizationViewer vv = (MyVisualizationViewer) viewerPanel.getVisualizationViewer();

        JFrame frame1 = new JFrame(" Diameter ");
        frame1.setSize(600,400);
        frame1.getContentPane().setLayout(new BorderLayout());
        JTextPane  text   = new JTextPane();
        text.setEditable(true);

        double diameterCurrent =  DistanceStatistics.diameter(viewerPanel.getCurrentGraph());
        double diameterEntire =  DistanceStatistics.diameter(viewerPanel.getEntireGraph(), new UnweightedShortestPath(viewerPanel.getEntireGraph()),false);


   //   Transformer transformer =    DistanceStatistics.averageDistances(viewerPanel.getCurrentGraph(), new UnweightedShortestPath(viewerPanel.getCurrentGraph()));

        StringBuffer sb = new StringBuffer();
        sb.append(String.format("%s: %2f \n", "Current Graph Diameter", diameterCurrent));
        sb.append(String.format("%s: %2f \n", "Entire Graph Diameter", diameterEntire));
        //sb.append(transformer.toString());

        //  sb.append(String.format("%s: %2f \n", "AverageDistances",DistanceStatistics.averageDistances(viewerPanel.getCurrentGraph())));


        text.setText(sb.toString());
        JScrollPane scrollPane = new JScrollPane(text);
        frame1.getContentPane().add("Center",scrollPane);

        frame1.setVisible(true);

    }
}
