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

package net.itransformers.idiscover.v2.core;

import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.utils.CmdLineParser;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<String, String> params = CmdLineParser.parseCmdLine(args);
        String connectionDetailsFileName = params.get("-f");
        if (connectionDetailsFileName == null) {
            printUsage("fileName"); return;
        }
        String depthCmdArg = params.get("-d");
        if (connectionDetailsFileName == null) {
            printUsage("depth"); return;
        }
        FileSystemXmlApplicationContext fileApplicationContext= new FileSystemXmlApplicationContext(File.separator+"iDiscover/conf/xml/generic.xml",File.separator+"iDiscover/conf/xml/snmpNetworkDiscovery.xml",File.separator+"iDiscover/conf/xml/floodlightNetworkDiscovery.xml",File.separator+"iDiscover/conf/xml/bgpInternetMapMRTDiscovery.xml","iDiscover/src/main/resources/connectionsDetails.xml");
        //ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(workingDir+File.separator+"iDiscover/conf/xml/generic.xml",workingDir+File.separator+"/iDiscover/conf/xml/snmpNetworkDiscovery.xml","connectionsDetails.xml");
        NetworkDiscoverer discoverer = fileApplicationContext.getBean("bgpPeeringMapDiscovery", NetworkDiscoverer.class);
//        NetworkDiscoverer discoverer = applicationContext.getBean("bgpPeeringMapDiscovery", NetworkDiscoverer.class);
        List connectionList = (List) fileApplicationContext.getBean("connectionList", connectionDetailsFileName == null ? null:new File(connectionDetailsFileName));
        int depth = (Integer)fileApplicationContext.getBean("discoveryDepth", depthCmdArg == null ? "-1":depthCmdArg);
        NetworkDiscoveryResult result = discoverer.discoverNetwork(connectionList, depth);
        for (String s : result.getNodes().keySet()) {
            System.out.println("\nNode: "+ s);
            for (String s1 : result.getNodes().keySet()) {
                System.out.println(s1 +"\t");

            }
        }
    }

    public static void main1(String[] args) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(
                "discovery.xml","connectionsDetails.xml");
        NetworkNodeDiscovererImpl nodeDiscovererImpl = applicationContext.getBean("discovery", NetworkNodeDiscovererImpl.class);
        ConnectionDetails connectionDetails = new ConnectionDetails();
        connectionDetails.setConnectionType("SNMP");
        connectionDetails.put("ipAddress","172.16.13.3");
        connectionDetails.put("version","1");
        connectionDetails.put("community-ro","netTransformer-r");
        connectionDetails.put("community-rw","netTransformer-rw");
        connectionDetails.put("timeout","3500");
        connectionDetails.put("retries","3");
        connectionDetails.put("port","161");
        connectionDetails.put("max-repetitions","65535");
        connectionDetails.put("mibDir","snmptoolkit/mibs");
        int depth = 10;
        NetworkDiscoveryResult result = nodeDiscovererImpl.discoverNetwork(Arrays.asList(connectionDetails), depth);

        System.out.println(result);
    }

    public static void main3(String[] args) {
        ConnectionDetails connectionDetails = new ConnectionDetails();;
        connectionDetails.setConnectionType("SNMP");
//        params.put("host","172.16.36.1");
//        params.put("version","1");
//        params.put("community-ro","test-r");
//        params.put("community-rw","test-rw");

        connectionDetails.put("host","172.16.13.1");
        connectionDetails.put("version","1");
        connectionDetails.put("community-ro","test-r");
        connectionDetails.put("community-rw","test-rw");

//        params.put("host","10.0.1.1");
//        params.put("version","1");
//        params.put("community-ro","test-r");
//        params.put("community-rw","test-rw");
        connectionDetails.put("timeout","500");
        connectionDetails.put("retries","1");
        connectionDetails.put("port","161");
        connectionDetails.put("max-repetitions","65535");
        connectionDetails.put("mibDir","snmptoolkit/mibs");
        NetworkNodeDiscovererImpl nodeDiscovererImpl = new NetworkNodeDiscovererImpl();
        NetworkDiscoveryResult result = nodeDiscovererImpl.discoverNetwork(Arrays.asList(connectionDetails));
        System.out.println(result);
    }
    private static void printUsage(String param){
        System.out.println("Usage:   iDiscoverv2.sh -f bgp-connection-details.txt -d 10");
        System.out.println("Missing parameter: "+param);
    }

}

/*<resource name="SNMP">
        <param name="protocol">SNMP</param>
        <connection-params connection-type="snmp">
            <param name="version">1</param>
            <param name="community-ro">europack-r</param>
            <param name="community-rw">itransformer-rw</param>
            <param name="timeout">500</param>
            <param name="retries">1</param>
                        <param name="port">161</param>
            <param name="max-repetitions">65535</param>
        </connection-params>
    </resource>*/

