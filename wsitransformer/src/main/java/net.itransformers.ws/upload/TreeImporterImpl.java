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

package net.itransformers.ws.upload;/*
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

import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.kernel.GraphDatabaseAPI;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.ServletContext;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import java.util.List;
import java.util.Map;

// http://www.java-tutorial.ch/web-services/web-services-java-tutorial
@WebService(endpointInterface = "net.itransformers.ws.upload.TreeImporter")
public class TreeImporterImpl implements TreeImporter{

    GraphDatabaseAPI graphDatabaseAPI;

    @Resource
    private WebServiceContext context;

    @Override
    public Long importNode(Long rootNodeId, Node node) throws Exception {
        Transaction tr = getGraphDatabaseAPI().beginTx();
        try {
            org.neo4j.graphdb.Node root = null;
            if (rootNodeId != null){
                root = getGraphDatabaseAPI().getNodeById(rootNodeId);
            }
            org.neo4j.graphdb.Node result = doImportNode(root, node);
            tr.success();
            return result.getId();
        } catch (Exception e) {
            tr.failure();
            throw e;
        } finally {
            tr.finish();
        }

    }

    public GraphDatabaseAPI getGraphDatabaseAPI() {
        if (graphDatabaseAPI == null){
            ServletContext servletContext = (ServletContext) context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
            graphDatabaseAPI = (GraphDatabaseAPI) servletContext.getAttribute("graphdb");
        }
        return graphDatabaseAPI;
    }

    public org.neo4j.graphdb.Node doImportNode(org.neo4j.graphdb.Node root, Node node) throws Exception {
        org.neo4j.graphdb.Node neonode= getGraphDatabaseAPI().createNode();
        if (root != null) {
            root.createRelationshipTo(neonode, DynamicRelationshipType.withName("parent"));
        }
        Map<String, String> attributes = node.getAttributes();
        if (attributes != null) {
            for (Map.Entry<String, String> param : attributes.entrySet()) {
                neonode.setProperty(param.getKey(),param.getValue());
            }
        }
        List<Node> children = node.getChildren();
        if (children != null){
            for (Node child : children) {
                doImportNode(neonode, child);
            }
        }
        return neonode;
    }

}
