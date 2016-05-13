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
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 * Date: 12-4-27
 * Time: 23:30
 * To change this template use File | Settings | File Templates.
 */
public class SearchByNamePatternCurrGraphMenuHandler implements ActionListener {

    private TopologyManagerFrame frame;

    public SearchByNamePatternCurrGraphMenuHandler(TopologyManagerFrame frame) throws HeadlessException {

        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final GraphViewerPanel viewerPanel = (GraphViewerPanel) frame.getTabbedPane().getSelectedComponent();
        final JFrame frame1 = new JFrame("Find Node");

        Container contentPane = frame1.getContentPane();

        final JTextField jt = new JTextField();
        JButton jb = new JButton("Search");
        JPanel jp = new JPanel();
        jp.setLayout(new BorderLayout());
        jp.add(jt, BorderLayout.CENTER);
        jp.add(jb, BorderLayout.EAST);

        contentPane.add(jp, BorderLayout.NORTH);

        final ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                String vertexNamePattern = jt.getText();
                Pattern p = Pattern.compile(vertexNamePattern);
                Collection verteces = viewerPanel.getCurrentGraph().getVertices();
                Set<String> matchedVerteces = new HashSet<>();
                for (Object vertex : verteces){
                    if (p.matcher((CharSequence) vertex).matches()) {
                        matchedVerteces.add((String) vertex);
                    }
                }
                viewerPanel.SetPickedStates(matchedVerteces);
                if (matchedVerteces.iterator().hasNext()) {
                    viewerPanel.Animator(matchedVerteces.iterator().next());
                } else {
                    JOptionPane.showMessageDialog(frame, "No nodes are found", "Finding nodes", JOptionPane.OK_OPTION);
                }
            }
        };
        jb.addActionListener(actionListener);

        frame1.setSize(400, 300);
        frame1.setVisible(true);
    }
}
