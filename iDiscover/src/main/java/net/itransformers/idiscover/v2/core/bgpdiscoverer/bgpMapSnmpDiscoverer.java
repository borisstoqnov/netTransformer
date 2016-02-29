/*
 * bgpMapSnmpDiscoverer.java
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

package net.itransformers.idiscover.v2.core.bgpdiscoverer;

import net.itransformers.idiscover.core.DiscoveryResourceManager;
import net.itransformers.idiscover.v2.core.*;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.resourcemanager.config.ResourceType;
import net.itransformers.snmptoolkit.Get;
import net.itransformers.snmptoolkit.MibLoaderHolder;
import net.itransformers.snmptoolkit.Node;
import net.itransformers.snmptoolkit.Walk;
import net.itransformers.snmptoolkit.messagedispacher.DefaultMessageDispatcherFactory;
import net.itransformers.snmptoolkit.transport.UdpTransportMappingFactory;
import net.itransformers.utils.AutoLabeler;
import net.itransformers.utils.CmdLineParser;
import net.itransformers.utils.StylesheetCache;
import net.percederberg.mibble.MibLoaderException;
import org.apache.log4j.Logger;
import org.snmp4j.CommunityTarget;
import org.snmp4j.util.SnmpConfigurator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;


/**
 * Created by niau on 2/28/16.
 */
public class bgpMapSnmpDiscoverer extends NetworkNodeDiscoverer {

    static Logger logger = Logger.getLogger(bgpMapSnmpDiscoverer.class);
    private DiscoveryResourceManager discoveryResource;
    private String labelDirName;
    private String projectPath;
    private String xsltFileName;
    private String queryParameters;
    private String mibDir;

//    public bgpMapSnmpDiscoverer(DiscoveryResourceManager discoveryResource, String labelDirName, String projectPath, String xsltFileName, String queryParameters) {
//        this.discoveryResource = discoveryResource;
//        this.labelDirName = labelDirName;
//        this.projectPath = projectPath;
//        this.xsltFileName = xsltFileName;
//        this.queryParameters = queryParameters;
//    }

    @Override
    public NetworkDiscoveryResult discoverNetwork(List<ConnectionDetails> connectionDetailsList, int depth) {


        NetworkDiscoveryResult networkDiscoveryResult = new NetworkDiscoveryResult();

        File baseDir = new File(projectPath, labelDirName);
        if (!baseDir.exists()) baseDir.mkdir();

        for (ConnectionDetails connectionDetails : connectionDetailsList) {
            Map<String, String> params1 = new HashMap<String, String>();

            ConnectionDetailsValidator connectionDetailsValidator = new ConnectionDetailsValidator(connectionDetails);


            if (connectionDetailsValidator.validateDeviceName()) {
                params1.put("deviceName", connectionDetails.getParam("deviceName"));

            } else {
                logger.info("Can't find deviceName in connection details");
            }
            if (connectionDetailsValidator.validateDeviceType()) {
                params1.put("deviceType", connectionDetails.getParam("deviceType"));

            } else {
                logger.info("Can't find deviceType in connection details");
            }

            if (connectionDetailsValidator.validateIpAddress()) {
                params1.put("ipAddress", connectionDetails.getParam("ipAddress"));

            } else {
                logger.info("Having an ipAddress in connectionDetails is obligatory! Can't discover BGP Peering Map with those connection details");
                return null;
            }


            ResourceType snmpResource = this.discoveryResource.returnResourceByParam(params1);
            Map<String, String> snmpConnParams = this.discoveryResource.getParamMap(snmpResource, "snmp");
            if (snmpConnParams != null) {
                snmpConnParams.put("ipAddress", connectionDetails.getParam("ipAddress"));
                snmpConnParams.put("query.parameters", queryParameters);
                snmpConnParams.put("mibDir", mibDir);
                byte[] rawData = new byte[0];
                try {
                    String sysDescr = snmpGet(snmpConnParams);
                    if (sysDescr != null) {
                        logger.info(sysDescr);
                        logger.info("SNMP walk start");

                        rawData = snmpWalk(snmpConnParams);
                        logger.debug(rawData.toString());
                    } else {
                        logger.info("Can't connect through SNMP to " + connectionDetails.getParam("ipAddress"));
                    }
                } catch (Exception e) {
                    logger.info(e.getMessage());
                }
                logger.info("SNMP walk end");

                NodeDiscoveryResult nodeDiscoveryResult = new NodeDiscoveryResult();
                nodeDiscoveryResult.setDiscoveredData("rawData", rawData);
                fireNodeDiscoveredEvent(nodeDiscoveryResult);
                networkDiscoveryResult.addDiscoveredData(connectionDetails.getParam("deviceName"), nodeDiscoveryResult);

            } else {
                logger.info("Can't find an SNMP resource for those connection details");
            }

        }
        fireNetworkDiscoveredEvent(networkDiscoveryResult);

        return networkDiscoveryResult;
//        logger.info("First-transformation has started with xsltTransformator "+settings.get("xsltFileName1"));
//
//        ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
//
//
//        File xsltFileName1 = new File(xsltFileName1);
//
//
//        ByteArrayInputStream inputStream1 = new ByteArrayInputStream(rawData);
//        transformer.transformXML(inputStream1, xsltFileName1, outputStream1, settings, null);
//
//
//        logger.info("First transformation finished");
//
//        File intermediateDataFile = new File(outputDir, "intermediate-bgpPeeringMap.xml");
//
//        FileUtils.writeStringToFile(intermediateDataFile, new String(outputStream1.toByteArray()));
//
//        logger.trace("First transformation output");
//
//        logger.trace(outputStream1.toString());
//        logger.info("Second transformation started with xsltTransformator "+settings.get("xsltFileName2"));
//
//        ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
//        File xsltFileName2 = new File(projectDir, settings.get("xsltFileName2"));
//        ByteArrayInputStream inputStream2 = new ByteArrayInputStream(outputStream1.toByteArray());
//        transformer.transformXML(inputStream2, xsltFileName2, outputStream2, settings, null);
//        logger.info("Second transformation info");
//        logger.trace("Second transformation Graphml output");
//        logger.trace(outputStream2.toString());
//
//
//        ByteArrayInputStream inputStream3 = new ByteArrayInputStream(outputStream2.toByteArray());
//        ByteArrayOutputStream outputStream3 = new ByteArrayOutputStream();
//        File xsltFileName3 = new File(System.getProperty("base.dir"), settings.get("xsltFileName3"));
//        transformer.transformXML(inputStream3, xsltFileName3, outputStream3, null, null);
//
//
//        File outputFile = new File(graphmlDir, "undirected-bgpPeeringMap.graphml");
//        FileUtils.writeStringToFile(outputFile, new String(outputStream3.toByteArray()));
//        logger.info("Output Graphml saved in a file in"+graphmlDir);
//
//
//        //FileUtils.writeStringToFile(nodesFileListFile, "bgpPeeringMap.graphml");
//        FileWriter writer = new FileWriter(new File(outputDir,"undirected"+".graphmls"),true);
//        writer.append("undirected-bgpPeeringMap.graphml").append("\n");
//        writer.close();
//        return null;
    }

    private String snmpGet(Map<String, String> settings) {

        Properties parameters = parametersAsslembler(settings);

        String address = settings.get("ipAddress");
        if (address == null) {
            logger.info("ipAddress is null can't discover that device");
        }


        SnmpConfigurator snmpConfig = new SnmpConfigurator();
        CommunityTarget t = (CommunityTarget) snmpConfig.getTarget(parameters);
        String oid = "1.3.6.1.2.1.1.1";

        Get get = new Get(oid, t, new UdpTransportMappingFactory(), new DefaultMessageDispatcherFactory());
        try {
            return get.getSNMPGetNextValue();
        } catch (IOException e) {

            e.printStackTrace();
            return null;
        }


    }

    private static byte[] snmpWalk(Map<String, String> settings) throws IOException, MibLoaderException {
        String queryParameters = settings.get("query.parameters");

        String[] params = queryParameters.split(",");
        String mibDir = settings.get("mibDir");

        MibLoaderHolder holder = new MibLoaderHolder(new File(System.getProperty("base.dir"), mibDir), false);
        Walk walker = new Walk(holder, new UdpTransportMappingFactory(), new DefaultMessageDispatcherFactory());


        Properties parameters = parametersAsslembler(settings);

        Node root = walker.walk(params, parameters);
        String xml = Walk.printTreeAsXML(root);
        return xml.getBytes();
    }


    private static Properties parametersAsslembler(Map<String, String> settings) {
        Properties parameterProperties = new Properties();

        parameterProperties.put(SnmpConfigurator.O_ADDRESS, Arrays.asList(settings.get("ipAddress")));
        parameterProperties.put(SnmpConfigurator.O_COMMUNITY, Arrays.asList(settings.get("community-ro")));

        String version = settings.get("version") == null ? "2c" : settings.get("version");
        int retriesInt = settings.get("retries") == null ? 3 : Integer.parseInt(settings.get("retries"));
        int timeoutInt = settings.get("timeout") == null ? 1200 : Integer.parseInt(settings.get("timeout"));
        int maxrepetitions = settings.get("max-repetitions") == null ? 100 : Integer.parseInt(settings.get("max-repetitions"));
        int nonrepeaters = settings.get("non-repeaters") == null ? 10 : Integer.parseInt(settings.get("max-repetitions"));


        parameterProperties.put(SnmpConfigurator.O_VERSION, Arrays.asList(version));
        parameterProperties.put(SnmpConfigurator.O_TIMEOUT, Arrays.asList(timeoutInt));
        parameterProperties.put(SnmpConfigurator.O_RETRIES, Arrays.asList(retriesInt));
        parameterProperties.put(SnmpConfigurator.O_MAX_REPETITIONS, Arrays.asList(maxrepetitions));
        parameterProperties.put(SnmpConfigurator.O_NON_REPEATERS, Arrays.asList(nonrepeaters));


        return parameterProperties;
    }

    public ByteArrayOutputStream transformRawDataToDeviceData(ByteArrayInputStream inputStream, Map<String, String> params) {
        //Create DeviceXml File
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        File xsltTransformer = new File(projectPath, xsltFileName);

        try {
            Transformer trans = StylesheetCache.newTransformer(xsltTransformer);
            if (params != null) {
                for (String param : params.keySet()) {
                    trans.setParameter(param, params.get(param));
                }
            }
            Source xmlSource = new StreamSource(inputStream);
            trans.transform(xmlSource, new StreamResult(outputStream));
        } catch (TransformerException e) {
            logger.info(e.getMessage());
        }

        return outputStream;
    }

    public String getXsltFileName() {
        return xsltFileName;
    }

    public void setXsltFileName(String xsltFileName) {
        this.xsltFileName = xsltFileName;
    }

    public DiscoveryResourceManager getDiscoveryResource() {
        return discoveryResource;
    }

    public void setDiscoveryResource(DiscoveryResourceManager discoveryResource) {
        this.discoveryResource = discoveryResource;
    }

    public String getLabelDirName() {
        return labelDirName;
    }

    public void setLabelDirName(String labelDirName) {
        this.labelDirName = labelDirName;
    }


    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }


    public String getQueryParameters() {
        return queryParameters;
    }

    public void setQueryParameters(String queryParameters) {
        this.queryParameters = queryParameters;
    }

    public String getMibDir() {
        return mibDir;
    }

    public void setMibDir(String mibDir) {
        this.mibDir = mibDir;
    }

    public static void main(String[] args) {
        logger.debug("bgpPeeringMap v2. gearing up");

        Map<String, String> params = CmdLineParser.parseCmdLine(args);
        String projectPath = params.get("-p");

        if (projectPath == null) {
            File cwd = new File(".");
            System.out.println("Project path is not specified. Will use current dir: " + cwd.getAbsolutePath());
            projectPath = cwd.getAbsolutePath();
        }

        File workingDir = new File(projectPath);
        if (!workingDir.exists()) {
            System.out.println("Invalid project path!");
            return;
        }

        logger.debug("Loading beans!!");
        File conDetails = new File(projectPath, "iDiscover/conf/txt/connection-details.txt");

        FileSystemXmlApplicationContext applicationContext = null;
        try {
            applicationContext = initializeDiscoveryContext(projectPath);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        NetworkDiscoverer discoverer = applicationContext.getBean("bgpMapSnmpDiscoverer", NetworkNodeDiscoverer.class);
        LinkedHashMap<String, ConnectionDetails> connectionList = (LinkedHashMap) applicationContext.getBean("connectionList", conDetails);
        NetworkDiscoveryResult result = discoverer.discoverNetwork(new ArrayList<ConnectionDetails>(connectionList.values()));

        if (result != null) {
            for (String s : result.getNodes().keySet()) {
                System.out.println("\nNode: " + s);

            }
        }
    }

    public static FileSystemXmlApplicationContext initializeDiscoveryContext(String projectPath) throws MalformedURLException {


        File generic = new File(projectPath, "iDiscover/conf/xml/generic.xml");
        String genericContextPath = generic.toURI().toURL().toString();

        File snmpDiscovery = new File(projectPath, "iDiscover/conf/xml/bgpInternetMapSNMPDiscovery.xml");
        String snmpDiscoveryContextPath = snmpDiscovery.toURI().toURL().toString();

        File connectionsDetails = new File(projectPath, "iDiscover/conf/xml/connectionsDetails.xml");
        String connectionsDetailsContextPath = connectionsDetails.toURI().toURL().toString();

        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        BeanDefinition beanDefinition = BeanDefinitionBuilder.
                rootBeanDefinition(String.class)
                .addConstructorArgValue(projectPath).getBeanDefinition();

        File networkPath = new File(projectPath, "network");

        String labelDirName;
        if (!networkPath.exists()) {
            networkPath.mkdir();
            labelDirName = "version" + "1";
            File labelDir = new File(networkPath, labelDirName);
            labelDir.mkdir();
        } else {
            AutoLabeler autoLabeler = new AutoLabeler(projectPath, "network", "version");
            labelDirName = AutoLabeler.autolabel();
        }

        BeanDefinition beanDefinition2 = BeanDefinitionBuilder.
                rootBeanDefinition(String.class)
                .addConstructorArgValue(labelDirName).getBeanDefinition();

        beanFactory.registerBeanDefinition("projectPath", beanDefinition);

        beanFactory.registerBeanDefinition("labelDirName", beanDefinition2);

        GenericApplicationContext cmdArgCxt = new GenericApplicationContext(beanFactory);
        // Must call refresh to initialize context
        cmdArgCxt.refresh();

        String[] paths = new String[]{genericContextPath, snmpDiscoveryContextPath, connectionsDetailsContextPath};
//        ,project.getAbsolutePath()+project.getAbsolutePath()+File.separator+"iDiscover/conf/xml/snmpNetworkDiscovery.xml", project.getAbsolutePath()+File.separator+"iDiscover/src/main/resources/connectionsDetails.xml"
        FileSystemXmlApplicationContext applicationContext = new FileSystemXmlApplicationContext(paths, cmdArgCxt);
//        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(workingDir+File.separator+"iDiscover/conf/xml/generic.xml",workingDir+File.separator+"/iDiscover/conf/xml/snmpNetworkDiscovery.xml","connectionsDetails.xml");
        // NetworkDiscoverer discoverer = fileApplicationContext.getBean("bgpPeeringMapDiscovery", NetworkDiscoverer.class);
        //NetworkDiscoverer discoverer = fileApplicationContext.getBean("floodLightNodeDiscoverer", NetworkDiscoverer.class);
        return applicationContext;
    }


}
