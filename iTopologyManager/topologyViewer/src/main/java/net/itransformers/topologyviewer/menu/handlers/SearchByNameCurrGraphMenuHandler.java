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

package net.itransformers.topologyviewer.menu.handlers;

import edu.uci.ics.jung.visualization.VisualizationViewer;
import net.itransformers.topologyviewer.gui.GraphViewerPanel;
import net.itransformers.topologyviewer.gui.TopologyManagerFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
        String [] test = vertices.toArray(new String[0]);
        JFrame frame1 = new JFrame("Find Node");

        Container contentPane = frame1.getContentPane();

        Arrays.sort(test);
        final JComboBox jcb = new JComboBox(test);
        jcb.setEditable(true);
        jcb.setVisible(true);
        contentPane.add(jcb, BorderLayout.NORTH);
        final String[] vertex = {null};


        final JTextArea textArea = new JTextArea();
        JScrollPane scrollPane = new JScrollPane(textArea);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                vertex[0] = (String) jcb.getSelectedItem();
                textArea.append("Selected: " + vertex[0]+"\n");
                if (viewerPanel.FindNodeByIDCurrentGraph(vertex[0])){
                    viewerPanel.SetPickedState(vertex[0]);
                    viewerPanel.Animator(vertex[0]);
                }   else {
                    JOptionPane.showMessageDialog(frame, "Can not find node:" + vertex[0], "Error", JOptionPane.ERROR_MESSAGE);
                }
                Map<String, String> graphMLParams =  viewerPanel.getVertexParams(vertex[0]);
                for (Map.Entry<String, String> entry : graphMLParams.entrySet()) {
                    textArea.append("Key: "+entry.getKey()+", "+"Value: "+entry.getValue()+"\n");
                }

            }
        };
        jcb.addActionListener(actionListener);

      //  frame1.add(jcb);
        frame1.setSize(300, 200);
        frame1.setVisible(true);

      //  String vertex = (String) JOptionPane.showInputDialog(frame,jcb, "Choose Node Name", JOptionPane.QUESTION_MESSAGE);


    }
}
