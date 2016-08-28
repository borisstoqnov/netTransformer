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
        String deviceId = selectDeviceId(connectionDetails.getParams());

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
        boolean icmpReachabilityFlag = isReachable(ipAddressStr, timeoutInt, retriesInt);

        if (icmpReachabilityFlag){
            NodeDiscoveryResult result = new NodeDiscoveryResult();
            result.setNodeId(deviceId);

            String dnsFQDN = doReverseDnsLookup(ipAddressStr);
            String dnsPQDN = null;
            if (dnsFQDN!=null && !dnsFQDN.equals(ipAddressStr)) {
                dnsPQDN =  subStringDeviceName(dnsFQDN);
                resultParams.put("dnsFQDN", dnsFQDN);
                resultParams.put("dnsPQDN", dnsPQDN);
            }
            IPNetConnectionDetails ipNetConnectionDetails = new IPNetConnectionDetails("snmp");
            ipNetConnectionDetails.put("ipAddress",ipAddressStr);
            ipNetConnectionDetails.put("dnsPQDN", dnsPQDN);
            ipNetConnectionDetails.put("dnsFQDN", dnsFQDN);
            ipNetConnectionDetails.put("icmpStatus", "REACHABLE");
            HashSet<ConnectionDetails> connectionDetailes = new HashSet<>();
            result.setNeighboursConnectionDetails(connectionDetailes);
            connectionDetailes.add(ipNetConnectionDetails);
           return result;
        }


        return null ;
    }
    private   boolean isReachable(String ipAddressStr,int timeout,int retries){
        boolean reachable = false;
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
        return reachable;

    }

}
