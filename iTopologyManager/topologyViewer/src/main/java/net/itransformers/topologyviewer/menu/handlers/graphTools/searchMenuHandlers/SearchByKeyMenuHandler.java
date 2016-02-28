/*
 * SearchByKeyMenuHandler.java
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

import net.itransformers.topologyviewer.gui.GraphViewerPanel;
import net.itransformers.topologyviewer.gui.TopologyManagerFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * Date: 12-4-27
 * Time: 23:30
 * To change this template use File | Settings | File Templates.
 */
public class SearchByKeyMenuHandler implements ActionListener {

    private TopologyManagerFrame frame;

    public SearchByKeyMenuHandler(TopologyManagerFrame frame) throws HeadlessException {

        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

//                Map<String, Map<String, GraphMLMetadata<G>>> test1 = graphmlLoader();

        String key = JOptionPane.showInputDialog(frame, "Choose Key Name", JOptionPane.QUESTION_MESSAGE);
        String value = JOptionPane.showInputDialog(frame, "Enter Key Value", "Value", JOptionPane.QUESTION_MESSAGE);

        GraphViewerPanel viewerPanel = (GraphViewerPanel) frame.getTabbedPane().getSelectedComponent();
        Set<String> foundVertexes = viewerPanel.FindNodeByKey(key, value);
        if (!foundVertexes.isEmpty()) {
            Iterator it = foundVertexes.iterator();
            if (foundVertexes.size() == 1) {
                Object element = it.next();
                System.out.println("Redrawing around " + element.toString());
                viewerPanel.SetPickedState(element.toString());
                viewerPanel.Animator(element.toString());
            } else {
                JOptionPane.showMessageDialog(frame, "Multiple Nodes with key " + key + " and value " + value + " found: " + foundVertexes, "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(frame, "Can not find node with key " + key + " and value " + value, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
