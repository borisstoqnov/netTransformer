package net.itransformers.idiscover.v2.core.node_discoverers;

import net.itransformers.idiscover.networkmodelv2.DiscoveredDevice;
import net.itransformers.idiscover.v2.core.NodeDiscoverer;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.idiscover.v2.core.node_discoverers.dns.DnsNodeDiscoverer;
import net.itransformers.idiscover.v2.core.node_discoverers.icmp.IcmpDiscoverer;
import net.itransformers.idiscover.v2.core.node_discoverers.snmpdiscoverer.SnmpParallelNodeDiscoverer;
import org.apache.log4j.Logger;

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


    @Override
    public NodeDiscoveryResult discover(ConnectionDetails connectionDetails) {

        String connectionDetailsNodeId = connectionDetails.getParam("deviceName");

        NodeDiscoveryResult nodeDiscoveryResult = new NodeDiscoveryResult();
        Map<String, Object> discoveryData  = new HashMap<String, Object>();
        Set<ConnectionDetails> neighbourConnectionDetails  = new HashSet<ConnectionDetails>();

        NodeDiscoveryResult icmpDiscoveryResult = icmpDiscoverer.discover(connectionDetails);

        if (icmpDiscoveryResult == null ){
            return null;
        }

        String icmpNodeId=icmpDiscoveryResult.getNodeId();


        Map<String, Object> icmpDiscoveryData = icmpDiscoveryResult.getDiscoveredData();
        String icmpStatus = (String) icmpDiscoveryData.get("icmpStatus");

        for (String s : icmpDiscoveryData.keySet()) {
             discoveryData.put(s,icmpDiscoveryData.get(s));
        }

        String dnsNodeId = null;
        NodeDiscoveryResult dnsDiscoveryResult = dnsNodeDiscoverer.discover(connectionDetails);
        if (dnsDiscoveryResult!=null) {

            if (dnsDiscoveryResult != null) {
                dnsNodeId = dnsDiscoveryResult.getNodeId();


                Map<String, Object> dnsDiscoveryData = dnsDiscoveryResult.getDiscoveredData();

                for (String s : dnsDiscoveryData.keySet()) {
                    discoveryData.put(s, dnsDiscoveryData.get(s));
                }
            }
        }
        String snmpNodeId = null;

        if (icmpStatus!=null) {
            if (icmpStatus.equals("REACHABLE")) {
                NodeDiscoveryResult snmpDiscoveryResult = snmpParallelNodeDiscoverer.discover(connectionDetails);

                if (snmpDiscoveryResult!=null) {

                    snmpNodeId = snmpDiscoveryResult.getNodeId();

                    Map<String, Object> snmpDiscoveryData = snmpDiscoveryResult.getDiscoveredData();
                    DiscoveredDevice discoveredDevice = (DiscoveredDevice) snmpDiscoveryData.get("DiscoveredDevice");

                    discoveryData.put("DiscoveredDevice",discoveredDevice);
                    Set<ConnectionDetails> snmpNeighbourConnectionDetails = snmpDiscoveryResult.getNeighboursConnectionDetails();

                    neighbourConnectionDetails.addAll(snmpNeighbourConnectionDetails);

                    for (String s : snmpDiscoveryData.keySet()) {
                        discoveryData.put(s, snmpDiscoveryData.get(s));
                    }
                }else{
                    logger.info("Can't discover "+connectionDetails+"through icmp");
                }


            }
        }
        String nodeId = determineNodeId(connectionDetailsNodeId,icmpNodeId,dnsNodeId,snmpNodeId);
        nodeDiscoveryResult.setNodeId(nodeId);
        nodeDiscoveryResult.setNeighboursConnectionDetails(neighbourConnectionDetails);
        nodeDiscoveryResult.setDiscoveredData(discoveryData);

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
