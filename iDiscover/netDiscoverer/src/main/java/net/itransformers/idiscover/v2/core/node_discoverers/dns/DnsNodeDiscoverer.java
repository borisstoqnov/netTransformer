package net.itransformers.idiscover.v2.core.node_discoverers.dns;

import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetails;
import net.itransformers.idiscover.api.NodeDiscoverer;
import net.itransformers.idiscover.api.NodeDiscoveryResult;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.net.UnknownHostException;

/**
 * Created by niau on 8/27/16.
 */
public class DnsNodeDiscoverer implements NodeDiscoverer {
    static Logger logger = Logger.getLogger(DnsNodeDiscoverer.class);

    @Override
    public NodeDiscoveryResult discover(ConnectionDetails connectionDetails) {
        String ipAddressString = connectionDetails.getParams().get("ipAddress");

        String dnsCanonicalDomainName = doReverseDnsLookup(ipAddressString);

        String dnsShortName = StringUtils.substringBefore(dnsCanonicalDomainName, ".");
        NodeDiscoveryResult result = new NodeDiscoveryResult();

        if (dnsCanonicalDomainName != null && !dnsCanonicalDomainName.isEmpty() && !dnsCanonicalDomainName.equals(ipAddressString)) {
            result.setDiscoveredData("FQDN", dnsCanonicalDomainName);
            result.setDiscoveredData("PQDN", dnsShortName);
            result.setNodeId(dnsShortName);

            return result;
        } else {
            return null;
        }
    }


    private String doReverseDnsLookup(String ipAddressStr) {

        DnsResolver dnsResolver = new DnsResolver();

        String dnsCannonicalHostName = null;
        if (ipAddressStr != null) {
            try {
                dnsCannonicalHostName = dnsResolver.resolveDNSCanonicalName(ipAddressStr);
            } catch (UnknownHostException e) {
                logger.error(e.getStackTrace().toString());
                logger.info("DNS reverse query for " + ipAddressStr + " has failed!!!");
                return null;
            }
            return dnsCannonicalHostName;
        } else {
            return null;
        }

    }


    private String resolveNamefromIP(String deviceName) {
        DnsResolver dnsResolver = new DnsResolver();
        try {
            return dnsResolver.resolveIpByName(deviceName);
        } catch (UnknownHostException e) {
            logger.info("DNS query for " + deviceName + " has failed!!!");
            logger.error(e.getStackTrace().toString());
            return null;
        }

    }

}
