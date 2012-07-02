import net.itransformers.snmptoolkit.MibLoaderHolder;
import net.itransformers.snmptoolkit.Node;
import net.itransformers.snmptoolkit.Walk;
import net.itransformers.snmptoolkit.messagedispacher.DefaultMessageDispatcherFactory;
import net.itransformers.snmptoolkit.transport.UdpTransportMappingFactory;
import net.itransformers.utils.XsltTransformer;
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

    public static void main(String[] args) throws Exception {
        Map<String, String> settings = loadProperties(new File("iMap/src/main/resources/settings.properties"));
        XsltTransformer transformer = new XsltTransformer();
        byte[] rawData = snmpWalk(settings);
        System.out.println(new String(rawData));

        ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
        File xsltFileName1 = new File(System.getProperty("base.dir"), settings.get("xsltFileName1"));
        ByteArrayInputStream inputStream1 = new ByteArrayInputStream(rawData);
        transformer.transformXML(inputStream1, xsltFileName1, outputStream1, settings, null);
        System.out.println(new String(outputStream1.toByteArray()));

        ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
        File xsltFileName2 = new File(System.getProperty("base.dir"), settings.get("xsltFileName2"));
        ByteArrayInputStream inputStream2 = new ByteArrayInputStream(outputStream1.toByteArray());
        transformer.transformXML(inputStream2, xsltFileName2, outputStream2, settings, null);
        System.out.println(new String(outputStream2.toByteArray()));

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
