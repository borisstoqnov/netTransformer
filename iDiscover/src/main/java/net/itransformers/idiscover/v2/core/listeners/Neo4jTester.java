/*
 * Neo4jTester.java
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

package net.itransformers.idiscover.v2.core.listeners;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.rest.graphdb.query.RestCypherQueryEngine;
import org.neo4j.rest.graphdb.util.QueryResult;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: niau
 * Date: 11/24/14
 * Time: 2:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class Neo4jTester {
    public static void main(String[] args) {
        GraphDatabaseService graphdb = new org.neo4j.rest.graphdb.RestGraphDatabase("http://54.69.21.203:7474/db/data");
        RestCypherQueryEngine engine = new RestCypherQueryEngine(((RestGraphDatabase)graphdb).getRestAPI());
        Transaction tx = graphdb.beginTx();
        try {
            QueryResult<Map<String, Object>> result = engine.query("CREATE (n { name : 'And\'res', title : 'Developer' }) return n", null);
            org.neo4j.graphdb.Node n = (Node) result.iterator().next().get("n");
            System.out.println(n);
            tx.success();
        } catch (Exception e) {
            if (tx != null){
                tx.failure();
            }
        }
    }
}
