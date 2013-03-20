package net.itransformers.topologyviewer.fulfilmentfactory;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 *
 **
 * 
 * Copyright 
 */
public class FulfilmentAdapter {
    private Fulfilment fulfilment;
    private Map<String,String> parameters;
    private Map<String, String> fulfilmentFactoryParams;
    private Logger logger;
    private File projectPath;

    public FulfilmentAdapter(File projectPath, Fulfilment fulfilment, Map<String, String> parameters,
                             Map<String, String> fulfilmentFactoryParams, Logger logger) {
        this.projectPath = projectPath;
        this.fulfilment = fulfilment;
        this.parameters = parameters;
        this.fulfilmentFactoryParams = fulfilmentFactoryParams;
        this.logger = logger;
    }
    public void fulfil() throws IOException {
        fulfilment.fulfil(projectPath, parameters, fulfilmentFactoryParams, logger);
    }
}
