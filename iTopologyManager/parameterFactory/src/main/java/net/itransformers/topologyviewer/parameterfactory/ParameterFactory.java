package net.itransformers.topologyviewer.parameterfactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 **
 * 
 * Copyright 
 */
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
