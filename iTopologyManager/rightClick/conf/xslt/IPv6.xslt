<!--
  ~ IPv6.xslt
  ~
  ~ This work is free software; you can redistribute it and/or modify
  ~ it under the terms of the GNU General Public License as published
  ~ by the Free Software Foundation; either version 2 of the License,
  ~ or (at your option) any later version.
  ~
  ~ This work is distributed in the hope that it will be useful, but
  ~ WITHOUT ANY WARRANTY; without even the implied warranty of
  ~ MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
  ~ GNU General Public License for more details.
  ~
  ~ You should have received a copy of the GNU General Public License
  ~ along with this program; if not, write to the Free Software
  ~ Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
  ~ USA
  ~
  ~ Copyright (c) 2010-2016 iTransformers Labs. All rights reserved.
  -->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:java="java" xmlns:SnmpGetNameForXslt="net.itransformers.idiscover.discoveryhelpers.xml.SnmpForXslt" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:functx="http://www.functx.com">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<xsl:variable name="nodeID">
			<xsl:value-of select="/DiscoveredDevice/name"/>
		</xsl:variable>
		<xsl:variable name="root" select="/DiscoveredDevice"/>

		<IPv4AddressSpace>
			<xsl:for-each select="//DiscoveredDevice/object/object[objectType='IPv6 Address']">
				<IPv6AddressRange>
				<xsl:variable name="ipv6">
					<xsl:value-of select="parameters/parameter[name='IPv6Address']/value"/>
				</xsl:variable>
				<IPv6Address><xsl:value-of select="$ipv6"/></IPv6Address>
				<xsl:variable name="subnetMask">
					<xsl:value-of select="parameters/parameter[name='ipv6AddrPfxLength']/value"/>
				</xsl:variable>
				<SubnetMask><xsl:value-of select="$subnetMask"/></SubnetMask>
                <ipv6AddrType><xsl:value-of select="parameters/parameter[name='ipv6AddrType']/value"/></ipv6AddrType>
                <ipv6AddrAnycastFlag><xsl:value-of select="parameters/parameter[name='ipv6AddrAnycastFlag']/value"/></ipv6AddrAnycastFlag>
				<xsl:variable name="interface" select="../name">
				</xsl:variable>

				<Interface><xsl:value-of select="$interface"/></Interface>

				</IPv6AddressRange>
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
