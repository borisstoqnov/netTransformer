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

package net.itransformers.topologyviewer.menu.handlers.graphTools.RankingAlgorithms;

import edu.uci.ics.jung.algorithms.importance.BetweennessCentrality;
import edu.uci.ics.jung.algorithms.importance.Ranking;
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

public class BetweennessCentralityMenuHandler implements ActionListener {

    private TopologyManagerFrame frame;


    public BetweennessCentralityMenuHandler(TopologyManagerFrame frame) throws HeadlessException {

        this.frame = frame;


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final GraphViewerPanel viewerPanel = (GraphViewerPanel) frame.getTabbedPane().getSelectedComponent();
        final MyVisualizationViewer vv = (MyVisualizationViewer) viewerPanel.getVisualizationViewer();

        JFrame frame1 = new JFrame("Betweenness Centrality Rankings ");
        frame1.setSize(new java.awt.Dimension(400, 300));
        frame1.getContentPane().setLayout(new BorderLayout());

        JTextPane text = new JTextPane();
        text.setEditable(false);
        text.setContentType("text/html");

        frame1.dispose();
        BetweennessCentrality ranker = new BetweennessCentrality(viewerPanel.getCurrentGraph(), true, false);

        ranker.setRemoveRankScoresOnFinalize(false);
        ranker.evaluate();
        StringBuffer sb = new StringBuffer();
        List<Ranking> rankingList = ranker.getRankings();

//        final XYSeries s1 = new XYSeries(" Betweenness Centrality Rankings");
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("<BetweennessCentralityRankings>");
        for (int i = 0; i < rankingList.size(); i++) {
            // sb.append(String.format("<%d, %s, %s\n/>", i, rankingList.get(i).getRanked(), rankingList.get(i)));
            sb.append("\n<entry>\n");
            sb.append("\t<position>" + i + "</position>\n");
            sb.append("\t<node>" + rankingList.get(i).getRanked() + "</node>\n");
            sb.append("\t<rank>" + rankingList.get(i) + "</rank>\n");
            sb.append("</entry>");
//            s1.add(i, Double.parseDouble(rankingList.get(i).toString()));

        }
        sb.append("</BetweennessCentralityRankings>");
        ByteArrayInputStream inputStream = new ByteArrayInputStream(sb.toString().getBytes());

        XsltReport testReport = new XsltReport(new File(frame.getPath(), "iTopologyManager/rightClick/conf/xslt/table_creator.xslt"), new StreamSource(inputStream));
        try {

            String report = testReport.singleTransformer().toString();
            text.setText(report);

        } catch (Exception ex) {
            testReport.handleException(ex);
        }

        //      dataset.addSeries(s1);

//        final JFreeChart chart = ChartFactory.createBarChart(
//                null,
//                "Category",               // domain axis label
//                "Value",                  // range axis label
//                (CategoryDataset) dataset,                  // data
//                PlotOrientation.VERTICAL,
//                false,                     // include legend
//                true,
//                false
//        );
//
//        final XYPlot plot = chart.getXYPlot();
//        final NumberAxis domainAxis = new NumberAxis("Node Ids");
//        final NumberAxis rangeAxis = new NumberAxis("Ranking Scores");
//        plot.setDomainAxis(domainAxis);
//        plot.setRangeAxis(rangeAxis);
//        chart.setBackgroundPaint(Color.white);
//
//        plot.setOutlinePaint(Color.black);
//        final ChartPanel chartPanel = new ChartPanel(chart);
//        chartPanel.setPreferredSize(new java.awt.Dimension(400, 300));
//        chartPanel.setMouseWheelEnabled(true);

//        Container container = frame1.getContentPane();
//        container.setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(text);
        frame1.getContentPane().add("Center", scrollPane);

//        JScrollPane scrollPane2 = new JScrollPane(chartPanel);
        // scrollPane1.setPreferredSize(new java.awt.Dimension(400, 300));

//        container.add(scrollPane2,BorderLayout.NORTH);
//        container.add(scrollPane1,BorderLayout.SOUTH);


        frame1.setVisible(true);

    }
}
