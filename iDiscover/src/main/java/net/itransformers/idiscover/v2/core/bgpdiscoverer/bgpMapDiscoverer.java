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

package net.itransformers.idiscover.v2.core.bgpdiscoverer;

import net.itransformers.idiscover.v2.core.NodeDiscoverer;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import org.apache.commons.io.output.NullOutputStream;
import org.apache.log4j.Logger;
import org.javamrt.dumper.CmdLineParser;
import org.javamrt.dumper.Route2GraphmlDumper;
import org.javamrt.dumper.structures.ASContainer;

import java.io.*;
import java.util.Map;

//         com.sun.jersey.api.client.Client;
//import com.sun.jersey.api.client.ClientResponse;
//import com.sun.jersey.api.client.WebResource;

public class bgpMapDiscoverer implements NodeDiscoverer {
    static Logger logger = Logger.getLogger(bgpMapDiscoverer.class);
    String fileLocation =null;


    public bgpMapDiscoverer(Map<String, String> attributes)  {

        fileLocation=attributes.get("file");
       // walker = (JsonDiscoverer) new DefaultDiscovererFactory().createDiscoverer(resource);
    }


    @Override
    public String probe(ConnectionDetails connectionDetails) {
        String nodeId = connectionDetails.getParam("nodeId");

        return nodeId;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public NodeDiscoveryResult discover(ConnectionDetails connectionDetails)  {

        NodeDiscoveryResult result = new NodeDiscoveryResult();
        String pathToFile = connectionDetails.getParam("pathToFile");
        String nodeId = connectionDetails.getParam("nodeId");
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


//        logger.debug ("Node id: " + nodeId+ "\n");
//        logger.debug ("Date: " + date + "\n");
        result.setNodeId(nodeId);
        result.setDiscoveredData("version",version);
        result.setDiscoveredData("graphml",writer.toString().getBytes());
        return result;
    }

}
