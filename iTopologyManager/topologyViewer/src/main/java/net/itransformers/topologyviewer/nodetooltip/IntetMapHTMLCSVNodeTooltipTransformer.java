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

package net.itransformers.topologyviewer.nodetooltip;

import edu.uci.ics.jung.io.GraphMLMetadata;
import net.itransformers.topologyviewer.config.TooltipType;
import org.apache.commons.collections15.Transformer;

import java.util.Map;

public class IntetMapHTMLCSVNodeTooltipTransformer extends NodeTooltipTransformerBase{
    public IntetMapHTMLCSVNodeTooltipTransformer(TooltipType tooltipType, Map<String, GraphMLMetadata<String>> nodeMetadatas) {
        super(tooltipType, nodeMetadatas);
    }

    public String transform(String node) {
        try {
             StringBuilder sb = new StringBuilder();
            sb.append("<html>");


             for ( String key : nodeMetadatas.keySet()){
                 GraphMLMetadata<String> stringGraphMLMetadata = nodeMetadatas.get(key);
                 Transformer<String, String> transformer = stringGraphMLMetadata.transformer;
                 final String value = transformer.transform(node);
                 if (value != null && !key.contains("Prefixes")){
                     sb.append("<p>"+key+": "+value+"</p>");
                 }
            }


            sb.append("</html>");
            return sb.toString().replaceAll("\\[\\]","");
        } catch (RuntimeException rte) {
            rte.printStackTrace();
            throw rte;
        }
    }
}
