/*
 * graphLoader.java
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

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.io.GraphMLMetadata;

import java.net.URL;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: niau
 * Date: 12/10/12
 * Time: 10:11 PM
 * To change this template use File | Settings | File Templates.
 */
    public interface graphLoader <G extends Graph<String,String>> {
        public void loadGraph(URL urlPath);
        public Map<String, Map<String, GraphMLMetadata<String>>> getVertexMetadatas();
        public Map<String, Map<String, GraphMLMetadata<String>>> getEdgeMetadatas();
        public Map<String, Map<String, GraphMLMetadata<G>>> getGraphMetadatas();
//        public void addGraphLoaderListener(GraphLoaderListener listener);
    }
