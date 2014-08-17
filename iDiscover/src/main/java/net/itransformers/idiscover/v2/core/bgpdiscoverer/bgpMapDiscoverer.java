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
import net.itransformers.utils.XsltTransformer;
import org.apache.log4j.Logger;
import org.javamrt.launcher.RouteDumper;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;

//         com.sun.jersey.api.client.Client;
//import com.sun.jersey.api.client.ClientResponse;
//import com.sun.jersey.api.client.WebResource;

public class bgpMapDiscoverer implements NodeDiscoverer {
    static Logger logger = Logger.getLogger(bgpMapDiscoverer.class);
    String fileLocation =null;


    public bgpMapDiscoverer(Map<String, String> attributes) throws Exception {

        fileLocation=attributes.get("file");
       // walker = (JsonDiscoverer) new DefaultDiscovererFactory().createDiscoverer(resource);
    }


    @Override
    public String probe(ConnectionDetails connectionDetails) {
        String nodeId = connectionDetails.getParam("nodeId");

        return nodeId;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public NodeDiscoveryResult discover(ConnectionDetails connectionDetails) {
        NodeDiscoveryResult result = new NodeDiscoveryResult();
        String[] args = new String[3];
        args[0] = "-z";
        args[1] ="-x";

        String pathToFile = connectionDetails.getParam("pathToFile");
        String date = connectionDetails.getParam("date");
        String nodeId = connectionDetails.getParam("nodeId");
        String version = connectionDetails.getParam("version");

        args[2] = pathToFile;

        RouteDumper dumper = new RouteDumper(args,false);
       // boolean oldall =  dumper.init(args,false);
      //  Getopts prueba = new Getopts(args, "46hmP:p:o:z:v:t:x");


     //   Dumper xmlDumper = new Dumper(prueba,false,args);
        String xml = dumper.dumpToXml();


        //Assemble EntryUri
       //connectionDetails.getParam("protocol") +"://" + connectionDetails.getParam("ipAddress")  + ":" + connectionDetails.getParam("port");
        logger.debug ("pathToFile: " + pathToFile+ "\n");

        logger.debug ("Node id: " + nodeId+ "\n");
        logger.debug ("Date: " + date + "\n");
        result.setNodeId(nodeId);
        result.setDiscoveredData("rawData",xml.getBytes());
        result.setDiscoveredData("version",version);

        return result;
    }

 private ByteArrayOutputStream xsltTranform(File xsltTransformator, String rawData, Map settings) throws TransformerException, IOException, SAXException, ParserConfigurationException {
     XsltTransformer transformer = new XsltTransformer();

     ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
     ByteArrayInputStream inputStream;
     inputStream = new ByteArrayInputStream(rawData.getBytes());
     transformer.transformXML(inputStream, xsltTransformator, outputStream1, settings, null);
     return outputStream1;

 }
}
