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

package net.itransformers.topologyviewer.parameterfactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParameterFactory {
    List<ParameterFactoryElement> elements;

    public ParameterFactory(List<ParameterFactoryElement> elements) {
        this.elements = elements;
    }

    /**
     *
     * @param context used to pass data to parameter factory elements.
     * @return null if one of the parameter factory elements returns null.
     * @throws Exception
     */
    public Map<String, String> createParameters(Map<String, Object> context) throws Exception {
        Map<String, String> params = new HashMap<String, String>();
        for (ParameterFactoryElement element : elements) {
            Map<String,String> elementParam = element.createParams(context,params);
            if (elementParam != null) {
                params.putAll(elementParam);
            } else {
                return null;
            }
        }
        return params;
    }
}
