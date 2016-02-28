/*
 * IconMapLoader.java
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
import edu.uci.ics.jung.visualization.LayeredIcon;
import net.itransformers.topologyviewer.config.DataMatcherType;
import net.itransformers.topologyviewer.config.IconType;
import net.itransformers.topologyviewer.config.TopologyViewerConfType;
import net.itransformers.topologyviewer.config.datamatcher.DataMatcher;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class IconMapLoader implements GraphmlLoaderListener{
    static Logger logger = Logger.getLogger(IconMapLoader.class);

    private Map<String, Icon> iconMap = new HashMap<String, Icon>();
    private TopologyViewerConfType viewerConfig;
    private Map<String, DataMatcher> matcherMap = new HashMap<String, DataMatcher>();
    public IconMapLoader(TopologyViewerConfType viewerConfig) {
        this.viewerConfig = viewerConfig;
        List<DataMatcherType> matcherList = viewerConfig.getDataMatcher();
        for (DataMatcherType dataMatcherType : matcherList) {
            String className = dataMatcherType.getClazz();
            Class clazz = null;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                logger.error("Can not find class: "+className, e);
            }
            try {
                DataMatcher dataMatcher = (DataMatcher) clazz.newInstance();
                matcherMap.put(dataMatcherType.getName(), dataMatcher);
            } catch (InstantiationException e) {
                logger.error("Can not instantiate class: " + className, e);
            } catch (IllegalAccessException e) {
                logger.error("Can not access constructor class: " + className, e);
            }
        }


    }

    public Map<String, Icon> getIconMap() {
        return iconMap;
    }

    private void updateIconsMap(final Map<String, GraphMLMetadata<String>> vertexMetadata, final Collection<String> graphVertices) {
//        final Collection<String> graphVertices = graph.getVertices();
        String[] vertices = graphVertices.toArray(new String[graphVertices.size()]);
        List<IconType> iconTypeList = viewerConfig.getIcon();
        List<IconType.Data> datas;
        for (String vertice : vertices) {
            System.out.println(vertice);
            for (IconType iconType : iconTypeList) {
                boolean match = true;
                datas = iconType.getData();
                boolean isDefaultIcon = datas.isEmpty();

                for (IconType.Data data : datas) {

                    final GraphMLMetadata<String> stringGraphMLMetadata = vertexMetadata.get(data.getKey());
                    if (stringGraphMLMetadata == null){
                        logger.error(String.format("Can not find vertex metadata key '%s'.",data.getKey()));
                        match = false;
                        break;
                    }
                    final String value = stringGraphMLMetadata.transformer.transform(vertice);
                    String matcher = data.getMatcher();
                    if (matcher == null) {
                        matcher = "default";
                    }
                    if (value == null){
                        match = false;
                        break;

                    }
                    DataMatcher matcherInstance = matcherMap.get(matcher);
                    boolean matchResult = matcherInstance.compareData(value, data.getValue());
                    if (value == null || !matchResult) {
                        match = false;
                        break;
                    }
                }
                boolean iconExists = iconMap.containsKey(vertice);
                if ((!isDefaultIcon && match) || (isDefaultIcon && !iconExists)) {
                    final String name = iconType.getName();
                    String[] iconNames = name.split(",");
                    logger.debug("Load icon: "+iconNames[0].trim() + " for node "+vertice);
                    final URL resource = TopologyManagerFrame.class.getResource(iconNames[0].trim());
                    if (resource == null) {
                        logger.error("Can not load icon: "+iconNames[0].trim());
                        continue;
                    }
                    final ImageIcon imageIcon = new ImageIcon(resource);
                    LayeredIcon iconImg = new LayeredIcon(imageIcon.getImage());
                    for (int i=1;i<iconNames.length;i++) {
                        final URL resource1 = TopologyManagerFrame.class.getResource(iconNames[i].trim());
                        logger.debug("Load icon: "+iconNames[0].trim() + " for node "+vertice);
                        iconImg.add(new ImageIcon(resource1));
                    }
                    iconMap.put(vertice, iconImg);
                    break;
                }
            }
        }
    }

    @Override
    public <G extends Graph<String,String>> void graphmlLoaded(Map<String, GraphMLMetadata<String>> vertexMetadata, Map<String, GraphMLMetadata<String>> edgeMetadata, G graph) {
        updateIconsMap(vertexMetadata, graph.getVertices());
    }
}
