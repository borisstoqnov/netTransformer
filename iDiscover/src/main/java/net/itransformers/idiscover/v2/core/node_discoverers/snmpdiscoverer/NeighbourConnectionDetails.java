package net.itransformers.idiscover.v2.core.node_discoverers.snmpdiscoverer;

import net.itransformers.idiscover.networkmodelv2.DeviceNeighbour;
import net.itransformers.idiscover.v2.core.connection_details.IPNetConnectionDetails;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by niau on 8/22/16.
 */
public class NeighbourConnectionDetails {
    Set<ConnectionDetails> connectionDetailses;

    NeighbourConnectionDetails(List<DeviceNeighbour> neighbours){
        connectionDetailses = create(neighbours);
    }

    public Set<ConnectionDetails> getConnectionDetailses() {
        return connectionDetailses;
    }

    public void setConnectionDetailses(Set<ConnectionDetails> connectionDetailses) {
        this.connectionDetailses = connectionDetailses;
    }

    private Set<ConnectionDetails> create(List<DeviceNeighbour> neighbours) {
        Set<ConnectionDetails> connectionDetailses = new HashSet<>();

        for (DeviceNeighbour neighbour : neighbours) {
            ConnectionDetails neighbourConnectionDetails = new IPNetConnectionDetails();
            String ipAddress = neighbour.getIpAddress();
            HashMap<String, String> neighbourParameters = neighbour.getParameters();
            String deviceType = neighbourParameters.get("Neighbor Device Type");

            if (deviceType != null && !deviceType.isEmpty()) {
                neighbourConnectionDetails.put("deviceType", deviceType);
            }

            String deviceName = neighbourParameters.get("Neighbor hostname");

            if (deviceName != null && !deviceName.isEmpty()) {
                neighbourConnectionDetails.put("deviceName", deviceName);
            }
            String neighbourMacAddress = neighbourParameters.get("Neighbor MAC Address");
            if (neighbourMacAddress != null && !neighbourMacAddress.isEmpty()) {
                neighbourConnectionDetails.put("neighborMacAddress", neighbourMacAddress);
            }
            String discoveryMethods = neighbourParameters.get("Discovery Method");

            if (neighbourParameters.get("Discovery Method") != null && !discoveryMethods.isEmpty()) {
                neighbourConnectionDetails.put("discoveryMethods", discoveryMethods);
            }
            if (ipAddress != null && !ipAddress.isEmpty())
                neighbourConnectionDetails.put("ipAddress", ipAddress);

            neighbourConnectionDetails.setConnectionType("snmp");
            connectionDetailses.add(neighbourConnectionDetails);
        }
        return connectionDetailses;

    }


}
