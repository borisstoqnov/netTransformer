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

import edu.uci.ics.jung.algorithms.scoring.KStepMarkov;
import net.itransformers.topologyviewer.gui.GraphViewerPanel;
import net.itransformers.topologyviewer.gui.MyVisualizationViewer;
import net.itransformers.topologyviewer.gui.TopologyManagerFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

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
        frame1.setSize(800,600);
        frame1.getContentPane().setLayout(new BorderLayout());
        JTextArea  text   = new JTextArea();
        text.setEditable(true);


        KStepMarkov ranker = new KStepMarkov(viewerPanel.getCurrentGraph(),8);
        ranker.evaluate();
        StringBuffer sb = new StringBuffer();
        sb.append("Position, Node Name, Node Rank \n");
        Collection<String> vertices = viewerPanel.getCurrentGraph().getVertices();
        ArrayList<String> list = new ArrayList<String>(vertices);
        ArrayList<Double> scores = new ArrayList<Double>();

        final XYSeriesCollection dataset = new XYSeriesCollection();
        final XYSeries s1 = new XYSeries("KStep Markov Rankings");
       // Map<String,Double> scores = new HashMap<String, Double>();

        for (int i = 0; i < vertices.size(); i++) {
            String vertex = list.get(i);
            Double score = (Double) ranker.getVertexScore(vertex);
            scores.add(score);
        }
        Collections.sort(scores);
        for (int i = 0; i < vertices.size(); i++) {
            s1.add(i,scores.get(i));
            //sb.append(String.format("%d, %s, %s\n", i, vertex, score));

        }
        s1.getAutoSort();
        dataset.addSeries(s1);

        text.setText(sb.toString());
        final JFreeChart chart = ChartFactory.createXYLineChart(
                " KStep Markow Rankings",
                "Category",               // domain axis label
                "Value",                  // range axis label
                dataset,                  // data
                PlotOrientation.VERTICAL,
                false,                     // include legend
                true,
                true
        );
//
        final XYPlot plot = chart.getXYPlot();
        final NumberAxis domainAxis = new NumberAxis("Nodes");
        final NumberAxis rangeAxis = new NumberAxis("Node Rankings");
        plot.setDomainAxis(domainAxis);
        plot.setRangeAxis(rangeAxis);
        chart.setBackgroundPaint(Color.white);
        plot.setOutlinePaint(Color.black);
        final ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(400, 300));
        chartPanel.setMouseWheelEnabled(true);


        Container container = frame1.getContentPane();
        container.setLayout(new BorderLayout());

        JScrollPane scrollPane = new JScrollPane(text);
        scrollPane.setPreferredSize(new java.awt.Dimension(400, 300));
        container.add(chartPanel,BorderLayout.NORTH);
        container.add(scrollPane,BorderLayout.SOUTH);

        frame1.setVisible(true);

    }
}
