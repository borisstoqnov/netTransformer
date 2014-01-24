package net.itransformers.postDiscoverer.core;


import groovy.util.ResourceException;
import groovy.util.ScriptException;
import net.itransformers.postDiscoverer.config.PostDiscoveryHelperType;
import net.itransformers.resourcemanager.ResourceManager;
import net.itransformers.resourcemanager.config.ConnectionParamsType;
import net.itransformers.resourcemanager.config.ParamType;
import net.itransformers.resourcemanager.config.ResourceType;
import net.itransformers.resourcemanager.config.ResourcesType;
import net.itransformers.utils.JaxbMarshalar;
import org.apache.commons.io.FileUtils;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws IOException {

        File projectDir =  new File ("/Users/niau/trunk");

        File postDiscoveryConfing = new File(projectDir+"/postDiscoverer/conf/xml/postDiscoveryHelper.xml");
        PostDiscoveryHelperType postDiscovery = null;

        FileInputStream is = new FileInputStream(postDiscoveryConfing);
        try {
            postDiscovery = JaxbMarshalar.unmarshal(PostDiscoveryHelperType.class, is);
        } catch (JAXBException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            is.close();
        }

        for (int i =0; i < postDiscovery.getDevice().size();i++ ){
           String deviceType = postDiscovery.getDevice().get(i).getType();

        }

        ResourceManager resourceManager;

        String xml = FileUtils.readFileToString(new File(projectDir, "/resourceManager/conf/xml/resource.xml"));

        InputStream is1 = new ByteArrayInputStream(xml.getBytes());
        ResourcesType deviceGroupsType = null;

        try {
            deviceGroupsType = net.itransformers.resourcemanager.util.JaxbMarshalar.unmarshal(ResourcesType.class, is1);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        resourceManager = new ResourceManager(deviceGroupsType);


        Map<String, String> params = new HashMap<String, String>();
        params.put("protocol","ssh");
        params.put("DeviceName","R5");
        params.put("deviceType","CISCO");
        params.put("address","172.16.35.5");
        params.put("port","22");
        params.put("command","sh runn");

        ResourceType resource = resourceManager.findResource(params);
       List connectParameters = resource.getConnectionParams();

        for (int i = 0; i< connectParameters.size(); i++){
            ConnectionParamsType connParamsType = (ConnectionParamsType) connectParameters.get(i);

            String connectionType = connParamsType.getConnectionType();
            if (connectionType.equalsIgnoreCase(params.get("protocol"))) {

                for (ParamType param : connParamsType.getParam()) {
                    params.put(param.getName(), param.getValue());
                }

            }
        }



        try {
            Expect4GroovyScriptLauncher launcher = new Expect4GroovyScriptLauncher();
            launcher.open(new String[]{"postDiscoverer/conf/groovy/"}, "cisco_login.groovy", params);
            launcher.sendCommand("cisco_sendCommand.groovy");
            launcher.close("cisco_logout.groovy");
                       // if (result.get("status")==1)
            //      System.out.println("Result \n"+result.get("data"));

        } catch (ResourceException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (ScriptException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


//        AssertionExecutor executor = new AssertionExecutor();
//        File assertionsConfig = new File("iAssertions/src/test/java/net/itransformers/assertions/IPv4_IPv6.xml");
      //  List<Map<File, AssertionResult>> result = executor.execute(inputFiles, assertionsConfig, AssertionLevel.CRITICAL);


    }

}