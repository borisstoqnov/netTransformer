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
import edu.uci.ics.jung.algorithms.filters.EdgePredicateFilter;
import edu.uci.ics.jung.io.GraphMLMetadata;
import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;


public class EdgeFilterFactory {
    static Logger logger = Logger.getLogger(EdgeFilterFactory.class);

    public static EdgePredicateFilter<String, String> createEdgeFilter(final FilterType filter, final Map<String, GraphMLMetadata<String>> edgeMetadata) {
        return new EdgePredicateFilter<String, String>(new Predicate<String>() {
                public boolean evaluate(String edge) {
                    try {
                        if (filter == null) return true;
                        List<IncludeType> includes = filter.getInclude();
                        for (IncludeType include: includes) {
                            if (ForType.EDGE.equals(include.getFor())) {
                                final String dataKey = include.getDataKey();
                                if (dataKey == null) { // lets include all edges
                                    return true;
                                }
                                final GraphMLMetadata<String> stringGraphMLMetadata = edgeMetadata.get(dataKey);
                                if (stringGraphMLMetadata == null) {
                                    throw new RuntimeException("Can not find metadata for key: "+dataKey);
                                }
                                final Transformer<String, String> transformer = stringGraphMLMetadata.transformer;
                                String value = transformer.transform(edge);
                                if (value != null){
                                    String[] dataValues = value.split(",");
                                    String includeDataValue = include.getDataValue();
                                    for (String dataValue : dataValues){
                                        if (dataValue.equals(includeDataValue)) {
                                            return true;
                                        }
                                    }
                                }

                            }
                        }
                        logger.debug("filter edge: "+edge);
                        return false;
                    } catch (RuntimeException rte){
                        rte.printStackTrace();
                        return false;
                    }
                }
            });
    }

}
