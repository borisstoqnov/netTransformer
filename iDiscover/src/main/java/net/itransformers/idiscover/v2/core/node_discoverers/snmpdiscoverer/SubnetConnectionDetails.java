package net.itransformers.idiscover.v2.core.node_discoverers.snmpdiscoverer;

import net.itransformers.idiscover.core.Subnet;
import net.itransformers.idiscover.v2.core.connection_details.IPNetConnectionDetails;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by niau on 8/22/16.
 */
public class SubnetConnectionDetails {

    Set<ConnectionDetails> subnetConnectionDetails;

    public SubnetConnectionDetails(Set<ConnectionDetails> subnetConnectionDetails) {
        this.subnetConnectionDetails = subnetConnectionDetails;
    }

    public SubnetConnectionDetails(List<Subnet> subnets) {
       this.subnetConnectionDetails = createSubnetConnectionDetails(subnets);
    }

    public Set<ConnectionDetails> getSubnetConnectionDetails() {
        return subnetConnectionDetails;
    }

    private Set<ConnectionDetails> createSubnetConnectionDetails(List<Subnet> subnets) {
        Set<ConnectionDetails> subnetConnectionDetails = new HashSet<ConnectionDetails>();

        for (Subnet subnet : subnets) {
            ConnectionDetails subnetConnection = new IPNetConnectionDetails();
            String ipAddress = subnet.getIpAddress();
            String protocolType = subnet.getSubnetProtocolType();
            String subnetMask = subnet.getsubnetMask();
            subnetConnection.put("ipAddress", ipAddress);
            subnetConnection.put("protocolType", protocolType);
            subnetConnection.put("subnetPrefixMask", subnet.getSubnetPrefixMask());
            subnetConnection.put("subnetMask",subnet.getsubnetMask());
            subnetConnection.put("port",subnet.getLocalInterface());
            subnetConnection.put("discoveryMethods",subnet.getSubnetDiscoveryMethods());
            subnetConnection.setConnectionType("subnet");

            subnetConnectionDetails.add(subnetConnection);


        }
        return subnetConnectionDetails;
    }
}
