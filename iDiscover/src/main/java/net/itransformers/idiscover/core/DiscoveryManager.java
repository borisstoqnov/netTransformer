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

package net.itransformers.idiscover.core;

import net.itransformers.idiscover.core.discoveryconfig.DiscoveryManagerListenerType;
import net.itransformers.idiscover.core.discoveryconfig.DiscoveryManagerType;
import net.itransformers.idiscover.core.discoveryconfig.ParamType;
import net.itransformers.idiscover.core.discoveryconfig.PostDiscoveryManagerListenerType;
import net.itransformers.idiscover.discoverers.DefaultDiscovererFactory;
import net.itransformers.idiscover.networkmodel.*;
import net.itransformers.idiscover.util.JaxbMarshalar;
import net.itransformers.idiscover.v2.core.NetworkDiscoveryListener;
import net.itransformers.resourcemanager.ResourceManager;
import net.itransformers.resourcemanager.config.ResourceType;
import net.itransformers.resourcemanager.config.ResourcesType;
import net.itransformers.utils.CmdLineParser;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import javax.xml.bind.JAXBException;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*Discovery Manager Class implemnts the core snmpDiscovery manager algorithm */

public class DiscoveryManager {
    static Logger logger = Logger.getLogger(DiscoveryManager.class);

    private DiscoveryHelperFactory discoveryHelperFactory;
    private List<DiscoveryListener> listeners = new ArrayList<DiscoveryListener>();
    private List<PostDiscoveryListener> postDiscoveryListeners = new ArrayList<PostDiscoveryListener>();

    private List<NetworkDiscoveryListener> networkListeners = new ArrayList<NetworkDiscoveryListener>();

    private ResourceManager resourceManager;
    private DiscovererFactory discovererFactory;
    public DiscoveryResourceManager discoveryResource;
    private boolean isRunning;
    private boolean isPaused;
    private boolean isStopped;
    private boolean postDiscoveryflag;

    public DiscoveryManager(File projectDir, String label, ResourceManager resourceManager, DiscovererFactory discovererFactory, DiscoveryHelperFactory discoveryHelperFactory, Boolean postDiscoveryflag) throws IllegalAccessException, InstantiationException {
        this.resourceManager = resourceManager;
        this.discovererFactory = discovererFactory;
        this.discoveryHelperFactory = discoveryHelperFactory;
        this.discoveryResource = new DiscoveryResourceManager(projectDir,label,"resourceManager/conf/xml/resource.xml");
        this.postDiscoveryflag = postDiscoveryflag;
    }

    public NetworkType discoverNetwork(Resource resource, String mode, String[] discoveryTypes) throws Exception {
        isRunning = true;
        Map<String, Device> foundDevices = new HashMap<String, Device>();
        NetworkType network = new NetworkType();
        Discoverer discoverer = discovererFactory.createDiscoverer(resource);
        this.discoverDevice(resource, discoveryTypes, mode, network, foundDevices, discoverer);
        stop();
        for (DiscoveredDeviceData  data : network.getDiscoveredDevice()) {
            System.out.println(data.getName() + "\n");
            for (ObjectType object : data.getObject()) {
                ParametersType params  = object.getParameters();
                for (ParameterType param : params.getParameter()) {
                    System.out.println(param.getName()+" " +param.getValue()+"\n");
                }

                for (ObjectType innterObject :object.getObject() ){
                    System.out.println(innterObject.getObjectType());
                    ParametersType innerParams  = innterObject.getParameters();
                    for (ParameterType param : innerParams.getParameter()) {
                        System.out.println(param.getName()+" " +param.getValue()+"\n");
                    }
                }
            }


        }

        return network;
    }

    private void discoverDevice(Resource resource, String[] discoveryTypes, String mode,
                                NetworkType network, Map<String,Device> foundDevices,
                                Discoverer discoverer) {
        if (isStopped) {
            logger.info("Discovery process stopped!");
            return;
        }
        if (isPaused) doPause();
        //Obtain resource hostname, attributes and deviceType;
        String deviceName=resource.getHost();
        Map<String, String> attributes = resource.getAttributes();
        String deviceType=resource.getDeviceType();
        String deviceStatus=null;
        //Obtain device status. Device status is set to "initial" for the first device and to discovered to any other. Topology viewer use device status to draw
        //initial picture around initial device.
        if (attributes.containsKey("status")){
            deviceStatus=attributes.get("status");
        }
        //if we discover the initial device
        if (deviceStatus !=null&&deviceStatus.equals("initial")){
           //get it's hostname
           deviceName=discoverer.getDeviceName(resource);
           //If we can't obtain the name of the initial device so there is something wrong so snmpDiscovery will stop.
           if (deviceName == null) return;
           //Strip domain string from hostname
           if  (deviceName.indexOf(".") != -1){
                deviceName=deviceName.substring(0,deviceName.indexOf("."));
           }
           //Set hostname into resource
           resource.setHost(deviceName);
            //Get deviceType from the device through snmp request;
           deviceType = discoverer.getDeviceType(resource);
           resource.setDeviceType(deviceType);
        }


        logger.info("Discovering device: "+deviceName);

        if(deviceName!=null){
            //Check if device is not already discovered if it is not so discover it.
            boolean deviceExists = foundDevices.containsKey(deviceName);
            if (!deviceExists) {
                logger.info("Device with "+deviceName+" is still undiscovered. So discover it!!!");

                DiscoveryHelper discoveryHelper = discoveryHelperFactory.createDiscoveryHelper(deviceType);
                //Check if device comply to stop criteria.
                boolean stopCriteriaReached = discoveryHelper.checkStopCriteria(resource);
                if (stopCriteriaReached) {
                    logger.info("stopCriteria Reached, for :"+ resource.getAddress());
                } else {
                    logger.info("stopCriteria not Reached, for :"+ resource.getAddress());
                }
                //If device comply so discover it.

                if (!stopCriteriaReached && mode.equals("network")) {
                    Device device = createDevice(resource, discoveryTypes, network, foundDevices, discoverer, deviceName, discoveryHelper);
                    //TODO Why is that???
                    discoveryHelper = null;
                    deviceName = null;
                    this.findNeighbours(device, discoveryTypes, mode, network, foundDevices, discoverer);
                } else if (!stopCriteriaReached && mode.equals("node")){
                    Device device;
                    device = createDevice(resource, discoveryTypes, network, foundDevices, discoverer, deviceName, discoveryHelper);

               }
// else if (!stopCriteriaReached && mode.equals("neighbors")){
//                    Device device = createDevice(resource, discoveryTypes, network, foundDevices, discoverer, deviceName, discoveryHelper);
//                    //TODO Why is that???
//                    discoveryHelper = null;
//                    deviceName = null;
//                    this.findNeighbours(resource, device, discoveryTypes, mode, network, foundDevices, discoverer);
//
//                }
         }else{
              logger.info("Device with "+deviceName+" is already discovered. So skip it!!!");
         }
        }

    }

    private synchronized void doPause() {
        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void pause(){
        isPaused = true;
    }

    public synchronized void resume(){
        isPaused = false;
        notifyAll();
    }
    public synchronized void stop(){
        isStopped = true;
        isRunning = false;
    }

    public synchronized boolean isStopped(){
        return isStopped;
    }

    public synchronized boolean isPaused(){
        return isPaused;
    }

    public synchronized boolean isRunning() {
        return isRunning;
    }

    private Device createDevice(Resource resource, String[] discoveryTypes, NetworkType network, Map<String, Device> foundDevices, Discoverer discoverer, String deviceName, DiscoveryHelper discoveryHelper) {
        Device device;
        String[] requestParamsList = discoveryHelper.getRequestParams(discoveryTypes);
        RawDeviceData rawData = discoverer.getRawDeviceData(resource, requestParamsList);
        DiscoveredDeviceData discoveredDeviceData = discoveryHelper.parseDeviceRawData(rawData,discoveryTypes, resource);
        device = discoveryHelper.createDevice(discoveredDeviceData);
        assert device.getName().equals(deviceName);
        foundDevices.put(deviceName, device);

        //Update resource attribute discovered status to discovered!
        Map<String, String> attributes = resource.getAttributes();
        resource.setAttributes(attributes);

        network.getDiscoveredDevice().add(discoveredDeviceData);
        fireNewDeviceEvent(deviceName, rawData, discoveredDeviceData, resource);
        return device;
    }

    private void findNeighbours(Device device, String[] discoveryTypes, String mode,
                                NetworkType network, Map<String, Device> foundDevices,
                                Discoverer discoverer) {
        List<DeviceNeighbour> deviceNeighbours = device.getDeviceNeighbours();
        if (deviceNeighbours == null) {
            logger.info("No neighbours found for device: "+device.getName());
            return;
        } else {
            logger.info("Found Neighbours: "+deviceNeighbours);
        }
        for (DeviceNeighbour deviceNeighbour : deviceNeighbours) {
            if (isStopped) return;
            if (isPaused) doPause();
            //Get IP address and Hostname of the discovered Neighbor
            IPv4Address ipAddress = deviceNeighbour.getIpAddress();
            String hostName = deviceNeighbour.getHostName();
            String deviceType = deviceNeighbour.getDeviceType();
            boolean Reachable =  deviceNeighbour.getStatus();
            //String SnmpCommunity =  deviceNeighbour.getROCommunity();
            //Discover only the neighbors that are reachable
             if (Reachable){


                Map<String,String> params = new HashMap<String, String>();
                if(hostName!=null){
                    params.put("DeviceName",hostName);
                }
                if (deviceType!=null){
                    params.put("DeviceType",deviceType);
                }
                params.put("protocol","SNMP");
                ResourceType SNMP = this.discoveryResource.ReturnResourceByParam(params);
                Map<String, String> SNMPconnParams = new HashMap<String, String>();
                SNMPconnParams = this.discoveryResource.getParamMap(SNMP);


                Resource resource = new Resource(hostName,ipAddress,deviceType, Integer.parseInt(SNMPconnParams.get("port")), SNMPconnParams);


                this.discoverDevice(resource, discoveryTypes, mode, network, foundDevices, discoverer);
             }
        }
    }

    private void fireNewDeviceEvent(String deviceName, RawDeviceData rawData, DiscoveredDeviceData discoveredDeviceData, Resource resource) {
        for (DiscoveryListener listener : listeners) {
            listener.handleDevice(deviceName,rawData, discoveredDeviceData, resource);
        }
        if (postDiscoveryflag){
            for (PostDiscoveryListener postDiscoverylistener: postDiscoveryListeners){
                postDiscoverylistener.handleDevice(deviceName,rawData, discoveredDeviceData, resource);

            }
        }
    }



//    private void fireNetworkDiscoveredEvent(NetworkType network, Resource resource) {
//        for (NetworkDiscoveryListener listener : networkListeners) {
//            listener.networkDiscovered();
//        }
//    }

    public void addDiscoveryManagerListener(DiscoveryListener listener){
        listeners.add(listener);
    }

    public void removedDiscoveryManagerListener(DiscoveryListener listener){
        listeners.remove(listener);
    }

    public void addPostDiscoveryManagerListener(PostDiscoveryListener listener){
        postDiscoveryListeners.add(listener);
    }

    public void removedPostDiscoveryManagerListener(PostDiscoveryListener listener){
        postDiscoveryListeners.remove(listener);
    }
    private static void printUsage(String param){
        System.out.println("Usage:   java -h <Initial IP address> -p <port> [-d <mode network|node> -c <community> -c2 <community> -f <discoveryParameters.xml>");
        System.out.println("Example WIN: java -h X.X.X.X -d network -m snmptoolkit\\mibs -f iDiscover\\conf\\xml\\discoveryManager.xml");
        System.out.print("Example UX: java -h X.X.X.X  -d network -m snmptoolkit/mibs -f iDiscover/conf/xml/discoveryManager.xml");
        System.out.println("Missing parameter: "+param);
    }

    public static void main(String[] args) throws Exception, IllegalAccessException, JAXBException {
        Map<String,String> params = CmdLineParser.parseCmdLine(args);
        if (params == null) {
            printUsage("mibDir"); return;
        }
        final String fileName = params.get("-f");
        if (fileName == null) {
            printUsage("fileName"); return;
        }
        File file = new File(fileName);

        DiscoveryManager manager = createDiscoveryManager(file.getParentFile(),file.getName(),"network",true);

        String mode = params.get("-d");
        if (mode == null){
            //Set default snmpDiscovery mode to discover network!!!
            mode = "network";
        }

        Map<String,String> resourceSelectionParams = new HashMap<String, String>();
        resourceSelectionParams.put("protocol","SNMP");
        ResourceType snmp = manager.discoveryResource.ReturnResourceByParam(resourceSelectionParams);

        Map<String, String> snmpConnParams = new HashMap<String, String>();
        snmpConnParams = manager.discoveryResource.getParamMap(snmp);

        String host = params.get("-h");
        IPv4Address initialIPaddress= new IPv4Address(host,null);
        snmpConnParams.put("status", "initial");
        String mibDir = params.get("-m");
        snmpConnParams.put("mibDir", mibDir);

       snmpConnParams.get("port");
       Resource resource = new Resource(initialIPaddress,null, Integer.parseInt(snmpConnParams.get("port")), snmpConnParams);


        if (resource == null) ;

        String[] discoveryTypes = new String[]{"PHYSICAL","NEXT_HOP","OSPF","ISIS","BGP","RIP","ADDITIONAL","IPV6"};

//        DiscovererFactory.init(new SimulSnmpWalker(resource, new File("logs.log")));
//        DiscovererFactory.init();
//        Discoverer discoverer = new SnmpWalker(resource);
        NetworkType network = manager.discoverNetwork(resource, mode, discoveryTypes);

        for (DiscoveredDeviceData  data : network.getDiscoveredDevice()) {
            System.out.println(data.getName() + "\n");
            for (ObjectType object : data.getObject()) {
                     for (ObjectType innterObject :object.getObject() ){
                         System.out.println(innterObject.getObjectType());
                     }
            }


        }
//        List<DiscoveredDeviceData> discoveredDeviceDatas = network.getDiscoveredDevice();
//        for (DiscoveredDeviceData discoveredDeviceData : discoveredDeviceDatas) {
//            discoveredDeviceData.getName();
//            ParametersType parameters = discoveredDeviceData.getParameters();
//            List<ParameterType> paras =   parameters.getParameter();
//            for (ParameterType para : paras) {
//
//            }
//
//        }
        
    }

    public static DiscoveryManager createDiscoveryManager(File projectDir, String discoveryManagerXmlRelPath, String label,Boolean postDiscoveryflag) throws JAXBException, IOException, ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        File file = new File(projectDir,discoveryManagerXmlRelPath);
        DiscoveryManagerType discoveryManagerType;
        FileInputStream is = new FileInputStream(file);
        try {
            discoveryManagerType = JaxbMarshalar.unmarshal(DiscoveryManagerType.class, is);
        } finally {
            is.close();
        }

        DiscoveryHelperFactory discoveryHelperFactory = createDiscoveryHelperFactory(discoveryManagerType);
        ResourceManager resourceManager;
        {
        String xml = FileUtils.readFileToString(new File(projectDir,"resourceManager/conf/xml/resource.xml"));
        InputStream is1 = new ByteArrayInputStream(xml.getBytes());
        ResourcesType deviceGroupsType = net.itransformers.resourcemanager.util.JaxbMarshalar.unmarshal(ResourcesType.class, is1);
        resourceManager = new ResourceManager(deviceGroupsType);
//        Map<String,String> params = new HashMap<String, String>();
//        params.put("geoloc","Moscow");
        }
        DiscovererFactory discovererFactory = new DefaultDiscovererFactory();
        DiscoveryManager manager = new DiscoveryManager(projectDir, label, resourceManager,discovererFactory, discoveryHelperFactory,postDiscoveryflag);


        List<DiscoveryManagerListenerType> listenerTypes = discoveryManagerType.getDiscoveryManagerListeners().getDiscoveryManagerListener();
        for (DiscoveryManagerListenerType listenerType : listenerTypes) {
            String classStr = listenerType.getClazz();
            Class clazz1 = Class.forName(classStr);
            Constructor constructor1 = clazz1.getConstructor(Map.class,File.class,String.class);
            Map<String, String> listenerParams = new HashMap<String, String>();
            List<ParamType> paramType = listenerType.getParam();
            for (ParamType type : paramType) {
                listenerParams.put(type.getName(),type.getValue());
            }
            DiscoveryListener discoveryManagerListener = (DiscoveryListener) constructor1.newInstance(listenerParams, projectDir, label);
            manager.addDiscoveryManagerListener(discoveryManagerListener);
        }

        List<PostDiscoveryManagerListenerType> postDiscoveryListenerTypes = discoveryManagerType.getPostDiscoveryManagerListeners().getPostDiscoveryManagerListener();
        for (PostDiscoveryManagerListenerType listenerType : postDiscoveryListenerTypes) {
            String classStr = listenerType.getClazz();
            Class clazz1 = Class.forName(classStr);
            Constructor constructor1 = clazz1.getConstructor(Map.class,File.class,String.class);
            Map<String, String> listenerParams = new HashMap<String, String>();
            List<ParamType> paramType = listenerType.getParam();
            for (ParamType type : paramType) {
                listenerParams.put(type.getName(),type.getValue());
            }
            PostDiscoveryListener postDiscoveryManagerListener = (PostDiscoveryListener) constructor1.newInstance(listenerParams, projectDir, label);
            manager.addPostDiscoveryManagerListener(postDiscoveryManagerListener);
        }

//        List<ParamType> vlanList = discoveryManagerType.getManagementVlans().getParam();
//        for (ParamType paramType : vlanList) {
//
//        }
        return manager;
    }

    private static DiscoveryHelperFactory createDiscoveryHelperFactory(DiscoveryManagerType discoveryManagerType) throws ClassNotFoundException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
        net.itransformers.idiscover.core.discoveryconfig.DiscoveryHelperType discoveryHelperType = discoveryManagerType.getDiscoveryHelper();
        String clazzStr = discoveryHelperType.getClazz();
        Class<?> clazz = Class.forName(clazzStr);
        Map<String, String> discoveryHelerpParams = new HashMap<String, String>();

        for (ParamType s : discoveryHelperType.getParameters().getParam()) {
            discoveryHelerpParams.put(s.getName(),s.getValue());
        }
        Constructor constructor = clazz.getConstructor(Map.class);
        return (DiscoveryHelperFactory) constructor.newInstance(discoveryHelerpParams);
    }

    public static Resource createResource(Map<String, String> params) {
        String host = params.get("-h");
        String port = params.get("-p");
        String mibDir = params.get("-m");
//        if (mibDir == null) {
//            printUsage("mibDir");
//            return null;
//        }
        String snmpROComm = params.get("-c");
        String snmpROComm2 = params.get("-c2");
        Map<String, String> resourceParams = new HashMap<String, String>();
        resourceParams.put("community", snmpROComm);
        resourceParams.put("community2", snmpROComm2);
        resourceParams.put("version", "1");
        resourceParams.put("mibDir", mibDir);
        resourceParams.put("status","initial");
        resourceParams.put("hopsToInitial","0");
        //resourceParams.put("deviceType","DEFAULT");
//        resourceParams.put("snmpRWcomm", snmpRWComm);
        IPv4Address initialIPaddress= new IPv4Address(host,null);
        Resource resource;
        if (port == null){
            resource = new Resource(initialIPaddress,null, resourceParams);
        } else {
            int portInt = Integer.parseInt(port);
            resource = new Resource(initialIPaddress, null,portInt, resourceParams);
        }
        return resource;
    }

}
