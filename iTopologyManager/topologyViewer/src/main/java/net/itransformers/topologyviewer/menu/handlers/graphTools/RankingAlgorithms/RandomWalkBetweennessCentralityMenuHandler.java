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

package net.itransformers.topologyviewer.menu.handlers.graphTools.RankingAlgorithms;

import edu.uci.ics.jung.algorithms.importance.RandomWalkBetweenness;
import edu.uci.ics.jung.algorithms.importance.Ranking;
import edu.uci.ics.jung.graph.UndirectedGraph;
import net.itransformers.topologyviewer.gui.GraphViewerPanel;
import net.itransformers.topologyviewer.gui.MyVisualizationViewer;
import net.itransformers.topologyviewer.gui.TopologyManagerFrame;
import net.itransformers.utils.XsltReport;

import javax.swing.*;
import javax.xml.transform.stream.StreamSource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * Date: 12-4-27
 * Time: 23:30
 * To change this template use File | Settings | File Templates.
 */
public class RandomWalkBetweennessCentralityMenuHandler implements ActionListener {

    private TopologyManagerFrame frame;


    public RandomWalkBetweennessCentralityMenuHandler(TopologyManagerFrame frame) throws HeadlessException {

        this.frame = frame;


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final GraphViewerPanel viewerPanel = (GraphViewerPanel) frame.getTabbedPane().getSelectedComponent();
        final MyVisualizationViewer vv = (MyVisualizationViewer) viewerPanel.getVisualizationViewer();

        JFrame frame1 = new JFrame(" Random Walk Betweenness Centrality Rankings ");
        frame1.setSize(400,300);
        frame1.getContentPane().setLayout(new BorderLayout());
        JTextPane  text   = new JTextPane();
        text.setEditable(false);
        text.setContentType("text/html");
        RandomWalkBetweenness ranker = new RandomWalkBetweenness((UndirectedGraph) viewerPanel.getCurrentGraph());

        ranker.setRemoveRankScoresOnFinalize(false);
        ranker.evaluate();
        StringBuffer sb = new StringBuffer();
        List<Ranking> rankingList  = ranker.getRankings();
        //sb.append("Position, Node Name, Node Rank \n");
//        final XYSeriesCollection dataset = new XYSeriesCollection();
//        final XYSeries s1 = new XYSeries("Random Walk Betweenness Centrality Rankings");

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<RandomWalkBetweennessCentralityRankings>");
        for(int i = 0; i<rankingList.size(); i++){
            sb.append("\n<entry>\n");
            sb.append("\t<position>"+i+"</position>\n");
            sb.append("\t<node>"+rankingList.get(i).getRanked()+"</node>\n");
            sb.append("\t<rank>"+rankingList.get(i)+"</rank>\n");
            sb.append("</entry>");
         //   s1.add(i,Double.parseDouble(rankingList.get(i).toString()));

        }
        sb.append("\n</RandomWalkBetweennessCentralityRankings>");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(sb.toString().getBytes());

        XsltReport testReport = new XsltReport(new File(frame.getPath(), "iTopologyManager/rightClick/conf/xslt/table_creator.xslt"), new StreamSource(inputStream));
        try {

            String report = testReport.singleTransformer().toString();
            text.setText(report);

        } catch (Exception ex) {
            testReport.handleException(ex);
        }

      //  dataset.addSeries(s1);



    //    dataset.addSeries(s1);
      //  text.setText(sb.toString());
//        Ranking betwennessMax = (Ranking) rankingList.get(0);
//        Ranking betwennessMin = (Ranking) rankingList.get(rankingList.size() - 1);
//
//        final JFreeChart chart = ChartFactory.createXYLineChart(
//                " Random Walk Betweenness Centrality Rankings",
//                "Category",               // domain axis label
//                "Value",                  // range axis label
//                dataset,                  // data
//                PlotOrientation.VERTICAL,
//                false,                     // include legend
//                false,
//                false
//        );
////
//        final XYPlot plot = chart.getXYPlot();
//        final NumberAxis domainAxis = new NumberAxis("Nodes");
//        final NumberAxis rangeAxis = new NumberAxis("Node Rankings");
//        plot.setDomainAxis(domainAxis);
//        plot.setRangeAxis(rangeAxis);
//        chart.setBackgroundPaint(Color.white);
//        plot.setOutlinePaint(Color.black);
//
//        final ChartPanel chartPanel = new ChartPanel(chart);
//
//        chartPanel.setPreferredSize(new java.awt.Dimension(400, 300));
//        chartPanel.setMouseWheelEnabled(false);


        Container container = frame1.getContentPane();
        container.setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(text);
        scrollPane.setPreferredSize(new java.awt.Dimension(400, 300));

//        JScrollPane scrollPane2 = new JScrollPane(chartPanel);

//        container.add(scrollPane2,BorderLayout.NORTH);
        container.add(scrollPane);


        frame1.setVisible(true);

    }


}
