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

import net.itransformers.topologyviewer.config.FilterType;
import net.itransformers.topologyviewer.config.ForType;
import net.itransformers.topologyviewer.config.IncludeType;
import edu.uci.ics.jung.algorithms.filters.VertexPredicateFilter;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.io.GraphMLMetadata;
import org.apache.commons.collections15.Predicate;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * Date: 11-11-8
 * Time: 12:c9
 * To change this template use File | Settings | File Templates.
 */
public class VertexFilterFactory {
    static Logger logger = Logger.getLogger(VertexFilterFactory.class);

    static VertexPredicateFilter<String, String> createVertexFilter(final FilterType filter, final Map<String, GraphMLMetadata<String>> vertexMetadata, final Graph<String, String> graph1) {
        return new VertexPredicateFilter<String, String>(new Predicate<String>() {
                public boolean evaluate(String v) {
                    if (graph1.getIncidentEdges(v).isEmpty()){
                        return false;
                    } else {
                        if (filter == null) return true;
                        List<IncludeType> includes = filter.getInclude();
                        boolean hasNodeInlcude = false;
                        for (IncludeType include: includes) {
                            if (ForType.NODE.equals(include.getFor())) {
                                hasNodeInlcude  = true;
                                final String dataKey = include.getDataKey();
                                if (dataKey == null) { // lets include all nodes
                                    return true;
                                }
                                if (vertexMetadata.get(dataKey) == null) {
                                    throw new RuntimeException("No data is defined in vertex metadata for dataKey="+dataKey);
                                }
                                String value = vertexMetadata.get(dataKey).transformer.transform(v);
                                if (value != null) {
                                    String[] dataValues = value.split(",");

                                    String includeDataValue = include.getDataValue();

                                    for (String dataValue : dataValues){
                                        if (dataValue.equals(includeDataValue)) {
                                            logger.debug("Node selected: "+v);

                                            return true;
                                        }
                                    }

                                }
                            }
                        }
                        if (hasNodeInlcude) {
                            return false;
                        } else {
                            return true;
                        }
                    }
                }
            });
    }

}
