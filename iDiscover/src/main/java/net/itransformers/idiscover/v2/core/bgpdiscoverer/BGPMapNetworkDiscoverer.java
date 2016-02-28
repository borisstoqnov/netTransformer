/*
 * BGPMapNetworkDiscoverer.java
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

package net.itransformers.idiscover.v2.core.bgpdiscoverer;

import net.itransformers.idiscover.v2.core.ANetworkDiscoverer;
import net.itransformers.idiscover.v2.core.NetworkDiscoveryResult;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.log4j.Logger;
import org.javamrt.dumper.CmdLineParser;
import org.javamrt.dumper.Route2GraphmlDumper;
import org.javamrt.dumper.structures.ASContainer;

import java.io.*;
import java.util.List;
import java.util.Map;

//         com.sun.jersey.api.client.Client;
//import com.sun.jersey.api.client.ClientResponse;
//import com.sun.jersey.api.client.WebResource;

public class BGPMapNetworkDiscoverer extends ANetworkDiscoverer {
    static Logger logger = Logger.getLogger(BGPMapNetworkDiscoverer.class);
    String fileLocation =null;


    public BGPMapNetworkDiscoverer(Map<String, String> attributes)  {

        fileLocation=attributes.get("file");
       // walker = (JsonDiscoverer) new DefaultDiscovererFactory().createDiscoverer(resource);
    }


    @Override
    public NetworkDiscoveryResult discoverNetwork(List<ConnectionDetails> connectionDetailsList,int depth) {
        NetworkDiscoveryResult result = new NetworkDiscoveryResult();

        for (ConnectionDetails connectionDetails : connectionDetailsList) {


            if (!"javaMRT".equals(connectionDetails.getConnectionType())){
                logger.debug("Connection details are not for javaMRTfile");

                return null;
            }

            String pathToFile = connectionDetails.getParam("pathToFile");
            String version = connectionDetails.getParam("version");


            String[] args = new String[4];
            args[0] = "-f";


            args[1] = pathToFile;


            args[2] = "-o";
            String outputFile="network.graphml";
            args[3] = outputFile;

            Map<String, String> params = CmdLineParser.parseCmdLine(args);



            StringWriter writer = new StringWriter();

            String file = null;
            OutputStream logOutputStream = new NullOutputStream();

            if (params.containsKey("-f")) {
                file = params.get("-f");
            } else {
                logger.info("no file passed");
                System.exit(1);
            }

            ASContainer ases = new ASContainer();
            System.out.println("Start reading MRT file");
            File tmpEdgeFile = null;
            try {
                tmpEdgeFile = File.createTempFile("test" + "_", ".txt");
                System.out.println("Creating edge tmp file: "+tmpEdgeFile.getAbsolutePath());
                Writer edgeWriter =  new PrintWriter(tmpEdgeFile);

                Route2GraphmlDumper.dumpToXmlString(new String[]{file}, new PrintWriter(logOutputStream), edgeWriter, ases);
                edgeWriter.close();
                Route2GraphmlDumper.dumpGraphml(ases, writer, tmpEdgeFile);

            }catch (IOException e){
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.

            } finally {
                tmpEdgeFile.delete();


            }

            NodeDiscoveryResult result1 = new NodeDiscoveryResult();

            result1.setDiscoveredData("version", version);
            result1.setDiscoveredData("graphml", writer.toString().getBytes());
            result1.setDiscoveredData("discoverer", "javaMRT");

            result.addDiscoveredData(file, result1);

        }
        fireNetworkDiscoveredEvent(result);

        return result;
    }



}
