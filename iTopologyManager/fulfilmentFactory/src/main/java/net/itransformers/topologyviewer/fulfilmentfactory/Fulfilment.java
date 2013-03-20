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
public interface Fulfilment {
    void fulfil(File projectPath, Map<String, String> parameters, Map<String, String> fulfilmentFactoryParams, Logger logger) throws IOException;
}
