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

package net.itransformers.topologyviewer.gui;

import edu.uci.ics.jung.visualization.control.GraphMouseListener;

import java.awt.event.MouseEvent;

/**
 * Created by IntelliJ IDEA.
 * Date: 11-11-8
 * Time: 10:08
 * To change this template use File | Settings | File Templates.
 */
public class TestGraphMouseListener<V> implements GraphMouseListener<V> {

    public void graphClicked(V v, MouseEvent me) {
        System.err.println("Vertex " + v + " was clicked at (" + me.getX() + "," + me.getY() + ")");
        if (me.getButton() == MouseEvent.BUTTON3) {
            //    JOptionPane.showMessageDialog(me.getComponent(), "Node name : "+v.toString());
        }
    }

    public void graphPressed(V v, MouseEvent me) {
        System.err.println("Vertex " + v + " was pressed at (" + me.getX() + "," + me.getY() + ")");
    }

    public void graphReleased(V v, MouseEvent me) {
        System.err.println("Vertex " + v + " was released at (" + me.getX() + "," + me.getY() + ")");
    }
}
