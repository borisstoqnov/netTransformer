package net.itransformers.idiscover.v2.core.listeners.groovy

import net.itransformers.utils.CIDRUtils

/**
 * Created by vasko on 1/29/2015.
 */

output << "<graphml>\n"
output << """\
        <graphml>
            <key id="hostname" for="node" attr.name="hostname" attr.type="string"/>
            <key id="deviceModel" for="node" attr.name="deviceModel" attr.type="string"/>
            <key id="deviceType" for="node" attr.name="deviceType" attr.type="string"/>
            <key id="nodeInfo" for="node" attr.name="nodeInfo" attr.type="string"/>
            <key id="DiscoveredIPv4Address" for="node" attr.name="DiscoveredIPv4Address" attr.type="string"/>
            <key id="geoCoordinates" for="node" attr.name="geoCoordinates" attr.type="string"/>
            <key id="site" for="node" attr.name="site" attr.type="string"/>
            <key id="diff" for="node" attr.name="diff" attr.type="string"/>
            <key id="diffs" for="node" attr.name="diffs" attr.type="string"/>
            <key id="IPv6Forwarding" for="node" attr.name="IPv6Forwarding" attr.type="string"/>
            <key id="IPv4Forwarding" for="node" attr.name="IPv4Forwarding" attr.type="string"/>
            <key id="SubnetPrefix" for="node" attr.name="SubnetPrefix" attr.type="string"/>

            <key id="name" for="edge" attr.name="name" attr.type="string"/>
            <key id="method" for="edge" attr.name="method" attr.type="string"/>
            <key id="dataLink" for="edge" attr.name="dataLink" attr.type="string"/>
            <key id="ipLink" for="edge" attr.name="ipLink" attr.type="string"/>
            <key id="MPLS" for="edge" attr.name="MPLS" attr.type="string"/>
            <key id="IPv6Forwarding" for="edge" attr.name="IPv6Forwarding" attr.type="string"/>
            <key id="IPv4Forwarding" for="edge" attr.name="IPv4Forwarding" attr.type="string"/>

            <key id="bgpLocalAS" for="node" attr.name="bgpLocalAS" attr.type="string"/>
            <key id="bgpAutonomousSystemA" for="edge" attr.name="bgpAutonomousSystemA" attr.type="string"/>
            <key id="bgpAutonomousSystemB" for="edge" attr.name="bgpAutonomousSystemB" attr.type="string"/>

            <key id="InterfaceNameA" for="edge" attr.name="InterfaceNameA" attr.type="string"/>
            <key id="InterfaceNameB" for="edge" attr.name="InterfaceNameB" attr.type="string"/>
            <key id="IPv4AddressA" for="edge" attr.name="IPv4AddressA" attr.type="string"/>
            <key id="IPv4AddressB" for="edge" attr.name="IPv4AddressB" attr.type="string"/>
            <key id="edgeTooltip" for="edge" attr.name="edgeTooltip" attr.type="string"/>
            <key id="diff" for="edge" attr.name="diff" attr.type="string"/>
            <key id="diffs" for="edge" attr.name="diffs" attr.type="string"/>

            <graph edgedefault="undirected">
"""


"""/
//
//                <xsl:variable name="nodeID">
//                    <xsl:value-of select="/DiscoveredDevice/name"/>
//                </xsl:variable>
//                <xsl:variable name="root" select="/DiscoveredDevice"/>
//                <xsl:variable name="deviceModel">
//                    <xsl:value-of select="//DiscoveredDevice/parameters/parameter[name='Device Model']/value"/>
//                </xsl:variable>
//                <xsl:variable name="deviceType">
//                    <xsl:value-of select="//DiscoveredDevice/parameters/parameter[name='Device Type']/value"/>
//                </xsl:variable>
//
//
//                <xsl:variable name="siteID">
//                    <xsl:value-of select="//DiscoveredDevice/parameters/parameter[name='siteID']/value"/>
//                </xsl:variable>
//                <xsl:variable name="DiscoveredIPv4Address">
//                    <xsl:value-of select="//DiscoveredDevice/parameters/parameter[name='Management IP Address']/value"/>
//                </xsl:variable>
//                <xsl:variable name="BGPLocalASInfo">
//                    <xsl:value-of select="//DiscoveredDevice/parameters/parameter[name='BGPLocalASInfo']/value"/>
//                </xsl:variable>
//                <xsl:variable name="X" select="//DiscoveredDevice/parameters/parameter[name='X Coordinate']/value"/>
//                <xsl:variable name="Y" select="//DiscoveredDevice/parameters/parameter[name='Y Coordinate']/value"/>
//                <xsl:variable name="IPv6Forwarding"
//                              select="//DiscoveredDevice/parameters/parameter[name='ipv6Forwarding']/value"/>
//                <xsl:variable name="IPv4Forwarding"
//                              select="//DiscoveredDevice/parameters/parameter[name='ipv4Forwarding']/value"/>
//                <!--Prepare Central Node-->
//                <node>
//                    <xsl:attribute name="id">
//                        <xsl:value-of select="\$nodeID"/>
//                    </xsl:attribute>
//                    <xsl:attribute name="label">
//                        <xsl:value-of select="\$nodeID"/>
//                    </xsl:attribute>
//
//                    <data key="hostname">
//                        <xsl:value-of select="\$nodeID"/>
//                    </data>
//                    <data key="deviceModel">
//                        <xsl:value-of select="\$deviceModel"/>
//                    </data>
//                    <data key="deviceType">
//                        <xsl:value-of select="\$deviceType"/>
//                    </data>
//
//                    <data key="DiscoveredIPv4Address">
//                        <xsl:value-of select="\$DiscoveredIPv4Address"/>
//                    </data>
//                    <data key="bgpLocalAS"><xsl:value-of select="\$BGPLocalASInfo"/></data>
//                    <data key="site">
//                        <xsl:value-of select="\$siteID"/>
//                    </data>
//                    <data key="geoCoordinates">
//                        <xsl:value-of select="\$Y"/>,<xsl:value-of select="\$X"/>
//                    </data>
//                    <data key="IPv6Forwarding">
//                        <xsl:value-of select="\$IPv6Forwarding"/>
//                    </data>
//                    <data key="IPv4Forwarding">
//                        <xsl:value-of select="\$IPv4Forwarding"/>
//                    </data>
//
//                </node>
//                """

output << "<node id=\"${input.name}\" label=\"${input.name}\">\n"


output << "\t<data key=\"hostname\">${input.name}</data>\n"
output <<
        "\t<data key=\"deviceModel\">" +
        input.parameters.parameter.findAll {
            it.name.text() == "Device Model"
        }.value.text() +
        "</data>\n"
output <<
        "\t<data key=\"deviceType\">" +
        input.parameters.parameter.findAll {
            it.name.text() == "Device Type"
        }.value.text() +
        "</data>\n"



output << "</node>\n"


def foundNeighbours = [] as Set

input.object.findAll {
    it.objectType.text() == "Discovery Interface"  || it.objectType.text() == "DeviceLogicalData"

}.object.findAll {
    it.objectType.text() == 'Discovered Neighbor'
}.each {
    def name = it.name.text()
    foundNeighbours.add(name)

}



Device device = new Device(""); // TODO put name here

private String calcSubnet(ipv4Address) {
    String[] ipAndSubnetPrefix = ipv4Address.split("/")
    if (ipAndSubnetPrefix.length == 2) {
        try {
            int subnetPrefix = Integer.parseInt(ipAndSubnetPrefix[1])
            if (subnetPrefix < 30) {
                return ipv4Address
            }
        } catch (NumberFormatException nfe) {
            println(nfe.toString())
        }
    }
    return null;
}

for (String node : foundNeighbours) {
    output << "<node id=\"${node}\" label=\"${node}\">\n"

}



def foundSubnets = [] as Set

input.object.findAll {
    it.objectType.text() == "Discovery Interface"   || it.objectType.text() == "DeviceLogicalData"
}.each { discoveryInterface ->
    def subnets = [] as Set
    discoveryInterface.object.findAll {
        it.objectType.text() == 'IPv4 Address'
    }.parameters.parameter.findAll {
        it.name.text() == 'ipv4Subnet'
    }.each {
        String subnet = it.value.text()
        subnets.add(subnet)
        foundSubnets.add(subnet)
        device.addSubnet(new Subnet(subnet))
    }

    discoveryInterface.object.findAll {
        it.objectType.text() == 'Discovered Neighbor'
    }.each {
        def neighborName = it.name.text();
        DeviceNeighbour neighbour = new DeviceNeighbour(neighborName);
        def neighborParams = neighbour.getProperties()

        it.parameters.parameter.find {
            it.name.text() == 'Discovery Method'
        }.each {
            neighborParams.put('Discovery Method', it.value.text())
        }

        it.parameters.parameter.find {
            it.name.text() == 'Neighbor IP Address'
        }.each {
            def neighbourIp = it.value.text()
            neighborParams.put('Neighbor IP Address', neighbourIp)

            for (String subnet : subnets) {
                CIDRUtils cidrUtils = new CIDRUtils(subnet)
                cidrUtils.isInRange(neighbourIp);
                device.getSubnets().get(subnet).addNeighbour(neighbour)
            }
        }

        it.parameters.parameter.find {
            it.name.text() == 'Neighbor Port'
        }.each {
            neighborParams.put('Neighbor Port', it.value.text())

        }
        it.parameters.parameter.find {
            it.name.text() == 'Reachable'
        }.each {
            neighborParams.put('Reachable', it.value.text())

        }
        it.parameters.parameter.find {
            it.name.text() == 'Neighbor Device Type'
        }.each {
            neighborParams.put('Neighbor Device Type', it.value.text())

        }

    }
}
//       CIDRUtils utils = new CIDRUtils("192.168");




for (String subnet : foundSubnets) {
    output << "<node id=\"${subnet}\" label=\"${subnet}\">\n"
    output << "\t<data key=\"deviceType\">Subnet</data>\"\n"
    output << "\t<data key=\"deviceModel\">passiveHub</data>\"\n"
    output << "\t<data key=\"SubnetPrefix\">${subnet}</data>\"\n"


}
for (Map.Entry<String, Subnet> subnetEntry : device.getSubnets()) {

    Subnet subnet  = subnetEntry.getValue()
    output << "Subnet: " << subnet.getName() << "\n"

    for (Map.Entry<String, DeviceNeighbour> neighboursEntry  : subnet.getNeighbours()) {

        output << "   neighbour: " << neighboursEntry.getKey() << "\n"

        for (Map.Entry<String, DeviceNeighbour> neighbourProps  : subnet.getNeighbours()) {
            output << "       param: " << neighbourProps.getKey() << " = " <<  neighbourProps.getValue() << "\n"
        }

    }

}
//parameters.parameter.findAll {
//    it.name.text() == 'Neighbor IP Address'
//}.each {
//
//    output << "Neighbor IP Address: " << it.value.text() << "\n"
//
//
//}

//if (foundNeighbours.contains())
//        it.objectType.text() == 'Discovery Interface'
//    }.object.findAll {
//        it.objectType.text() == 'IPv4 Address'
//    }.parameters.parameter.findAll {
//        it.name.text() == 'IPv4Address'
//    }.eachWithIndex { ip, i ->
//        output << "IP_${i+1}: " << ip.value.text() << "\n"
//    }

output << "</graphml>\n"