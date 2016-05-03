/*
 * XmlTopologyDeviceLogger.java
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

package net.itransformers.idiscover.discoverylisteners;

import net.itransformers.idiscover.core.DiscoveryListener;
import net.itransformers.idiscover.core.RawDeviceData;
import net.itransformers.idiscover.core.Resource;
import net.itransformers.idiscover.networkmodel.DiscoveredDeviceData;
import net.itransformers.idiscover.util.JaxbMarshalar;
import net.itransformers.utils.XmlFormatter;
import net.itransformers.utils.XsltTransformer;
import net.itransformers.utils.graphmlmerge.GrahmlMerge;
import net.itransformers.utils.graphmlmerge.MergeConflictResolver;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;

//import net.itransformers.utils.XmlFormatter;

public class XmlTopologyDeviceLogger implements DiscoveryListener{
    static Logger logger = Logger.getLogger(XmlTopologyDeviceLogger.class);
    private final String graphtType;
    private File path;
    private File deviceCentricPath;
    private File networkCentricPath;
    private File xsltFileName;
    private final File labelPath;

    public XmlTopologyDeviceLogger(Map<String, String> params, File baseDir, String label) {
        File base1 = new File(baseDir,params.get("path"));
        if (!base1.exists()){
            base1.mkdir();
        }
        labelPath = new File(base1, label);
        if (!labelPath.exists()) {
            labelPath.mkdir();
        }
        graphtType = params.get("type");
     //   this.path = new File(labelPath, graphtType);

//        if (!this.path.exists()) {
//            this.path.mkdir();
            this.deviceCentricPath = new File(labelPath, params.get("device-centric-logging-path"));
            if (!this.deviceCentricPath.exists()){
                this.deviceCentricPath.mkdir();
            }
            this.networkCentricPath = new File(labelPath, params.get("network-centric-logging-path"));

            if (!this.networkCentricPath.exists()){
                this.networkCentricPath.mkdir();

            }
//        }
        this.xsltFileName = new File(baseDir,params.get("xslt"));
    }

    public void handleDevice(String deviceName, RawDeviceData rawData, DiscoveredDeviceData discoveredDeviceData, Resource resource) {
        ByteArrayOutputStream graphMLOutputStream = new ByteArrayOutputStream();
        ByteArrayOutputStream os  = new ByteArrayOutputStream();
        try {
            JaxbMarshalar.marshal(discoveredDeviceData, os, "DiscoveredDevice");
        } catch (JAXBException e) {
            logger.error(e.getMessage(),e);
        }
        XsltTransformer transformer = new XsltTransformer();
        try {
            transformer.transformXML(new ByteArrayInputStream(os.toByteArray()), xsltFileName, graphMLOutputStream);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

        try {
            final String fileName = "node-" + deviceName + ".graphml";
//            String fullFileName = path + File.separator + fileName;
            final File nodeFile = new File(deviceCentricPath,fileName);
            String graphml = null;
            try {
                graphml = new XmlFormatter().format(new String(graphMLOutputStream.toByteArray()));
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
            FileUtils.writeStringToFile(nodeFile, graphml);

            try {
                final File networkGraphml = new File(networkCentricPath+File.separator+ "network.graphml");
                if (networkGraphml.exists()){
                    Map<String, String> edgesTypes = new HashMap<String, String>();
                    edgesTypes.put("name","string");
                    edgesTypes.put("method","string");
                    edgesTypes.put("dataLink","string");
                    edgesTypes.put("ipLink","string");
                    edgesTypes.put("IPv4Forwarding","string");
                    edgesTypes.put("IPv6Forwarding","string");
                    edgesTypes.put("InterfaceNameA","string");
                    edgesTypes.put("InterfaceNameB","string");
                    edgesTypes.put("IPv4AddressA","string");
                    edgesTypes.put("IPv4AddressB","string");
                    edgesTypes.put("edgeTooltip","string");
                    edgesTypes.put("diff","string");
                    edgesTypes.put("diffs","string");
                    edgesTypes.put("bgpAutonomousSystemA","string");
                    edgesTypes.put("bgpAutonomousSystemB","string");

                    Map<String, String> vertexTypes = new HashMap<String, String>();
                    vertexTypes.put("deviceModel","string");
                    vertexTypes.put("deviceType","string");
                    vertexTypes.put("nodeInfo","string");
                    vertexTypes.put("hostname","string");
                    vertexTypes.put("deviceStatus","string");
                    vertexTypes.put("DiscoveredIPv4Address","string");
                    vertexTypes.put("geoCoordinates","string");
                    vertexTypes.put("SubnetPrefix","string");
                    vertexTypes.put("site","string");
                    vertexTypes.put("diff","string");
                    vertexTypes.put("diffs","string");
                    vertexTypes.put("diffs","string");
                    vertexTypes.put("IPv6Forwarding","string");
                    vertexTypes.put("IPv4Forwarding","string");
                    vertexTypes.put("bgpLocalAS","string");

                    Map<String, MergeConflictResolver> edgeConflictResolver = new HashMap<String, MergeConflictResolver>();
                    Map<String, MergeConflictResolver> nodeConflictResolver = new HashMap<String, MergeConflictResolver>();
                    edgeConflictResolver.put("method", new MergeConflictResolver(){
                        @Override
                        public Object resolveConflict(Object srcValue, Object targetValue) {
                            // if (srcValue instanceof String && targetValue instanceof String) {
                            String[] srcArray = ((String) srcValue).split(",");
                            String[] targetArray = ((String) targetValue).split(",");


                            String[] both = (String[]) ArrayUtils.addAll(srcArray, targetArray);
                            Arrays.sort(both);
                            LinkedHashSet<String> m = new LinkedHashSet<String>();
                            Collections.addAll(m, both);

                            return StringUtils.join(m, ',');

                        }
                    });
                    new GrahmlMerge(nodeConflictResolver,edgeConflictResolver).merge(nodeFile, networkGraphml, networkGraphml, vertexTypes, edgesTypes, graphtType);
                }else {
                    //networkGraphml.createNewFile();
                    FileUtils.writeStringToFile(networkGraphml, graphml);
                   // new GrahmlMerge().merge(nodeFile, networkGraphml, networkGraphml);
                }
                //TODO remove when you have some time and do the diff in the correct way!
//                File networkGraphmls = new File(labelPath,graphtType+".graphmls");
//                if (!networkGraphmls.exists()){
//                     FileWriter writer = new FileWriter(networkGraphmls);
//                     writer.write("network.graphml\n");
//                     writer.close();
//                }

            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, JAXBException {
        String path = "devices_and_models\\lab\\undirected";
        File dir = new File (path);
        String[] files = dir.list(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return (name.endsWith(".hml"));
            }
        });
        Map<String, String> params = new HashMap<String, String >();
        params.put("path", path);
        params.put("conf/xslt", "iDiscover\\conf\\xslt\\transformator3.xslt");
        XmlTopologyDeviceLogger logger = new XmlTopologyDeviceLogger(params,null,null);

        for (String fileName: files){
            FileInputStream is = new FileInputStream(path + File.separator + fileName);
            DiscoveredDeviceData discoveredDeviceData;
            try {
                discoveredDeviceData = JaxbMarshalar.unmarshal(DiscoveredDeviceData.class, is);
            } finally {
                is.close();
            }
            String deviceName = fileName.substring("device-".length(),fileName.length()-".xml".length());
            logger.handleDevice(deviceName, null, discoveredDeviceData, null);
        }

    }

}
