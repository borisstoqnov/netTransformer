package net.itransformers.topologyviewer.parameterfactory;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 **
 * 
 * Copyright 
 */
public interface ParameterFactoryElement {
    /**
     * Initialise factory element. Acts like constructor. This method is supposed to be invoked once in the
     * ParameterFactoryElement life cycle.
     * @param config some static configuration data for initializing this factory element
     * @param params parameters that should be initialized. The value of this map contains information
     * per each parameter that will be used to create value for this parameter.
     */
    void init(Map<String, String> config, Map<String, String> params);

    /**
     * Creates map of parameters using the configuration obtained from init method invocation.
     * The contract is that only such parameters will be crated which names are defined in the key set of the params
     * received in init method.
     * Obviously this method could be invoked several times. While init method is invoked once
     * per ParameterFactoryElement life cycle.
     * @param context some dynamic configuration data that could support parameter creation this factory element
     * @param currentParams Currently created params by the ParameterFactory.
     * This currentParams usually comes from the invocation of other ParameterFactoryElement.
     * @return parameters created by this object.
     */
    Map<String, String> createParams(Map<String, Object> context, Map<String, String> currentParams) throws Exception;
}
