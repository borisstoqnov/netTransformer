package net.itransformers.idiscover.v2.core.listeners.network;

import net.itransformers.idiscover.v2.core.NetworkDiscoveryListener;
import net.itransformers.idiscover.v2.core.NetworkDiscoveryResult;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.postDiscoverer.core.ReportManager;
import net.itransformers.postDiscoverer.reportGenerator.ReportGeneratorType;
import net.itransformers.resourcemanager.ResourceManager;
import net.itransformers.resourcemanager.config.ConnectionParamsType;
import net.itransformers.resourcemanager.config.ParamType;
import net.itransformers.resourcemanager.config.ResourceType;
import net.itransformers.resourcemanager.config.ResourcesType;
import net.itransformers.utils.JaxbMarshalar;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostNetworkDiscoveryListener implements NetworkDiscoveryListener {
    static Logger logger = Logger.getLogger(PostNetworkDiscoveryListener.class);

    private String postDiscoveryDataDirName;
    private String labelDirName;
    private String scriptPath;
    private File projectPath;
    private String resourceManagerPath;
    private String reportGeneratorPath;


    private String tableTransfomator;

//    public PostNetworkDiscoveryListener(){
//
//    }
//    public PostNetworkDiscoveryListener(String graphmlDataDirName, String labelDirName, String scriptPath, File projectPath, String resourceManagerPath, String reportGeneratorPath) {
//        this.graphmlDataDirName = graphmlDataDirName;
//        this.labelDirName = labelDirName;
//        this.scriptPath = scriptPath;
//        this.projectPath = projectPath;
//        this.resourceManagerPath = resourceManagerPath;
//        this.reportGeneratorPath = reportGeneratorPath;
//    }



    @Override
    public void networkDiscovered(NetworkDiscoveryResult result) {
        logger.info("Starting PostNetwork Discovery Listener");
        String xml = null;
        try {
            xml = FileUtils.readFileToString(new File(projectPath, resourceManagerPath));
        } catch (IOException e) {
            e.printStackTrace();
        }

        InputStream is1 = new ByteArrayInputStream(xml.getBytes());
        ResourcesType deviceGroupsType = null;

        try {
            deviceGroupsType = net.itransformers.resourcemanager.util.JaxbMarshalar.unmarshal(ResourcesType.class, is1);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        ResourceManager resourceManager = new ResourceManager(deviceGroupsType);

        File postDiscoveryConfing = new File(projectPath + reportGeneratorPath);

        FileInputStream is = null;
        ReportGeneratorType reportGenerator = null;
        try {
            is = new FileInputStream(postDiscoveryConfing);
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        try {
             reportGenerator = JaxbMarshalar.unmarshal(ReportGeneratorType.class, is);
        } catch (JAXBException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        //TODO remove xsltReport hardCode
        final ReportManager reportManager = new ReportManager(reportGenerator, "postDiscoverer/conf/groovy/",projectPath,new File(projectPath,tableTransfomator));


        for (String nodeName : result.getSourceConnectionDetails().keySet()) {

            logger.info("Post Network Discovery of "+nodeName);
            final Map<String, String> params = new HashMap<String, String>();
            params.put("deviceName",nodeName);
           // NodeDiscoveryResult result1 = result.getSourceConnectionDetails().get(nodeName);

           ConnectionDetails  connectionDetails = result.getSourceConnectionDetails().get(nodeName);
            params.put("deviceType",connectionDetails.getParam("deviceType"));
            params.put("protocol", "telnet");
            params.put("address",connectionDetails.getParam("ipAddress"));

            ResourceType resource =  resourceManager.findResource(params);
            if(resource==null){
                continue;
            }
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
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        reportManager.reportExecutor(new File(labelDirName),params);
                    } catch (ParserConfigurationException e) {
                        e.printStackTrace();
                    } catch (SAXException e) {
                        e.printStackTrace();
                    }
                }
            });
           // threads.add(thread);
            thread.run();
        }
//        for (Thread thread : threads) {
//            try {
//                thread.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
//            }
//        }

    }
    public String getPostDiscoveryDataDirName() {
        return postDiscoveryDataDirName;
    }

    public void setPostDiscoveryDataDirName(String postDiscoveryDataDirName) {
        this.postDiscoveryDataDirName = postDiscoveryDataDirName;
    }

    public String getLabelDirName() {
        return labelDirName;
    }

    public void setLabelDirName(String labelDirName) {
        this.labelDirName = labelDirName;
    }

    public String getScriptPath() {
        return scriptPath;
    }

    public void setScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
    }

    public File getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(File projectPath) {
        this.projectPath = projectPath;
    }

    public String getResourceManagerPath() {
        return resourceManagerPath;
    }

    public void setResourceManagerPath(String resourceManagerPath) {
        this.resourceManagerPath = resourceManagerPath;
    }

    public String getReportGeneratorPath() {
        return reportGeneratorPath;
    }

    public void setReportGeneratorPath(String reportGeneratorPath) {
        this.reportGeneratorPath = reportGeneratorPath;
    }

    public String getTableTransfomator() {
        return tableTransfomator;
    }

    public void setTableTransfomator(String tableTransfomator) {
        this.tableTransfomator = tableTransfomator;
    }


}
