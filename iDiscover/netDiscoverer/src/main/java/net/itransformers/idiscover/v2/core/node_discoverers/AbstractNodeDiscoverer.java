package net.itransformers.idiscover.v2.core.node_discoverers;

import net.itransformers.idiscover.core.DiscoveryResourceManager;
import net.itransformers.idiscover.v2.core.node_discoverers.dns.DnsResolver;
import net.itransformers.resourcemanager.config.ResourceType;
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


    public AbstractNodeDiscoverer( DiscoveryResourceManager discoveryResource)  {
        this.discoveryResource = discoveryResource;
    }


    protected ResourceType resourceProvider(Map<String, String> params){


        return this.discoveryResource.returnResourceByParam(params);
    }
    protected Map<String,String> returnResourceSelectionParams(String deviceName, String deviceType, String ipAddressStr){

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
                DnsResolver dnsResolver = new DnsResolver();
                try {
                    ipAddressStr = dnsResolver.resolveIpByName(deviceName);
                } catch (UnknownHostException e) {
                    logger.error(e.getMessage());
                }

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
}
