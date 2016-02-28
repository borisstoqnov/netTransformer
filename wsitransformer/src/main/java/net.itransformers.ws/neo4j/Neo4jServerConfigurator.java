/*
 * Neo4jServerConfigurator.java
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

package net.itransformers.ws.neo4j;

//import org.neo4j.kernel.GraphDatabaseAPI;
//import org.neo4j.server.configuration.Configurator;
//import org.neo4j.server.configuration.ServerConfigurator;
//import org.neo4j.server.configuration.ThirdPartyJaxRsPackage;

import java.util.Map;
import java.util.Set;

public class Neo4jServerConfigurator {//implements Configurator {
//	private final ServerConfigurator configurator;
//
//	public Neo4jServerConfigurator(GraphDatabaseAPI db, Map<String, Object> initialProperties) {
//		configurator = new ServerConfigurator(db);
//		for (Map.Entry<String, Object> entry : initialProperties.entrySet()) {
//			configurator.configuration().addProperty(entry.getKey(), entry.getValue());
//		}
//	}
//
//	public Object getProperty(String property) {
//		return configurator.configuration().getProperty(property);
//	}
//
//	public void setProperty(String property, Object value) {
//		configurator.configuration().setProperty(property, value);
//	}
//
//	public void addProperty(String property, Object value) {
//		configurator.configuration().addProperty(property, value);
//	}
//
//	@Override
//	public org.apache.commons.configuration.Configuration configuration() {
//		return configurator.configuration();
//	}
//
//	@Override
//	public Map<String, String> getDatabaseTuningProperties() {
//		return configurator.getDatabaseTuningProperties();
//	}
//
//	@Override
//	public Set<ThirdPartyJaxRsPackage> getThirdpartyJaxRsClasses() {
//		return configurator.getThirdpartyJaxRsClasses();
//	}
}
