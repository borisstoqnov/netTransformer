/*
 * HTMLCSVNodeTooltipTransformer.java
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

package net.itransformers.topologyviewer.nodetooltip;

import net.itransformers.topologyviewer.config.TooltipType;
import edu.uci.ics.jung.io.GraphMLMetadata;
import org.apache.commons.collections15.Transformer;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class HTMLCSVNodeTooltipTransformer extends NodeTooltipTransformerBase{
    public HTMLCSVNodeTooltipTransformer(TooltipType tooltipType, Map<String, GraphMLMetadata<String>> nodeMetadatas) {
        super(tooltipType, nodeMetadatas);
    }

    public String transform(String node) {
        try {
             StringBuilder sb = new StringBuilder();
            sb.append("<html>");
             Set<String> valueSet = new HashSet<String>();
             GraphMLMetadata<String> stringGraphMLMetadata = nodeMetadatas.get(tooltipType.getDataKey());
            if(stringGraphMLMetadata!=null){
             Transformer<String, String> transformer = stringGraphMLMetadata.transformer;
             final String value = transformer.transform(node);
             if (value != null && !sb.toString().contains(value)){
                 sb.append(value);
             }
            sb.append(valueSet);
            }
            sb.append("</html>");
            return sb.toString().replaceAll("\\[\\]","");
        } catch (RuntimeException rte) {
            rte.printStackTrace();
            throw rte;
        }
    }
}
