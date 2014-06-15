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

import net.itransformers.idiscover.networkmodel.DiscoveredDeviceData;
import net.itransformers.idiscover.networkmodel.ObjectType;
import net.itransformers.idiscover.networkmodel.ParameterType;
import net.itransformers.idiscover.networkmodel.ParametersType;
import net.itransformers.idiscover.util.JaxbMarshalar;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.ws.rs.core.UriBuilder;
import java.io.*;
import java.util.List;

//@Deprecated
public class Neo4JDataImporter {
    public static int nodeCounter = 0;
    public static void main(String[] args) throws Exception {
        /*
        org.neo4j.rest.read_timeout=30
org.neo4j.rest.connect_timeout=30
org.neo4j.rest.driver="neo4j-rest-graphdb/1.8.RC1"
org.neo4j.rest.stream=true
org.neo4j.rest.batch_transactions=false (convert transaction scope into batch-rest-operations)
org.neo4j.rest.logging_filter=false (set to true if verbose request/response logging should be enabled)

 org.neo4j.rest.read_timeout=30
* org.neo4j.rest.connect_timeout=30
* org.neo4j.rest.driver="neo4j-rest-graphdb/1.8M07"
* org.neo4j.rest.stream=true
        * */
        System.setProperty("org.neo4j.rest.read_timeout","30");
        System.setProperty("org.neo4j.rest.connect_timeout","30");
        System.setProperty("org.neo4j.rest.driver","neo4j-rest-graphdb/1.8.RC1");
        System.setProperty("org.neo4j.rest.stream","true");
        System.setProperty("org.neo4j.rest.batch_transactions","true");
        System.setProperty("org.neo4j.rest.logging_filter","false");
//        org.springframework.data.neo4j.rest.SpringRestGraphDatabase     d;
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("neo4j-spring.xml");
        GraphDatabaseService graphdb = applicationContext.getBean("graphDbService", GraphDatabaseService.class);
//        GraphDatabaseService gds = new RestGraphDatabase("http://localhost:7474/db/data");
//        GraphDatabaseService graphdb = new RestGraphDatabase("http://localhost:7474/db/data",username,password);
        doImport(graphdb);

    }
    public static void main1(String[] args) throws Exception {
// let the database accept remote neo4j-shell connections
        GraphDatabaseService graphdb = new GraphDatabaseFactory()
                .newEmbeddedDatabaseBuilder("target/bigDB")
//                .setConfig(GraphDatabaseSettings.node_keys_indexable, "name,objectType")
//                .setConfig( GraphDatabaseSettings.relationship_keys_indexable, "relProp1,relProp2" )
                .setConfig(GraphDatabaseSettings.node_auto_indexing, Boolean.TRUE.toString())
                .setConfig(GraphDatabaseSettings.relationship_auto_indexing, Boolean.TRUE.toString())
                .newGraphDatabase();

//        ServerConfigurator config;
//        config = new ServerConfigurator( graphdb );
//// let the server endpoint be on a custom port
//        config.configuration().setProperty(
//                Configurator.WEBSERVER_PORT_PROPERTY_KEY, 7575 );
//
//        WrappingNeoServerBootstrapper srv;
//        srv = new WrappingNeoServerBootstrapper( graphdb, config );
//        srv.start();
////        Transaction tr = graphdb.beginTx();
////        try {
//            doImport(graphdb);
//            tr.success();
//        } catch (Exception e) {
//            tr.failure();
//            throw e;
//        } finally {
//            tr.finish();
//        }

    }

    private static void doImport(GraphDatabaseService graphdb) throws Exception {
        //        IndexManager index = graphdb.index();
//        Index<Node> actors = index.forNodes( "actors" );
//        actors.add( fishburn, "name", fishburn.getProperty( "name" ) );

//       importData(graphdb);
// add some data first
        File dir = new File("ideas/src/main/resources/device-xml");

        Node root = createRootNetworkNode(graphdb);
        File[] files = dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".xml");
            }
        });

        long start = System.currentTimeMillis();
//        importData(graphdb, root, new File(dir, "device-data-MAG-112-1.xml"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Press enter to continue");
//        reader.readLine();
        for (File file : files) {
            System.out.println("importing file: "+file.getName());
            importData(graphdb, root, file);
            System.out.println("Press enter to continue");
//            reader.readLine();
            Thread.sleep(300);
        }
        System.out.println("Created nodes: "+nodeCounter);
        System.out.println("Imported ... "+(System.currentTimeMillis()-start)/1000 + " seconds");
    }

    public static org.neo4j.graphdb.Node createRootNetworkNode(GraphDatabaseService dbApi) throws Exception {
        Transaction tr = dbApi.beginTx();
        try {
            org.neo4j.graphdb.Node node= dbApi.createNode();
            nodeCounter++;
            node.setProperty("name","network");
            System.out.println("before to commit transaction (createRootNetworkNode)");
            tr.success();
            System.out.println("after to commit transaction (createRootNetworkNode)");
            return node;
        } catch (Exception e) {
            tr.failure();
            throw e;
        } finally {
            System.out.println("before to finish transaction (createRootNetworkNode)");
            tr.finish();
            System.out.println("after to finish transaction (createRootNetworkNode)");
        }
    }

    public static void importData(GraphDatabaseService dbApi, Node root, File file) throws Exception {
        Transaction tr = dbApi.beginTx();
        try {
            FileInputStream is = new FileInputStream(file);
            DiscoveredDeviceData discoveryManagerType = null;
            try {
                discoveryManagerType = JaxbMarshalar.unmarshal(DiscoveredDeviceData.class, is);
            } finally {
                is.close();
            }
            importDiscoveredDeviceData(dbApi,root, discoveryManagerType);
            System.out.println("before to commit transaction");
            tr.success();
            System.out.println("after to commit transaction");
        } catch (Exception e) {
            e.printStackTrace();
            tr.failure();
        } finally {
            System.out.println("before to finish transaction");
            tr.finish();
            System.out.println("after to finish transaction");
        }
    }
    public static void importDiscoveredDeviceData(GraphDatabaseService dbApi, Node root, DiscoveredDeviceData discoveryManagerType) throws Exception {
        org.neo4j.graphdb.Node node= dbApi.createNode();
        nodeCounter++;
        root.createRelationshipTo(node,DynamicRelationshipType.withName("parent"));
//        System.out.println("Created node id= "+node.getId());
        System.out.println("Created node count= "+nodeCounter);
        node.setProperty("name",discoveryManagerType.getName());
        node.setProperty("objectType","Node"); // Hardcoded because the DiscoveredDeviceData is not natural data type
        ParametersType parameters = discoveryManagerType.getParameters();
        for (ParameterType param : parameters.getParameter()) {
            String nameURI = UriBuilder.fromPath(param.getName()).build("").toString();
            node.setProperty(nameURI,param.getValue());
        }
        List<ObjectType> objectTypeList = discoveryManagerType.getObject();
        for (ObjectType objectType : objectTypeList) {
            org.neo4j.graphdb.Node child = importObjectType(dbApi, objectType);
            node.createRelationshipTo(child, DynamicRelationshipType.withName("parent"));
        }
    }

    public static org.neo4j.graphdb.Node importObjectType(GraphDatabaseService dbApi, ObjectType objectType) throws Exception {
        org.neo4j.graphdb.Node node= dbApi.createNode();
        nodeCounter++;
//        System.out.println("Created node id= "+node.getId());
        System.out.println("Created node count= "+nodeCounter);
        if (objectType.getName() != null) node.setProperty("name",objectType.getName());
        node.setProperty("objectType",objectType.getObjectType());
        ParametersType parameters = objectType.getParameters();
        for (ParameterType param : parameters.getParameter()) {
            String nameURI = UriBuilder.fromPath(param.getName()).build("").toString();
            node.setProperty(nameURI,param.getValue());
        }
        List<ObjectType> objectTypeList = objectType.getObject();
        for (ObjectType childObjectType : objectTypeList) {
            org.neo4j.graphdb.Node child = importObjectType(dbApi, childObjectType);
            node.createRelationshipTo(child, DynamicRelationshipType.withName("parent"));
        }
        return node;
    }

}
