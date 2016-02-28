/*
 * TopologyManagementResource.java
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

package net.itransformers.ws.rs;

import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.io.GraphMLMetadata;
import edu.uci.ics.jung.io.GraphMLReader;
import org.apache.commons.collections15.Factory;
import org.xml.sax.SAXException;

import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.print.Book;
import java.io.*;
import java.net.URI;
import java.util.List;

// The Java class will be hosted at the URI path "/helloworld"
@Path("/topology-management")
public class TopologyManagementResource {
    @Context
    ServletContext context;
    private Hypergraph<String, String> graph;


    @Context
    UriInfo uriInfo;
    private GraphMLReader gmlr;

    @Context
    private <G extends Hypergraph<String,String>> void init(Factory<G> factory, File graphmlFile) throws IOException, SAXException, ParserConfigurationException {
        gmlr = new GraphMLReader(null, null);
        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(graphmlFile)));
        graph = factory.create();
        gmlr.load(in, graph);

    }

    @GET
    @Produces({MediaType.APPLICATION_XML+";version=1.0"})
    @Path("/nodes")
    public String getNodesAsXml() throws IOException, SAXException, ParserConfigurationException {
        if (graph == null) {
            File graphmlFile = new File(context.getInitParameter("config.file"));
            this.init(DirectedSparseGraph.<String, String>getFactory(), graphmlFile);
        }
        // Return some cliched textual content
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("<nodes size = \"%s\" uri=\">/api/topology-management/nodes\">\n", graph.getVertices().size()));
        for (String vertex : graph.getVertices()) {
            sb.append(String.format("\t<node id=\"%s\"/>\n",vertex));
        }
        sb.append("</nodes>\n");
        return sb.toString();
    }

  //  http://localhost:8080/wsitransformer/rest/topology-management/nodes/
//    @GET
//    @Produces("text/html")
//    @Path("/nodes")
    public String getNodesAsHtml() throws IOException, SAXException, ParserConfigurationException {
        if (graph == null) {
            File graphmlFile = new File(context.getInitParameter("config.file"));
            this.init(DirectedSparseGraph.<String, String>getFactory(), graphmlFile);
        }
        // Return some cliched textual content
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("<pre>&lt;nodes size=\"%s\"&gt;<br/>", graph.getVertices().size()));

        for (String vertex : graph.getVertices()) {
            UriBuilder ub = uriInfo.getAbsolutePathBuilder();
            URI userUri = ub.path(vertex).build();
            sb.append(String.format("\t&lt;node id=\"<a href=\"%s\">%s</a>\"/&gt;<br/>", userUri.toASCIIString(),vertex));
        }
        sb.append("&lt;/nodes&gt;</pre>");
        return sb.toString();
    }

//    @GET
//    @Produces(MediaType.APPLICATION_XML+";version=1.0")
//    @Path("/nodes/{nodeId}/")
    public String getNodeAsXml(@PathParam("nodeId") String nodeId) throws IOException, SAXException, ParserConfigurationException {
        if (graph == null) {
            File graphmlFile = new File(context.getInitParameter("config.file"));
            this.init(DirectedSparseGraph.<String, String>getFactory(), graphmlFile);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("<node id=\"%s\">\n", nodeId));
        for (Object mdKey : gmlr.getVertexMetadata().keySet()) {
            GraphMLMetadata<String> o = (GraphMLMetadata<String>) gmlr.getVertexMetadata().get(mdKey);
            String value = o.transformer.transform((String) nodeId);
            if (value != null) {
                sb.append(String.format("\t<data key=\"%s\">%s</data>\n",mdKey, value));
            }
        }
        sb.append("</node>");
        return sb.toString();
    }
// http://localhost:8080/wsitransformer/rest/topology-management/nodes/R1
    @GET
    @Produces(MediaType.TEXT_HTML+";version=1.0")
    @Path("/nodes/{nodeId}/")
    public String getNodeAsHtml(@PathParam("nodeId") String nodeId) throws IOException, SAXException, ParserConfigurationException {
        if (graph == null) {
            File graphmlFile = new File(context.getInitParameter("config.file"));
            this.init(DirectedSparseGraph.<String, String>getFactory(), graphmlFile);
        }
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("<pre>&lt;node id=\"%s\"&gt;<br/>", nodeId));
        for (Object mdKey : gmlr.getVertexMetadata().keySet()) {
            GraphMLMetadata<String> o = (GraphMLMetadata<String>) gmlr.getVertexMetadata().get(mdKey);
            String value = o.transformer.transform((String) nodeId);
            if (value != null) {
                sb.append(String.format("\t&lt;data key=\"%s\"&gt;%s&lt;/data&gt;<br/>",mdKey, value));
            }
        }
        sb.append("&lt;/node&gt;</pre>");
        return sb.toString();
    }
    @GET
    @Produces(MediaType.TEXT_HTML+";version=1.0")
    @Path("/nodes")
    public String getNodeAsHtmlById(@QueryParam("nodeId") String nodeId) throws IOException, SAXException, ParserConfigurationException {
        if (graph == null) {
            File graphmlFile = new File(context.getInitParameter("config.file"));
            this.init(DirectedSparseGraph.<String, String>getFactory(), graphmlFile);
        }
        if (nodeId == null) {
            return getNodesAsHtml();
        }
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("<pre>&lt;node id=\"%s\"&gt;<br/>", nodeId));
        for (Object mdKey : gmlr.getVertexMetadata().keySet()) {
            GraphMLMetadata<String> o = (GraphMLMetadata<String>) gmlr.getVertexMetadata().get(mdKey);
            String value = o.transformer.transform((String) nodeId);
            if (value != null) {
                sb.append(String.format("\t&lt;data key=\"%s\"&gt;%s&lt;/data&gt;<br/>",mdKey, value));
            }
        }
        sb.append("&lt;/node&gt;</pre>");
        return sb.toString();
    }
}