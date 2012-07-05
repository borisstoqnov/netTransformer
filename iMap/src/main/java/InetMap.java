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

import net.itransformers.snmptoolkit.MibLoaderHolder;
import net.itransformers.snmptoolkit.Node;
import net.itransformers.snmptoolkit.Walk;
import net.itransformers.snmptoolkit.messagedispacher.DefaultMessageDispatcherFactory;
import net.itransformers.snmptoolkit.transport.UdpTransportMappingFactory;
import net.itransformers.utils.XsltTransformer;
import org.snmp4j.util.SnmpConfigurator;
import net.itransformers.utils.CmdLineParser;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class InetMap {
    private static void printUsage(String param){
        System.out.println("Usage:   java -s <Path to settings.properties>");
        System.out.println("Example: java -s iMap\\conf\\txt\\settings.properties");
        System.out.println("Missing parameter: "+param);
    }
    public static void main(String[] args) throws Exception {
        Map<String,String> params = CmdLineParser.parseCmdLine(args);
        if (params == null) {
            printUsage("settings.properties"); return;
        }
        final String settingsFile = params.get("-s");
        if (settingsFile==null){
            printUsage("settings.properties"); return;
        }
        Map<String, String> settings = loadProperties(new File(settingsFile));
        XsltTransformer transformer = new XsltTransformer();
        byte[] rawData = snmpWalk(settings);
        System.out.println(new String(rawData));


        ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
        FileOutputStream fileOutputStream = new FileOutputStream(settings.get("imapFile"));
        File xsltFileName1 = new File(System.getProperty("base.dir"), settings.get("xsltFileName1"));
        ByteArrayInputStream inputStream1 = new ByteArrayInputStream(rawData);
        transformer.transformXML(inputStream1, xsltFileName1, outputStream1, settings, null);
        System.out.println(new String(outputStream1.toByteArray()));

        ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
        File xsltFileName2 = new File(System.getProperty("base.dir"), settings.get("xsltFileName2"));
        ByteArrayInputStream inputStream2 = new ByteArrayInputStream(outputStream1.toByteArray());
        transformer.transformXML(inputStream2, xsltFileName2, fileOutputStream, settings, null);
        //System.out.println(new String(outputStream2.toByteArray()));

    }

    private static Map<String, String> loadProperties(File file) throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream(file));
        HashMap<String, String> settings = new HashMap<String, String>();
        for (Object key : props.keySet()) {
            settings.put((String)key,(String)props.get(key));
        }
        return settings;
    }

    private static byte[] snmpWalk(Map<String,String> settings) throws Exception {//}, MibLoaderException {
        String queryParameters = settings.get("query.parameters");
        String[] params = queryParameters.split(",");
        String mibDir = settings.get("mibDir");
        MibLoaderHolder holder = new MibLoaderHolder(new File(System.getProperty("base.dir"), mibDir), false);
        Walk walker = new Walk(holder,new UdpTransportMappingFactory(), new DefaultMessageDispatcherFactory());
        String address = settings.get("address");
        if (address == null) throw new RuntimeException("Resource Address is null");
        Properties parameters = new Properties();
        parameters.put(SnmpConfigurator.O_ADDRESS, Arrays.asList(address));
//        parameters.put(SnmpConfigurator.O_PORT, Arrays.asList(resource.getAddress()));
        parameters.put(SnmpConfigurator.O_COMMUNITY, Arrays.asList(settings.get("community-ro")));
        //parameters.put(SnmpConfigurator.O_VERSION, Arrays.asList("2c"));// TODO

        String version = settings.get("version") == null ? "2c" : settings.get("version");
        int retriesInt = settings.get("retries") == null ? 3 : Integer.parseInt(settings.get("retries"));
        int timeoutInt = settings.get("timeout") == null ? 1200 : Integer.parseInt(settings.get("timeout"));
        int maxrepetitions = settings.get("max-repetitions") == null ? 65535 : Integer.parseInt(settings.get("timeout"));

        parameters.put(SnmpConfigurator.O_VERSION, Arrays.asList(version));
        parameters.put(SnmpConfigurator.O_TIMEOUT, Arrays.asList(timeoutInt));
        parameters.put(SnmpConfigurator.O_RETRIES, Arrays.asList(retriesInt));
        parameters.put(SnmpConfigurator.O_MAX_REPETITIONS, Arrays.asList(maxrepetitions));

        Node root = walker.walk(params, parameters);
        String xml = Walk.printTreeAsXML(root);
        return xml.getBytes();
    }

}
