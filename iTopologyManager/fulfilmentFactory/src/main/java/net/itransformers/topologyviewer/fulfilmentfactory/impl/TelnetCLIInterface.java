package net.itransformers.topologyviewer.fulfilmentfactory.impl;

import com.wittams.gritty.Questioner;
import org.apache.commons.net.telnet.TelnetClient;

import java.awt.*;
import java.io.*;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 *
 **
 * 
 * Copyright 
 */
public class TelnetCLIInterface implements CLIInterface {

    private TelnetClient telnet = new TelnetClient();
    private InputStream in;
    private PrintStream out;
    private String prompt = "$";
    private Logger logger;

    private String user = null;
    private String host = null;
    private String password = null;
    private int port = 23;
    private int timeout;

    public TelnetCLIInterface(String host, String user, String password, String prompt, int timeout, Logger logger) {
        this.host = host;
        this.user = user;
        this.password = password;
        this.timeout = timeout;
//        this.prompt = host + ">"; // todo config this
//        this.prompt = "R112>"; // todo config this
        this.prompt = prompt;
        this.logger = logger;
        logger.info(String.format("Crating telnet cli interface. host: %s, port: %s, user: %s, pass: %s, timeout: %s, prompt: %s", host, port, user, password, timeout, prompt));
    }

    @Override
    public void open() {
//        if (true) return;
        init(new Questioner() {
            @Override
            public String questionVisible(String s, String s1) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public String questionHidden(String s) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void showMessage(String s) {
                //To change body of implemented methods use File | Settings | File Templates.
            }
        });
    }

    @Override
    public void sendData(String data) {
        this.write(data);
    }

    @Override
    public void readUntil(String regexp, int timeout) throws IOException {
        this.readUntil(regexp);
    }

    public void close() {
            in = null;
            out = null;
        try {
            telnet.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void resize(Dimension dimension, Dimension dimension1) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean init(Questioner q) {
        logger.info("Open telnet connection to: " + host + ":" + port);
        try {
            telnet.connect(host, 23);
        } catch (IOException e) {
            e.printStackTrace();
            q.showMessage(e.getMessage());
            return false;
        }
        telnet.setDefaultTimeout(timeout);
        in = telnet.getInputStream();
        out = new PrintStream(telnet.getOutputStream());
        return true;
    }

    public String readUntil(String regexp) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        logger.info("looking for : "+regexp);
        int chInt;
        StringBuffer sb = new StringBuffer();
        while ((chInt = reader.read()) != -1) {
            char  ch = (char)chInt;
            if (ch == '\n'){
                logger.info(sb.toString());
                sb.setLength(0);
                continue;
            }
            sb.append(ch);
            String st = sb.toString();
            if (st.matches(regexp)) {
                logger.info("### Found match: "+sb);
                return st;
            }
            if (!reader.ready()) {
                logger.info("Buffer is not ready. Read characters until now: "+ sb.toString());
            }
        }
        return null;
    }

    public void write(String value) {
        try {
            out.println(value);
            out.flush();
            logger.info(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return "ConnectRunnable";
    }

    public int read(byte[] buf, int offset, int length) throws IOException {
        return in.read(buf, offset, length);
    }

    public void write(byte[] bytes) throws IOException {
        out.write(bytes);
        out.flush();
    }
}
