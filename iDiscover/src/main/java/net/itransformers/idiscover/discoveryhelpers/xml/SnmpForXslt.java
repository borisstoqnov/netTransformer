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

package net.itransformers.idiscover.discoveryhelpers.xml;

import net.itransformers.idiscover.core.Discoverer;
import net.itransformers.idiscover.core.DiscovererFactory;
import net.itransformers.idiscover.core.Resource;
import net.itransformers.idiscover.discoverers.DefaultDiscovererFactory;
import net.itransformers.idiscover.discoverers.SnmpWalker;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class SnmpForXslt {
    static Logger logger = Logger.getLogger(SnmpForXslt.class);
    private static DiscovererFactory discovererFactory;

//    public SnmpForXslt() {
//        discovererFactory = new DefaultDiscovererFactory();
//    }

    public static String getName(String hostName, String community) throws Exception {
        Map<String, String> resourceParams = new HashMap<String, String>();
        resourceParams.put("community-ro", community);
        resourceParams.put("version", "1");
        Resource resource = new Resource(hostName,null, resourceParams);// TODO specify port
        discovererFactory = new DefaultDiscovererFactory();

        Discoverer discoverer = discovererFactory.createDiscoverer(resource);
        final String deviceName = discoverer.getDeviceName(resource);
        logger.debug("hostname:"+hostName+", community: "+community+", found deviceName:"+deviceName);
        return deviceName;
    }

    public static String getSymbolByOid(String mibName, String oid) throws Exception {
        discovererFactory = new DefaultDiscovererFactory();
        SnmpWalker discoverer = (SnmpWalker) discovererFactory.createDiscoverer(null);
        return discoverer.getSymbolByOid(mibName, oid);
    }

    public static String getByOid(String hostName, String oid, String community) throws Exception {
        Map<String, String> resourceParams = new HashMap<String, String>();
        resourceParams.put("community-ro", community);
        resourceParams.put("version", "1");
        Resource resource = new Resource(hostName,null, resourceParams);// TODO specify port
        discovererFactory = new DefaultDiscovererFactory();
        SnmpWalker discoverer = (SnmpWalker) discovererFactory.createDiscoverer(resource);
        final String deviceName = discoverer.getByOid(resource, oid);
        logger.debug("hostname:"+hostName+", community: "+community+", found deviceName:"+deviceName);
        return deviceName;
    }

    public boolean setByOID(String hostName, String oid, String community, String value) throws Exception{
        Map<String, String> resourceParams = new HashMap<String, String>();
        resourceParams.put("community-rw", community);
        resourceParams.put("version", "1");
        Resource resource = new Resource(hostName,null, resourceParams);// TODO specify port
        discovererFactory = new DefaultDiscovererFactory();
        SnmpWalker discoverer = (SnmpWalker) discovererFactory.createDiscoverer(resource);
        final String result = discoverer.setByOid(resource, oid, value);
        logger.debug("snmpSet for hostname:"+hostName+", community: "+community+", oid "+oid+ ", value "+value +" = "+result);
        return true;

    }

}
