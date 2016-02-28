/*
 * BGIPv4IntPeeringIncluder.java
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

package net.itransformers.topologyviewer.customfilterincluders;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.io.GraphMLMetadata;
import net.itransformers.topologyviewer.gui.VertexIncluder;

import java.util.Collection;
import java.util.Map;

/**
 * Created by vasko on 10/13/14.
 */
public class BGIPv4IntPeeringIncluder implements VertexIncluder {
    @Override
    public boolean hasToInclude(String vertexName, Map<String, GraphMLMetadata<String>> vertexMetadata, Graph<String, String> graph1) {
        String country = vertexMetadata.get("Country").transformer.transform(vertexName);
        String ipv4Flag = vertexMetadata.get("IPv4Flag").transformer.transform(vertexName);
            if("BG".equals(country)&"TRUE".equals(ipv4Flag)){
                return true;
            }

            if (!"BG".equals(country)) {
            Collection<String> inEdges = graph1.getInEdges(vertexName);
            for (String inEdge : inEdges) {
                Pair<String> endpoints = graph1.getEndpoints(inEdge);
                String oppositeVertex;
                if (endpoints.getFirst().equals(vertexName)){
                    oppositeVertex = endpoints.getSecond();
                } else {
                    oppositeVertex = endpoints.getFirst();
                }
                String oppositeCountry = vertexMetadata.get("Country").transformer.transform(oppositeVertex);
                String oppositeIPv6Flag = vertexMetadata.get("IPv4Flag").transformer.transform(oppositeVertex);

                if ("BG".equals(oppositeCountry)&&"TRUE".equals(oppositeIPv6Flag)) {
//                   System.out.println("BGAndIntBGPeeringIncluder returns "+vertexName);
                    return true;
                }
           }
        }
        return false;
    }
}
