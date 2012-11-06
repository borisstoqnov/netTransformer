/*
 * iTransformer is an open source tool able to discover and transform
 *  IP network infrastructures.
 *  Copyright (C) 2012  http://itransformers.net
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */



import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import net.i2cat.netconf.errors.NetconfProtocolException;
import net.i2cat.netconf.errors.TransportNotImplementedException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.i2cat.netconf.INetconfSession;
import net.i2cat.netconf.NetconfSession;
import net.i2cat.netconf.SessionContext;
import net.i2cat.netconf.errors.TransportException;
import net.i2cat.netconf.messageQueue.MessageQueueListener;
import net.i2cat.netconf.rpc.Capability;
import net.i2cat.netconf.rpc.Query;
import net.i2cat.netconf.rpc.QueryFactory;
import net.i2cat.netconf.rpc.RPCElement;
import net.i2cat.netconf.rpc.Reply;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * User: niau
 * Date: 11/6/12
 * Time: 10:02 PM
 * To change this template use File | Settings | File Templates.
 */
public class Netconf4jTest {

    public static void main(String[] args) throws ConfigurationException, URISyntaxException, TransportNotImplementedException, NetconfProtocolException, TransportException {
         Log     log     = LogFactory.getLog(Netconf4jTest.class);

        SessionContext   sessionContext;
        INetconfSession  session;

            sessionContext = new SessionContext();


            sessionContext.setURI(new URI(System.getProperty("net.i2cat.netconf.test.transportUri", "ssh://comptel:C0mptel321@88.203.203.226:22")));

        session = new NetconfSession(sessionContext);
        try {
        session.connect();
            session.getServerCapabilities();
        session.getClientCapabilities();
        Query queryGetConfig = QueryFactory.newGetConfig("running", null, null);

        log.info(queryGetConfig.toXML());

        Reply reply = session.sendSyncQuery(queryGetConfig);
            System.out.println("<" + reply.getContainName() + ">\n" + reply.getContain() + "\n<\\" + reply.getContainName() + ">");

            if (reply.containsErrors()) {
            System.out.println( reply.getErrors());
            System.out.println("The response received errors");
        }

        reply = session.sendSyncQuery(QueryFactory.newCloseSession());

        if (reply.containsErrors()) {
            System.out.println(reply.getErrors());
            System.out.println("The response received errors");
        }

        session.disconnect();
        System.out.println("<" + reply.getContainName() + ">\n" + reply.getContain() + "\n<\\" + reply.getContainName() + ">");
    } catch (Exception e) {
        e.printStackTrace();
        System.out.println(e.getMessage());
    }

    session.disconnect();
    }

}
