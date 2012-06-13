package net.itransformers.topologyviewer.fulfilmentfactory.impl;

import java.io.IOException;
import java.util.LinkedList;

/**
 * Created by IntelliJ IDEA.
 *
 **
 * 
 * Copyright 
 */
public class SimulationCLIAdapter implements CLIInterface {
    private String name;
    private SimulationCLIAdapter adapter;
    final LinkedList<String> inputBuffer = new LinkedList();

    public SimulationCLIAdapter(String name) {
        this.name = name;
    }

    public void setAdapter(SimulationCLIAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public void open() {
        adapter.open();
        inputBuffer.clear();
    }

    @Override
    public void sendData(String data) {
        System.out.println("(" + name + ") Sending:" + data);
        adapter.receiveData(data);
    }
    public synchronized void receiveData(String data){
        System.out.println("("+name+") Receiving:" + data);
        inputBuffer.addFirst(data);
        notifyAll();
    }
    @Override
    public synchronized void readUntil(String regexp, int timeout) throws IOException {
        while (true) {
            if (readBuffer(regexp)) return;
            try {
                wait(timeout);
                if (inputBuffer.isEmpty()){
                    throw new IOException("("+name+") Timeout waiting: "+regexp);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private synchronized boolean readBuffer(String regexp) {
        while (!inputBuffer.isEmpty()){
            String st = inputBuffer.removeLast();
            System.out.println("("+name+") Testing: "+st);
            if (st.matches(regexp)) {
                System.out.println("("+name+") Match: "+st);
                return true;
            } else {
                System.out.println("("+name+") Not Match: "+st);
            }
        }
        return false;
    }

    @Override
    public void close() {
        inputBuffer.clear();
    }

}
