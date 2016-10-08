package net.itransformers.idiscover.v2.core.node_discoverers.subnetDiscoverer;

import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetails;
import net.itransformers.connectiondetails.connectiondetailsapi.IPNetConnectionDetails;
import net.itransformers.idiscover.v2.core.IPv4BogonIdentitifier;
import net.itransformers.idiscover.api.NodeDiscoverer;
import net.itransformers.idiscover.api.NodeDiscoveryResult;
import net.itransformers.utils.CIDRUtils;
import org.apache.commons.net.util.SubnetUtils;
import org.apache.log4j.Logger;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Vasil Yordanov on 22-Jun-16.
 */
public class SubnetDiscoverer implements NodeDiscoverer {
    static Logger logger = Logger.getLogger(SubnetDiscoverer.class);

    int subnetMaxMaskSize;
    boolean generateIPconnectionsForSubnetMembers;

    public SubnetDiscoverer(boolean generateIPconnectionsForSubnetMembers) {
        this.generateIPconnectionsForSubnetMembers = generateIPconnectionsForSubnetMembers;
    }
    public SubnetDiscoverer(boolean generateIPconnectionsForSubnetMembers, int subnetMaxMaskSize) {
        this.generateIPconnectionsForSubnetMembers = generateIPconnectionsForSubnetMembers;
        this.subnetMaxMaskSize = subnetMaxMaskSize;
    }

    @Override
    public NodeDiscoveryResult discover(ConnectionDetails connectionDetails) {


        String protocolType = connectionDetails.getParam("protocolType");
        String subnetIpAddress = connectionDetails.getParam("ipAddress");
        String subnetPrefixMask = connectionDetails.getParam("subnetPrefixMask");

        String subnetPrefix=subnetIpAddress+"/"+subnetPrefixMask;

        NodeDiscoveryResult nodeDiscoveryResult = new NodeDiscoveryResult(subnetPrefix,null,null);
        nodeDiscoveryResult.setDiscoveredData("subnetDetails", connectionDetails.getParams());
        int discoverySubnetPrefixSize =  Integer.parseInt(subnetPrefixMask);

        if (generateIPconnectionsForSubnetMembers && discoverySubnetPrefixSize<=subnetMaxMaskSize);
            nodeDiscoveryResult.setNeighboursConnectionDetails(getSubnetIpNeighborConnections(subnetPrefix));

        try {
            InetAddress inetAddress = InetAddress.getByName(subnetIpAddress);
            if (inetAddress instanceof Inet4Address) {

                IPv4BogonIdentitifier iPv4BogonIdentitifier = new IPv4BogonIdentitifier(subnetIpAddress);

                if (iPv4BogonIdentitifier.identifyBogon()) {
                    nodeDiscoveryResult.setDiscoveredData("bogon", "YES");

                }
            }
            if (privateSubnetIdentifier(subnetIpAddress)) {
                nodeDiscoveryResult.setDiscoveredData("private", "YES");
            }
        }

         catch (UnknownHostException e) {
            logger.error(e);
        }


        return nodeDiscoveryResult;
    }

    private Set<ConnectionDetails> getSubnetIpNeighborConnections(String subnetPrefix){
        Set<ConnectionDetails> ipConnectionDetailsSet = new HashSet<>();
        SubnetUtils utils = new SubnetUtils(subnetPrefix);
        String[] allIps = utils.getInfo().getAllAddresses();
        for (String ip : allIps) {
            IPNetConnectionDetails ipConnectionDetails = new IPNetConnectionDetails("icmp");
            ipConnectionDetails.put("ipAddress",ip);
            ipConnectionDetailsSet.add(ipConnectionDetails);
        }
        return ipConnectionDetailsSet;
    }

    public int getSubnetMaxMaskSize() {
        return subnetMaxMaskSize;
    }

    public void setSubnetMaxMaskSize(int subnetMaxMaskSize) {
        this.subnetMaxMaskSize = subnetMaxMaskSize;
    }

    private boolean privateSubnetIdentifier(String ipv4Address) {

        CIDRUtils cidrUtils = null;
        try {


            cidrUtils = new CIDRUtils("192.168.0.0/16");
            if (cidrUtils.isInRange(ipv4Address)) {
                return true;
            }
            cidrUtils = new CIDRUtils("172.16.0.0/12");
            if (cidrUtils.isInRange(ipv4Address)) {
                return true;
            }
            cidrUtils = new CIDRUtils("10.0.0.0/8");
            if (cidrUtils.isInRange(ipv4Address)) {
                return true;
            }
        }catch (UnknownHostException ex){
             logger.error(ex.getMessage());
        }
        return false;

    }
}
