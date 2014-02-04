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

package net.itransformers.topologyviewer.rightclick.impl;

import net.itransformers.topologyviewer.fulfilmentfactory.FulfilmentAdapter;
import net.itransformers.topologyviewer.fulfilmentfactory.FulfilmentAdapterFactory;
import net.itransformers.topologyviewer.gui.GraphViewerPanel;
import net.itransformers.topologyviewer.gui.TopologyManagerFrame;
import net.itransformers.topologyviewer.parameterfactory.ParameterFactoryBuilder;
import net.itransformers.resourcemanager.ResourceManager;
import net.itransformers.resourcemanager.config.ResourceType;
import net.itransformers.topologyviewer.gui.MyVisualizationViewer;
import net.itransformers.topologyviewer.rightclick.RightClickHandler;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.io.GraphMLMetadata;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class ShortestPathProvisioning implements RightClickHandler {
    public <G> void  handleRightClick(JFrame parent, String v,
                                     Map<String, String> graphMLParams,
                                     Map<String, String> rightClickParams,
                                     File projectPath,
                                     java.io.File deviceDataXmlFileName) throws Exception {

        TopologyManagerFrame viewer = (TopologyManagerFrame) parent;
        final GraphViewerPanel viewerPanel = (GraphViewerPanel) viewer.getTabbedPane().getSelectedComponent();
        final MyVisualizationViewer vv = (MyVisualizationViewer) viewerPanel.getVisualizationViewer();
        Object [] test = viewerPanel.getCurrentGraph().getVertices().toArray();
        Arrays.sort(test);
        final String mTo = (String) JOptionPane.showInputDialog(parent, "Choose B Node", "B Node", JOptionPane.PLAIN_MESSAGE, null, test, test[0]);

        final Graph<String, String> mGraph = viewerPanel.getCurrentGraph();
        final Set<String> mPred = viewerPanel.findShortest(v, mTo, mGraph);
        if(mPred == null) {
            JOptionPane.showMessageDialog(parent,String.format("Shortest path between %s,%s is not found",v,mTo),"Message",JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        ParameterFactoryBuilder builder = new ParameterFactoryBuilder(new File(projectPath,rightClickParams.get("parameterFactoryXml")));

        ResourceManager resourceManager = new ResourceManager(new File(projectPath, rightClickParams.get("resource")));
        Map<String, Map<String, GraphMLMetadata<String>>> vertexMetadatas = viewer.getCurrentGraphViewerManager().getVertexMetadatas();
//
        final Layout<String,String> layout = vv.getGraphLayout();
//                for all edges, paint edges that are in shortest path
        for (final String edge : layout.getGraph().getEdges()) {
            Pair<String> endpoints = mGraph.getEndpoints(edge);
            String v1= endpoints.getFirst()	;

            String v2= endpoints.getSecond() ;
            if (!v1.equals(v2) && mPred.contains(v1) && mPred.contains(v2)){
                vv.setEdgeStroke(edge, new BasicStroke(4f));

            }
        }
        viewerPanel.repaint();

        Iterator it = mPred.iterator();
        while (it.hasNext()){
            Object element = it.next();
            viewerPanel.Animator(element.toString());
            viewerPanel.SetPickedState(element.toString());
            Map<String, Object> context = new HashMap<String, Object>();
            Map<String, String> graphMLParams1 = getParams(element.toString(), vertexMetadatas);

//            context.put("graphml", graphMLParams1);
            context.put("rightClickParams", rightClickParams);
            context.put("xmlFileName", deviceDataXmlFileName.toURI().toString());
            context.put("parentFrame", parent);

            ResourceType resource = resourceManager.findResource(graphMLParams1);
            context.put("connection-params", ResourceResolver.getConnectionParams(resource, graphMLParams1));
            FulfilmentAdapterFactory factory = new FulfilmentAdapterFactory(projectPath, new File (projectPath, rightClickParams.get("fulfilment-factory")),
                    builder,resource);
            String[] factoryNames = factory.getFulfilmentFactoryNamesForResource(resource.getName());
            createGUI(element.toString(),context, factoryNames, factory);

        }



        }
    private void createGUI(String v, final Map<String, Object> context, String[] factoryNames, final FulfilmentAdapterFactory factory) {
        final JFrame parent = context == null ? null : context.get("parentFrame") == null? null : (JFrame) context.get("parentFrame");
        final JDialog jDialog = new JDialog(parent, "Fulfill "+v,true);

        jDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                jDialog.dispose();
            }
        });
        final JTextArea area = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(area);

        jDialog.getContentPane().add(BorderLayout.CENTER, scrollPane);

        JPanel buttonPanel = new JPanel();
        final JButton okButton = new JButton("OK");
        buttonPanel.add(okButton);
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jDialog.dispose();
            }
        });
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jDialog.dispose();
            }
        });
        buttonPanel.add(cancelButton);

        jDialog.add(BorderLayout.SOUTH, buttonPanel);
        String[] items = new String[factoryNames.length+1];
        items[0] = "";
        System.arraycopy(factoryNames,0,items,1,factoryNames.length);
        JComboBox fulfilmentFactories = new JComboBox(items);
        fulfilmentFactories.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent e) {
                String fulfilmentName = (String) ((JComboBox)e.getSource()).getSelectedItem();
                if (fulfilmentName.equals("")) return; // do nothing
                try {
                    Logger logger = Logger.getLogger("factoryLogger");
                    logger.addHandler(new Handler(){

                        @Override
                        public void publish(final LogRecord record) {
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    area.append(record.getMessage());
                                    area.append("\n");
                                }
                            });

                        }

                        @Override
                        public void flush() {}

                        @Override
                        public void close() throws SecurityException {}
                    });
                    final FulfilmentAdapter fulfilment = factory.createFulfilmentAdapter(fulfilmentName, context, logger);
                    (new Thread() {
                        @Override
                        public void run() {
                            try {
                                fulfilment.fulfil();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                                JOptionPane.showMessageDialog(parent,"Error: "+e1.getMessage());
                            }
                        }
                    }).start();

                } catch (Exception e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(parent,"Error: "+e1.getMessage());
                }
            }
        });
        JPanel northPanel = new JPanel();
        northPanel.setLayout(new FlowLayout());
        northPanel.add(fulfilmentFactories);
        jDialog.add(BorderLayout.NORTH, northPanel);
        jDialog.setPreferredSize(new Dimension(300,300));
        jDialog.pack();
        jDialog.setVisible(true);


    }
    private static <G> Map<String, String> getParams(String v, Map<String, Map<String, GraphMLMetadata<String>>> vertexMetadatas) {
        HashMap<String, String> params = new HashMap<String, String>();
        Collection<Map<String, GraphMLMetadata<String>>> test = vertexMetadatas.values();
        for (Map<String, GraphMLMetadata<String>> stringGraphMLMetadataMap : test) {
            for (String key : stringGraphMLMetadataMap.keySet()){

                String value = stringGraphMLMetadataMap.get(key).transformer.transform(v);
                if (value == null) continue;
                if (!params.containsKey(key)){
                    params.put(key,value);
                } else{
                    value = value.concat(", ").concat(params.get(key));
                    params.put(key,value);
                }
            }
        }
//        for (Collection<String,GraphMLMetadata<String>> vertexMetadata : vertexMetadatas.values()) {
//            for (String key : vertexMetadata.keySet()){
//                String value = vertexMetadata.get(key).transformer.transform(v);
//                if (value == null) continue;
//                if (!params.containsKey(key)){
//                    params.put(key,value);
//                } else{
//                    value = value.concat(", ").concat(params.get(key));
//                    params.put(key,value);
//                }
//            }
//        }
        return params;
    }
}
