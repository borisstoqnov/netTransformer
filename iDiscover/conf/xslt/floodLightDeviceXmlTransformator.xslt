
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:SnmpForXslt="net.itransformers.idiscover.discoveryhelpers.xml.SnmpForXslt">
    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
    <xsl:include href="utils.xslt"/>
    <xsl:include href="discovery-methods.xslt"/>
    <xsl:param name="community-ro"/>
    <xsl:param name="community-rw"/>


    <xsl:template match="/">
        <!--xsl:for-each select="//root/devices/e">
            <xsl:variable name="switchDPID" select="attachmentPoint/e/switchDPID"/>
            <xsl:variable name="ipv4Address" select="ipv4/e"/>
            <xsl:variable name="ipv6Address" select="ipv6/e"/>
            <xsl:variable name="mac" select="mac/e"/>
        </xsl:for-each-->
        <!--xsl:variable name="Devices"-->
            <DiscoveredDevices>
                <xsl:for-each select="//root/switches/e">
            <DiscoveredDevice>
                <name>
                    <xsl:call-template name="return-hostname">
                        <xsl:with-param name="hostname-unformated"
                                        select="SnmpForXslt:getName(attributes/inetAddress, $comm)"/>
                    </xsl:call-template>

                </name>


                <!-- Node specific parameters-->
                <parameters>

                    <parameter>
                        <name>Device State</name>
                        <value>discovered</value>
                    </parameter>
                    <parameter>
                        <name>DPID</name>
                        <value><xsl:value-of select="dpid"/></value>
                    </parameter>
                    <parameter>
                        <name>datapathDescription</name>
                        <value>
                            <xsl:value-of select="attributes/DescriptionData/datapathDescription"/>
                        </value>
                    </parameter>
                    <parameter>
                        <name>hardwareDescription</name>
                        <value>
                            <xsl:value-of select="attributes/DescriptionData/hardwareDescription"/>
                        </value>
                    </parameter>
                    <parameter>
                        <name>deviceType</name>
                        <value>
                            <xsl:value-of select="attributes/DescriptionData/manufacturerDescription"/>
                        </value>
                    </parameter>
                    <parameter>
                        <name>serialNumber</name>
                        <value>
                            <xsl:value-of select="attributes/DescriptionData/serialNumber"/>
                        </value>
                    </parameter>
                    <parameter>
                        <name>softwareDescription</name>
                        <value>
                            <xsl:value-of select="attributes/DescriptionData/softwareDescription"/>
                        </value>
                    </parameter>
                    <parameter>
                        <name>capabilities</name>
                        <value>
                            <xsl:value-of select="attributes/capabilities"/>
                        </value>
                    </parameter>
                    <parameter>
                        <name>inetAddress</name>
                        <value>
                            <xsl:value-of select="attributes/inetAddress"/>
                        </value>
                    </parameter>
                    </parameters>
                    <xsl:for-each select="ports/e">
                        <object>
                            <name>
                                <xsl:value-of select="name"/>
                            </name>
                            <objectType>Discovery Interface</objectType>
                            <parameters>
                                <parameter>
                                    <name>hardwareAddress</name>
                                    <value>
                                        <xsl:value-of select="hardwareAddress"/>
                                    </value>
                                </parameter>
                                <parameter>
                                    <name>portNumber</name>
                                    <value>
                                        <xsl:value-of select="portNumber"/>
                                    </value>
                                </parameter>
                            </parameters>
                        </object>
                    </xsl:for-each>
            </DiscoveredDevice>
        </xsl:for-each>
            </DiscoveredDevices>
        <!--/xsl:variable-->


        <!--xsl:for-each-group select="$Devices/DiscoveredDevices/DiscoveredDevice" group-by="name">
            <xsl:result-document href="network/node-data-{name}.xml">
                    <xsl:copy-of select="current-group()"/>
            </xsl:result-document>
        </xsl:for-each-group-->
    </xsl:template>

</xsl:stylesheet>