package net.itransformers.idiscover.v2.core.node_discoverers.snmpdiscoverer;

import net.itransformers.idiscover.core.DiscoveryHelper;
import net.itransformers.idiscover.core.DiscoveryResourceManager;
import net.itransformers.idiscover.core.RawDeviceData;
import net.itransformers.idiscover.discoveryhelpers.xml.SnmpForXslt;
import net.itransformers.idiscover.discoveryhelpers.xml.XmlDiscoveryHelperFactory;
import net.itransformers.idiscover.discoveryhelpers.xml.XmlDiscoveryHelperV2;
import net.itransformers.idiscover.networkmodel.DiscoveredDeviceData;
import net.itransformers.idiscover.v2.core.node_discoverers.dnsresolver.DnsResolver;
import net.itransformers.idiscover.v2.core.node_discoverers.icmp.IcmpStatus;
import net.itransformers.idiscover.v2.core.node_discoverers.snmpdiscoverer.snmpmanager.SnmpManagerCreator;
import net.itransformers.resourcemanager.config.ResourceType;
import net.itransformers.snmp2xml4j.snmptoolkit.MibLoaderHolder;
import net.itransformers.snmp2xml4j.snmptoolkit.Node;
import net.itransformers.snmp2xml4j.snmptoolkit.SnmpManager;
import net.itransformers.snmp2xml4j.snmptoolkit.SnmpXmlPrinter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by niau on 8/22/16.
 */
public abstract class SnmpNodeDiscoverer {
    static Logger logger = Logger.getLogger(SnmpNodeDiscoverer.class);

    protected XmlDiscoveryHelperFactory discoveryHelperFactory;
    protected String[] discoveryTypes;
    protected DiscoveryResourceManager discoveryResource;
    protected MibLoaderHolder mibLoaderHolder;
    protected boolean icmpStatus;


    public SnmpNodeDiscoverer(XmlDiscoveryHelperFactory discoveryHelperFactory, String[] discoveryTypes, DiscoveryResourceManager discoveryResource, MibLoaderHolder mibLoaderHolder, boolean icmpStatus) throws Exception {
        this.discoveryHelperFactory = discoveryHelperFactory;
        this.discoveryTypes = discoveryTypes;
        this.discoveryResource = discoveryResource;
        this.mibLoaderHolder = mibLoaderHolder;
        this.icmpStatus = icmpStatus;
    }

    protected String getSymbolByOid(String mibName, String oid) {
        return mibLoaderHolder.getSymbolByOid(mibName, oid);
    }


    protected String subStringDeviceName(String sysName) {
        if (sysName == null) return null;
        String hostName =  StringUtils.substringBefore(sysName, ".");
        return hostName;

    }

    protected String snmpGet(SnmpManager snmpManager, String oidString) {
        try {
            return snmpManager.snmpGet(oidString);
        } catch (IOException e) {
            logger.info(e.getMessage());
            return null;
        }
    }
    protected String getDeviceId(Map<String, String> params) {
        String deviceName = params.get("deviceName");
        String dnsCanonicalName = params.get("dnsCanonicalName");
        String dnsShort = subStringDeviceName(dnsCanonicalName);
        String ipAddress = params.get("ipAddress");
        String hostName = params.get("hostName");


        String id;
        if (params.get("deviceName") != null) {
            id = deviceName;
        } else if (dnsCanonicalName != null) {
            id = dnsShort;
        } else if (hostName != null){
            id=hostName;
        }else {
        id =ipAddress;
        }
        return id;
    }


    protected HashMap<String,String> doReverseDnsLookup(String ipAddressStr){


        HashMap<String,String> dnsParams = new HashMap<>();

        //We have an ipAddress let's try a reverse lookup for it so to find the DNS name!!!
        if (ipAddressStr!=null){
            try{
                String dnsCannonicalHostName = DnsResolver.resolveDNSCanonicalName(ipAddressStr);
                dnsParams.put("dnsNameFullString", dnsCannonicalHostName);
                String dnsShort=subStringDeviceName(dnsCannonicalHostName);
                dnsParams.put("dnsName",dnsShort);
            } catch (UnknownHostException e) {
                logger.error(e.getStackTrace().toString());
                logger.info("DNS reverse query for " + ipAddressStr + " has failed!!!");
            }

           return dnsParams;
        }else {
            return null;
        }

    }
    protected  boolean isReachable(String ipAddressStr){
        boolean reachable = false;
        try {
            IcmpStatus icmpStatus = new IcmpStatus(ipAddressStr);
            reachable = icmpStatus.getIcmpStatus(ipAddressStr);

        }catch ( IOException e) {
            logger.info("Device with " + ipAddressStr + " is unreachable!!!");
            logger.error(e.getStackTrace());

        }
        return reachable;

    }
    protected  Map<String, String> snmpConnectionAssembler(Map<String, String> params, String ipAddressStr){

        Map<String, String> snmpConnParams = new HashMap<String, String>();

        ResourceType snmpResource = snmpResourceProvider(params);
        snmpConnParams = this.discoveryResource.getParamMap(snmpResource, "snmp");
        snmpConnParams.put("ipAddress", ipAddressStr);
        return snmpConnParams;
    }

    protected ResourceType snmpResourceProvider(Map<String,String> params){
       return this.discoveryResource.returnResourceByParam(params);
    }

    protected Map<String,String> getConnectionDetailsParams(String deviceName, String deviceType, String ipAddressStr){

        Map<String, String> params1 = new HashMap<String, String>();

        if (deviceName!=null && !deviceName.isEmpty()){
            params1.put("deviceName", deviceName);

        }
        if (deviceType!=null && !deviceType.isEmpty()){
            params1.put("deviceType", deviceType);

        }

        if (ipAddressStr!=null && !ipAddressStr.isEmpty()){
            params1.put("ipAddress", ipAddressStr);

        } else {
            if (deviceName!=null && !deviceName.isEmpty()) {
                ipAddressStr = resolveNamefromIP(deviceName);

                if (ipAddressStr!=null && !ipAddressStr.isEmpty())
                    params1.put("ipAddress", ipAddressStr);
                else {
                    logger.info("DNS has returned an empty or null ipAddress for "+deviceName);
                }

            }
            else {
                logger.info("We have a device with an empty ipAddress and empty deviceName,Can't discover!!!");
                return null;
            }
        }
        return params1;
    }
    private String resolveNamefromIP(String deviceName){

            try {
                return DnsResolver.resolveIpByName(deviceName);
            } catch (UnknownHostException e) {
                logger.info("DNS query for " + deviceName + " has failed!!!");
                logger.error(e.getStackTrace().toString());
                return null;
            }

    }

    protected SnmpManager getSnmpManager(Map<String,String> resourceSelectionParams,String ipAddressStr){


        String sysDescr;
        SnmpManager snmpManager;

        ResourceType snmpResource = snmpResourceProvider(resourceSelectionParams);
        logger.info("Discovering "+ipAddressStr+" with "+snmpResource.getName());
        Map<String,String> initialSnmpConnParams = this.discoveryResource.getParamMap(snmpResource, "snmp");
        initialSnmpConnParams.put("ipAddress", ipAddressStr);
        ArrayList<ResourceType> snmpResources = this.discoveryResource.returnResourcesByConnectionType("snmp");



            SnmpManagerCreator snmpManagerCreator = new SnmpManagerCreator(mibLoaderHolder);

            snmpManager = snmpManagerCreator.create(initialSnmpConnParams);

        try {
            snmpManager.init();
            sysDescr = snmpManager.snmpGet("1.3.6.1.2.1.1.1.0");


            if (sysDescr == null) {
                snmpManager.closeSnmp();
                logger.info("Can't connect to: " + initialSnmpConnParams.get("ipAddress") + " with " + initialSnmpConnParams);

                for (ResourceType resourceType : snmpResources) {
                    Map<String,String> secondartySnmpConnParams = this.discoveryResource.getParamMap(resourceType, "snmp");
                    logger.info("Discovering "+ipAddressStr+" with "+resourceType.getName());

                    secondartySnmpConnParams.put("ipAddress", ipAddressStr);
                    if (!resourceType.getName().equals(snmpResource.getName())) {
                        snmpManager = snmpManagerCreator.create(initialSnmpConnParams);

                        snmpManager.init();
                        sysDescr = snmpManager.snmpGet("1.3.6.1.2.1.1.1.0");
                        if (sysDescr == null) {
                            logger.info("Can't connect to: " + ipAddressStr + " with " + initialSnmpConnParams);
                            snmpManager.closeSnmp();
                        } else {
                            break;
                        }
                    }
                }
            }
        } catch (IOException e) {
            logger.error("Something went wrong in SNMP communication with "+ipAddressStr+":Check the stacktrace \n"+e.getStackTrace());
            return null;
        }
        return snmpManager;
    }
    protected RawDeviceData getRawData (SnmpManager snmpManager,DiscoveryHelper discoveryHelper, String deviceType){
        String[] requestParamsList = discoveryHelper.getRequestParams(discoveryTypes);

        Node rawDatNode = null;
        RawDeviceData rawData = null;

        try {
            rawDatNode = snmpManager.snmpWalk(requestParamsList);
            snmpManager.closeSnmp();

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (rawDatNode != null) {
            SnmpXmlPrinter snmpXmlPrinter = new SnmpXmlPrinter(mibLoaderHolder.getLoader(), rawDatNode);
            rawData = new RawDeviceData(snmpXmlPrinter.printTreeAsXML().getBytes());

            logger.trace(new String(rawData.getData()));

        } else {

            return null;
        }
        return rawData;

    }

    protected DiscoveredDeviceData getDeviceData(DiscoveryHelper discoveryHelper,RawDeviceData rawData ){
        SnmpForXslt.setMibLoaderHolder(mibLoaderHolder);

        Map<String,String> transformationParameters = new HashMap<String, String>();

        transformationParameters.put("neighbourIPDryRun", "true");
        DiscoveredDeviceData discoveredDeviceData = discoveryHelper.parseDeviceRawData(rawData, discoveryTypes, transformationParameters);
        return discoveredDeviceData;
    }

    protected XmlDiscoveryHelperV2 createXmlDiscoveryHelper(String deviceType){
        return discoveryHelperFactory.createDiscoveryHelperv2(deviceType);

    }

}
