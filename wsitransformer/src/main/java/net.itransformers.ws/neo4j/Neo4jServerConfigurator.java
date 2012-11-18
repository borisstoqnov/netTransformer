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

package net.itransformers.ws.neo4j;

import org.neo4j.kernel.GraphDatabaseAPI;
import org.neo4j.server.configuration.Configurator;
import org.neo4j.server.configuration.ServerConfigurator;
import org.neo4j.server.configuration.ThirdPartyJaxRsPackage;

import java.util.Map;
import java.util.Set;

public class Neo4jServerConfigurator implements Configurator {
	private final ServerConfigurator configurator;

	public Neo4jServerConfigurator(GraphDatabaseAPI db, Map<String, Object> initialProperties) {
		configurator = new ServerConfigurator(db);
		for (Map.Entry<String, Object> entry : initialProperties.entrySet()) {
			configurator.configuration().addProperty(entry.getKey(), entry.getValue());
		}
	}

	public Object getProperty(String property) {
		return configurator.configuration().getProperty(property);
	}

	public void setProperty(String property, Object value) {
		configurator.configuration().setProperty(property, value);
	}

	public void addProperty(String property, Object value) {
		configurator.configuration().addProperty(property, value);
	}

	@Override
	public org.apache.commons.configuration.Configuration configuration() {
		return configurator.configuration();
	}

	@Override
	public Map<String, String> getDatabaseTuningProperties() {
		return configurator.getDatabaseTuningProperties();
	}

	@Override
	public Set<ThirdPartyJaxRsPackage> getThirdpartyJaxRsClasses() {
		return configurator.getThirdpartyJaxRsClasses();
	}
}
