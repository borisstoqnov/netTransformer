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

package net.itransformers.topologyviewer.rightclick.impl;

import net.itransformers.topologyviewer.fulfilmentfactory.FulfilmentAdapter;
import net.itransformers.topologyviewer.fulfilmentfactory.FulfilmentAdapterFactory;
import net.itransformers.topologyviewer.parameterfactory.ParameterFactoryBuilder;
import net.itransformers.resourcemanager.ResourceManager;
import net.itransformers.resourcemanager.config.ResourceType;
import net.itransformers.topologyviewer.rightclick.RightClickHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class CmdRightClickHandler implements RightClickHandler {
//    public <G> void handleRightClick(JFrame parent, String v,
//                                     Map<String, String> graphMLParams,
//                                     Map<String, String> rightClickParams,
//                                     String deviceDataXmlFileName) throws Exception {
//        Map<String,String> connParams;
//        connParams = ResourceResolver.getResource(parent, graphMLParams, rightClickParams.get("resource"));
//        String deviceType = graphMLParams.get("deviceType");
//        ParameterFactoryBuilder builder = new ParameterFactoryBuilder(rightClickParams.get("parameterFactoryXml"));
//        ParameterFactory factory = builder.buildParameterFactory(deviceType);
//        Map<String, Object> context = new HashMap<String, Object>();
//        context.put("graphml", graphMLParams);
//        context.put("resource", connParams);
//        context.put("rightClickParams", rightClickParams);
//        context.put("xmlFileName",deviceDataXmlFileName);
//        Map<String,String> params = factory.createParameters(context);
//        System.out.println(params);
//
//
//
//    }
    public <G> void handleRightClick(JFrame parent, String v,
                                     Map<String, String> graphMLParams,
                                     Map<String, String> rightClickParams,
                                     File projectPath,
                                     File deviceDataXmlFileName) throws Exception {
        ParameterFactoryBuilder builder = new ParameterFactoryBuilder(new File(projectPath,rightClickParams.get("parameterFactoryXml")));
        Map<String, Object> context = new HashMap<String, Object>();
        context.put("graphml", graphMLParams);
        context.put("rightClickParams", rightClickParams);
        context.put("xmlFileName", deviceDataXmlFileName.toURI().toString());
        context.put("parentFrame", parent);
        ResourceManager resourceManager = new ResourceManager(new File(projectPath, rightClickParams.get("resource")));
        ResourceType resource = resourceManager.findResource(graphMLParams);
        context.put("connection-params", ResourceResolver.getConnectionParams(resource, graphMLParams));
        FulfilmentAdapterFactory factory = new FulfilmentAdapterFactory(projectPath, new File(projectPath, rightClickParams.get("fulfilment-factory")),
                builder,resource);
        String[] factoryNames = factory.getFulfilmentFactoryNamesForResource(resource.getName());
        createGUI(v,context, factoryNames, factory);
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

}
