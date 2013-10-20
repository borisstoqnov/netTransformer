/*
 * iTransformer is an open source tool able to discover IP networks
 * and to perform dynamic data data population into a xml based inventory system.
 * Copyright (C) 2010  http://itransformers.net
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 *
 */
package net.itransformers.topologyviewer.rightclick.impl.telnet;

import com.wittams.gritty.Questioner;
import com.wittams.gritty.Tty;
import org.apache.commons.net.telnet.TelnetClient;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;

public class JTelnetTty implements Tty {

    private TelnetClient telnet = new TelnetClient();
    private InputStream in;
    private PrintStream out;


    private String user = null;
    private String host = null;
    private String password = null;
    private int port = 23;
    private int timeout;

    public JTelnetTty(String host, String user, String password, int timeout) {
        this.host = host;
        this.user = user;
        this.password = password;


//        this.prompt = "R112>"; // todo config this
    }
//    public JTelnetTty(String host, int port, int timeout) {
//        this.host = host;
//        this.port = port;
//        this.timeout = timeout;
//    }

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

        getAuthDetails(q);
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
        readUntil("Username: ");
        write(user);
        readUntil("Password: ");
        write(password);

        // Advance to a prompt
        readUntil("*");
        return true;
    }

    public String readUntil(String pattern) {
        try {
            char lastChar = pattern.charAt(pattern.length() - 1);
            StringBuffer sb = new StringBuffer();
            boolean found = false;
            char ch = (char) in.read();
            while (true) {
                System.out.print(ch);
                sb.append(ch);
                if (ch == lastChar) {
                    if (sb.toString().endsWith(pattern)) {
                        return sb.toString();
                    }
                }
                ch = (char) in.read();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void write(String value) {
        try {
            out.println(value);
            out.flush();
            System.out.println(value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getAuthDetails(Questioner q) {
        while (true) {
            if (host == null)
                host = q.questionVisible("host:", "localhost");
            if (host == null || host.length() == 0)
                continue;
            if (host.indexOf(':') != -1) {
                final String portString = host.substring(host.indexOf(':') + 1);
                try {
                    port = Integer.parseInt(portString);
                } catch (final NumberFormatException eee) {
                    q.showMessage("Could not parse port : " + portString);
                    continue;
                }
                host = host.substring(0, host.indexOf(':'));
            }

            if (user == null)
                user = q.questionVisible("user:", System.getProperty("user.name").toLowerCase());
            if (host == null || host.length() == 0)
                continue;
            break;
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
