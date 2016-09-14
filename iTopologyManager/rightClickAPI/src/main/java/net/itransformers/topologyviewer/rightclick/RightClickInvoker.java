/*
 * RightClickInvoker.java
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

package net.itransformers.topologyviewer.rightclick;

import net.itransformers.topologyviewer.config.ParamType;
import net.itransformers.topologyviewer.config.RightClickItemType;
import edu.uci.ics.jung.io.GraphMLMetadata;

import javax.swing.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class RightClickInvoker {

    public static void invokeRightClickHandler(JFrame frame, String v, final RightClickItemType rcItemType,
                                               Map<String, GraphMLMetadata<String>> vertexMetadatas,
                                               File path,
                                               File versionDir) throws Exception {
        String clazzStr = rcItemType.getHandlerClass();
        Class<?> clazz;
        clazz = Class.forName(clazzStr);
        RightClickHandler inst;
        inst = (RightClickHandler) clazz.newInstance();
        Map<String, String> graphMLParams = getParams(v, vertexMetadatas);
        Map<String, String> rcParams = new HashMap<String, String>();
        for (ParamType param : rcItemType.getParam()) {
            rcParams.put(param.getName(), param.getValue());
        }
        inst.handleRightClick(frame, v, graphMLParams, rcParams, path, versionDir);
        // todo remove this hardcode

    }

    private static <G> Map<String, String> getParams(String v, Map<String, GraphMLMetadata<String>> vertexMetadata) {
        HashMap<String, String> params = new HashMap<String, String>();
        for (String key : vertexMetadata.keySet()){
            String value = vertexMetadata.get(key).transformer.transform(v);
            if (value == null) continue;
            if (!params.containsKey(key)){
                params.put(key,value);
            } else{
                value = value.concat(", ").concat(params.get(key));
                params.put(key,value);
            }
        }
        return params;
    }


}
