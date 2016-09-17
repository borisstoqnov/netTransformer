/*
 * Neo4jContextListener.java
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

package neo4j;/*
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
//
//import org.neo4j.graphdb.factory.GraphDatabaseFactory;
//import org.neo4j.graphdb.factory.GraphDatabaseSetting;
//import org.neo4j.graphdb.factory.GraphDatabaseSettings;
//import org.neo4j.kernel.GraphDatabaseAPI;
//import org.neo4j.server.WrappingNeoServerBootstrapper;
//import org.neo4j.server.configuration.Configurator;
//import org.neo4j.server.configuration.ServerConfigurator;
//import org.neo4j.shell.ShellSettings;

//import javax.servlet.ServletContextEvent;
//import javax.servlet.ServletContextListener;

public class Neo4jContextListener{// implements ServletContextListener {
//    private WrappingNeoServerBootstrapper srv;
//
//    @Override
//    public void contextInitialized(ServletContextEvent servletContextEvent) {
//        GraphDatabaseAPI graphdb = (GraphDatabaseAPI) new GraphDatabaseFactory()
//                .newEmbeddedDatabaseBuilder( "target/bigDB" )
//                .setConfig( ShellSettings.remote_shell_enabled, "true" )
//                .setConfig( GraphDatabaseSettings.node_keys_indexable, "name,objectType" )
////                .setConfig( GraphDatabaseSettings.relationship_keys_indexable, "relProp1,relProp2" )
//                .setConfig( GraphDatabaseSettings.node_auto_indexing, "true" )
//                .setConfig( GraphDatabaseSettings.relationship_auto_indexing, "true" )
//                .newGraphDatabase();
//        ServerConfigurator config;
//        config = new ServerConfigurator( graphdb );
//// let the server endpoint be on a custom port
//        config.configuration().setProperty(
//                Configurator.WEBSERVER_PORT_PROPERTY_KEY, 7474 );
//
//        srv = new WrappingNeoServerBootstrapper( graphdb, config );
//        System.out.println("Starting NEO4J server");
//        srv.start();
//
//        servletContextEvent.getServletContext().setAttribute("graphdb",graphdb);
//    }
//
//    @Override
//    public void contextDestroyed(ServletContextEvent servletContextEvent) {
//        System.out.println("Stopping NEO4J server");
//        srv.stop();
//    }
}
