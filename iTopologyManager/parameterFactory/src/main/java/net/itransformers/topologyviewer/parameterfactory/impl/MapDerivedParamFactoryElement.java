package net.itransformers.topologyviewer.parameterfactory.impl;

import net.itransformers.topologyviewer.parameterfactory.ParameterFactoryElement;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 **
 * 
 * Copyright 
 */
public class MapDerivedParamFactoryElement implements ParameterFactoryElement{
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
            result.put(key,contextParams.get(key));
        }
        return result;
    }
}
