package net.itransformers.topologyviewer.gui;/*
 * iDiscovery is an open source tool able to discover IP networks
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

import edu.uci.ics.jung.graph.UndirectedGraph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

import javax.swing.*;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * Date: 11-12-30
 * Time: 9:37
 * To change this template use File | Settings | File Templates.
 */
public class TopologyViewerApplet extends JApplet {
    TopologyViewer<UndirectedGraph<String, String>> fFrame;

    /** Build an applet interface with a menubar. A
     * a drop down menu includes Open/Close items
     * for opening and closing an instance of ParticleFrame.
     **/
    public void init () {

        try {
            URL dirname = new URL("http://localhost:8081/bg_peering/");
            String deviceXmlPath = "undirected";
            URL viewerConfig = new URL("http://localhost:8081/bg_peering/inet-map-conf.xml");
            fFrame = new TopologyViewer<UndirectedGraph<String, String>>(dirname, deviceXmlPath, UndirectedSparseGraph.<String, String>getFactory(), viewerConfig,null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    } // init

}
