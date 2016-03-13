/*
 * BgpPeeringMap.java
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

package net.itransformers.bgpPeeringMap;

import net.itransformers.snmp2xml4j.snmptoolkit.MibLoaderHolder;
import net.itransformers.snmp2xml4j.snmptoolkit.Node;
import net.itransformers.snmp2xml4j.snmptoolkit.Walk;
import net.itransformers.snmp2xml4j.snmptoolkit.messagedispacher.DefaultMessageDispatcherFactory;
import net.itransformers.snmp2xml4j.snmptoolkit.transport.UdpTransportMappingFactory;
import net.itransformers.utils.CmdLineParser;
import net.itransformers.utils.XsltTransformer;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.snmp4j.util.SnmpConfigurator;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Deprecated
public class BgpPeeringMap {
    private static String settingsFile;
    private static String ipAddress;
    private boolean isRunning;
    private boolean isPaused;
    private boolean isStopped;
    private static File projectDir;
    private static String label;

    static Logger logger = Logger.getLogger(BgpPeeringMap.class);
    private static void printUsage(String param){
        System.out.println("Usage:   java net.itransformers.bgpPeeringMap.bgpPeeringMap -s <Path to bgpPeeringMap.properties>");
        System.out.println("Example [Windows]: java net.itransformers.bgpPeeringMap.bgpPeeringMap -s bgpPeeringMap\\conf\\bgpPeeringMap.properties");
        System.out.println("Example [Unix]: java net.itransformers.imap.INetMap -s iMap/conf/bgpPeeringMap.properties");
        System.out.println("Missing parameter: "+param);
    }

    public BgpPeeringMap(File projectDir, String ipAddress, String settingsFile,String label) {
        this.projectDir = projectDir;
        this.ipAddress = ipAddress;
        this.settingsFile=settingsFile;
        this.label = label;
    }
    public static  void discover() throws Exception {
        Map<String, String> settings = loadProperties(new File(settingsFile));
        logger.info("Settings"+settings.toString());

        String folderPlaceholder = settings.get("output.dir");

        File outputDir = new File(projectDir+File.separator+folderPlaceholder, label);

        System.out.println(outputDir.getAbsolutePath());
        boolean result = outputDir.mkdir();
        File graphmlDir = new File(outputDir, "undirected");

        result = outputDir.mkdir();
        XsltTransformer transformer = new XsltTransformer();
        logger.info("SNMP walk start");
        byte[] rawData = snmpWalk(settings);
        logger.info("SNMP walk end");
        File rawDataFile = new File(outputDir, "raw-data-bgpPeeringMap.xml");
        FileUtils.writeStringToFile(rawDataFile,new String(rawData));

        logger.info("Raw-data written to a file in folder "+outputDir);
        logger.info("First-transformation has started with xsltTransformator "+settings.get("xsltFileName1"));

        ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
        File xsltFileName1 = new File(projectDir, settings.get("xsltFileName1"));
        ByteArrayInputStream inputStream1 = new ByteArrayInputStream(rawData);
        transformer.transformXML(inputStream1, xsltFileName1, outputStream1, settings, null);
        logger.info("First transformation finished");
        File intermediateDataFile = new File(outputDir, "intermediate-bgpPeeringMap.xml");

        FileUtils.writeStringToFile(intermediateDataFile, new String(outputStream1.toByteArray()));
        logger.trace("First transformation output");

        logger.trace(outputStream1.toString());
        logger.info("Second transformation started with xsltTransformator "+settings.get("xsltFileName2"));

        ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
        File xsltFileName2 = new File(projectDir, settings.get("xsltFileName2"));
        ByteArrayInputStream inputStream2 = new ByteArrayInputStream(outputStream1.toByteArray());
        transformer.transformXML(inputStream2, xsltFileName2, outputStream2, settings, null);
        logger.info("Second transformation info");
        logger.trace("Second transformation Graphml output");
        logger.trace(outputStream2.toString());


        ByteArrayInputStream inputStream3 = new ByteArrayInputStream(outputStream2.toByteArray());
        ByteArrayOutputStream outputStream3 = new ByteArrayOutputStream();
        File xsltFileName3 = new File(System.getProperty("base.dir"), settings.get("xsltFileName3"));
        transformer.transformXML(inputStream3, xsltFileName3, outputStream3, null, null);


        File outputFile = new File(graphmlDir, "undirected-bgpPeeringMap.graphml");
        FileUtils.writeStringToFile(outputFile, new String(outputStream3.toByteArray()));
        logger.info("Output Graphml saved in a file in"+graphmlDir);


        //FileUtils.writeStringToFile(nodesFileListFile, "bgpPeeringMap.graphml");
        FileWriter writer = new FileWriter(new File(outputDir,"undirected"+".graphmls"),true);
        writer.append("undirected-bgpPeeringMap.graphml").append("\n");
        writer.close();


    }
    public static void main(String[] args) throws Exception {

        Map<String,String> params = CmdLineParser.parseCmdLine(args);
        logger.info("input params"+params.toString());
        final String settingsFile = params.get("-s");
        if (settingsFile==null){
            printUsage("bgpPeeringMap.properties"); return;
        }

        discover() ;



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
        String address = ipAddress;
        if (address == null) throw new RuntimeException("Resource Address is null");
        Properties parameters = new Properties();
        parameters.put(SnmpConfigurator.O_ADDRESS, Arrays.asList(address));
        parameters.put(SnmpConfigurator.O_COMMUNITY, Arrays.asList(settings.get("community-ro")));

        String version = settings.get("version") == null ? "2c" : settings.get("version");
        int retriesInt = settings.get("retries") == null ? 3 : Integer.parseInt(settings.get("retries"));
        int timeoutInt = settings.get("timeout") == null ? 1200 : Integer.parseInt(settings.get("timeout"));
        int maxrepetitions = settings.get("max-repetitions") == null ? 100 : Integer.parseInt(settings.get("max-repetitions"));
        int nonrepeaters = settings.get("non-repeaters") == null ?  10 : Integer.parseInt(settings.get("max-repetitions"));


        parameters.put(SnmpConfigurator.O_VERSION, Arrays.asList(version));
        parameters.put(SnmpConfigurator.O_TIMEOUT, Arrays.asList(timeoutInt));
        parameters.put(SnmpConfigurator.O_RETRIES, Arrays.asList(retriesInt));
        parameters.put(SnmpConfigurator.O_MAX_REPETITIONS, Arrays.asList(maxrepetitions));
        parameters.put(SnmpConfigurator.O_NON_REPEATERS,Arrays.asList(nonrepeaters));
        Node root = walker.walk(params, parameters);
        String xml = Walk.printTreeAsXML(root);
        return xml.getBytes();
    }
    private synchronized void doPause() {
        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void pause(){
        isPaused = true;
    }

    public synchronized void resume(){
        isPaused = false;
        notifyAll();
    }
    public synchronized void stop(){
        isStopped = true;
        isRunning = false;
    }

    public synchronized boolean isStopped(){
        return isStopped;
    }

    public synchronized boolean isPaused(){
        return isPaused;
    }

    public synchronized boolean isRunning() {
        return isRunning;
    }
}
