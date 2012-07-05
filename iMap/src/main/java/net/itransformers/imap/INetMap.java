package net.itransformers.imap;

import net.itransformers.snmptoolkit.MibLoaderHolder;
import net.itransformers.snmptoolkit.Node;
import net.itransformers.snmptoolkit.Walk;
import net.itransformers.snmptoolkit.messagedispacher.DefaultMessageDispatcherFactory;
import net.itransformers.snmptoolkit.transport.UdpTransportMappingFactory;
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

/**
 * Created with IntelliJ IDEA.
 * User: LYCHO
 * Date: 12-6-28
 * Time: 19:57
 * To change this template use File | Settings | File Templates.
 */
public class INetMap {
    static Logger logger = Logger.getLogger(INetMap.class);
    private static void printUsage(String param){
        System.out.println("Usage:   java net.itransformers.imap.INetMap -s <Path to settings.properties>");
        System.out.println("Example [Windows]: java net.itransformers.imap.INetMap -s iMap\\conf\\imap.properties");
        System.out.println("Example [Unix]: java net.itransformers.imap.INetMap -s iMap/conf/imap.properties");
        System.out.println("Missing parameter: "+param);
    }
    public static void main(String[] args) throws Exception {

        Map<String,String> params = CmdLineParser.parseCmdLine(args);
        logger.info("input params"+params.toString());
        final String settingsFile = params.get("-s");
        if (settingsFile==null){
            printUsage("imap.properties"); return;
        }

        Map<String, String> settings = loadProperties(new File(System.getProperty("base.dir"),settingsFile));
        logger.info("Settings"+settings.toString());

        File outputDir = new File(System.getProperty("base.dir"), settings.get("output.dir"));
        System.out.println(outputDir.getAbsolutePath());
        boolean result = outputDir.mkdir();
//        if (!result) {
//            System.out.println("result:"+result);
//            System.out.println("Unable to create dir: "+outputDir);
//            return;
//        }

        File graphmlDir = new File(outputDir, settings.get("output.dir.graphml"));
        result = outputDir.mkdir();
//        if (!result) {
//            System.out.println("Unable to create dir: "+graphmlDir);
//            return;
//        }

        XsltTransformer transformer = new XsltTransformer();
        byte[] rawData = snmpWalk(settings);
        File rawDataFile = new File(outputDir, "raw-data.xml");
        FileUtils.writeStringToFile(rawDataFile,new String(rawData));


        ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
        File xsltFileName1 = new File(System.getProperty("base.dir"), settings.get("xsltFileName1"));
        ByteArrayInputStream inputStream1 = new ByteArrayInputStream(rawData);
        transformer.transformXML(inputStream1, xsltFileName1, outputStream1, settings, null);

        ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
        File xsltFileName2 = new File(System.getProperty("base.dir"), settings.get("xsltFileName2"));
        ByteArrayInputStream inputStream2 = new ByteArrayInputStream(outputStream1.toByteArray());
        transformer.transformXML(inputStream2, xsltFileName2, outputStream2, settings, null);

        File outputFile = new File(graphmlDir, "imap.graphml");
        File nodesFileListFile = new File(graphmlDir, "nodes-file-list.txt");
        FileUtils.writeStringToFile(outputFile, new String(outputStream2.toByteArray()));

        FileUtils.writeStringToFile(nodesFileListFile, "imap.graphml");

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
