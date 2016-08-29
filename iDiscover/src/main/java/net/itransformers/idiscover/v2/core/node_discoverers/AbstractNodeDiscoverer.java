package net.itransformers.idiscover.v2.core.node_discoverers;

import net.itransformers.idiscover.core.DiscoveryResourceManager;
import net.itransformers.idiscover.v2.core.node_discoverers.dnsresolver.DnsResolver;
import net.itransformers.resourcemanager.config.ResourceType;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by niau on 8/27/16.
 */
public abstract class  AbstractNodeDiscoverer {
    static Logger logger = Logger.getLogger(AbstractNodeDiscoverer.class);
    protected DiscoveryResourceManager discoveryResource;


    public AbstractNodeDiscoverer( DiscoveryResourceManager discoveryResource) throws Exception {
        this.discoveryResource = discoveryResource;
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
    protected String subStringDeviceName(String sysName) {
        if (sysName == null) return null;
        String hostName =  StringUtils.substringBefore(sysName, ".");
        return hostName;

    }
    protected String doReverseDnsLookup(String ipAddressStr){


        String dnsCannonicalHostName = null;
        if (ipAddressStr!=null){
            try{
                dnsCannonicalHostName = DnsResolver.resolveDNSCanonicalName(ipAddressStr);
            } catch (UnknownHostException e) {
                logger.error(e.getStackTrace().toString());
                logger.info("DNS reverse query for " + ipAddressStr + " has failed!!!");
            }

            return dnsCannonicalHostName;
        }else {
            return null;
        }

    }


    protected String selectDeviceId(Map<String, String> params) {
        String deviceName = params.get("deviceName");
        String dnsPQDN = params.get("dnsPQDN");
        String ipAddress = params.get("ipAddress");
        String hostName = params.get("hostName");
        String id;
        if (deviceName != null && !deviceName.isEmpty()) {
            id = deviceName;
        } else if (dnsPQDN != null && !dnsPQDN.isEmpty()) {
            id = dnsPQDN;
        } else if (hostName != null && !hostName.isEmpty()){
            id=hostName;
        }else {
            id =ipAddress;
        }
        return id;
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

    protected ResourceType resourceProvider(Map<String, String> params){
        return this.discoveryResource.returnResourceByParam(params);
    }
}
