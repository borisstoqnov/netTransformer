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

		<mplsL3VRFs>
			<xsl:for-each select="//object[objectType='mplsL3VPNs']/object/parameters/parameter[name='vrfName']/value">
				<mplsL3VPN>
				<xsl:variable name="vrfName">
					<xsl:value-of select="."/>
				</xsl:variable>
				<vrfName><xsl:value-of select="$vrfName"/></vrfName>
				<xsl:variable name="rd">
					<xsl:value-of select="../../../name"/>
				</xsl:variable>
				<RD><xsl:value-of select="$rd"/></RD>
				<xsl:variable name="rt">
					<xsl:for-each select="../../../object[objectType='RT']/name">
						<xsl:value-of select="."/>,
					</xsl:for-each>
				</xsl:variable>
				<RTs><xsl:value-of select="functx:substring-before-last-match($rt,',')"/></RTs>
				<xsl:variable name="Interfaces">
				<xsl:for-each select="/DiscoveredDevice/object[objectType='Discovery Interface']/parameters/parameter[name='vrfForwarding' and value = $vrfName]/../../name">
					<xsl:value-of select="."/>,
				</xsl:for-each>
				</xsl:variable>
				<Interfaces><xsl:value-of select="functx:substring-before-last-match($Interfaces,',')"/></Interfaces>
				</mplsL3VPN>
			</xsl:for-each>
			</mplsL3VRFs>
	</xsl:template>
    <xsl:function name="functx:substring-before-last-match" as="xs:string?" xmlns:functx="http://www.functx.com">
		<xsl:param name="arg" as="xs:string?"/>
		<xsl:param name="regex" as="xs:string"/>
		<xsl:sequence select="
   replace($arg,concat('^(.*)',$regex,'.*'),'$1')
   "/>
    </xsl:function>
</xsl:stylesheet>
