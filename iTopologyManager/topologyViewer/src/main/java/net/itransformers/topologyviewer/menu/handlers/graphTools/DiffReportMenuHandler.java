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

package net.itransformers.topologyviewer.menu.handlers.graphTools;

import net.itransformers.topologyviewer.gui.GraphViewerPanel;
import net.itransformers.topologyviewer.gui.MyVisualizationViewer;
import net.itransformers.topologyviewer.gui.TopologyManagerFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * Date: 12-4-27
 * Time: 23:30
 * To change this template use File | Settings | File Templates.
 */
public class DiffReportMenuHandler implements ActionListener {

    private TopologyManagerFrame frame;


    public DiffReportMenuHandler(TopologyManagerFrame frame) throws HeadlessException {

        this.frame = frame;


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final GraphViewerPanel viewerPanel = (GraphViewerPanel) frame.getTabbedPane().getSelectedComponent();
        final MyVisualizationViewer vv = (MyVisualizationViewer) viewerPanel.getVisualizationViewer();

        JFrame frame1 = new JFrame(" Graph Diff Statistics ");
        frame1.setSize(600, 400);
        frame1.getContentPane().setLayout(new BorderLayout());
        JTextPane text = new JTextPane();
        text.setEditable(true);
        text.setContentType("text/html");


//        double diameterCurrent = DistanceStatistics.diameter(viewerPanel.getCurrentGraph());
//        double diameterEntire = DistanceStatistics.diameter(viewerPanel.getEntireGraph(), new UnweightedShortestPath(viewerPanel.getEntireGraph()), false);


        //   Transformer transformer =    DistanceStatistics.averageDistances(viewerPanel.getCurrentGraph(), new UnweightedShortestPath(viewerPanel.getCurrentGraph()));




        StringBuffer sb = new StringBuffer();
        sb.append("<html>");
        Set<String> addCounter = new HashSet<String>();
        Set<String> changedCounter = new HashSet<String>();
        Set<String> removedCounter = new HashSet<String>();
        Map<String,String> changes = new HashMap<String, String>();

        for (Iterator iterator = viewerPanel.getCurrentGraph().getVertices().iterator(); iterator.hasNext();) {
            String vertex = (String) iterator.next();
            HashMap<String, String> vertexMetadata = (HashMap<String, String>) viewerPanel.getVertexParams(vertex);
            if (vertexMetadata != null) {

                String diff = vertexMetadata.get("diff");
                String diffs = vertexMetadata.get("diffs");
                    if("ADDED".equals(diff)){
                        addCounter.add(vertex);
                    } else if("REMOVED".equals(diff)){
                        removedCounter.add(vertex);
                    } else if("YES".equals(diff)){
                        changedCounter.add(vertex);
                        String asName = vertexMetadata.get("ASName");
                        changes.put(vertex+" "+asName,diffs);
                    }
                }

        }

        sb.append("<p><b>Diff counters</b>");
        sb.append("<p><b>Nodes added:</b> "+addCounter.size());
        sb.append("<p><b><br>Nodes removed:</b> "+removedCounter.size());
        sb.append("<p><b><br>Nodes changed:</b> "+changedCounter.size());

        sb.append("<p><b>Changes per node</b>");
        for (String s : changes.keySet()) {
            sb.append("<b>Node: "+s+"</b>");
            sb.append(changes.get(s)+"<br>");
        }
        sb.append("</html>");
        text.setText(sb.toString());
        JScrollPane scrollPane = new JScrollPane(text);

        frame1.getContentPane().add("Center", scrollPane);

        frame1.setVisible(true);

}
}
