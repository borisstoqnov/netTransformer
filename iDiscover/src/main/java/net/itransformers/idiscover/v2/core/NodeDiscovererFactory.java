/*
 * iMap is an open source tool able to upload Internet BGP peering information
 *  and to visualize the beauty of Internet BGP Peering in 2D map.
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

package net.itransformers.idiscover.v2.core;

import net.itransformers.idiscover.discoveryhelpers.xml.XmlDiscoveryHelperFactory;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

public class NodeDiscovererFactory {

    public static NodeDiscoverer createNodeDiscoverer(String connectionType) throws Exception {
        if ("SNMP".equals(connectionType)){
            Map<String, String> attributes = new HashMap<String, String>();
            attributes.put("mibDir","snmptoolkit/mibs");
            HashMap<String, String> discoveryHelperParams = new HashMap<String, String>();
            discoveryHelperParams.put("fileName","iDiscover/conf/xml/discoveryParameters.xml");
            discoveryHelperParams.put("resourceXML","iDiscover/conf/xml/discoveryResource.xml");
            XmlDiscoveryHelperFactory discoveryHelperFactory;
            discoveryHelperFactory = new XmlDiscoveryHelperFactory(discoveryHelperParams);

            return new SnmpNodeDiscoverer(attributes, discoveryHelperFactory);
        }
        return null;
    }
}
