<?xml version="1.0" encoding="UTF-8"?>
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

		<deviceNeighbors>
			<xsl:for-each select="(//object[objectType='Discovery Interface']/object[objectType='Discovered Neighbor']/name)">
				<xsl:variable name="neighID">
					<xsl:value-of select="."/>
				</xsl:variable>
				<xsl:if test="$neighID !=$nodeID and $neighID!='' and not(contains($neighID,'Unknown'))">
					<deviceNeighbor>
						<NeighborID><xsl:value-of select ="$neighID"/></NeighborID>
					<xsl:variable name="methods" select="../parameters/parameter[name='Discovery Method']/value">
					</xsl:variable>
					<discoveryMethods><xsl:value-of select="functx:substring-before-last-match($methods,',')"/></discoveryMethods>
                        <xsl:variable name="interfaces">
							<xsl:for-each select="../../name">
								<xsl:if test=". != 'DeviceLogicalData'">
									<xsl:value-of select="."/>
								</xsl:if>
						</xsl:for-each>
					</xsl:variable>
                    <discoveryInterface><xsl:value-of select="$interfaces"/></discoveryInterface>
                    <localIP><xsl:value-of select="../../object[objectType='IPv4 Address']/name"/></localIP>
                     <neighborIP><xsl:value-of select="../parameters/parameter[name='Neighbor IP Address']/value"/></neighborIP>
                    
					</deviceNeighbor>
				</xsl:if>
			</xsl:for-each>
			</deviceNeighbors>
	</xsl:template>
    <xsl:function name="functx:substring-before-last-match" as="xs:string?" xmlns:functx="http://www.functx.com">
		<xsl:param name="arg" as="xs:string?"/>
		<xsl:param name="regex" as="xs:string"/>
		<xsl:sequence select="
   replace($arg,concat('^(.*)',$regex,'.*'),'$1')
   "/>
    </xsl:function>
</xsl:stylesheet>
