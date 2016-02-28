/*
 * TestGraphMouseListener.java
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
