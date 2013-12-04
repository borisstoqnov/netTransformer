package net.itransformers.topologyviewer.rightclick;

/*
 * iTransformer is an open source tool able to discover IP networks
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
                                               File deviceXmlPath) throws Exception {
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
        inst.handleRightClick(frame, v, graphMLParams, rcParams, path, new File(deviceXmlPath+File.separator+"device-data"+"-"+v+".xml")); // todo remove this hardcode

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
