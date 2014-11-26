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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Date: 12-4-27
 * Time: 23:30
 * To change this template use File | Settings | File Templates.
 */
public class BGMapStatisticsMenuHandler implements ActionListener {

    private TopologyManagerFrame frame;


    public BGMapStatisticsMenuHandler(TopologyManagerFrame frame) throws HeadlessException {

        this.frame = frame;


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final GraphViewerPanel viewerPanel = (GraphViewerPanel) frame.getTabbedPane().getSelectedComponent();
        final MyVisualizationViewer vv = (MyVisualizationViewer) viewerPanel.getVisualizationViewer();

        JFrame frame1 = new JFrame(" Graph Statistics ");
        frame1.setSize(600, 400);
        frame1.getContentPane().setLayout(new BorderLayout());
        JTextPane text = new JTextPane();
        text.setEditable(true);

//        double diameterCurrent = DistanceStatistics.diameter(viewerPanel.getCurrentGraph());
//        double diameterEntire = DistanceStatistics.diameter(viewerPanel.getEntireGraph(), new UnweightedShortestPath(viewerPanel.getEntireGraph()), false);


        //   Transformer transformer =    DistanceStatistics.averageDistances(viewerPanel.getCurrentGraph(), new UnweightedShortestPath(viewerPanel.getCurrentGraph()));




        StringBuffer sb = new StringBuffer();

        Map<String,Integer> countryCounters = new HashMap<String,Integer>();
        Map<String,Long> countryIPv4Counters = new HashMap<String,Long>();
        Map<String,Long> countryIPv6Counters = new HashMap<String,Long>();

        for (Iterator iterator = viewerPanel.getCurrentGraph().getVertices().iterator(); iterator.hasNext();) {
            String vertex = (String) iterator.next();
            HashMap<String, String> vertexMetadata = (HashMap<String, String>) viewerPanel.getVertexParams(vertex);


            if (vertexMetadata != null) {
                String country = vertexMetadata.get("Country");


                if("BG".equals(country)){
                    if (countryCounters.get(country)!=null){
                        int counter = countryCounters.get(country);
                        countryCounters.put(country,++counter);
                    } else{
                        countryCounters.put(country,1);
                    }

                    Long IPv4AddressSpace = Long.valueOf(vertexMetadata.get("IPv4AddressSpace"));
                    if (countryIPv4Counters.get(country)!=null){
                        long counter = countryIPv4Counters.get(country);
                        countryIPv4Counters.put(country,counter+IPv4AddressSpace);
                    } else{
                        countryIPv4Counters.put(country, IPv4AddressSpace);
                    }

                    Long IPv6AddressSpace = Long.valueOf(vertexMetadata.get("IPv6AddressSpace"));
                    if (countryIPv6Counters.get(country)!=null){
                        long counter = countryIPv6Counters.get(country);
                        countryIPv6Counters.put(country,counter+IPv6AddressSpace);
                    } else{
                        countryIPv6Counters.put(country,IPv6AddressSpace);
                    }
                } else {
                    if (countryCounters.get("InternationalPeering")!=null){
                        int counter = countryCounters.get("InternationalPeering");
                        countryCounters.put("InternationalPeering",++counter);
                    } else{
                        countryCounters.put("InternationalPeering",1);
                    }

                }
            }

        }
        sb.append("\nCountry counters: \n");
        for (Map.Entry<String, Integer> stringIntegerEntry : countryCounters.entrySet()) {
            sb.append(stringIntegerEntry.getKey()+ ": "+stringIntegerEntry.getValue()+"\n");
        }
        sb.append("\nBG IPv4 Address Space counters: \n");

        for (Map.Entry<String, Long> stringLongEntry : countryIPv4Counters.entrySet()) {
            sb.append(stringLongEntry.getKey()+ ": "+stringLongEntry.getValue()+"\n");
        }
        sb.append("\nBG IPv6 Address Space counters: \n");

        for (Map.Entry<String, Long> stringLongEntry : countryIPv6Counters.entrySet()) {
            sb.append(stringLongEntry.getKey()+ ": "+stringLongEntry.getValue()+"\n");
        }
    text.setText(sb.toString());
    JScrollPane scrollPane = new JScrollPane(text);
    frame1.getContentPane().add("Center", scrollPane);

    frame1.setVisible(true);

}
}
