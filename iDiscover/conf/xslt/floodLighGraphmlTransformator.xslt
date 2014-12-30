<?xml version="1.0" encoding="UTF-8"?>

<!--
  ~ iTransformer is an open source tool able to discover and transform
  ~  IP network infrastructures.
  ~  Copyright (C) 2012  http://itransformers.net
  ~
  ~  This program is free software: you can redistribute it and/or modify
  ~  it under the terms of the GNU General Public License as published by
  ~  the Free Software Foundation, either version 3 of the License, or
  ~  any later version.
  ~
  ~  This program is distributed in the hope that it will be useful,
  ~  but WITHOUT ANY WARRANTY; without even the implied warranty of
  ~  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  ~  GNU General Public License for more details.
  ~
  ~  You should have received a copy of the GNU General Public License
  ~  along with this program.  If not, see <http://www.gnu.org/licenses/>.
  -->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions"
                xmlns:functx="http://www.functx.com" xmlns:SnmpForXslt="net.itransformers.idiscover.discoveryhelpers.xml.SnmpForXslt">
    <xsl:include href="utils.xslt"/>
    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
    <xsl:include href="discovery-methods.xslt"/>
    <xsl:param name="community-ro"/>
    <xsl:param name="community-rw"/>


    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>

    <xsl:template match="/">
        <graphml>
            <graph edgedefault="undirected">
                <!-- data schema -->
                <key id="hostname" for="node" attr.name="hostname" attr.type="string"/>
                <key id="deviceModel" for="node" attr.name="deviceModel" attr.type="string"/>
                <key id="deviceType" for="node" attr.name="deviceType" attr.type="string"/>
                <key id="nodeInfo" for="node" attr.name="nodeInfo" attr.type="string"/>
                <key id="deviceStatus" for="node" attr.name="deviceStatus" attr.type="string"/>
                <key id="ManagementIPAddress" for="node" attr.name="ManagementIPAddress" attr.type="string"/>
                <key id="geoCoordinates" for="node" attr.name="geoCoordinates" attr.type="string"/>
                <key id="site" for="node" attr.name="site" attr.type="string"/>
                <key id="diff" for="node" attr.name="diff" attr.type="string"/>
                <key id="diffs" for="node" attr.name="diffs" attr.type="string"/>
                <key id="ipv4Forwarding" for="node" attr.name="ipv4Forwarding" attr.type="string"/>

                <key id="ipv6Forwarding" for="node" attr.name="ipv6Forwarding" attr.type="string"/>
                <key id="name" for="edge" attr.name="name" attr.type="string"/>
                <key id="method" for="edge" attr.name="method" attr.type="string"/>
                <key id="dataLink" for="edge" attr.name="dataLink" attr.type="string"/>
                <key id="ipLink" for="edge" attr.name="ipLink" attr.type="string"/>
                <key id="MPLS" for="edge" attr.name="MPLS" attr.type="string"/>
                <key id="ipv4Forwarding" for="edge" attr.name="ipv4Forwarding" attr.type="string"/>

                <key id="ipv6Forwarding" for="edge" attr.name="ipv6Forwarding" attr.type="string"/>
                <key id="localInterfaceName" for="edge" attr.name="localInterfaceName" attr.type="string"/>
                <key id="remoteInterfaceName" for="edge" attr.name="remoteInterfaceName" attr.type="string"/>
                <key id="localIPv4Address" for="edge" attr.name="localIPv4Address" attr.type="string"/>
                <key id="remoteIPv4Address" for="edge" attr.name="remoteIPv4Address" attr.type="string"/>
                <key id="localIPv6Address" for="edge" attr.name="localIPv6Address" attr.type="string"/>
                <key id="remoteIPv6Address" for="edge" attr.name="remoteIPv6Address" attr.type="string"/>

                <key id="edgeTooltip" for="edge" attr.name="edgeTooltip" attr.type="string"/>
                <key id="diff" for="edge" attr.name="diff" attr.type="string"/>
                <key id="diffs" for="edge" attr.name="diffs" attr.type="string"/>
                <key id="localPortState" for="edge" attr.name="localPortState"
                     attr.type="string"/>
                <key id="dstPortState" for="edge" attr.name="dstPortState"
                     attr.type="string"/>
                <key id="type" for="edge" attr.name="tyle"
                     attr.type="string"/>
                <xsl:variable name="comm" select="$community-ro"/>
                <xsl:variable name="comm2" select="$community-rw"/>

                <xsl:for-each select="/root/switches/e">
                    <xsl:variable name="hostnameFromSnmp" select="SnmpForXslt:getName(attributes/inetAddress, $comm)"/>
                    <node>
                        <xsl:attribute name="id">
                           <xsl:choose>
                            <xsl:when test="$hostnameFromSnmp!=''"><xsl:value-of select="$hostnameFromSnmp"/></xsl:when>
                            <xsl:otherwise><xsl:value-of select="dpid"/></xsl:otherwise>
                           </xsl:choose>
                        </xsl:attribute>
                        <data key="hostname">
                            <xsl:value-of select="$hostnameFromSnmp"/>
                        </data>
                        <data key="deviceModel">
                            <xsl:value-of select="attributes/DescriptionData/hardwareDescription"/>
                        </data>
                        <data key="deviceType">
                            <xsl:value-of select="attributes/DescriptionData/manufacturerDescription"/>
                        </data>
                        <data key="deviceStatus">discovered</data>
                        <data key="ManagementIPAddress">
                            <xsl:value-of select="inetAddress"/>
                        </data>
                        <data key="nodeInfo" diffignore="YES">
                            <xsl:text disable-output-escaping="yes">&lt;![CDATA[ &lt;html&gt;</xsl:text>
                            <xsl:text disable-output-escaping="yes">&lt;b&gt;Type: &lt;/b&gt;</xsl:text>
                            <xsl:value-of select="attributes/DescriptionData/manufacturerDescription"/>
                            <xsl:text disable-output-escaping="yes"> &lt;b&gt;Model:&lt;/b&gt; </xsl:text>
                            <xsl:value-of select="attributes/DescriptionData/hardwareDescription"/>
                            <xsl:text disable-output-escaping="yes">&lt;br&gt;&lt;b&gt;Mgmt IP address:&lt;/b&gt; </xsl:text>
                            <xsl:value-of select="inetAddress"/>
                            <xsl:text disable-output-escaping="yes">&lt;br/&gt;</xsl:text>
                            <xsl:text disable-output-escaping="yes">&lt;/html&gt;]]&gt;</xsl:text>
                        </data>
                    </node>
                </xsl:for-each>

                <xsl:for-each select="/root/devices/e">
                    <xsl:if test="mac!=''">
                        <xsl:variable name="ipv4Forwarding">
                            <xsl:choose>
                                <xsl:when test="ipv4/e!=''">YES</xsl:when>
                                <xsl:otherwise>NO</xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:variable name="ipv6Forwarding">
                            <xsl:choose>
                                <xsl:when test="ipv6/e!=''">YES</xsl:when>
                                <xsl:otherwise>NO</xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                    <node>
                        <xsl:attribute name="id">
                            <xsl:value-of select="mac"/>
                        </xsl:attribute>

                        <data key="deviceModel">SDN host</data>
                        <data key="deviceType">SDN host</data>
                        <data key="deviceStatus">discovered</data>
                        <data key="ManagementIPv4Address">
                            <xsl:value-of select="ipv4/e"/>
                        </data>
                        <data key="ManagementIPv6Address">
                            <xsl:value-of select="ipv6/e"/>
                        </data>
                        <data key="ipv6Forwarding">
                            <xsl:value-of select="$ipv6Forwarding"/>

                        </data>
                        <data key="ipv4Forwarding">
                            <xsl:value-of select="$ipv4Forwarding"/>
                        </data>
                        <data key="nodeInfo" diffignore="YES">
                            <xsl:text disable-output-escaping="yes">&lt;![CDATA[ &lt;html&gt;</xsl:text>
                            <xsl:text disable-output-escaping="yes">&lt;b&gt;Type: &lt;/b&gt;</xsl:text>
                            <xsl:text>SDN host</xsl:text>
                            <xsl:text disable-output-escaping="yes"> &lt;b&gt;Model:&lt;/b&gt; </xsl:text>
                            <xsl:value-of select="unknown"/>
                            <xsl:text disable-output-escaping="yes">&lt;br&gt;&lt;b&gt;Mgmt IP address:&lt;/b&gt; </xsl:text>
                            <xsl:value-of select="ipv4/e"/>
                            <xsl:text disable-output-escaping="yes">&lt;br/&gt;</xsl:text>
                            <xsl:text disable-output-escaping="yes">&lt;b&gt;ipv4Forwarding:&lt;/b&gt; </xsl:text>
                            <xsl:value-of select="$ipv4Forwarding"/>
                            <xsl:text disable-output-escaping="yes">&lt;br/&gt;</xsl:text>
                            <xsl:text disable-output-escaping="yes">&lt;b&gt;ipv6Forwarding:&lt;/b&gt; </xsl:text>
                            <xsl:value-of select="$ipv6Forwarding"/>
                            <xsl:text disable-output-escaping="yes">&lt;br/&gt;</xsl:text>
                            <xsl:text disable-output-escaping="yes">&lt;/html&gt;]]&gt;</xsl:text>
                        </data>
                    </node>
                    </xsl:if>
                </xsl:for-each>

                <xsl:for-each select="/root/links/e">
                    <xsl:variable name="edgeID"><xsl:value-of select="src-switch"/>-<xsl:value-of select="dst-switch"/>:<xsl:value-of
                            select="src-port"/>-<xsl:value-of select="dst-port"/>
                    </xsl:variable>
                    <edge>
                        <xsl:attribute name="id">
                            <xsl:value-of select="$edgeID"/>
                        </xsl:attribute>
                        <xsl:attribute name="source">
                            <xsl:value-of select="src-switch"/>
                        </xsl:attribute>
                        <xsl:attribute name="target">
                            <xsl:value-of select="dst-switch"/>
                        </xsl:attribute>
                        <data key="edgeTooltip">
                            &lt;html&gt;
                                <xsl:value-of select="$edgeID"/>,
                            &lt;/html&gt;
                        </data>

                        <data key="localInterfaceName">
                            <xsl:value-of select="src-port"/>
                        </data>

                        <data key="remoteInterfaceName">
                            <xsl:value-of select="dst-port"/>
                        </data>

                        <data key="localPortState">
                            <xsl:value-of select="src-port-state"/>
                        </data>

                        <data key="dstPortState">
                            <xsl:value-of select="dst-port-state"/>
                        </data>

                        <data key="type">
                            <xsl:value-of select="type"/>
                        </data>

                        <data key="method">OpenFlow</data>

                    </edge>

                </xsl:for-each>
                <xsl:for-each select="/root/devices/e">
                    <xsl:if test="attachmentPoint/e/switchDPID!='' and mac/e!=''">
                    <xsl:variable name="edgeID"><xsl:value-of select="attachmentPoint/e/switchDPID"/>-<xsl:value-of
                            select="mac/e"/>:<xsl:value-of select="attachmentPoint/e/port"/>
                    </xsl:variable>
                    <edge>
                        <xsl:attribute name="id">
                            <xsl:value-of select="$edgeID"/>
                        </xsl:attribute>
                        <xsl:attribute name="source">
                            <xsl:value-of select="attachmentPoint/e/switchDPID"/>
                        </xsl:attribute>
                        <xsl:attribute name="target">
                            <xsl:value-of select="mac/e"/>
                        </xsl:attribute>
                        <data key="edgeTooltip">
                            &lt;html&gt;
                            <xsl:value-of select="$edgeID"/>,
                            &lt;/html&gt;
                        </data>
                        <data>
                            <xsl:attribute name="key">localInterfaceName</xsl:attribute>
                            <xsl:value-of select="attachmentPoint/e/port"/>
                        </data>
                        <data>
                            <xsl:attribute name="key">remoteIPv4Address</xsl:attribute>
                            <xsl:value-of select="ipv4/e"/>
                        </data>
                        <data>
                            <xsl:attribute name="key">remoteIPv6Address</xsl:attribute>
                            <xsl:value-of select="ipv6/e"/>
                        </data>
                        <data key="method">OpenFlow</data>
                    </edge>
                    </xsl:if>
                </xsl:for-each>
            </graph>
        </graphml>
    </xsl:template>

</xsl:stylesheet>