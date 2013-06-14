iTransformer is a Network Transformation Manager.

Version 0.5 provides a number of enhancements and critical bug fixes.

New features:

- Ability to select multiple devices and to trigger a right click method. This simple but powerful feature allows network engineers to
 execute configuration templates, reports on multiple devices with a single click.

- New rightclick called "IPv4 to IPv6 network activation". It demonstrates how network engineers could apply an IPv4 to IPv6 transformation steps, part of an IPv4 to IPv6
 network transition strategy on one or more network devices with a single click.


Raised issues that have been fixed:

- Olive v10.1 IPV6 discovery problem http://sourceforge.net/p/itransformer/tickets/8/
This issue was critical for discovering networks containing newer versions of Juniper network routers. Hopefully now the issue is fixed.

- iTransformer has no capability for integration between xslt and log4j log levels http://sourceforge.net/p/itransformer/tickets/9/
Capability has been added and in xslt transformations have been placed a number of xsl:message tags with TRACE log levels.If you want to trace xslt transformations you have to add TRACE log level to your log4j.properties file otherwise TRACE is disabled by default

- Is it possible to have additional layouts in iTransformer  http://sourceforge.net/p/itransformer/tickets/10/
A new menu has been added called "Layout". Currently the GUI supports FR, KR, Circle, ISOM and Spring layout algorithms

- bug in ipCidrRoutingTable discovery method http://sourceforge.net/p/itransformer/tickets/11/

- ipRouteTable discovery method is missing protocol value http://sourceforge.net/p/itransformer/tickets/12/



Find out more on http://www.youtube.com/channel/UCVrXTSM9Hj6d3OFbIdF4Z2w

Enjoy http://iTransformers.net
