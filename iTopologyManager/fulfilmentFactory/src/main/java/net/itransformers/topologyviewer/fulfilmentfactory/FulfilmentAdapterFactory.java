package net.itransformers.topologyviewer.fulfilmentfactory;

import net.itransformers.topologyviewer.fulfilmentfactory.config.FulfilmentFactoriesType;
import net.itransformers.topologyviewer.fulfilmentfactory.config.FulfilmentFactoryType;
import net.itransformers.topologyviewer.fulfilmentfactory.config.ParamType;
import net.itransformers.topologyviewer.fulfilmentfactory.config.TypeType;
import net.itransformers.topologyviewer.fulfilmentfactory.util.JaxbMarshalar;
import net.itransformers.topologyviewer.parameterfactory.ParameterFactory;
import net.itransformers.topologyviewer.parameterfactory.ParameterFactoryBuilder;
import net.itransformers.resourcemanager.config.ResourceType;

import javax.xml.bind.JAXBException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 *
 **
 * 
 * Copyright 
 */
public class FulfilmentAdapterFactory {
    private String configFileName;
    private ParameterFactoryBuilder parameterFactoryBuilder;
    private Map<String,TypeType> fulfilmentFactoryTypesMap = new HashMap<String, TypeType>();
    private Map<String,FulfilmentFactoryType> fulfilmentFactoryMap = new HashMap<String, FulfilmentFactoryType>();

    public FulfilmentAdapterFactory(String configFileName,
                                    ParameterFactoryBuilder parameterFactoryBuilder,
                                    ResourceType resource
    ) throws IOException, JAXBException {
        this.configFileName = configFileName;
        this.parameterFactoryBuilder = parameterFactoryBuilder;
        FileInputStream is = null;
        try {
            is = new FileInputStream(configFileName);
            FulfilmentFactoriesType factoriesType = JaxbMarshalar.unmarshal(FulfilmentFactoriesType.class, is);
            for (FulfilmentFactoryType fulfilmentFactoryType : factoriesType.getFulfilmentFactory()){
                fulfilmentFactoryMap.put(fulfilmentFactoryType.getName(),fulfilmentFactoryType);
            }
            for (TypeType typeType : factoriesType.getFulfilmentFactoryTypes().getType()) {
                fulfilmentFactoryTypesMap.put(typeType.getName(), typeType);
            }
        } finally {
            if (is != null) is.close();
        }
        
    }

    public FulfilmentAdapter createFulfilmentAdapter(String factoryName, Map<String, Object> parameterFactoryContext, Logger logger) throws Exception, IllegalAccessException, InstantiationException {
        FulfilmentFactoryType fulfilmentFactory = fulfilmentFactoryMap.get(factoryName);
        if (fulfilmentFactory == null) {
            throw new RuntimeException(String.format("Can not find fulfilment factory '%s' in file : %s",factoryName,configFileName));
        }
        String paramFactoryName = fulfilmentFactory.getParameterFactoryName();
        ParameterFactory parameterFactory = parameterFactoryBuilder.buildParameterFactory(paramFactoryName);
        Map<String,String> parameters = parameterFactory.createParameters(parameterFactoryContext);
        String type = fulfilmentFactory.getType();
        TypeType typeType = fulfilmentFactoryTypesMap.get(type);
        Map<String, String> fulfilmentFactoryParams = new HashMap<String, String>();

        List<ParamType> paramList = typeType.getParam();
        for (ParamType paramType : paramList) {
            if (fulfilmentFactoryParams.containsKey(paramType.getName())){
                throw new RuntimeException("can not define two parameters with the same name");
            }
            fulfilmentFactoryParams.put(paramType.getName(), paramType.getValue());
        }
        String strClass = typeType.getClazz();
        Class<?> clazz = Class.forName(strClass);
        Fulfilment inst = (Fulfilment) clazz.newInstance();
        FulfilmentAdapter adapter = new FulfilmentAdapter(inst,parameters, fulfilmentFactoryParams, logger);
        return adapter;
    }

    public String[] getFulfilmentFactoryNamesForResource(String resourceName){
        List<String> result = new ArrayList<String>();
        for (String key : fulfilmentFactoryMap.keySet()) {
            if (fulfilmentFactoryMap.get(key).getResourceName().equals(resourceName)){
                result.add(key);
            }
        }
        return result.toArray(new String[0]);
    }
}
