Prerequirements:

Discovery needs JRE  "1.6.0_25" or later.
In order to work properly you have to have connectivity to all network IP addresses in your network.
Will be good (but not a must) if the station that is used for running the discovery is able to resolve device hostname through DNS.
All the devices in your network have to have the same SNMP community string.

So if all those are met:

How to discover your network?
Edit discovery-manager/conf/xml/discoveryResource.xml and add your snmp network community into the resource with name SNMP.
With other words change <param name="community-ro">public</param> the "public" community string with your string.
<resource name="SNMP">
        <param name="protocol">SNMP</param>
        <connection-params connection-type="snmp">
            <param name="version">1</param>
            <param name="community-ro">public</param>
            <param name="community-rw">private</param>
            <param name="timeout">500</param>
            <param name="retries">1</param>
			<param name="port">161</param>
            <param name="max-repetitions">65535</param>
        </connection-params>
</resource>

Then change the same way the rest of the resouces.

!On Windows
cd bin

iDiscover.bat -h 10.10.10.10


! On Linux/Unix
cd bin
./iDiscover.sh -h 10.10.10.10


How to reveal the discovered network topology?

On windows try the following to reveal undirected network graph view
u_topoManager.bat

On windows try the following to reveal directed network graph view
d_topoManager.bat

On linux/Unix/macOS try the following to reveal undirected network graph view:
u_topoManager.sh

On linux/Unix/macOS try the following to reveal directed network graph view
d_topoManager.sh


Since the fact that the discovery process might continue quite a while (currnet estimate is 6 hours 500 devices) note that iTopoManager supports dynamic topology update through the Reload button.


Customizing the discovery config:

So in \disct\bin\conf\xml you will find a discoveryParameters.xml file. It allows you to customize your SNMP requests based on the devices you have in the network.
For example if you do not want an arp discovery method to be used just strip ipNetToMediaTable from the request for the expected deviceTypes in your network.
Or if you don't want the whole routing table to be examined strip the ipRouteNextHop and  inetCidrRouteType, inetCidrRouteIfIndex, inetCidrRouteNextHop, inetCidrRouteProto, inetCidrRouteNextHopAS from the requests.
Same for bgp4PathAttrEntry if you do not want the process to obtain all your BGP ASPATH routes. For example if you have full BGP on certain router.

Stop Criteria
Discovery process is flexible enough to avoid from Discovering certain network devices. That part of the configuration is located in discoveryParameters.xml under
the stop-criteria section.
You can perform a regex matches of device Hostnames and IP addresses.

<match property="host">CE*</match> -> will not discover any device that has a hostname starting with CE.
<match property="host">R11</match> -> will not discover any device that has a hostname R11.
<match property="ipAddress.ipAddress">.*</match> -> won't discover any device with any IP address.
<match-not property="ipAddress.ipAddress">10\..*</match-not> - will discover only devices with IP address starting with 10.

For any further information about iTransformer please do not hesitate to consult with our web-site.
