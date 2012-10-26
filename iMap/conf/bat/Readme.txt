Prerequirements:

iMap needs JRE  "1.6.0_25" or later.
In order to work properly you have to have IP& SNMP connectivity to a device with BGP internet table.

How to create an Internet BGP peering map?
Edit bin/iMap/conf/imap.properties and add there your snmp communities and the IP address of the BGP enabled device .
With other words change X.X.X.X and test-r, test-rw with your values.
-------------------------------------------------------------------------------

query.parameters=system,bgpLocalAs,bgpPeerEntry,bgp4PathAttrEntry
xsltFileName1=iMap/conf/xslt/bgp_as_path.xslt
xsltFileName2=iMap/conf/xslt/inet-map.xslt
output.dir=network
output.dir.graphml=undirected
mibDir=snmptoolkit/mibs
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

iMap.bat


! On Linux/Unix
cd bin
chmod 775 *.sh
iMap.sh


How to reveal the Internet Map?

On windows try the following to reveal undirected network graph view
iMapTopoManager.bat


On linux/Unix/macOS try the following to reveal undirected network graph view:
./iMapTopoManager.sh

For any further information about iTransformer please do not hesitate to consult http://itransformers.net.
