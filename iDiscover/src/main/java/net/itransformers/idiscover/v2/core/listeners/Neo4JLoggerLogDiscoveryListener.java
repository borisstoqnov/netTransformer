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

package net.itransformers.idiscover.v2.core.listeners;/*
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

import net.itransformers.idiscover.networkmodel.DiscoveredDeviceData;
import net.itransformers.idiscover.networkmodel.ObjectType;
import net.itransformers.idiscover.networkmodel.ParameterType;
import net.itransformers.idiscover.networkmodel.ParametersType;
import net.itransformers.idiscover.util.JaxbMarshalar;
import net.itransformers.idiscover.v2.core.NodeDiscoveryListener;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.utils.XmlFormatter;
import net.itransformers.utils.XsltTransformer;
import org.apache.log4j.Logger;
//import org.neo4j.graphdb.DynamicRelationshipType;
//import org.neo4j.graphdb.GraphDatabaseService;
//import org.neo4j.graphdb.Node;
//import org.neo4j.graphdb.Transaction;

//import javax.ws.rs.core.UriBuilder;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.List;

public class Neo4JLoggerLogDiscoveryListener implements NodeDiscoveryListener {
    static Logger logger = Logger.getLogger(Neo4JLoggerLogDiscoveryListener.class);
    String label;
    private String xsltFileName;

    public Neo4JLoggerLogDiscoveryListener() {
    }

    @Override
    public void nodeDiscovered(NodeDiscoveryResult discoveryResult) {

        String deviceName = discoveryResult.getNodeId();
        DiscoveredDeviceData discoveredDeviceData = (DiscoveredDeviceData) discoveryResult.getDiscoveredData("deviceData");
        ByteArrayOutputStream graphMLOutputStream = new ByteArrayOutputStream();


        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            JaxbMarshalar.marshal(discoveredDeviceData, out, "DiscoveredDevice");
            File xsltFile = new File(xsltFileName);
            XsltTransformer transformer = new XsltTransformer();
            transformer.transformXML(new ByteArrayInputStream(out.toByteArray()), xsltFile,graphMLOutputStream, null,null);


        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
    public String getXsltFileName() {
        return xsltFileName;
    }

    public void setXsltFileName(String xsltFileName) {
        this.xsltFileName = xsltFileName;
    }

}
