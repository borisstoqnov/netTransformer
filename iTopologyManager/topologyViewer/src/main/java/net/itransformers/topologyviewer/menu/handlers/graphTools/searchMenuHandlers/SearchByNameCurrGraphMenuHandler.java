/*
 * SearchByNameCurrGraphMenuHandler.java
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

package net.itransformers.topologyviewer.menu.handlers.graphTools.searchMenuHandlers;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import net.itransformers.topologyviewer.gui.GraphViewerPanel;
import net.itransformers.topologyviewer.gui.TopologyManagerFrame;
import net.itransformers.utils.XsltReport;

import javax.swing.*;
import javax.xml.transform.stream.StreamSource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Date: 12-4-27
 * Time: 23:30
 * To change this template use File | Settings | File Templates.
 */
public class SearchByNameCurrGraphMenuHandler implements ActionListener {

    private TopologyManagerFrame frame;

    public SearchByNameCurrGraphMenuHandler(TopologyManagerFrame frame) throws HeadlessException {

        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final GraphViewerPanel viewerPanel = (GraphViewerPanel) frame.getTabbedPane().getSelectedComponent();
        final VisualizationViewer vv = viewerPanel.getVisualizationViewer();
        Collection<String> vertices = viewerPanel.getCurrentGraph().getVertices();
        String[] test = vertices.toArray(new String[0]);
        final JFrame frame1 = new JFrame("Find Node");

        Container contentPane = frame1.getContentPane();

        Arrays.sort(test);
        final JComboBox jcb = new JComboBox(test);
        jcb.setEditable(true);
        jcb.setVisible(true);
        contentPane.add(jcb, BorderLayout.NORTH);
        final String[] vertex = {null};


        //final JTextArea textArea = new JTextArea();
        final JTextPane textArea = new JTextPane();
        textArea.setEditable(false);
        textArea.setContentType("text/html");

        JScrollPane scrollPane = new JScrollPane(textArea);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        final ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                vertex[0] = (String) jcb.getSelectedItem();
                if (viewerPanel.FindNodeByIDCurrentGraph(vertex[0])) {
                    viewerPanel.SetPickedState(vertex[0]);
                    viewerPanel.Animator(vertex[0]);

                    Map<String, String> graphMLParams = viewerPanel.getVertexParams(vertex[0]);
                    final StringBuffer sb = new StringBuffer();
                    sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");

                    sb.append("<findNodeinCurrentGraph>");

                    for (Map.Entry<String, String> entry : graphMLParams.entrySet()) {
                        //    textArea.append("Key: "+entry.getKey()+", "+"Value: "+entry.getValue()+"\n");
                        if(!entry.getKey().equals("nodeInfo")){

                            sb.append("\n<entry>\n");
                            sb.append("\t<key>" + entry.getKey() + "</key>" + "\n");
                            if (!entry.getValue().contains("CDATA")) {
                                sb.append("\t<value><![CDATA[" + entry.getValue() + "]]></value>" + "\n");
                            } else {
                                sb.append("\t<value>" + entry.getValue() + "</value>" + "\n");

                            }
                            sb.append("</entry>");

                        }


                    }
                    sb.append("</findNodeinCurrentGraph>");
                    //  System.out.println(sb.toString());
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(sb.toString().getBytes());

                    XsltReport testReport = new XsltReport(new File(frame.getPath(), "iTopologyManager/rightClick/conf/xslt/table_creator.xslt"), new StreamSource(inputStream));
                    try {

                        String report = testReport.singleTransformer().toString();
                        textArea.setText(report);

                    } catch (Exception ex) {
                        testReport.handleException(ex);
                    }
                } else {
                    textArea.setText("Node with id " + vertex[0] + " can't be found in the current graph!");
                }


            }
        };
        jcb.addActionListener(actionListener);

        frame1.setSize(400, 300);
        frame1.setVisible(true);


    }
}
