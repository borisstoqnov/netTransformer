/*
 * Main.java
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

package net.itransformers.idiscover.v2.core;

import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.utils.AutoLabeler;
import net.itransformers.utils.CmdLineParser;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.io.File;
import java.net.MalformedURLException;
import java.util.*;

public class Main {
    public static final String VERSION_LABEL = "version";
    static Logger logger = Logger.getLogger(Main.class);
    public static void main(String[] args) throws MalformedURLException {
        logger.debug("iDiscover v2. gearing up");

        Map<String, String> params = CmdLineParser.parseCmdLine(args);
//        String connectionDetailsFileName = params.get("-f");
//        if (connectionDetailsFileName == null) {
//            printUsage("fileName"); return;
//        }
        String depthCmdArg = params.get("-d");
//        if (depthCmdArg == null) {
//            printUsage("depth"); return;
//        }
        String projectPath = params.get("-p");

        if (projectPath == null) {
            File cwd = new File(".");
            System.out.println("Project path is not specified. Will use current dir: "+ cwd.getAbsolutePath());
            projectPath = cwd.getAbsolutePath();
        }

        File workingDir = new File(projectPath);
        if (!workingDir.exists()){
            System.out.println("Invalid project path!");
            return;
        }

        logger.debug("Loading beans!!");
        File conDetails =new File(projectPath,"iDiscover/conf/txt/connection-details.txt");

        FileSystemXmlApplicationContext applicationContext = initializeDiscoveryContext(projectPath);
        NetworkDiscoverer discoverer =applicationContext.getBean("snmpDiscovery", NetworkDiscoverer.class);
        LinkedHashMap<String,ConnectionDetails> connectionList = (LinkedHashMap) applicationContext.getBean("connectionList", conDetails);
        int depth = (Integer)applicationContext.getBean("discoveryDepth", depthCmdArg == null ? "-1" : depthCmdArg);
        NetworkDiscoveryResult result = discoverer.discoverNetwork(new ArrayList<ConnectionDetails>(connectionList.values()), depth);


        if(result!=null) {
            for (String s : result.getNodes().keySet()) {
                System.out.println("\nNode: "+ s);

            }
        }


//
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
        System.out.println("Usage:   iDiscoverv2.sh -p ~/Projects/MyDiscoveryProject -d 10");
        System.out.println("Missing parameter: "+param);
    }



    public static FileSystemXmlApplicationContext initializeDiscoveryContext(String projectPath) throws MalformedURLException {


        File generic = new File(projectPath,"iDiscover/conf/xml/generic.xml");
        String genericContextPath = generic.toURI().toURL().toString();

        File snmpDiscovery = new File(projectPath,"iDiscover/conf/xml/snmpNetworkDiscovery.xml");
        String snmpDiscoveryContextPath = snmpDiscovery.toURI().toURL().toString();

        File connectionsDetails = new File(projectPath,"iDiscover/conf/xml/connectionsDetails.xml");
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
        FileSystemXmlApplicationContext applicationContext= new FileSystemXmlApplicationContext(paths, cmdArgCxt);
//        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(workingDir+File.separator+"iDiscover/conf/xml/generic.xml",workingDir+File.separator+"/iDiscover/conf/xml/snmpNetworkDiscovery.xml","connectionsDetails.xml");
        // NetworkDiscoverer discoverer = fileApplicationContext.getBean("bgpPeeringMapDiscovery", NetworkDiscoverer.class);
        //NetworkDiscoverer discoverer = fileApplicationContext.getBean("floodLightNodeDiscoverer", NetworkDiscoverer.class);
        return applicationContext;
    }

}


