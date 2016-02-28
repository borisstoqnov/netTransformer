<!--
  ~ cableCut.xslt
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

		<CableCutReport>
			<xsl:for-each select="/DiscoveredDevice/object[objectType='Discovery Interface']/parameters/parameter[name='CableCut' and (value ='YES' or value='UNKNOWN') ]">
				<Interface>
                <xsl:variable name="interfaceName">
					<xsl:value-of select="../../name"/>
				</xsl:variable>
                <InterfaceName><xsl:value-of select="$interfaceName"/></InterfaceName>
				<xsl:variable name="adminStatus">
					<xsl:value-of select="../parameter[name='ifAdminStatus']/value"/>
				</xsl:variable>
				<AdminStatus><xsl:value-of select="$adminStatus"/></AdminStatus>
				<xsl:variable name="operStatus">
					<xsl:value-of select="../parameter[name='ifOperStatus']/value"/>
				</xsl:variable>
				<operStatus><xsl:value-of select="$operStatus"/></operStatus>
				</Interface>
			</xsl:for-each>
		</CableCutReport>
	</xsl:template>
    <xsl:function name="functx:substring-before-last-match" as="xs:string?" xmlns:functx="http://www.functx.com">
		<xsl:param name="arg" as="xs:string?"/>
		<xsl:param name="regex" as="xs:string"/>
		<xsl:sequence select="
   replace($arg,concat('^(.*)',$regex,'.*'),'$1')
   "/>
    </xsl:function>
</xsl:stylesheet>
