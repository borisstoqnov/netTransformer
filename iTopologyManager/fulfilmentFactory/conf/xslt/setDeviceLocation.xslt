<xsl:stylesheet version="2.0"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:xs="http://www.w3.org/2001/XMLSchema"
 xmlns:fn="http://www.w3.org/2005/xpath-functions"
 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 xmlns:snmpSet="net.itransformers.snmptoolkit.SnmpSet"
 xmlns:functx="http://www.functx.com">
 <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
	<xsl:variable name="ManagementIPAddress" select="/root/parameters[name='ManagementIPAddress']/value"/>
	<xsl:variable name="community" select="/root/parameters[name='community']/value"/>
   <xsl:variable name="value" select="/root/parameters[name='value']/Location"/>

        <xsl:variable name="SnmpSetResponse" select="snmpSet:createSNMPOID('.1.3.6.1.2.1.1.4.0',$ManagementIPAddress, 161, 'SnmpConstants.version1',1,200,$community, $value)"/>
	<test>
	   <name><xsl:value-of select="$ManagementIPAddress"/></name>
	   <name><xsl:value-of select="$community"/></name>
        <name><xsl:value-of select="$value"/></name>
	</test>
	</xsl:template>
</xsl:stylesheet>