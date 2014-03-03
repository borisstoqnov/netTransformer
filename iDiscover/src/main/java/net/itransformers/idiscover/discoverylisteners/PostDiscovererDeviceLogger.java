/*
 * iTransformer is an open source tool able to discover and transform
 *  IP network infrastructures.
 *  Copyright (C) 2012  http://itransformers.net
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.itransformers.idiscover.discoverylisteners;

import net.itransformers.idiscover.core.DiscoveryListener;
import net.itransformers.idiscover.core.RawDeviceData;
import net.itransformers.idiscover.core.Resource;
import net.itransformers.idiscover.networkmodel.DiscoveredDeviceData;
import net.itransformers.postDiscoverer.core.ReportManager;
import net.itransformers.postDiscoverer.reportGenerator.ReportGeneratorType;
import net.itransformers.resourcemanager.ResourceManager;
import net.itransformers.resourcemanager.config.ConnectionParamsType;
import net.itransformers.resourcemanager.config.ParamType;
import net.itransformers.resourcemanager.config.ResourceType;
import net.itransformers.resourcemanager.config.ResourcesType;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import net.itransformers.utils.XmlFormatter;

public class PostDiscovererDeviceLogger implements DiscoveryListener{
    static Logger logger = Logger.getLogger(PostDiscovererDeviceLogger.class);
    private File path;
    private File scriptPath;
    private File resourceManagerPath;
    private File reportGeneratorPath;
    private File postDiscoveryPath;
    private File tableTransfomator;
    private final File labelPath;
    private ResourceManager resourceManager;
    private ReportManager reportManager;

    public PostDiscovererDeviceLogger(Map<String, String> params, File baseDir, String label) {
        File base1 = new File(baseDir,params.get("path"));
        if (!base1.exists()){
            base1.mkdir();
        }
        labelPath = new File(base1, label);
        if (!labelPath.exists()) {
            labelPath.mkdir();
        }
            this.postDiscoveryPath = new File(labelPath, params.get("postDiscovery-logging-path"));
            if (!this.postDiscoveryPath.exists()){
                this.postDiscoveryPath.mkdir();
            }

//        }
        this.scriptPath = new File(baseDir,params.get("scriptPath"));
        this.resourceManagerPath = new File(baseDir,params.get("resourceManagerPath"));
        this.reportGeneratorPath = new File(baseDir,params.get("reportGeneratorPath"));
        this.tableTransfomator = new File(baseDir,params.get("tableTransformator"));

        String xml = null;
        try {
            xml = FileUtils.readFileToString(resourceManagerPath);
        } catch (IOException e) {
            logger.debug("Unable to read resource xml", e);
        }

        InputStream is1 = new ByteArrayInputStream(xml.getBytes());
        ResourcesType deviceGroupsType = null;

        try {
            deviceGroupsType = net.itransformers.resourcemanager.util.JaxbMarshalar.unmarshal(ResourcesType.class, is1);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        resourceManager = new ResourceManager(deviceGroupsType);


        FileInputStream is = null;
        ReportGeneratorType reportGenerator = null;
        try {
            is = new FileInputStream(reportGeneratorPath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        try {
            reportGenerator = net.itransformers.utils.JaxbMarshalar.unmarshal(ReportGeneratorType.class, is);
        } catch (JAXBException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                logger.debug("Unable to load report generator!", e);  //To change body of catch statement use File | Settings | File Templates.
            }
        }
        reportManager = new ReportManager(reportGenerator, scriptPath.getPath(),baseDir,tableTransfomator);

    }

    public void handleDevice(final String deviceName, RawDeviceData rawData, DiscoveredDeviceData discoveredDeviceData, Resource resource) {


            final Map<String, String> params = new HashMap<String, String>();
            params.put("deviceName",deviceName);
            params.put("deviceType",resource.getDeviceType());
            params.put("protocol", "ssh");
            params.put("address",resource.getIpAddress().getIpAddress());

            ResourceType resourceType =  resourceManager.findResource(params);
            List connectParameters = resourceType.getConnectionParams();

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

                        reportManager.reportExecutor(postDiscoveryPath,params);
                    }catch (Exception e){
                       logger.debug("Unable to execute a report for device " + deviceName, e);  //To change body of catch statement use File | Settings | File Templates.

                    }
                }
            });

            thread.run();


    }



}
