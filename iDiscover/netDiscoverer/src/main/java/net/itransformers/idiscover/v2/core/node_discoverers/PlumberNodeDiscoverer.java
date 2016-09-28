package net.itransformers.idiscover.v2.core.node_discoverers;

import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetails;
import net.itransformers.idiscover.networkmodelv2.DiscoveredDevice;
import net.itransformers.idiscover.v2.core.NodeDiscoverer;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
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

   private String doDns(String deviceName){
       DnsResolver dnsResolver = new DnsResolver();
       try {
           String ipAddress = dnsResolver.resolveIpByName(deviceName);
           if (ipAddress!=null && !ipAddress.isEmpty()) {
              return ipAddress;
           } else {
               return null;
           }

       } catch (UnknownHostException e) {
           logger.error(e.getMessage(), e);
           return null;
       }


   }
    @Override
    public NodeDiscoveryResult discover(ConnectionDetails connectionDetails) {
        Set<String> nodeAliases = new HashSet<>();
        NodeDiscoveryResult nodeDiscoveryResult = new NodeDiscoveryResult();

        Map<String, Object> discoveryData  = new HashMap<>();


        String ipAddressString = connectionDetails.getParam("ipAddress");

        if (ipAddressString!=null && !ipAddressString.isEmpty()) {
            nodeAliases.add(ipAddressString);
            discoveryData.put("discoveredIPv4Address", ipAddressString);
        }

        String neighborMacAddress = connectionDetails.getParam("neighborMacAddress");

        if (neighborMacAddress!=null && !neighborMacAddress.isEmpty()) {
            nodeAliases.add(neighborMacAddress);
        }


        String connDetailsDeviceName= connectionDetails.getParam("deviceName");
        if (connDetailsDeviceName!=null && !connDetailsDeviceName.isEmpty())
            nodeAliases.add(connDetailsDeviceName);


        String connectionDetailsNodeId = null;

        //If we don't have an ipAddress we have to try to get one.
        if ((ipAddressString==null || ipAddressString.isEmpty()) && (connDetailsDeviceName!=null&& !connDetailsDeviceName.isEmpty()) ) {
            logger.info("Connection details with empty IP address: "+connectionDetails.toString());
            connectionDetailsNodeId = connDetailsDeviceName;

            ipAddressString = doDns(connDetailsDeviceName);

            if (ipAddressString==null){
                nodeDiscoveryResult.setNodeId(connDetailsDeviceName);
                nodeDiscoveryResult.setNodeAliases(nodeAliases);
                return nodeDiscoveryResult;
            }else {
                connectionDetails.put("ipAddress", ipAddressString);
                nodeAliases.add(ipAddressString);
            }
        }   else {

            if (connDetailsDeviceName != null && !connDetailsDeviceName.isEmpty()){
                connectionDetailsNodeId=connDetailsDeviceName;
            } else if (ipAddressString!=null && !ipAddressString.isEmpty()){
                connectionDetailsNodeId = ipAddressString;
            }
        }




        Set<ConnectionDetails> neighbourConnectionDetails  = new HashSet<>();

        NodeDiscoveryResult icmpDiscoveryResult =  doIcmpDiscovery(connectionDetails);
        String icmpNodeId = null;
        if (icmpDiscoveryResult!=null) {
            icmpNodeId = icmpDiscoveryResult.getNodeId();
        }

        if (icmpDiscoveryResult != null) {
            Map<String, Object> icmpDiscoveryData = icmpDiscoveryResult.getDiscoveredData();

            for (Map.Entry<String, Object> entry: icmpDiscoveryData.entrySet()) {
                discoveryData.put(entry.getKey(), entry.getValue());
            }
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
            logger.info("Can't discover "+connectionDetails+"through snmp!!!");
        }

        if (icmpDiscoveryResult==null && snmpDiscoveryResult==null)
            return null;

        String nodeId = determineNodeId(connectionDetailsNodeId,icmpNodeId,dnsNodeId,snmpNodeId);
        nodeDiscoveryResult.setNodeId(nodeId);
        nodeDiscoveryResult.setNeighboursConnectionDetails(neighbourConnectionDetails);
        nodeDiscoveryResult.setDiscoveredData(discoveryData);
        nodeDiscoveryResult.setNodeAliases(nodeAliases);


        return nodeDiscoveryResult;

    }

    private String determineNodeId(String connectionDetailsNodeId, String icmpNodeId,String dnsNodeId,String snmpNodeId ){

        if (snmpNodeId != null && !snmpNodeId.isEmpty())
            return snmpNodeId;
        if (dnsNodeId!=null && !dnsNodeId.isEmpty())
            return dnsNodeId;
        if (connectionDetailsNodeId != null && !connectionDetailsNodeId.isEmpty())
            return connectionDetailsNodeId;
        return icmpNodeId;

    }


}
