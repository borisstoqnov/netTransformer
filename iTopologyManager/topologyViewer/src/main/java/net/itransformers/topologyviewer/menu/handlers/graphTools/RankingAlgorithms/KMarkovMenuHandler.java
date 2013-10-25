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

import edu.uci.ics.jung.algorithms.scoring.KStepMarkov;
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
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * Date: 12-4-27
 * Time: 23:30
 * To change this template use File | Settings | File Templates.
 */
public class KMarkovMenuHandler implements ActionListener {

    private TopologyManagerFrame frame;


    public KMarkovMenuHandler(TopologyManagerFrame frame) throws HeadlessException {

        this.frame = frame;


    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final GraphViewerPanel viewerPanel = (GraphViewerPanel) frame.getTabbedPane().getSelectedComponent();
        final MyVisualizationViewer vv = (MyVisualizationViewer) viewerPanel.getVisualizationViewer();

        JFrame frame1 = new JFrame(" KStep Markov Rankings ");
        frame1.setSize(400,300);
        frame1.getContentPane().setLayout(new BorderLayout());
        JTextPane  text   = new JTextPane();

        text.setEditable(false);
        text.setContentType("text/html");


        KStepMarkov ranker = new KStepMarkov(viewerPanel.getCurrentGraph(),8);
        ranker.evaluate();
        StringBuffer sb = new StringBuffer();
        //sb.append("Position, Node Name, Node Rank \n");
        Collection<String> vertices = viewerPanel.getCurrentGraph().getVertices();
        ArrayList<String> list = new ArrayList<String>(vertices);
        ArrayList<Double> scores = new ArrayList<Double>();

        //final XYSeriesCollection dataset = new XYSeriesCollection();
       // final XYSeries s1 = new XYSeries("KStep Markov Rankings");
       // Map<String,Double> scores = new HashMap<String, Double>();

        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        sb.append("\n<KStepMarkovRankings>");
        for(int i = 0; i<vertices.size(); i++){
            sb.append("\n<entry>\n");
            sb.append("\t<position>"+i+"</position>\n");
            sb.append("\t<node>"+list.get(i)+"</node>\n");
            sb.append("\t<rank>"+(Double)ranker.getVertexScore(list.get(i))+"</rank>\n");
            sb.append("</entry>");
        }

        sb.append("\n</KStepMarkovRankings>");
        System.out.println(sb.toString());
        ByteArrayInputStream inputStream = new ByteArrayInputStream(sb.toString().getBytes());

        XsltReport testReport = new XsltReport(new File(frame.getPath(), "iTopologyManager/rightClick/conf/xslt/table_creator.xslt"), new StreamSource(inputStream));
        try {

            String report = testReport.singleTransformer().toString();
            text.setText(report);

        } catch (Exception ex) {
            testReport.handleException(ex);
        }
//        for (int i = 0; i < vertices.size(); i++) {
//            String vertex = list.get(i);
//            Double score = (Double) ranker.getVertexScore(vertex);
//            scores.add(score);
//        }
//        Collections.sort(scores);
//        for (int i = 0; i < vertices.size(); i++) {
//            s1.add(i,scores.get(i));
//            //sb.append(String.format("%d, %s, %s\n", i, vertex, score));
//
//        }
//        s1.getAutoSort();
//        dataset.addSeries(s1);

        //text.setText(sb.toString());
//        final JFreeChart chart = ChartFactory.createXYLineChart(
//                " KStep Markow Rankings",
//                "Category",               // domain axis label
//                "Value",                  // range axis label
//                dataset,                  // data
//                PlotOrientation.VERTICAL,
//                false,                     // include legend
//                true,
//                true
//        );
////
//        final XYPlot plot = chart.getXYPlot();
//        final NumberAxis domainAxis = new NumberAxis("Nodes");
//        final NumberAxis rangeAxis = new NumberAxis("Node Rankings");
//        plot.setDomainAxis(domainAxis);
//        plot.setRangeAxis(rangeAxis);
//        chart.setBackgroundPaint(Color.white);
//        plot.setOutlinePaint(Color.black);
//        final ChartPanel chartPanel = new ChartPanel(chart);
//        chartPanel.setPreferredSize(new java.awt.Dimension(400, 300));
//        chartPanel.setMouseWheelEnabled(true);


        JScrollPane scrollPane = new JScrollPane(text);
        frame1.getContentPane().add("Center",scrollPane);

        frame1.setVisible(true);

    }
}
