/*
 * SimulationCLIAdapter.java
 *
 * This work is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * This work is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * Copyright (c) 2010-2016 iTransformers Labs. All rights reserved.
 */

package net.itransformers.topologyviewer.fulfilmentfactory.impl;

import java.io.IOException;
import java.util.LinkedList;

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
