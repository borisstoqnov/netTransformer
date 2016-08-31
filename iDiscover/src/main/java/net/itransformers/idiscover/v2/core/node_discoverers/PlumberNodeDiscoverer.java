package net.itransformers.idiscover.v2.core.node_discoverers;

import net.itransformers.idiscover.v2.core.NodeDiscoverer;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.idiscover.v2.core.node_discoverers.icmp.IcmpDiscoverer;
import net.itransformers.idiscover.v2.core.node_discoverers.snmpdiscoverer.SnmpParallelNodeDiscoverer;

/**
 * Created by niau on 8/30/16.
 */
public class PlumberNodeDiscoverer implements NodeDiscoverer {
    IcmpDiscoverer icmpDiscoverer = new IcmpDiscoverer();
    SnmpParallelNodeDiscoverer snmpParallelNodeDiscoverer = new SnmpParallelNodeDiscoverer();



    @Override
    public NodeDiscoveryResult discover(ConnectionDetails connectionDetails) {
        return null;
    }
}
