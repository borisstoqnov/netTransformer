package net.itransformers.idiscover.v2.core.listeners.groovy

import net.itransformers.utils.CIDRUtils

/**
 * Created by vasko on 1/29/2015.
 */

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
            <key id="Discovery Method" for="edge" attr.name="Discovery Method" attr.type="string"/>
            <key id="dataLink" for="edge" attr.name="dataLink" attr.type="string"/>
            <key id="ipLink" for="edge" attr.name="ipLink" attr.type="string"/>
            <key id="MPLS" for="edge" attr.name="MPLS" attr.type="string"/>
            <key id="IPv6Forwarding" for="edge" attr.name="IPv6Forwarding" attr.type="string"/>
            <key id="IPv4Forwarding" for="edge" attr.name="IPv4Forwarding" attr.type="string"/>

            <key id="bgpLocalAS" for="node" attr.name="bgpLocalAS" attr.type="string"/>
            <key id="bgpAutonomousSystemA" for="edge" attr.name="bgpAutonomousSystemA" attr.type="string"/>
            <key id="bgpAutonomousSystemB" for="edge" attr.name="bgpAutonomousSystemB" attr.type="string"/>

            <key id="Interface" for="edge" attr.name="Interface" attr.type="string"/>
            <key id="InterfaceNameB" for="edge" attr.name="InterfaceNameB" attr.type="string"/>
            <key id="IPv4AddressA" for="edge" attr.name="IPv4AddressA" attr.type="string"/>
            <key id="IPv4AddressB" for="edge" attr.name="IPv4AddressB" attr.type="string"/>
            <key id="edgeTooltip" for="edge" attr.name="edgeTooltip" attr.type="string"/>
            <key id="diff" for="edge" attr.name="diff" attr.type="string"/>
            <key id="diffs" for="edge" attr.name="diffs" attr.type="string"/>

            <graph edgedefault="undirected">
"""


String deviceName = input.name;

Device device = new Device(deviceName)

output << "\t\t<node id=\"${input.name}\" label=\"${input.name}\">\n"


output << "\t\t\t<data key=\"hostname\">${input.name}</data>\n"
output <<
        "\t\t\t<data key=\"deviceModel\">" +
        input.parameters.parameter.findAll {
            it.name.text() == "Device Model"
        }.value.text() +
        "</data>\n"
output <<
        "\t\t\t<data key=\"deviceType\">" +
        input.parameters.parameter.findAll {
            it.name.text() == "Device Type"
        }.value.text() +
        "</data>\n"

output << "\t\t</node>\n"



//Populate Neighbour Ids
def foundNeighbours = [] as Set

input.object.findAll {
    it.objectType.text() == "Discovery Interface"  || it.objectType.text() == "DeviceLogicalData"

}.object.findAll {
    it.objectType.text() == 'Discovered Neighbor'
}.each {
    def name = it.name.text()
    foundNeighbours.add(name)

}





def foundSubnets = [] as Set

input.object.findAll {
    it.objectType.text() == "Discovery Interface"
}.each { discoveryInterface ->

    //Capture localInterface Name
    String localInterfaceName = discoveryInterface.name;
    //Populate subnets
    def subnets = [] as Set
    discoveryInterface.object.findAll {
        it.objectType.text() == 'IPv4 Address'

    }.each {
        def Network subnet = new Network()
        String subnetId
        boolean bogon = false
        boolean hostOnly = false;
        String ipv4Address
        it.parameters.parameter.findAll {
            it.name.text() == 'ipv4Subnet'
        }.each {
            subnetId = it.value.text()

        }
        it.parameters.parameter.findAll {
            it.name.text() == 'IPv4Address'
        }.each {
             ipv4Address = it.value.text()
            cidrUtils = new CIDRUtils("0.0.0.0/8");

            if (cidrUtils.isInRange(ipv4Address)){
                bogon=true;
            }
            cidrUtils = new CIDRUtils("127.0.0.0/8");
            if (cidrUtils.isInRange(ipv4Address)){
                bogon = true;
            }
            cidrUtils = new CIDRUtils("128.0.0.0/8");
            if (cidrUtils.isInRange(ipv4Address)){
                bogon = true;
            }
            cidrUtils = new CIDRUtils("169.254.0.0/16");
            if (cidrUtils.isInRange(ipv4Address)){
                bogon = true;
            }
            cidrUtils = new CIDRUtils("192.0.0.0/24");
            if (cidrUtils.isInRange(ipv4Address)){
                bogon = true;
            }
            cidrUtils = new CIDRUtils("192.0.2.0/24");
            if (cidrUtils.isInRange(ipv4Address)){
                bogon = true;
            }
            cidrUtils = new CIDRUtils("224.0.0.0/4");
            if (cidrUtils.isInRange(ipv4Address)){
                bogon = true;
            }
            cidrUtils = new CIDRUtils("240.0.0.0/4");
            if (cidrUtils.isInRange(ipv4Address)){
                bogon = true;
            }
            cidrUtils = new CIDRUtils("255.255.255.255/32");
            if (cidrUtils.isInRange(ipv4Address)){
                bogon = true;
            }


        }
        it.parameters.parameter.findAll {
            it.name.text() == 'ipv4SubnetPrefix'
        }.each {
           // subnet = subnetId
            if (bogon==false){
                String ipv4SubnetMaskPrefix = it.value.text()
                if (ipv4SubnetMaskPrefix=="32"){
                    hostOnly=true;
                }
                String subnetPrefix = subnetId+"/"+ipv4SubnetMaskPrefix
                subnet.setName(subnetPrefix)
                subnet.setPrefix(subnetPrefix)
                subnet.setIpv4SubnetMaskPrefix(ipv4SubnetMaskPrefix)
                subnets.add(subnetPrefix)
            }
        }
        if (bogon==false&&hostOnly==false){
            subnet.setLocalInterface(localInterfaceName)
            device.addSubnet(subnet)
        }
    }
   //Populate Neighbours
    discoveryInterface.object.findAll {
        it.objectType.text() == 'Discovered Neighbor'

    }.each {

        def neighborName = it.name.text();
        DeviceNeighbour neighbour = new DeviceNeighbour(neighborName);
        def neighborParams = neighbour.getProperties()
        boolean subnetFlag = false;

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
               if ( cidrUtils.isInRange(neighbourIp)) {
                    device.getSubnets().get(subnet).addNeighbour(neighbour)
                    subnetFlag = true;
               }
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
        neighborParams.put('Local Interface Name',localInterfaceName)
        if (!subnetFlag){
            device.addPhysicalNeighbour(neighbour)
        }

    }
}



//Dump found end node neighbours

for (String node : foundNeighbours) {
    output << "\t\t<node id=\"${node}\" label=\"${node}\"/>\n"

}

//Dump subnet nodes
for (Map.Entry<String, Network> subnetEntry : device.getSubnets()) {
   // output << subnetEntry

    output << "\t\t<node id=\"" << subnetEntry.getKey() << "\" label=\"" << subnetEntry.getKey() << "\">\n"
    output << "\t\t\t<data key=\"deviceType\">Subnet</data>\n"
    output << "\t\t\t<data key=\"deviceModel\">passiveHub</data>\n"
    output << "\t\t\t<data key=\"SubnetPrefix\">" << subnetEntry.getValue().getPrefix() << "</data>\n"
    output << "\t\t</node>\n"

}

//Dump node direct edges to the subnet




//Dump edges between main node and subnets
for (Map.Entry<String, Network> subnetEntry : device.getSubnets()) {

     Network subnet  = subnetEntry.getValue()
     String subnetId = subnet.getPrefix();

    String SubnetEdgeId= subnetEntry.getKey()+"-" +deviceName;
    output << "\t\t<edge id=\"" << SubnetEdgeId << "\" source=\"" << subnetEntry.getKey() <<"\" target=\""<<deviceName << "\" label=\"" << SubnetEdgeId << "\">\n"
    output << "\t\t\t<data key=\"Interface\">" <<  subnetEntry.getValue().getLocalInterface() << "</data>\n"

    def discoveryMethods = [] as Set


    for (Map.Entry<String, DeviceNeighbour> neighboursEntry  : subnet.getNeighbours()) {
        DeviceNeighbour neighbour = neighboursEntry.getValue();

        for (Map.Entry<String, String> neighbourProps  : neighbour.getProperties()) {
                 if (neighbourProps.getKey()=='Discovery Method'){
                     discoveryMethods.add(neighbourProps.getValue())
                     break;
                 }

            }
        }
    if (discoveryMethods.size()!=0){
        output << "\t\t\t<data key=\"Discovery Method\">"
        for (String method : discoveryMethods) {
            output << method
        }
        output << "</data>\n"

    }
//    idiscoveryMethods.addAll()


    output << "\t\t\t</edge>\n"


    for (Map.Entry<String, DeviceNeighbour> neighboursEntry  : subnet.getNeighbours()) {

        String neighbourId = neighboursEntry.getKey();
        String edgeId = subnetId+"-"+neighbourId

        output << "\t\t<edge id=\"" << edgeId<<"\" source=\"" << subnetId<< "\" target=\""<<neighbourId <<"\" label=\"" << edgeId <<"\">\n"

        DeviceNeighbour neighbour = neighboursEntry.getValue();


        for (Map.Entry<String, String> neighbourProps  : neighbour.getProperties()) {
            if (neighbourProps.getValue()!=null && !neighbourProps.getValue().toString().equals("") && !neighbourProps.getKey().toString().equals('Local Interface Name'))
                   output << "\t\t\t<data key=\"" << neighbourProps.getKey() << "\">" <<  neighbourProps.getValue() << "</data>\n"

        }
        output << "\t\t</edge>\n"
    }

}
output << "\t</graph>\n"

output << "</graphml>\n"


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