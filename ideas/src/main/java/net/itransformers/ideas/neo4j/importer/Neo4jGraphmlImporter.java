/*
 * Neo4jGraphmlImporter.java
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

package net.itransformers.ideas.neo4j.importer;

import net.itransformers.utils.neo4j.merge.Neo4jGraphmlMerger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;

import java.io.File;
import java.io.IOException;

/**
 * Created by vasko on 6/16/14.
 */
public class Neo4jGraphmlImporter {
    public static void main(String[] args) throws IOException {

        GraphDatabaseService graphdb = new org.neo4j.rest.graphdb.RestGraphDatabase("http://193.19.172.133:7474/db/data");
        Neo4jGraphmlMerger neo4jMerger = new Neo4jGraphmlMerger(graphdb,"test_1");
        Transaction tx = null;
        try {
            tx = graphdb.beginTx();

            File file = new File("src/main/resources/graphml/1.graphml");
            neo4jMerger.merge(file);
            tx.success();
        } catch (Exception e) {
            if (tx != null) tx.failure();
        } finally {
            if (tx != null) tx.close();
        }

    }
}
