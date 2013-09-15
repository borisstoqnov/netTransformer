bgpPeeringMap v.02.

bgpPeeringMap is the direct ancestor of iMap able to discover bgpPeeringTopology through an SNMP requests towards SNMP enabled BGP routers.

Prerequirements:

iMap needs JRE  "1.6.0_25" or later.
In order to work properly you have to have IP& SNMP connectivity to a node with BGP routing table containg certain number of preferably Internet BGP routes. However with bgpPeeringMap you can discover the peering topology of any kind of BGP connectivity (Internet, GRX or something else)

How to create an Internet BGP peering map?
Edit bgpPeeringMap/bin/bgpPeeringMap/conf/bgpPeeringMap.properties and add there your snmp communities and the IP address of the BGP enabled node .
With other words change X.X.X.X and test-r, test-rw with your values.
-------------------------------------------------------------------------------

query.parameters=system,bgpLocalAs,bgpPeerEntry,bgp4PathAttrEntry
xsltFileName1=bgpPeeringMap/conf/xslt/bgp_as_path.xslt
xsltFileName2=bgpPeeringMap/conf/xslt/inet-map.xslt
output.dir=network
output.dir.graphml=undirected
mibDir=snmp2xml/mibs
address=X.X.X.X
port=161
version=1
community-ro=test-r
community-rw=test-rw
timeout=500
retries=1
max-repetitions=65535
as-numbers=../xml/autnums.xml

--------------------------------------------------------------------------------
Then run

!On Windows
cd bin

bgpPeeringMap.bat

! On Linux/Unix
cd bin
chmod 775 *.sh
bgpPeeringMap.sh

How to reveal topology map of the BGP peering?

On windows try the following to reveal undirected network graph view
bgpTopologyManager.bat

On linux/Unix/macOS try the following to reveal undirected network graph view:
./bgpTopologyManager.sh

For any further information about bgpPeeringMap please do not hesitate to contact us on info@itransformers.net