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

package net.itransformers.topologyviewer.edgetooltip;

import net.itransformers.topologyviewer.config.TooltipType;
import edu.uci.ics.jung.io.GraphMLMetadata;
import org.apache.commons.collections15.Transformer;

import java.util.*;

public class CSVEdgeTooltipTransformer extends EdgeTooltipTransformerBase{
    public CSVEdgeTooltipTransformer(TooltipType tooltipType, Map<String, Map<String, GraphMLMetadata<String>>> edgeMetadatas) {
        super(tooltipType, edgeMetadatas);
    }

    public String transform(String edge) {
        try {
             StringBuilder sb = new StringBuilder();
             Set<String> valueSet = new HashSet<String>();
             for (Map<String, GraphMLMetadata<String>> edgeMetadata : edgeMetadatas.values()) {
                 GraphMLMetadata<String> stringGraphMLMetadata = edgeMetadata.get(tooltipType.getDataKey());
                 Transformer<String, String> transformer = stringGraphMLMetadata.transformer;
                 final String value = transformer.transform(edge);
                 if (value != null){
                     valueSet.addAll(Arrays.asList(value.split(",")));
                 }
             }
            sb.append(valueSet);
            return sb.toString();
        } catch (RuntimeException rte) {
            rte.printStackTrace();
            throw rte;
        }
    }
}
