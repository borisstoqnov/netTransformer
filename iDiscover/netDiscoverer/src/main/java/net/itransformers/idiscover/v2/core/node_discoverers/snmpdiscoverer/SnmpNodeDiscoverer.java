package net.itransformers.idiscover.v2.core.node_discoverers.snmpdiscoverer;

import net.itransformers.idiscover.api.models.node_data.DiscoveredDeviceData;
import net.itransformers.idiscover.core.DiscoveryHelper;
import net.itransformers.idiscover.core.DiscoveryResourceManager;
import net.itransformers.idiscover.api.models.node_data.RawDeviceData;
import net.itransformers.idiscover.discoveryhelpers.xml.SnmpForXslt;
import net.itransformers.idiscover.discoveryhelpers.xml.XmlDiscoveryHelperFactory;
import net.itransformers.idiscover.discoveryhelpers.xml.XmlDiscoveryHelperV2;
import net.itransformers.idiscover.v2.core.node_discoverers.AbstractNodeDiscoverer;
import net.itransformers.idiscover.v2.core.node_discoverers.snmpdiscoverer.snmpmanager.SnmpManagerCreator;
import net.itransformers.resourcemanager.config.ResourceType;
import net.itransformers.snmp2xml4j.snmptoolkit.MibLoaderHolder;
import net.itransformers.snmp2xml4j.snmptoolkit.Node;
import net.itransformers.snmp2xml4j.snmptoolkit.SnmpManager;
import net.itransformers.snmp2xml4j.snmptoolkit.SnmpXmlPrinter;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by niau on 8/22/16.
 */
public abstract class SnmpNodeDiscoverer extends AbstractNodeDiscoverer {
    static Logger logger = Logger.getLogger(SnmpNodeDiscoverer.class);

    protected XmlDiscoveryHelperFactory discoveryHelperFactory;
    protected String[] discoveryTypes;
    boolean useOnlyTheFirstSnmpBeingMatched;
    protected MibLoaderHolder mibLoaderHolder;


    public SnmpNodeDiscoverer(XmlDiscoveryHelperFactory discoveryHelperFactory, String[] discoveryTypes, DiscoveryResourceManager discoveryResource, MibLoaderHolder mibLoaderHolder, boolean useOnlyTheFirstSnmpBeingMatched)  {
        super(discoveryResource);
        this.discoveryHelperFactory = discoveryHelperFactory;
        this.discoveryTypes = discoveryTypes;
        this.mibLoaderHolder = mibLoaderHolder;
        this.useOnlyTheFirstSnmpBeingMatched = useOnlyTheFirstSnmpBeingMatched;
    }

    protected String getSymbolByOid(String mibName, String oid) {
        return mibLoaderHolder.getSymbolByOid(mibName, oid);
    }




    protected String snmpGet(SnmpManager snmpManager, String oidString) {
        try {
            return snmpManager.snmpGet(oidString);
        } catch (IOException e) {
            logger.info(e.getMessage());
            return null;
        }
    }




    protected  Map<String, String> snmpConnectionAssembler(Map<String, String> params, String ipAddressStr){

        Map<String, String> snmpConnParams = new HashMap<String, String>();

        ResourceType snmpResource = resourceProvider(params);
        snmpConnParams = this.discoveryResource.getParamMap(snmpResource, "snmp");
        snmpConnParams.put("ipAddress", ipAddressStr);
        return snmpConnParams;
    }






    protected SnmpManager getSnmpManager(Map<String,String> resourceSelectionParams,String ipAddressStr){


        String sysDescr;
        SnmpManager snmpManager;

        ResourceType snmpResource = resourceProvider(resourceSelectionParams);
        logger.info("Discovering "+ipAddressStr+" with "+snmpResource.getName());
        Map<String,String> initialSnmpConnParams = this.discoveryResource.getParamMap(snmpResource, "snmp");
        initialSnmpConnParams.put("ipAddress", ipAddressStr);
        List<ResourceType> snmpResources = this.discoveryResource.returnResourcesByConnectionType("snmp");



        SnmpManagerCreator snmpManagerCreator = new SnmpManagerCreator(mibLoaderHolder);
        snmpManager = snmpManagerCreator.create(initialSnmpConnParams);

        try {
            snmpManager.init();
            sysDescr = snmpManager.snmpGet("1.3.6.1.2.1.1.1.0");

            if (sysDescr == null ) {

                if (useOnlyTheFirstSnmpBeingMatched) {


                    snmpManager.closeSnmp();
                    logger.info("Can't connect to: " + initialSnmpConnParams.get("ipAddress") + " with " + initialSnmpConnParams);
                    return null;
                } else {

                    for (ResourceType resourceType : snmpResources) {
                        Map<String, String> secondartySnmpConnParams = this.discoveryResource.getParamMap(resourceType, "snmp");
                        logger.info("Discovering " + ipAddressStr + " with " + resourceType.getName());

                        secondartySnmpConnParams.put("ipAddress", ipAddressStr);
                        if (!resourceType.getName().equals(snmpResource.getName())) {
                            snmpManager = snmpManagerCreator.create(initialSnmpConnParams);

                            snmpManager.init();
                            sysDescr = snmpManager.snmpGet("1.3.6.1.2.1.1.1.0");
                            if (sysDescr == null) {
                                logger.info("Can't connect to: " + ipAddressStr + " with " + initialSnmpConnParams);
                                snmpManager.closeSnmp();
                            } else {
                                return snmpManager;

                            }
                        }
                    }

                }
            }else {
                return snmpManager;
            }




        } catch (IOException e) {
            logger.error("Something went wrong in SNMP communication with "+ipAddressStr+":Check the stacktrace \n"+e.getStackTrace());
            return null;
        }

        return null;
    }
    protected RawDeviceData getRawData (SnmpManager snmpManager,DiscoveryHelper discoveryHelper){
        String[] requestParamsList = discoveryHelper.getRequestParams(discoveryTypes);

        Node rawDatNode = null;
        RawDeviceData rawData = null;

        try {
            rawDatNode = snmpManager.snmpWalk(requestParamsList);
            snmpManager.closeSnmp();

        } catch (IOException e) {
            logger.error(e.getMessage(),e);
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
