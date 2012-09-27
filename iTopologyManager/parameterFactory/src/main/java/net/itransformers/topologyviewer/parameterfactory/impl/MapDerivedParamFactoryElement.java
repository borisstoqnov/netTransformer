/*
 * iTransformer is an open source tool able to discover and transform
 *  IP network infrastructures.
 *  Copyright (C) 2012  http://itransformers.net
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.itransformers.topologyviewer.parameterfactory.impl;

import net.itransformers.topologyviewer.parameterfactory.ParameterFactoryElement;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class MapDerivedParamFactoryElement implements ParameterFactoryElement{
    static Logger logger = Logger.getLogger(MapDerivedParamFactoryElement.class);
    private Map<String, String> params;
    private String contextKey;

    @Override
    public void init(Map<String, String> config, Map<String, String> params) {
        this.contextKey = config.get("contextKey");
        this.params = params;
    }
    @Override
    public Map<String, String> createParams(Map<String, Object> context, Map<String, String> currentParams) {
        Map<String,String> contextParams;
        try {
            contextParams = (Map<String, String>) context.get(contextKey);
        } catch (ClassCastException cce) {
            throw new RuntimeException(cce.getMessage()+", for key: "+contextKey,cce);
        }
        if (contextParams == null){
            throw new RuntimeException(String.format("can not find key '%s' in context ",contextKey));
        }
        Map<String, String> result = new HashMap<String, String>();
        for (String key : params.keySet()) {
            if (!contextParams.containsKey(key)) {
                logger.log(Level.DEBUG, "Can not find key: "+key+", in context: "+contextParams);
            } else {
                String value = contextParams.get(key);
                result.put(key, value);
            }
        }
        return result;
    }
}
