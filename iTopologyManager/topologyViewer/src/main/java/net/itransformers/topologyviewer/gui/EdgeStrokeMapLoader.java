/*
 * EdgeStrokeMapLoader.java
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

import net.itransformers.topologyviewer.config.EdgeStrokeType;
import net.itransformers.topologyviewer.config.TopologyViewerConfType;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.io.GraphMLMetadata;
import org.apache.log4j.Logger;

import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Date: 11-11-8
 * Time: 11:27
 * To change this template use File | Settings | File Templates.
 */
public class EdgeStrokeMapLoader implements GraphmlLoaderListener{
    static Logger logger = Logger.getLogger(EdgeStrokeMapLoader.class);

    private Map<String,Stroke> edgesStrokeMap = new HashMap<String, Stroke>();
    private TopologyViewerConfType viewerConfig;

    public EdgeStrokeMapLoader(TopologyViewerConfType viewerConfig) {
        this.viewerConfig = viewerConfig;
    }

    public Map<String, Stroke> getEdgesStrokeMap() {
        return edgesStrokeMap;
    }

    private void updateEdgeStrokeMap(final Map<String, GraphMLMetadata<String>> edgeMetadata, final Collection<String> graphEdges) {
//        final Collection<String> graphVertices = graph.getVertices();
        String[] vertices = graphEdges.toArray(new String[graphEdges.size()]);
        List<EdgeStrokeType> edgeStrokeTypeList = viewerConfig.getEdgeStroke();
        List<EdgeStrokeType.Data> datas;
        for (String edge : vertices) {
            for (EdgeStrokeType edgeStrokeType : edgeStrokeTypeList) {
                boolean match = true;
                datas = edgeStrokeType.getData();
                boolean isDefaultIcon = datas.isEmpty();
                for (EdgeStrokeType.Data data : datas) {
                    final GraphMLMetadata<String> stringGraphMLMetadata = edgeMetadata.get(data.getKey());
                    if (stringGraphMLMetadata == null){
                        logger.error(String.format("Can not find edge metadata key '%s'.",data.getKey()));
                        match = false;
                        break;
//                        throw new RuntimeException(String.format("Can not find vertex metadata key '%s' in file '%s'.",data.getKey(), fileName));
                    }
                    final String value = stringGraphMLMetadata.transformer.transform(edge);
                    if (value == null || !value.equals(data.getValue())) {
                        match = false;
                        break;
                    }
                }
                boolean iconExists = edgesStrokeMap.containsKey(edge);
                if ((!isDefaultIcon && match) || (isDefaultIcon && !iconExists)) {
//                    final String name = edgeStrokeType.getName();
//                    final URL resource = TopologyManagerFrame.class.findResourceByName(name);
//                    logger.debug("Load icon: "+name);
//                    final ImageIcon imageIcon = new ImageIcon(resource);
//                    Icon iconImg = new LayeredIcon(imageIcon.getImage());
                    try {
                        String dashStr = edgeStrokeType.getDash();
                        if(dashStr!=null){
                            String[] dashArr = dashStr.split(",");
                            float[] dash = new float[dashArr.length];
                            for (int i=0;i < dashArr.length; i++) {
                                dash[i] = Float.parseFloat(dashArr[i]);
                            }
                            float width = edgeStrokeType.getWidth();
                            float dashPhase = edgeStrokeType.getDashPhase();
                            int join = edgeStrokeType.getJoin();
                            int cap = edgeStrokeType.getCap();
                            float miterlimit = edgeStrokeType.getMiterlimit();
                            final Stroke edgeStroke = new BasicStroke(width, cap,
                                    join, miterlimit, dash, dashPhase);
                            edgesStrokeMap.put(edge, edgeStroke);

                        }
                    } catch (NumberFormatException nfe) {
                        nfe.printStackTrace();
                        break;
                    } catch (NullPointerException npe) {
                        npe.printStackTrace();
                        break;
                    }
                    break;
                }
            }
        }
    }

    @Override
    public <G extends Graph<String,String>> void graphmlLoaded(Map<String, GraphMLMetadata<String>> vertexMetadata, Map<String, GraphMLMetadata<String>> edgeMetadata, G graph) {
        updateEdgeStrokeMap(edgeMetadata, graph.getEdges());
    }
}
