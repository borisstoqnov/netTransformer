/*
 * BgpPeeringMapFromFile.java
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

import net.itransformers.utils.CmdLineParser;
import net.itransformers.utils.XsltTransformer;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Deprecated
public class BgpPeeringMapFromFile {
    static Logger logger = Logger.getLogger(BgpPeeringMapFromFile.class);
    private static void printUsage(String param){
        System.out.println("Usage:   java net.itransformers.bgpPeeringMap.bgpPeeringMap -s <Path to bgpPeeringMap.properties>");
        System.out.println("Example [Windows]: java net.itransformers.bgpPeeringMap.bgpPeeringMap -s bgpPeeringMap\\conf\\bgpPeeringMap.properties");
        System.out.println("Example [Unix]: java net.itransformers.imap.INetMap -s iMap/conf/bgpPeeringMap.properties");
        System.out.println("Missing parameter: "+param);
    }
    public static void main(String[] args) throws Exception {

        Map<String,String> params = CmdLineParser.parseCmdLine(args);
        logger.info("input params"+params.toString());
        final String settingsFile = params.get("-s");
        if (settingsFile==null){
            printUsage("bgpPeeringMap.properties"); return;
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
        byte[] rawData = readRawDataFile(settings.get("raw-data-file"));
        logger.info("First-transformation has started with xsltTransformator "+settings.get("xsltFileName1"));

        ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
        File xsltFileName1 = new File(System.getProperty("base.dir"), settings.get("xsltFileName1"));
        ByteArrayInputStream inputStream1 = new ByteArrayInputStream(rawData);
        transformer.transformXML(inputStream1, xsltFileName1, outputStream1, settings, null);
        logger.info("First transformation finished");
        File outputFile1 = new File(graphmlDir, "bgpPeeringMap-intermediate.xml");

        FileUtils.writeStringToFile(outputFile1, new String(outputStream1.toByteArray()));

        logger.info("Second transformation started with xsltTransformator "+settings.get("xsltFileName2"));

        ByteArrayOutputStream outputStream2 = new ByteArrayOutputStream();
        File xsltFileName2 = new File(System.getProperty("base.dir"), settings.get("xsltFileName2"));
        ByteArrayInputStream inputStream2 = new ByteArrayInputStream(outputStream1.toByteArray());
        transformer.transformXML(inputStream2, xsltFileName2, outputStream2, settings, null);
        logger.info("Second transformation finished");
        File outputFile = new File(graphmlDir, "bgpPeeringMap.graphml");
        File nodesFileListFile = new File(graphmlDir, "nodes-file-list.txt");
        FileUtils.writeStringToFile(outputFile, new String(outputStream2.toByteArray()));
        logger.info("Output Graphml saved in a file in"+graphmlDir);

        FileUtils.writeStringToFile(nodesFileListFile, "bgpPeeringMap.graphml");

        ByteArrayInputStream inputStream3 = new ByteArrayInputStream(outputStream2.toByteArray());
        ByteArrayOutputStream outputStream3 = new ByteArrayOutputStream();
        File xsltFileName3 = new File(System.getProperty("base.dir"), settings.get("xsltFileName3"));
        transformer.transformXML(inputStream3, xsltFileName3, outputStream3, null, null);



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

    public void thirdTransformation(String graphmlFile, String xsltFileName3 ) throws Exception {
        XsltTransformer transformer = new XsltTransformer();
        byte[] rawData = readRawDataFile(graphmlFile);
        logger.info("Third-transformation has started with xsltTransformator ");
        ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
        File xsltFileName1 = new File(xsltFileName3);
        ByteArrayInputStream inputStream1 = new ByteArrayInputStream(rawData);
        transformer.transformXML(inputStream1, xsltFileName1, outputStream1, null, null);


    }
    private static byte[] readRawDataFile(String rawData) throws Exception {
        FileInputStream is = new FileInputStream(rawData);
        byte[] data = new byte[is.available()];
        is.read(data);
        return data;
    }

}
