/*
 * ReportManager.java
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

package net.itransformers.postDiscoverer.core;

import groovy.util.ResourceException;
import groovy.util.ScriptException;
import net.itransformers.postDiscoverer.reportGenerator.*;
import net.itransformers.resourcemanager.ResourceManager;
import net.itransformers.resourcemanager.config.ConnectionParamsType;
import net.itransformers.resourcemanager.config.ParamType;
import net.itransformers.resourcemanager.config.ResourceType;
import net.itransformers.resourcemanager.config.ResourcesType;
import net.itransformers.utils.JaxbMarshalar;
import net.itransformers.utils.XmlFormatter;
import net.itransformers.utils.XsltTransformer;
import net.itransformers.utils.cli.Expect4GroovyScriptLauncher;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ReportManager {
    static Logger logger = Logger.getLogger(ReportManager.class);

    private ReportGeneratorType reportGenerator;
    private String scriptPath;
    private File projectPath;
    private File xsltTransformator;


    public ReportManager(ReportGeneratorType reportGenerator, String scriptPath,File projectPath, File xsltTransformator) {
        this.reportGenerator = reportGenerator;
        this.scriptPath = scriptPath;
        this.projectPath = projectPath;
        this.xsltTransformator = xsltTransformator;
    }

    public StringBuffer reportExecutor(File postDiscoveryPath, Map<String, String> params) throws ParserConfigurationException, SAXException {

        List<ReportEntryType> reportEnties = reportGenerator.getReportEntry();
        StringBuffer sb = new StringBuffer();
        String deviceType =  params.get("deviceType");
        String deviceName = params.get("deviceName");
        File postDiscoveryNodeFolder = new File(postDiscoveryPath,params.get("deviceName"));
        if(!postDiscoveryNodeFolder.exists()){
            postDiscoveryNodeFolder.mkdir();
        }

        for (ReportEntryType reportEntry : reportEnties) {
            String reportName = reportEntry.getName();
            File postDiscoveryReportOutput = new File(postDiscoveryNodeFolder+File.separator+reportName+".xml");

            logger.debug("Report id: " + reportName + " " + reportEntry.getDeviceType() + " " + reportEntry.getDeviceName()+ "with params"+params.toString());
            if (reportEntry.getDeviceType().equals(deviceType)) {

                sb.append("<?xml version=\"1.0\"?>\n");

               // sb.append("<report name=\""+reportEntry.getName()+"\" " + "deviceType=\""+deviceType+"\" "+ "deviceName=\""+deviceName+"\">");
               sb.append("<report>\n");
                List<CommandType> commands = reportEntry.getCommand();
                List<CommandDescriptorType> commandDescriptors = reportGenerator.getCommandDescriptor();

                for (CommandDescriptorType commandDescriptor : commandDescriptors) {

                    if (commandDescriptor.getDeviceType().equals(deviceType)) {
                        LoginScriptType loginScript = commandDescriptor.getLoginScript();
                        LogoutScriptType logoutScript = commandDescriptor.getLogoutScript();
                        SendCommandScriptType sendCommandScript = commandDescriptor.getSendCommandScript();

                        try {
                            Expect4GroovyScriptLauncher launcher = new Expect4GroovyScriptLauncher();
                            logger.debug("Launch connection to :" +deviceName+" with params "+params.toString());
                            logger.debug("Script path = " + scriptPath + "login script "+loginScript.getValue());

                            Map<String, Integer> loginResult = launcher.open(new String[]{scriptPath + File.separator}, loginScript.getValue(), params);

                            if(loginResult.get("status")==2){
                                logger.debug(loginResult);
                                return null;
                            }

                            List<CommandType> commandDescriptorCommands = commandDescriptor.getCommand();

                            for (CommandType descriptorCommand : commandDescriptorCommands) {

                                for (CommandType command : commands) {

                                    if (descriptorCommand.getName().equals(command.getName())) {
                                        String evalScriptName = descriptorCommand.getEvalScript();
                                        String commandString = descriptorCommand.getSendCommand();
                                        File script = null;
                                        Map<String, Object> result = null;


                                        if (evalScriptName!=null){
                                            script = new File(scriptPath,evalScriptName);
                                            result = launcher.sendCommand(sendCommandScript.getValue(), commandString, script.getAbsolutePath());
                                            Map<String, Object> evalData = (Map<String, Object>) result.get("reportResult");
                                            if(evalData!=null){
                                                for (String key : evalData.keySet()) {
                                                    sb.append("\n\t<entry>\n");
                                                    sb.append("\t\t<AuditRule>" + key + "</AuditRule>" + "\n");
                                                    Map<String,Map<String,Integer>> keyMap= (Map<String, Map<String,Integer>>) evalData.get(key);
                                                    if(keyMap.get("message")!=null){
                                                     sb.append("\t\t<Statement><![CDATA[" + keyMap.get("message") + "]]></Statement>" + "\n");
                                                    }

                                                    if(keyMap.get("score")!=null){

                                                                sb.append("\t\t<Score>"+keyMap.get("score")+"</Score>"+"\n");
                                                    }
                                                    sb.append("\t</entry>");
                                                    logger.debug(key + " " + evalData.get(key));
                                                }



                                            }
                                           // result = launcher.sendCommand(sendCommandScript.getValue(), commandString, null);
                                            if(result.get("commandResult")!=null){
                                                File postDiscoveryCommandOutput = new File(postDiscoveryNodeFolder+File.separator+command.getName()+".txt");
                                                try {
                                                        FileUtils.writeStringToFile(postDiscoveryCommandOutput,result.get("commandResult").toString());
                                                } catch (IOException e) {
                                                    logger.info(e);  //To change body of catch statement use File | Settings | File Templates.
                                                }
                                            }
                                        } else {
                                            result = launcher.sendCommand(sendCommandScript.getValue(), commandString, null);
                                            if(result.get("commandResult")!=null){
                                                File postDiscoveryCommandOutput = new File(postDiscoveryNodeFolder+File.separator+command.getName()+".txt");
                                                try {
                                                    FileUtils.writeStringToFile(postDiscoveryCommandOutput,result.get("commandResult").toString());
                                                } catch (IOException e) {
                                                    logger.info(e);  //To change body of catch statement use File | Settings | File Templates.
                                                }
                                            }

                                        }

                                    }

                                }
                            }

                            launcher.close(logoutScript.getValue());
                        } catch (ResourceException e) {
                            logger.info(e);  //To change body of catch statement use File | Settings | File Templates.
                        } catch (ScriptException e) {
                            logger.info(e);  //To change body of catch statement use File | Settings | File Templates.
                            //To change body of catch statement use File | Settings | File Templates.
                        }
                    }

                }
                sb.append("\n</report>");
                try {
                    String report = new XmlFormatter().format(sb.toString());

                    ByteArrayOutputStream finalReport = generateTableReport(new ByteArrayInputStream(report.getBytes()));
                    FileUtils.writeStringToFile(postDiscoveryReportOutput,finalReport.toString());
                } catch (IOException e) {
                    logger.info(e);  //To change body of catch statement use File | Settings | File Templates.
                }

            }
        }

        return sb;
    }

    public static void main(String[] args) throws IOException {

        File projectDir = new File("/Users/niau/trunk");
        File scriptPath = new File("/postDiscoverer/conf/groovy/");
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
        params.put("protocol", "telnet");
        params.put("deviceName", "R1");
        params.put("deviceType", "CISCO");
        params.put("address", "172.16.13.1");
        params.put("port", "23");

        ResourceType resource = resourceManager.findResource(params);
        List connectParameters = resource.getConnectionParams();

        for (int i = 0; i < connectParameters.size(); i++) {
            ConnectionParamsType connParamsType = (ConnectionParamsType) connectParameters.get(i);

            String connectionType = connParamsType.getConnectionType();
            if (connectionType.equalsIgnoreCase(params.get("protocol"))) {

                for (ParamType param : connParamsType.getParam()) {
                    params.put(param.getName(), param.getValue());
                }

            }
        }

        File postDiscoveryConfing = new File(projectDir + "/postDiscoverer/conf/xml/reportGenerator.xml");


        ReportGeneratorType reportGenerator = null;

        FileInputStream is = new FileInputStream(postDiscoveryConfing);
        try {
            reportGenerator = JaxbMarshalar.unmarshal(ReportGeneratorType.class, is);
        } catch (JAXBException e) {
           logger.info(e);  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            is.close();
        }

        ReportManager reportManager = new ReportManager(reportGenerator, "postDiscoverer/conf/groovy/",projectDir,new File(projectDir,"iTopologyManager/rightClick/conf/xslt/table_creator.xslt"));
        StringBuffer report = null;
        try {
            report = reportManager.reportExecutor(new File("/Users/niau/trunk/version1/post-discovery"),params);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        if(report!=null){
                   System.out.println(report.toString());

        }  else{
            System.out.println("Report generation failed!");

        }
    }

    private ByteArrayOutputStream generateTableReport(ByteArrayInputStream inputStream){
        XsltTransformer transformer = new XsltTransformer();
        ByteArrayOutputStream os  = new ByteArrayOutputStream();
        ByteArrayOutputStream reportOutputStream = new ByteArrayOutputStream();


        try {
            transformer.transformXML(inputStream, xsltTransformator, reportOutputStream, null);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

       return  reportOutputStream;
    }
}
