package net.itransformers.topologyviewer.fulfilmentfactory;

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

    public FulfilmentAdapter(Fulfilment fulfilment, Map<String, String> parameters,
                             Map<String, String> fulfilmentFactoryParams, Logger logger) {
        this.fulfilment = fulfilment;
        this.parameters = parameters;
        this.fulfilmentFactoryParams = fulfilmentFactoryParams;
        this.logger = logger;
    }
    public void fulfil() throws IOException {
        fulfilment.fulfil(parameters, fulfilmentFactoryParams, logger);
    }
}
