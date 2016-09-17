package net.itransformers.postDiscoverer.core;


import net.itransformers.postDiscoverer.reportGenerator.ReportGeneratorType;
import net.itransformers.resourcemanager.ResourceManager;
import net.itransformers.resourcemanager.config.ConnectionParamsType;
import net.itransformers.resourcemanager.config.ParamType;
import net.itransformers.resourcemanager.config.ResourceType;
import net.itransformers.resourcemanager.config.ResourcesType;
import net.itransformers.utils.JaxbMarshalar;
import org.apache.commons.io.FileUtils;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
//    public static void main(String[] args) throws IOException {
//
//        File projectDir = new File("/Users/niau/trunk");
//
//        ResourceManager resourceManager;
//
//        String xml = FileUtils.readFileToString(new File(projectDir, "/resourceManager/conf/xml/resource.xml"));
//
//        InputStream is1 = new ByteArrayInputStream(xml.getBytes());
//        ResourcesType deviceGroupsType = null;
//
//        try {
//            deviceGroupsType = net.itransformers.resourcemanager.util.JaxbMarshalar.unmarshal(ResourcesType.class, is1);
//        } catch (JAXBException e) {
//            e.printStackTrace();
//        }
//        resourceManager = new ResourceManager(deviceGroupsType);
//
//
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("protocol", "ssh");
//        params.put("DeviceName", "R5");
//        params.put("deviceType", "CISCO");
//        params.put("address", "172.16.35.5");
//        params.put("port", "22");
//        //params.put("command","sh runn");
//
//        ResourceType resource = resourceManager.findFirstResourceBy(params);
//        List connectParameters = resource.getConnectionParams();
//
//        for (int i = 0; i < connectParameters.size(); i++) {
//            ConnectionParamsType connParamsType = (ConnectionParamsType) connectParameters.get(i);
//
//            String connectionType = connParamsType.getConnectionType();
//            if (connectionType.equalsIgnoreCase(params.get("protocol"))) {
//
//                for (ParamType param : connParamsType.getParam()) {
//                    params.put(param.getName(), param.getValue());
//                }
//
//            }
//        }
//
//        File postDiscoveryConfing = new File(projectDir + "/postDiscoverer/conf/xml/reportGenerator.xml");
//
//
//        ReportGeneratorType reportGenerator = null;
//
//        FileInputStream is = new FileInputStream(postDiscoveryConfing);
//        try {
//            reportGenerator = JaxbMarshalar.unmarshal(ReportGeneratorType.class, is);
//        } catch (JAXBException e) {
//            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//        } finally {
//            is.close();
//        }
//
//        ReportManager reportManager = new ReportManager(reportGenerator,"postDiscoverer/conf/groovy/",projectDir,new File(projectDir,"iTopologyManager/rightClick/conf/xslt/table_creator.xslt"));
//        try {
//            HashMap<String,Object> groovyExecutorParams = new HashMap<String,Object>();
//
//            for (String s : params.keySet()) {
//                groovyExecutorParams.put(s,params.get(s));
//            }
//
//            StringBuffer report = reportManager.reportExecutor(new File("/Users/niau/trunk/version1/post-discovery"),groovyExecutorParams);
//        } catch (ParserConfigurationException e) {
//            e.printStackTrace();
//        } catch (SAXException e) {
//            e.printStackTrace();
//        }
//    }
//



}