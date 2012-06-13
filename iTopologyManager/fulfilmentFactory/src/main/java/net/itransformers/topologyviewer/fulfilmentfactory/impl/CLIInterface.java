package net.itransformers.topologyviewer.fulfilmentfactory.impl;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 *
 **
 * 
 * Copyright 
 */
public interface CLIInterface {
    void open();
    void sendData(String data);
    void readUntil(String regexp, int timeout) throws IOException;
    void close();
}
