/*
 * MyPersistentLayoutImpl.java
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

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.layout.PersistentLayoutImpl;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: niau
 * Date: 10/30/14
 * Time: 11:02 AM
 * To change this template use File | Settings | File Templates.
 */
public class MyPersistentLayoutImpl extends PersistentLayoutImpl {
    /**
     * create an instance with a passed layout
     * create containers for graph components
     *
     * @param layout
     */
    public MyPersistentLayoutImpl(Layout layout) {
        super(layout);
    }

    public void restore(String fileName) throws IOException,
            ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(
                fileName));
        map = (Map) ois.readObject();
        ois.close();
        Layout newLayout = this.getDelegate();
        Graph newGraph = newLayout.getGraph();
        Set<String> oldVertexes = map.keySet();
        for (String oldVertex : oldVertexes) {
            if (!newGraph.containsVertex(oldVertex)){
                this.lock(oldVertex,true);
            }
        }
        for (Object newVertex : newGraph.getVertices()) {
            if (!oldVertexes.contains(newVertex)){
                this.lock(newVertex,true);
            }
        }
        initializeLocations();

        locked = true;
        fireStateChanged();
    }
}
