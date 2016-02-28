/*
 * DashEdgeTooltipTransformer.java
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

package net.itransformers.topologyviewer.edgetooltip;

import net.itransformers.topologyviewer.config.TooltipType;
import edu.uci.ics.jung.io.GraphMLMetadata;
import org.apache.commons.collections15.Transformer;

import java.util.Map;

public class DashEdgeTooltipTransformer extends EdgeTooltipTransformerBase{
    public DashEdgeTooltipTransformer(TooltipType tooltipType, Map<String, GraphMLMetadata<String>> edgeMetadata) {
        super(tooltipType, edgeMetadata);
    }
    public String transform(String edge) {
        try {
             StringBuilder sb = new StringBuilder();
             boolean isFirst = true;
             sb.append("<html>");
             GraphMLMetadata<String> stringGraphMLMetadata = edgeMetadatas.get(tooltipType.getDataKey());
             Transformer<String, String> transformer = stringGraphMLMetadata.transformer;
             final String value = transformer.transform(edge);
             if (value != null){
                 if (!isFirst) {
                     sb.append("<b> - </b>");
                 } else {
                     isFirst = false;
                 }
                 sb.append(value);

             }
            sb.append("</html>");
            return sb.toString();
        } catch (RuntimeException rte) {
            rte.printStackTrace();
            throw rte;
        }
    }
}
