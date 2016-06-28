<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:SnmpForXslt="net.itransformers.idiscover.discoveryhelpers.xml.SnmpForXslt" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:functx="http://www.functx.com">
    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>

    <xsl:template name="interfaceParameters">
        <xsl:param name="ifDescr"/>
        <xsl:param name="ifIndex"/>
        <xsl:param name="ifName"/>
        <xsl:param name="ifType"/>
        <xsl:param name="ifSped"/>
        <xsl:param name="ifAdminStatus"/>
        <xsl:param name="ifOperStatus"/>
        <xsl:param name="IPv4Forwarding"/>
        <xsl:param name="IPv6Forwarding"/>
        <xsl:param name="vrfForwarding"/>

        <xsl:variable name="cableCut">
            <xsl:choose>
                <xsl:when test="$ifAdminStatus='UP' and $ifOperStatus='UP'">NO</xsl:when>
                <xsl:when test="$ifAdminStatus='YES' and $ifOperStatus='DOWN'">YES</xsl:when>
                <xsl:when test="$ifAdminStatus='DOWN' and $ifOperStatus='DOWN'">NO</xsl:when>
                <xsl:otherwise>UNKNOWN</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

            <parameters>
                <parameter>
                    <name>ifIndex</name>
                    <value>
                        <xsl:value-of select="$ifIndex"/>
                    </value>
                </parameter>
                <parameter>
                    <name>ifDescr</name>
                    <value>
                        <xsl:value-of select="$ifDescr"/>
                    </value>
                </parameter>
                <parameter>
                    <name>ifName</name>
                    <value>
                        <xsl:value-of select="$ifName"/>
                    </value>
                </parameter>
                <parameter>
                    <name>ifType</name>
                    <value>
                        <xsl:call-template name="determine-ifType">
                            <xsl:with-param name="ifType" select="$ifType"/>
                        </xsl:call-template>
                    </value>
                </parameter>
                <parameter>
                    <name>ifSpeed</name>
                    <value><xsl:value-of select="$ifSped"/></value>
                </parameter>
                <parameter>
                    <name>ifAdminStatus</name>
                    <value>
                        <xsl:value-of select="$ifAdminStatus"/>
                    </value>
                </parameter>
                <parameter>
                    <name>ifOperStatus</name>
                    <value>
                        <xsl:value-of select="$ifOperStatus"/>
                    </value>
                </parameter>
                <parameter>
                    <name>ifPhysAddress</name>
                    <value>
                        <xsl:value-of select="ifPhysAddress"/>
                    </value>
                </parameter>
                <parameter>
                    <name>CableCut</name>
                    <value><xsl:value-of select="$cableCut"/></value>
                </parameter>
                <parameter>
                    <name>IPv4Forwarding</name>
                    <value>
                        <xsl:value-of select="$IPv4Forwarding"/>
                    </value>
                </parameter>
                <parameter>
                    <name>IPv6Forwarding</name>
                    <value>
                        <xsl:value-of select="$IPv6Forwarding"/>
                    </value>
                </parameter>
                <parameter>
                    <name>vrfForwarding</name>
                    <value>
                        <xsl:value-of select="$vrfForwarding"/>
                    </value>
                </parameter>
            </parameters>
    </xsl:template>

</xsl:stylesheet>