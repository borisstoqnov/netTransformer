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

// This juit test case creates a dummy discoverer and transforms raw-data from file to device-data.
// It allows testing of the custom tags part in xslt transformer.
// It has been done as a test for issue https://sourceforge.net/p/itransformer/tickets/8/


package net.itransformers.idiscover.v2.core;

import net.itransformers.utils.Pair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;
import java.util.List;
import java.util.Set;

public class bgpMapFileTestCase {
    Set<String> networkNodes;
    Set<Pair<String,String>> links;
    private NetworkNodeDiscovererImpl networkNodeDiscovererImpl;
    List connectionList;

    @Before
    public void setUp(){
        //networkDiscoverer = new NetworkDiscoverer();
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
        "discovery.xml","connectionsDetails.xml");
        networkNodeDiscovererImpl = applicationContext.getBean("bgpPeeringMapDiscovery", NetworkNodeDiscovererImpl.class);
        String connectionDetailsFileName = "iDiscover/src/test/resources/bgp-connection-details.txt";
        connectionList = (List) applicationContext.getBean("connectionList", connectionDetailsFileName == null ? null:new File(connectionDetailsFileName));
    }
    @Test
    public void testDoDiscoverNodes(){
        NetworkDiscoveryResult result = networkNodeDiscovererImpl.discoverNetwork(connectionList, 1);
        System.out.println("Result: \n"+result);
        Assert.assertEquals("ATLA",result.getNodes().get("ATLA").getId());
    }

}

