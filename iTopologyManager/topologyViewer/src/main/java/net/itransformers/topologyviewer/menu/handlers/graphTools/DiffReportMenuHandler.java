/*
 * DiffReportMenuHandler.java
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

package net.itransformers.topologyviewer.menu.handlers.graphTools;

import net.itransformers.topologyviewer.gui.GraphViewerPanel;
import net.itransformers.topologyviewer.gui.MyVisualizationViewer;
import net.itransformers.topologyviewer.gui.TopologyManagerFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;


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


//   double diameterCurrent = DistanceStatistics.diameter(viewerPanel.getCurrentGraph());
//   double diameterEntire = DistanceStatistics.diameter(viewerPanel.getEntireGraph(), new UnweightedShortestPath(viewerPanel.getEntireGraph()), false);
//   Transformer transformer =    DistanceStatistics.averageDistances(viewerPanel.getCurrentGraph(), new UnweightedShortestPath(viewerPanel.getCurrentGraph()));




        StringBuffer sb = new StringBuffer();
        sb.append("<html>");
        Map<String,String> addCounter = new HashMap<String, String>();
        Map<String,String> changedCounter = new HashMap<String, String>();
        Map<String,String> removedCounter = new HashMap<String, String>();
        Map<String,String> changes = new HashMap<String, String>();

        for (Iterator iterator = viewerPanel.getCurrentGraph().getVertices().iterator(); iterator.hasNext();) {
            String vertex = (String) iterator.next();
            HashMap<String, String> vertexMetadata = (HashMap<String, String>) viewerPanel.getVertexParams(vertex);
            if (vertexMetadata != null) {

                String diff = vertexMetadata.get("diff");
                String diffs = vertexMetadata.get("diffs");
                String asName = vertexMetadata.get("ASName");
                String country = vertexMetadata.get("Country");
                if ("BG".equals(country))
                    if("ADDED".equals(diff)){
                            addCounter.put(vertex,asName);
                        } else if("REMOVED".equals(diff)){
                            removedCounter.put(vertex,asName);
                        } else if("YES".equals(diff)){
                            changedCounter.put(vertex,asName);
                            changes.put(vertex+" "+asName,diffs);
                        }
                    }

        }

        sb.append("<p><b>Diff counters</b></br>");
        sb.append("<p><b>Node births:</b> "+addCounter.size());
        for( String node : addCounter.keySet()){
            sb.append("<p>"+node+": "+addCounter.get(node)+"</p>");
        }
        sb.append("<p><b><br>Nodes deads:</b> "+removedCounter.size());
        for( String node : removedCounter.keySet()){
            sb.append("<p>"+node+": "+removedCounter.get(node)+"</p>");

        }

        sb.append("<p><b><br>Nodes changed:</b> "+changedCounter.size());

        sb.append("<p><b>Changes per node</b>");
        for (String s : changes.keySet()) {
            sb.append("<p><b><br>Node: "+s+"</b>");
            sb.append(changes.get(s));
            sb.append("</p>");
        }
        sb.append("</html>");
        text.setText(sb.toString());
        JScrollPane scrollPane = new JScrollPane(text);

        frame1.getContentPane().add("Center", scrollPane);

        frame1.setVisible(true);

}
}
