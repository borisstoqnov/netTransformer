package net.itransformers.idiscover.v2.core.node_discoverers;

import net.itransformers.idiscover.networkmodelv2.DiscoveredDevice;
import net.itransformers.idiscover.v2.core.NodeDiscoverer;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.idiscover.v2.core.node_discoverers.dns.DnsNodeDiscoverer;
import net.itransformers.idiscover.v2.core.node_discoverers.dns.DnsResolver;
import net.itransformers.idiscover.v2.core.node_discoverers.icmp.IcmpDiscoverer;
import net.itransformers.idiscover.v2.core.node_discoverers.snmpdiscoverer.SnmpParallelNodeDiscoverer;
import org.apache.log4j.Logger;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by niau on 8/30/16.
 */
public class PlumberNodeDiscoverer implements NodeDiscoverer {
    private IcmpDiscoverer icmpDiscoverer;
    private SnmpParallelNodeDiscoverer snmpParallelNodeDiscoverer;
    private DnsNodeDiscoverer dnsNodeDiscoverer;
    static Logger logger = Logger.getLogger(PlumberNodeDiscoverer.class);



    public PlumberNodeDiscoverer(IcmpDiscoverer icmpDiscoverer, DnsNodeDiscoverer dnsNodeDiscoverer, SnmpParallelNodeDiscoverer snmpParallelNodeDiscoverer)  {

        this.icmpDiscoverer = icmpDiscoverer;
        this.dnsNodeDiscoverer = dnsNodeDiscoverer;
        this.snmpParallelNodeDiscoverer = snmpParallelNodeDiscoverer;
    }


   private NodeDiscoveryResult doIcmpDiscovery(ConnectionDetails connectionDetails){
        return icmpDiscoverer.discover(connectionDetails);

    }

   private NodeDiscoveryResult doDnsDiscovery(ConnectionDetails connectionDetails){
       return dnsNodeDiscoverer.discover(connectionDetails);
   }
    @Override
    public NodeDiscoveryResult discover(ConnectionDetails connectionDetails) {
        Set<String> nodeAliases = new HashSet<>();

        String ipAddressString = connectionDetails.getParam("ipAddress");
        String connectionDetailsNodeId = connectionDetails.getParam("deviceName");


        if (ipAddressString==null || ipAddressString.isEmpty()) {
                logger.info("Connection details with empty IP address: "+connectionDetails.toString());
                DnsResolver dnsResolver = new DnsResolver();
                try {
                    String ipAddress = dnsResolver.resolveIpByName(connectionDetailsNodeId);
                    connectionDetails.put("ipAddress", ipAddress);

                } catch (UnknownHostException e) {
                    logger.error(e.getMessage(), e);
                    return null;
                }
        }


        if (connectionDetailsNodeId!=null && !connectionDetailsNodeId.isEmpty())
            nodeAliases.add(connectionDetailsNodeId);


        NodeDiscoveryResult nodeDiscoveryResult = new NodeDiscoveryResult();
        Map<String, Object> discoveryData  = new HashMap<>();
        Set<ConnectionDetails> neighbourConnectionDetails  = new HashSet<>();

        NodeDiscoveryResult icmpDiscoveryResult =  doIcmpDiscovery(connectionDetails);
        String icmpNodeId = null;
        if (icmpDiscoveryResult!=null) {
            icmpNodeId = icmpDiscoveryResult.getNodeId();
        }else{
            return null;
        }
        if (icmpNodeId!=null && !icmpNodeId.isEmpty())
            nodeAliases.add(icmpNodeId);
        Map<String, Object> icmpDiscoveryData = icmpDiscoveryResult.getDiscoveredData();

        for (String s : icmpDiscoveryData.keySet()) {
             discoveryData.put(s,icmpDiscoveryData.get(s));
        }

        String dnsNodeId = null;
        NodeDiscoveryResult dnsDiscoveryResult = doDnsDiscovery(connectionDetails);

        if (dnsDiscoveryResult!=null) {

            dnsNodeId = dnsDiscoveryResult.getNodeId();
            if (dnsNodeId!=null && !dnsNodeId.isEmpty())
                nodeAliases.add(dnsNodeId);

            Map<String, Object> dnsDiscoveryData = dnsDiscoveryResult.getDiscoveredData();

            for (String s : dnsDiscoveryData.keySet()) {
                discoveryData.put(s, dnsDiscoveryData.get(s));
            }
        }
        String snmpNodeId = null;

                NodeDiscoveryResult snmpDiscoveryResult = snmpParallelNodeDiscoverer.discover(connectionDetails);

                if (snmpDiscoveryResult!=null) {

                    snmpNodeId = snmpDiscoveryResult.getNodeId();
                    if (snmpNodeId!=null && !snmpNodeId.isEmpty())
                        nodeAliases.add(snmpNodeId);

                    Map<String, Object> snmpDiscoveryData = snmpDiscoveryResult.getDiscoveredData();
                    DiscoveredDevice discoveredDevice = (DiscoveredDevice) snmpDiscoveryData.get("DiscoveredDevice");

                    discoveryData.put("DiscoveredDevice", discoveredDevice);

                    nodeAliases.addAll(discoveredDevice.getDeviceAliases());

                    Set<ConnectionDetails> snmpNeighbourConnectionDetails = snmpDiscoveryResult.getNeighboursConnectionDetails();

                    neighbourConnectionDetails.addAll(snmpNeighbourConnectionDetails);

                    for (String s : snmpDiscoveryData.keySet()) {
                        discoveryData.put(s, snmpDiscoveryData.get(s));
                    }


                }else{
                    logger.info("Can't discover "+connectionDetails+"through icmp");
                }


        String nodeId = determineNodeId(connectionDetailsNodeId,icmpNodeId,dnsNodeId,snmpNodeId);
        nodeDiscoveryResult.setNodeId(nodeId);
        nodeDiscoveryResult.setNeighboursConnectionDetails(neighbourConnectionDetails);
        nodeDiscoveryResult.setDiscoveredData(discoveryData);
        nodeDiscoveryResult.setNodeAliases(nodeAliases);


        return nodeDiscoveryResult;

    }

    private String determineNodeId(String connectionDetailsNodeId, String icmpNodeId,String dnsNodeId,String snmpNodeId ){
        String nodeId = null;

        if (snmpNodeId != null && !snmpNodeId.isEmpty())
            return snmpNodeId;
        if (dnsNodeId!=null && !dnsNodeId.isEmpty())
            return dnsNodeId;
        if (connectionDetailsNodeId != null && !connectionDetailsNodeId.isEmpty())
            return connectionDetailsNodeId;

        return icmpNodeId;

    }


}
