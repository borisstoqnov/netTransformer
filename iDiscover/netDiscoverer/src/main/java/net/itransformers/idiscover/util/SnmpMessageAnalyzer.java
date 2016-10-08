/*
 * SnmpMessageAnalyzer.java
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

package net.itransformers.idiscover.util;

import org.snmp4j.*;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.TcpAddress;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.transport.DefaultTcpTransportMapping;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Deprecated
public class SnmpMessageAnalyzer implements CommandResponder {
    public static Pattern p = Pattern.compile("^(.*DefaultUdpTransportMapping.*message .*with length.*: )(.*)$");
    public static void main(String[] args) throws IOException {
        InputStream in = System.in;
        OutputStream out = System.out;
        if (args.length == 1) {
            try {
                in = new FileInputStream(args[0]);
            } catch (FileNotFoundException e) {
                System.out.println("File not found: "+e.getMessage());
            }
        } else if (args.length == 2) {
              out = new FileOutputStream(args[1]);
        }
        doAnalyze(in, out);
    }

    public static void doAnalyze(InputStream in, OutputStream out) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String s;
        PrintWriter writer = new PrintWriter(out);
        while ((s = reader.readLine()) != null) {
            Matcher m = p.matcher(s);
            if (m.find()) {
                String text =  m.group(1);
                String msg = m.group(2);
                writer.print(text);
                writer.println(snmpMessageToHumanReadable(msg));
            } else {
                writer.println(s);
            }
        }
        writer.flush();
    }

    static PDU snmpMessageToHumanReadable(String s) throws IOException {
        final OctetString message = OctetString.fromHexString(s, ':');
        final Address address = new UdpAddress();
        final TransportMapping transportMapping = (address instanceof UdpAddress) ?
                new DefaultUdpTransportMapping((UdpAddress) address)
                : new DefaultTcpTransportMapping((TcpAddress) address);
        final MessageDispatcher messageDispatcher = new
                MessageDispatcherImpl();
        SecurityProtocols.getInstance().addDefaultProtocols();
        final SnmpMessageAnalyzer snmpMessageAnalyzer = new SnmpMessageAnalyzer();
        final CommandResponder commandResponder = snmpMessageAnalyzer;
        messageDispatcher.addCommandResponder(commandResponder);
        messageDispatcher.addMessageProcessingModel(new MPv1());
        messageDispatcher.addMessageProcessingModel(new MPv2c());
        messageDispatcher.addMessageProcessingModel(new MPv3());
        messageDispatcher.processMessage(transportMapping, address, ByteBuffer.wrap(message.getValue()));
        PDU result = snmpMessageAnalyzer.msg;
        snmpMessageAnalyzer.msg = null;
        return result;
    }

    private PDU msg;
    public void processPdu(CommandResponderEvent commandResponderEvent) {
        msg = commandResponderEvent.getPDU();
    }
}
