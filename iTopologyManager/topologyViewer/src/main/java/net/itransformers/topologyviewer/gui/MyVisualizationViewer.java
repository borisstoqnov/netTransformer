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

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.io.GraphMLMetadata;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.DefaultVertexIconTransformer;
import edu.uci.ics.jung.visualization.decorators.EllipseVertexShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.decorators.VertexIconShapeTransformer;
import edu.uci.ics.jung.visualization.renderers.DefaultEdgeLabelRenderer;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import net.itransformers.topologyviewer.config.FilterType;
import net.itransformers.topologyviewer.config.ForType;
import net.itransformers.topologyviewer.config.TooltipType;
import net.itransformers.topologyviewer.config.TopologyViewerConfType;
import net.itransformers.topologyviewer.edgetooltip.DefaultEdgeTooltipTransformer;
import net.itransformers.topologyviewer.edgetooltip.EdgeTooltipTransformerBase;
import net.itransformers.topologyviewer.nodetooltip.DefaultNodeTooltipTransformer;
import net.itransformers.topologyviewer.nodetooltip.NodeTooltipTransformerBase;
import org.apache.commons.collections15.Transformer;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Date: 11-11-8
 * Time: 10:c9
 * To change this template use File | Settings | File Templates.
 */
public class MyVisualizationViewer extends VisualizationViewer<String,String> {
    static Logger logger = Logger.getLogger(MyVisualizationViewer.class);
    private FilterType currentFilter;
    private TopologyViewerConfType viewerConfig;
    private Map<String, GraphMLMetadata<String>> vertexMetadatas;
    private Map<String, GraphMLMetadata<String>> edgeMetadatas;
    // TODO: Think more how to implement this in a better way (temporary for shortest path).
    private Map<String,Stroke> edgesStrokeMap;// = new HashMap<String, Stroke>();
    private Map<String, Color> edgesColorMap;
    private Map<String, Icon> iconMap;


    public MyVisualizationViewer(TopologyViewerConfType viewerConfig, Graph<String, String> graph,
                                 Map<String, GraphMLMetadata<String>> vertexMetadatas,
                                 Map<String, GraphMLMetadata<String>> edgeMetadatas,
                                 Map<String, Icon> iconMap,
                                 Map<String, Stroke> edgesStrokeMap,
                                 Map<String, Color> edgesColorMap) {
//        RadialTreeLayout<String,Integer> radialLayout;
        super(new MyPersistentLayoutImpl(new FRLayout<String,String>(graph)));
//        super(new PersistentLayoutImpl(new RadialTreeLayout(<String,Integer>(graph)));
        this.viewerConfig = viewerConfig;
        this.vertexMetadatas = vertexMetadatas;
        this.edgeMetadatas = edgeMetadatas;
        this.iconMap = iconMap;
        this.edgesStrokeMap = edgesStrokeMap;
        this.edgesColorMap = edgesColorMap;
        createGraphViewer();
    }

    public void setCurrentFilter(FilterType currentFilter) {
        this.currentFilter = currentFilter;
    }

    private void createGraphViewer() {
        MyVisualizationViewer vv = this;
        vv.addGraphMouseListener(new TestGraphMouseListener<String>());
        // add my listeners for ToolTips
        vv.setVertexToolTipTransformer(createNodeTooltipTransformer());
        final VertexIconShapeTransformer<String> vertexImageShapeFunction =
            new VertexIconShapeTransformer<String>(new EllipseVertexShapeTransformer<String>());
        vv.getRenderContext().setVertexShapeTransformer(vertexImageShapeFunction);
        final DefaultVertexIconTransformer<String> vertexIconFunction =
        	new DefaultVertexIconTransformer<String>();
        vv.getRenderContext().setVertexIconTransformer(vertexIconFunction);
        vertexImageShapeFunction.setIconMap(iconMap);
        vertexIconFunction.setIconMap(iconMap);

        vv.setEdgeToolTipTransformer(createEdgeToolTipTransformer());

        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<String>());
        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.AUTO);

        // Set up a new stroke Transformer for the edges
//        float dash[] = {10.0f};
//        final Stroke edgeStroke = new BasicStroke(1.0f, BasicStroke.CAP_BUTT,
//             BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
//        Transformer<String, Stroke> edgeStrokeTransformer =
//              new Transformer<String, Stroke>() {
//                    public Stroke transform(String s) {
//                        return edgeStroke;
//                    }
//        };
//        vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);
        vv.getRenderContext().setEdgeStrokeTransformer(createEdgeStrokeTransformer());
        vv.getRenderContext().setEdgeLabelTransformer(createEdgeLabelTransformer());
        vv.getRenderContext().setEdgeLabelRenderer(new DefaultEdgeLabelRenderer(Color.cyan));
        vv.getRenderContext().setEdgeDrawPaintTransformer(createEdgeDrawPaintTransformer());
    }

    private Transformer<String,Stroke> createEdgeStrokeTransformer() {
//        return new ConstantTransformer(new BasicStroke(1.5f));
        final Stroke basicStroke = new BasicStroke(1.0f);
        return new Transformer<String,Stroke>() {
            @Override
            public Stroke transform(String edge) {
                if (edgesStrokeMap.containsKey(edge)){
                    return edgesStrokeMap.get(edge);
                } else {
                    return basicStroke; 
                }
            }
        };
    }
    public void setEdgeStroke(String edge, Stroke stroke){
        edgesStrokeMap.put(edge,stroke);
    }
    public void clearEdgesStroke(){
        edgesStrokeMap.clear();
    }

//    @Override
//    public void paintComponent(Graphics g) {
//        super.paintComponent(g);    //To change body of overridden methods use File | Settings | File Templates.
//    }

    private Transformer<String, String> createNodeTooltipTransformer() {
        return new Transformer<String, String>(){
                private Transformer<String, String> createTransformer(){
                    TooltipType tooltipType = getTooltip(currentFilter, ForType.NODE);
                    if (tooltipType == null) return null;
                    String transformerClassName = tooltipType.getTransformer();
                    if (transformerClassName == null){
                        return new DefaultNodeTooltipTransformer(tooltipType, vertexMetadatas);
                    } else {
                        try {
                            final Class<?> aClass = Class.forName(transformerClassName, true, this.getClass().getClassLoader());
                            if (NodeTooltipTransformerBase.class.isAssignableFrom(aClass)) {
                                Class<NodeTooltipTransformerBase> transformerBaseClass = (Class<NodeTooltipTransformerBase>) aClass;
                                Constructor<NodeTooltipTransformerBase> constr = transformerBaseClass.getConstructor(new Class[]{TooltipType.class, Map.class});
                                 try {
                                    NodeTooltipTransformerBase inst = constr.newInstance(tooltipType, vertexMetadatas);
                                    return inst;
                                 }catch (IllegalArgumentException iae){
                                    logger.error(constr.toString());
                                    throw iae;
                                 }
                            } else {
                                throw new RuntimeException("not implemented");
                            }
                        } catch (Exception e){
                            throw new RuntimeException(e);
                        }
                    }
                }

                public String transform(String s) {
                    final Transformer<String, String> transformer = createTransformer();
                    if (transformer == null) return null;
                    return transformer.transform(s);
                }
            };
    }

    private Transformer<String, String> createEdgeToolTipTransformer() {
        return new Transformer<String, String>(){
            private Transformer<String, String> createTransformer(){
                TooltipType tooltipType = getTooltip(currentFilter, ForType.EDGE);
                if (tooltipType == null) return null;
                String transformerClassName = tooltipType.getTransformer();
                if (transformerClassName == null){
                    return new DefaultEdgeTooltipTransformer(tooltipType, edgeMetadatas);
                } else {
                    try {
                        final Class<?> aClass = Class.forName(transformerClassName, true, this.getClass().getClassLoader());
                        if (EdgeTooltipTransformerBase.class.isAssignableFrom(aClass)) {
                            Class<EdgeTooltipTransformerBase> transformerBaseClass = (Class<EdgeTooltipTransformerBase>) aClass;
                            Constructor<EdgeTooltipTransformerBase> constr = transformerBaseClass.getConstructor(new Class[]{TooltipType.class, Map.class});
                             try {
                                EdgeTooltipTransformerBase inst = constr.newInstance(tooltipType, edgeMetadatas);
                                return inst;
                            } catch (IllegalArgumentException iae){
                                logger.error(constr.toString());
                                throw iae;
                            }
//                            return inst;
                        } else {
                            throw new RuntimeException("not implemented");
                        }
                    } catch (Exception e){
                        throw new RuntimeException(e);
                    }
                }
            }

            public String transform(String s)  {
                final Transformer<String, String> transformer = createTransformer();
                if (transformer == null) return "";
                return transformer.transform(s);
            }
        };
    }

    private TooltipType getTooltip(FilterType filter, ForType forType) {
        TooltipType tooltipType = null;
        if (filter == null) return null;
        List<TooltipType> tooltips = filter.getTooltip();
        for (TooltipType currentTooltipType : tooltips) {
            if (forType == currentTooltipType.getFor()){
                 tooltipType = currentTooltipType;
                break;
            }
        }
        if (tooltipType == null) {
            tooltips = viewerConfig.getTooltip();
            for (TooltipType currentTooltipType : tooltips) {
                if (forType == currentTooltipType.getFor()){
                     tooltipType = currentTooltipType;
                    break;
                }
            }
        }
        return tooltipType;
    }

        private Transformer<String, Paint> createEdgeDrawPaintTransformer() {
        return new Transformer<String, Paint>() {
            final Color defaultColor = new Color(0x0000ff);
            public Paint transform(String edge) {
                if (edgesColorMap.containsKey(edge)){
                    return edgesColorMap.get(edge);
                } else {
                    return defaultColor;
                }
//                for (Map<String, GraphMLMetadata<String>> edgeMetadata : edgeMetadatas.values()) {
//                    final GraphMLMetadata<String> color1 = edgeMetadata.get("color");
//                    if (color1 == null) {
//                        logger.error("color metadata is not defined in edge metadata");
//                        continue;
//                        //throw new RuntimeException("color metadata is not defined in edge metadata");
//                    }
//                    final String value = color1.transformer.transform(edge);
//                    if (value != null) {
//                        color = Integer.parseInt(value, 16);
//                        break;
//                    }
//                }
//                return new Color(color);
            }
        };
    }

    private Transformer<String, String> createEdgeLabelTransformer() {
        return new Transformer<String, String>(){
               public String transform(String edge) {
//                   StringBuilder sb = new StringBuilder();
//                   Set<String> valueSet = new HashSet<String>();
//                   for (Map<String, GraphMLMetadata<String>> edgeMetadata : edgeMetadatas) {
//                       final String value = edgeMetadata.get("name").transformer.transform(edge);
//                       if (value != null){
//                           valueSet.add(value);
//                       }
//                   }
//                   sb.append(valueSet.toString());
//                   return sb.toString();
                   return "";
                }
        };
    }


}
