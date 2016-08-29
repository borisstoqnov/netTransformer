package net.itransformers.idiscover.v2.core.node_discoverers.icmp;

import net.itransformers.idiscover.core.DiscoveryResourceManager;
import net.itransformers.idiscover.v2.core.NodeDiscoverer;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.idiscover.v2.core.connection_details.IPNetConnectionDetails;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.idiscover.v2.core.node_discoverers.AbstractNodeDiscoverer;
import net.itransformers.resourcemanager.config.ResourceType;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by niau on 8/27/16.
 */
public class IcmpDiscoverer extends AbstractNodeDiscoverer implements NodeDiscoverer {
    static Logger logger = Logger.getLogger(IcmpDiscoverer.class);

    public IcmpDiscoverer(DiscoveryResourceManager discoveryResource) throws Exception {
        super(discoveryResource);
    }

    @Override
    public NodeDiscoveryResult discover(ConnectionDetails connectionDetails) {

        String deviceName = connectionDetails.getParam("deviceName");
        String ipAddressStr = connectionDetails.getParam("ipAddress");
        String deviceType = connectionDetails.getParam("deviceType");

        Map<String, String> resourceSelectionParams = getConnectionDetailsParams(deviceName, deviceType, ipAddressStr);

        ResourceType icmpResource = resourceProvider(resourceSelectionParams);
        icmpResource.getConnectionParams();
        Map<String, String> icmpConnectionParams = this.discoveryResource.getParamMap(icmpResource, "icmp");
        int timeoutInt = 1000;
        int retriesInt = 1;

        if (icmpConnectionParams != null) {
            String timeout = icmpConnectionParams.get("timeout");
            if (timeout != null) {
                if (!timeout.isEmpty()) {
                    timeoutInt = new Integer(timeout);
                }
            }
            String retries = icmpConnectionParams.get("retries");
            if (retries != null) {
                if (!retries.isEmpty()) {
                    retriesInt = new Integer(retries);
                }
            }
        }
        Map<String, String> resultParams = new HashMap<>();
        resultParams.put("deviceName",deviceName);
        resultParams.put("ipAddress",ipAddressStr);
        boolean icmpReachabilityFlag = isReachable(ipAddressStr, timeoutInt, retriesInt);
        IPNetConnectionDetails ipNetConnectionDetails = new IPNetConnectionDetails("snmp");


        String dnsFQDN = doReverseDnsLookup(ipAddressStr);
        String dnsPQDN = null;
        if (dnsFQDN!=null && !dnsFQDN.equals(ipAddressStr)) {
            dnsPQDN =  subStringDeviceName(dnsFQDN);
            ipNetConnectionDetails.put("dnsPQDN",dnsPQDN);
            ipNetConnectionDetails.put("dnsFQDN",dnsFQDN);
        }
        String deviceId = selectDeviceId(resultParams);
        NodeDiscoveryResult result = new NodeDiscoveryResult();
        result.setNodeId(deviceId);

        if (ipAddressStr!=null )
            if (!ipAddressStr.isEmpty())
                ipNetConnectionDetails.put("ipAddress",ipAddressStr);

        if (deviceName!=null )
            if (!deviceName.isEmpty())
                ipNetConnectionDetails.put("deviceName",deviceName);

        if (icmpReachabilityFlag){
            logger.info("Device with "+ipAddressStr+" and "+resultParams +" is reachable through icmp!!!");

            ipNetConnectionDetails.put("icmpStatus", "REACHABLE");

            HashSet<ConnectionDetails> connectionDetailes = new HashSet<>();
            connectionDetailes.add(ipNetConnectionDetails);
            result.setNeighboursConnectionDetails(connectionDetailes);
        } else {
            logger.info("Device with "+ipAddressStr+" and "+resultParams +" is unreachable through icmp!!!");

        }


        return result ;
    }
    private   boolean isReachable(String ipAddressStr,int timeout,int retries){
        boolean reachable;
        for (int i =0; i<retries;i++) {
            try {
                IcmpStatus icmpStatus = new IcmpStatus(ipAddressStr);
                reachable = icmpStatus.getIcmpStatus(ipAddressStr);
                if (reachable)
                    return true;

            } catch (IOException e) {
                logger.info("Pinging device with " + ipAddressStr + "timeout:" + timeout + "retries:" + retries + " produced!!!");
                logger.error(e.getStackTrace());
                return false;
            }
        }
        return false;

    }

}
