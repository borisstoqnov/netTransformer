package net.itransformers.idiscover.v2.core.node_discoverers.icmp;

import net.itransformers.idiscover.core.DiscoveryResourceManager;
import net.itransformers.idiscover.v2.core.NodeDiscoverer;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.idiscover.v2.core.node_discoverers.AbstractNodeDiscoverer;
import net.itransformers.resourcemanager.config.ResourceType;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Map;

/**
 * Created by niau on 8/27/16.
 */
public class IcmpDiscoverer extends AbstractNodeDiscoverer implements NodeDiscoverer {
    static Logger logger = Logger.getLogger(IcmpDiscoverer.class);

    public IcmpDiscoverer(DiscoveryResourceManager discoveryResource)  {
        super(discoveryResource);
    }

    @Override
    public NodeDiscoveryResult discover(ConnectionDetails connectionDetails) {

        String deviceName = connectionDetails.getParam("deviceName");
        String ipAddressStr = connectionDetails.getParam("ipAddress");
        String deviceType = connectionDetails.getParam("deviceType");

        Map<String, String> resourceSelectionParams = returnResourceSelectionParams(deviceName, deviceType, ipAddressStr);

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
        boolean icmpReachabilityFlag = isReachable(ipAddressStr, timeoutInt, retriesInt);



        if (icmpReachabilityFlag){
            NodeDiscoveryResult result = new NodeDiscoveryResult();

            logger.info("Device with "+ipAddressStr+" and "+connectionDetails.getParams() +" is reachable through icmp!!!");
            result.setNodeId(ipAddressStr);
            result.setDiscoveredData("icmpStatus", "REACHABLE");
            return result ;

        } else {
            logger.info("Device with "+ipAddressStr+" and "+connectionDetails.getParams() +" is unreachable through icmp!!!");
            return null;
        }


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
