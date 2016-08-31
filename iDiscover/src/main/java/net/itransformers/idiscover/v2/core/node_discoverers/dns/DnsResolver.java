package net.itransformers.idiscover.v2.core.node_discoverers.dns;

import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by niau on 8/22/16.
 */
public class DnsResolver {

    static Logger logger = Logger.getLogger(DnsResolver.class);



    public String resolveIpByName(String name) throws UnknownHostException {

        String inetAddress = InetAddress.getByName(name).getHostAddress();
        logger.debug("Name: " + name + " resolved to " + inetAddress);
        return inetAddress;

    }
    public  String resolveDNSCanonicalName(String ipAddress) throws UnknownHostException {

        String dnsCannonicalHostName = InetAddress.getByName(ipAddress).getCanonicalHostName();
        logger.debug("ipAddress: "+ipAddress+" resolved to "+dnsCannonicalHostName);

        return dnsCannonicalHostName;

    }

}
