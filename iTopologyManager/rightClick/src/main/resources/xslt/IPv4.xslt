<!--
  ~ iTransformer is an open source tool able to discover IP networks
  ~ and to perform dynamic data data population into a xml based inventory system.
  ~ Copyright (C) 2010  http://itransformers.net
  ~
  ~ This program is free software: you can redistribute it and/or modify
  ~ it under the terms of the GNU General Public License as published by
  ~ the Free Software Foundation, either version 3 of the License, or
  ~ any later version.
  ~
  ~ This program is distributed in the hope that it will be useful,
  ~ but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~ GNU General Public License for more details.
  ~
  ~ You should have received a copy of the GNU General Public License
  ~ along with this program.  If not, see <http://www.gnu.org/licenses/>.
  -->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:java="java" xmlns:SnmpGetNameForXslt="net.itransformers.idiscover.discoveryhelpers.xml.SnmpForXslt" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:functx="http://www.functx.com">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<xsl:variable name="nodeID">
			<xsl:value-of select="/DiscoveredDevice/name"/>
		</xsl:variable>
		<xsl:variable name="root" select="/DiscoveredDevice"/>

		<IPv4AddressSpace>
			<xsl:for-each select="/DiscoveredDevice/object/object[objectType='IPv4 Address']/parameters">
				<IPv4AddressRange>
                <xsl:variable name="subnet">
					<xsl:value-of select="parameter[name='ipv4Subnet']/value"/>
				</xsl:variable>
                <Subnet><xsl:value-of select="$subnet"/></Subnet>
				<xsl:variable name="ipv4">
					<xsl:value-of select="parameter[name='IPv4Address']/value"/>
				</xsl:variable>
				<IPv4Address><xsl:value-of select="$ipv4"/></IPv4Address>
				<xsl:variable name="subnetMask">
					<xsl:value-of select="parameter[name='ipSubnetMask']/value"/>
				</xsl:variable>
				<SubnetMask><xsl:value-of select="$subnetMask"/></SubnetMask>

				<xsl:variable name="interface" select="../../name">
				</xsl:variable>

				<Interface><xsl:value-of select="$interface"/></Interface>
				</IPv4AddressRange>
			</xsl:for-each>
		</IPv4AddressSpace>
	</xsl:template>
    <xsl:function name="functx:substring-before-last-match" as="xs:string?" xmlns:functx="http://www.functx.com">
		<xsl:param name="arg" as="xs:string?"/>
		<xsl:param name="regex" as="xs:string"/>
		<xsl:sequence select="
   replace($arg,concat('^(.*)',$regex,'.*'),'$1')
   "/>
    </xsl:function>
</xsl:stylesheet>
