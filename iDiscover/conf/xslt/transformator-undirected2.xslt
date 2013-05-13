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
                xmlns:functx="http://www.functx.com">
    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
    <xsl:include href="utils.xslt"/>
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
                <key id="ipv6Forwarding" for="node" attr.name="ipv6Forwarding" attr.type="string"/>

                <key id="name" for="edge" attr.name="name" attr.type="string"/>
                <key id="method" for="edge" attr.name="method" attr.type="string"/>
                <key id="dataLink" for="edge" attr.name="dataLink" attr.type="string"/>
                <key id="ipLink" for="edge" attr.name="ipLink" attr.type="string"/>
                <key id="MPLS" for="edge" attr.name="MPLS" attr.type="string"/>
                <key id="ipv6Forwarding" for="edge" attr.name="ipv6Forwarding" attr.type="string"/>
                <key id="localInterfaceName" for="edge" attr.name="localInterfaceName" attr.type="string"/>
                <key id="remoteInterfaceName" for="edge" attr.name="remoteInterfaceName" attr.type="string"/>
                <key id="localIPAddress" for="edge" attr.name="localIPAddress" attr.type="string"/>
                <key id="remoteIPAddress" for="edge" attr.name="remoteIPAddress" attr.type="string"/>
                <key id="edgeTooltip" for="edge" attr.name="edgeTooltip" attr.type="string"/>
                <key id="diff" for="edge" attr.name="diff" attr.type="string"/>
                <key id="diffs" for="edge" attr.name="diffs" attr.type="string"/>

                <xsl:variable name="nodeID">
                    <xsl:value-of select="/DiscoveredDevice/name"/>
                </xsl:variable>
                <xsl:variable name="root" select="/DiscoveredDevice"/>
                <xsl:variable name="deviceModel">
                    <xsl:value-of select="//DiscoveredDevice/parameters/parameter[name='Device Model']/value"/>
                </xsl:variable>
                <xsl:variable name="deviceType">
                    <xsl:value-of select="//DiscoveredDevice/parameters/parameter[name='Device Type']/value"/>
                </xsl:variable>
                <xsl:variable name="deviceStatus"
                              select="//DiscoveredDevice/parameters/parameter[name='Device State']/value"/>
                <xsl:variable name="siteID">
                    <xsl:value-of select="//DiscoveredDevice/parameters/parameter[name='siteID']/value"/>
                </xsl:variable>
                <xsl:variable name="ManagementIPAddress">
                    <xsl:value-of select="//DiscoveredDevice/parameters/parameter[name='Management IP Address']/value"/>
                </xsl:variable>
                <xsl:variable name="BGPLocalASInfo">
                    <xsl:value-of select="//DiscoveredDevice/parameters/parameter[name='BGPLocalASInfo']/value"/>
                </xsl:variable>
                <xsl:variable name="X" select="//DiscoveredDevice/parameters/parameter[name='X Coordinate']/value"/>
                <xsl:variable name="Y" select="//DiscoveredDevice/parameters/parameter[name='Y Coordinate']/value"/>
                <xsl:variable name="ipv6Forwarding"
                              select="//DiscoveredDevice/parameters/parameter[name='ipv6Forwarding']/value"/>
                <!--Prepare Central Node-->
                <node>
                    <xsl:attribute name="id">
                        <xsl:value-of select="$nodeID"/>
                    </xsl:attribute>
                    <data key="hostname">
                        <xsl:value-of select="$nodeID"/>
                    </data>
                    <data key="deviceModel">
                        <xsl:value-of select="$deviceModel"/>
                    </data>
                    <data key="deviceType">
                        <xsl:value-of select="$deviceType"/>
                    </data>
                    <data key="deviceStatus">
                        <xsl:value-of select="$deviceStatus"/>
                    </data>
                    <data key="ManagementIPAddress">
                        <xsl:value-of select="$ManagementIPAddress"/>
                    </data>
                    <data key="site">
                        <xsl:value-of select="$siteID"/>
                    </data>
                    <data key="geoCoordinates">
                        <xsl:value-of select="$Y"/>,<xsl:value-of select="$X"/>
                    </data>
                    <data key="ipv6Forwarding">
                        <xsl:value-of select="$ipv6Forwarding"/>
                    </data>
                    <data key="nodeInfo" diffignore="YES">
                        <xsl:text disable-output-escaping="yes">&lt;![CDATA[ &lt;html&gt;</xsl:text>
                        <xsl:text disable-output-escaping="yes">&lt;b&gt;Type: &lt;/b&gt;</xsl:text>
                        <xsl:value-of select="$deviceType"/>
                        <xsl:text disable-output-escaping="yes"> &lt;b&gt;Model:&lt;/b&gt; </xsl:text>
                        <xsl:value-of select="$deviceModel"/>
                        <xsl:text disable-output-escaping="yes"> &lt;b&gt;Site:&lt;/b&gt; </xsl:text>
                        <xsl:value-of select="$siteID"/>
                        <xsl:text
                                disable-output-escaping="yes">&lt;br&gt;&lt;b&gt;Mgmt IP address:&lt;/b&gt; </xsl:text>
                        <xsl:value-of select="$ManagementIPAddress"/>
                        <xsl:text disable-output-escaping="yes">&lt;br/&gt;</xsl:text>
                        <xsl:text disable-output-escaping="yes">&lt;b&gt;ipv6Forwarding:&lt;/b&gt; </xsl:text>
                        <xsl:value-of select="$ipv6Forwarding"/>
                        <xsl:text disable-output-escaping="yes">&lt;br/&gt;</xsl:text>
                        <xsl:text disable-output-escaping="yes">&lt;b&gt;BGPLocalASInfo:&lt;/b&gt; </xsl:text>
                        <xsl:value-of select="$BGPLocalASInfo"/>
                        <xsl:text disable-output-escaping="yes">&lt;/html&gt;]]&gt;</xsl:text>
                    </data>
                </node>
                <xsl:variable name="nodeIDs"
                              select="distinct-values($root//object[objectType='Discovery Interface']/object[objectType='Discovered Neighbor']/name)"/>
                <xsl:for-each select="$nodeIDs">
                    <xsl:variable name="neighID">
                        <xsl:value-of select="."/>
                    </xsl:variable>
                    <xsl:if test="($neighID !=$nodeID and $neighID!='')">
                        <node>
                            <xsl:attribute name="id">
                                <xsl:value-of select="$neighID"/>
                            </xsl:attribute>
                        </node>
                    </xsl:if>
                </xsl:for-each>

                <xsl:variable name="edges">
                    <xsl:for-each select="$root//object[objectType='Discovery Interface']">
                        <xsl:variable name="interface">
                            <xsl:value-of select="name"/>
                        </xsl:variable>
                        <xsl:variable name="remoteInterfaces">
                            <xsl:value-of
                                    select="distinct-values($root//object[name=$interface]/object[objectType='Discovered Neighbor']/parameters/parameter[name='Neighbor Port']/value)"/>
                        </xsl:variable>
                        <xsl:variable name="localIPaddress">
                            <xsl:value-of
                                    select="distinct-values($root//object[name=$interface]/object[objectType='IPv4 Address']/parameters/parameter[name='IPv4Address']/value)"/>
                        </xsl:variable>
                        <xsl:variable name="neighborIDs">
                            <xsl:value-of
                                    select="distinct-values($root//object[name=$interface]/object[objectType='Discovered Neighbor']/name)"/>
                        </xsl:variable>
                        <xsl:variable name="methods_all">
                            <xsl:for-each
                                    select="distinct-values($root//object[name=$interface]/object[objectType='Discovered Neighbor']/parameters/parameter[name='Discovery Method']/value)">
                                <xsl:value-of select="."/>
                                <xsl:text>,</xsl:text>
                            </xsl:for-each>
                            <xsl:for-each select="$neighborIDs">
                                <xsl:variable name="neighID" select="."/>
                                <xsl:for-each
                                        select="distinct-values($root//object[objectType='DeviceLogicalData']/object[name=$neighID]/parameters/parameter[name='Discovery Method']/value)">
                                    <xsl:value-of select="."/>
                                    <xsl:text>,</xsl:text>
                                </xsl:for-each>
                            </xsl:for-each>
                        </xsl:variable>
                        <xsl:variable name="neighborCount"
                                      select="count(distinct-values($root//object[name=$interface]/object[objectType='Discovered Neighbor']/name))">
                        </xsl:variable>
                        <xsl:variable name="node">
                            <xsl:choose>
                                <xsl:when test="$neighborCount>1">
                                    <xsl:value-of
                                            select="object[objectType='IPv4 Address']/parameters/parameter[name='ipv4Subnet']/value"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$nodeID"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:choose>
                            <xsl:when test="$neighborCount>1">
                                <node>
                                    <xsl:attribute name="id">
                                        <xsl:value-of select="$node"/>
                                    </xsl:attribute>
                                    <data key="deviceStatus">passive</data>
                                    <data key="nodeInfo">
                                        <xsl:value-of select="$node"/>
                                    </data>
                                    <data key="deviceModel">passiveHub</data>

                                </node>
                                <xsl:call-template name="neighbor">
                                    <xsl:with-param name="localInterface" select="$interface"/>
                                    <xsl:with-param name="remoteInterface"/>
                                    <xsl:with-param name="nodeID" select="$nodeID"/>
                                    <xsl:with-param name="neighID" select="$node"/>
                                    <xsl:with-param name="localIP"></xsl:with-param>
                                    <xsl:with-param name="remoteIP"></xsl:with-param>
                                    <xsl:with-param name="methods" select="$methods_all"/>
                                </xsl:call-template>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:variable name="cdp_check">
                                    <xsl:choose>
                                        <xsl:when test="contains($methods_all,'CDP')">YES</xsl:when>
                                        <xsl:otherwise>NO</xsl:otherwise>
                                    </xsl:choose>
                                </xsl:variable>
                                <xsl:variable name="lldp_check">
                                    <xsl:choose>
                                        <xsl:when test="contains($methods_all,'LLDP')">YES</xsl:when>
                                        <xsl:otherwise>NO</xsl:otherwise>
                                    </xsl:choose>
                                </xsl:variable>
                                <xsl:variable name="SLASH30">
                                    <xsl:choose>
                                        <xsl:when test="contains($methods_all,'Slash30')">YES</xsl:when>
                                        <xsl:otherwise>NO</xsl:otherwise>
                                    </xsl:choose>
                                </xsl:variable>
                                <xsl:variable name="SLASH31">
                                    <xsl:choose>
                                        <xsl:when test="contains($methods_all,'Slash31')">YES</xsl:when>
                                        <xsl:otherwise>NO</xsl:otherwise>
                                    </xsl:choose>
                                </xsl:variable>
                                <xsl:variable name="next_hop">
                                    <xsl:choose>
                                        <xsl:when test="contains($methods_all,'r_')">YES</xsl:when>
                                        <xsl:otherwise>NO</xsl:otherwise>
                                    </xsl:choose>
                                </xsl:variable>
                                <xsl:variable name="c_next_hop">
                                    <xsl:choose>
                                        <xsl:when test="contains($methods_all,'c_')">YES</xsl:when>
                                        <xsl:otherwise>NO</xsl:otherwise>
                                    </xsl:choose>
                                </xsl:variable>
                                <xsl:variable name="MAC">
                                    <xsl:choose>
                                        <xsl:when test="contains($methods_all,'MAC')">YES</xsl:when>
                                        <xsl:otherwise>NO</xsl:otherwise>
                                    </xsl:choose>
                                </xsl:variable>
                                <xsl:variable name="ARP">
                                    <xsl:choose>
                                        <xsl:when test="contains($methods_all,'ARP')">YES</xsl:when>
                                        <xsl:otherwise>NO</xsl:otherwise>
                                    </xsl:choose>
                                </xsl:variable>
                                <!--methods>
                                    <xsl:value-of select="$methods_all"/>
                                </methods-->
                                <!--flags>
                                    <cdp><xsl:value-of select="$cdp_check"/></cdp>
                                </flags-->
                                <xsl:choose>
                                    <xsl:when test="$cdp_check='YES' or $lldp_check = 'YES'">
                                        <xsl:for-each select="object[objectType='Discovered Neighbor']/name">
                                            <xsl:call-template name="neighbor">
                                                <xsl:with-param name="localInterface" select="$interface"/>
                                                <xsl:with-param name="remoteInterface"
                                                                select="../parameters/parameter[name='Neighbor Port']/value"/>
                                                <xsl:with-param name="nodeID" select="$node"/>
                                                <xsl:with-param name="neighID" select="."/>
                                                <xsl:with-param name="localIP"
                                                                select="../../object[objectType='IPv4 Address']/parameters/parameter[name='IPv4Address']/value"/>
                                                <xsl:with-param name="remoteIP"
                                                                select="../parameters/parameter[name='Neighbor IP Address']/value"/>
                                                <xsl:with-param name="methods" select="$methods_all"/>
                                            </xsl:call-template>
                                        </xsl:for-each>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:choose>
                                            <xsl:when test="$SLASH30='YES' or $SLASH31='YES'">
                                                <xsl:for-each select="object[objectType='Discovered Neighbor']/name">
                                                    <xsl:variable name="neighID">
                                                        <xsl:value-of select="."/>
                                                    </xsl:variable>
                                                    <xsl:call-template name="neighbor">
                                                        <xsl:with-param name="localInterface" select="$interface"/>
                                                        <xsl:with-param name="remoteInterface"/>
                                                        <xsl:with-param name="nodeID" select="$node"/>
                                                        <xsl:with-param name="neighID" select="$neighID"/>
                                                        <xsl:with-param name="localIP"
                                                                        select="../../object[objectType='IPv4 Address']/parameters/parameter[name='IPv4Address']/value"/>
                                                        <xsl:with-param name="remoteIP"
                                                                        select="../parameters/parameter[name='Neighbor IP Address']/value"/>
                                                        <xsl:with-param name="methods" select="$methods_all"/>
                                                    </xsl:call-template>
                                                </xsl:for-each>
                                            </xsl:when>
                                            <xsl:otherwise>
                                                <xsl:choose>
                                                    <xsl:when test="$next_hop='YES' or $c_next_hop ='YES'">
                                                        <!--xsl:for-each select="$root//object[name=$interface]/object[objectType='Discovered Neighbor' and parameters/parameter[name='Discovery Method' and (contains(value, 'NEXT') or contains(value,'c_NEXT') or contains(value,'ARP') or contains(value,'MAC'))]]/name"-->
                                                        <xsl:for-each
                                                                select="$root//object[name=$interface]/object[objectType='Discovered Neighbor' and parameters/parameter[name='Discovery Method' and (contains(value, 'NEXT') or contains(value,'c_'))]]/name">
                                                            <xsl:variable name="neighID">
                                                                <xsl:value-of select="."/>
                                                            </xsl:variable>
                                                            <xsl:call-template name="neighbor">
                                                                <xsl:with-param name="localInterface"
                                                                                select="$interface"/>
                                                                <xsl:with-param name="remoteInterface"/>
                                                                <xsl:with-param name="nodeID" select="$node"/>
                                                                <xsl:with-param name="neighID" select="$neighID"/>
                                                                <xsl:with-param name="localIP">
                                                                    <xsl:value-of
                                                                            select="../../object[objectType='IPv4 Address']/parameters/parameter[name='IPv4Address']/value"/>
                                                                </xsl:with-param>
                                                                <xsl:with-param name="remoteIP"
                                                                                select="../parameters/parameter[name='Neighbor IP Address']/value"/>
                                                                <xsl:with-param name="methods" select="$methods_all"/>
                                                            </xsl:call-template>
                                                        </xsl:for-each>
                                                    </xsl:when>
                                                    <xsl:otherwise>
                                                        <xsl:for-each
                                                                select="$root//object[name=$interface]/object[objectType='Discovered Neighbor' and parameters/parameter[name='Discovery Method'  and contains(value,'MAC')]]/name">
                                                            <xsl:variable name="neighID">
                                                                <xsl:value-of select="."/>
                                                            </xsl:variable>
                                                            <xsl:call-template name="neighbor">
                                                                <xsl:with-param name="localInterface"
                                                                                select="$interface"/>
                                                                <xsl:with-param name="remoteInterface"/>
                                                                <xsl:with-param name="nodeID" select="$node"/>
                                                                <xsl:with-param name="neighID" select="$neighID"/>
                                                                <xsl:with-param name="localIP"
                                                                                select="../../object[objectType='IPv4 Address']/parameters/parameter[name='IPv4Address']/value"/>
                                                                <xsl:with-param name="remoteIP"
                                                                                select="../parameters/parameter[name='Neighbor IP Address']/value"/>
                                                                <xsl:with-param name="methods" select="$methods_all"/>
                                                            </xsl:call-template>
                                                        </xsl:for-each>
                                                    </xsl:otherwise>
                                                </xsl:choose>
                                            </xsl:otherwise>
                                        </xsl:choose>
                                    </xsl:otherwise>

                                </xsl:choose>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:for-each>

                </xsl:variable>
                <!--xsl:copy-of select="$edges" /-->
                <xsl:for-each select="distinct-values($edges//node/@id)">
                    <xsl:variable name="node-id" select="."/>
                    <xsl:copy-of select="$edges//node[@id=$node-id][1]"/>
                </xsl:for-each>
                <xsl:for-each select="distinct-values($edges//edge/@id)">
                    <xsl:variable name="edge-id" select="."/>
                    <xsl:copy-of select="$edges//edge[@id=$edge-id][1]"/>
                </xsl:for-each>
            </graph>
        </graphml>
    </xsl:template>
    <xsl:template match="test">
        <xsl:value-of select="."/>
        <xsl:text> </xsl:text>
    </xsl:template>
    <xsl:template name="neighbor">
        <xsl:param name="nodeID"/>
        <xsl:param name="neighID"/>
        <xsl:param name="localInterface"/>
        <xsl:param name="remoteInterface"/>
        <xsl:param name="localIP"/>
        <xsl:param name="remoteIP"/>
        <xsl:param name="methods"/>
        <xsl:variable name="sort">
            <root>
                <test>
                    <node>
                        <xsl:value-of select="$nodeID"/>
                    </node>
                </test>
                <test>
                    <node>
                        <xsl:value-of select="$neighID"/>
                    </node>
                </test>
            </root>
        </xsl:variable>
        <edge>
            <xsl:attribute name="id">
                <xsl:choose>
                    <xsl:when test="$localInterface!='' and $remoteInterface!=''">
                        <xsl:variable name="sort2">
                            <root>
                                <test>
                                    <node>
                                        <xsl:value-of select="$localInterface"/>
                                    </node>
                                </test>
                                <test>
                                    <node>
                                        <xsl:value-of select="$remoteInterface"/>
                                    </node>
                                </test>
                            </root>
                        </xsl:variable>
                        <xsl:apply-templates select="$sort//root/test">
                            <xsl:sort select="node"/>
                        </xsl:apply-templates>
                        -
                        <xsl:apply-templates select="$sort2//root/test">
                            <xsl:sort select="node"/>
                        </xsl:apply-templates>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:choose>
                            <xsl:when test="$localIP!='' and $remoteIP!=''">
                                <xsl:variable name="sort2">
                                    <root>
                                        <test>
                                            <node>
                                                <xsl:value-of select="$localIP"/>
                                            </node>
                                        </test>
                                        <test>
                                            <node>
                                                <xsl:value-of select="$remoteIP"/>
                                            </node>
                                        </test>
                                    </root>
                                </xsl:variable>
                                <xsl:apply-templates select="$sort//root/test">
                                    <xsl:sort select="node"/>
                                </xsl:apply-templates>
                                -
                                <xsl:apply-templates select="$sort2//root/test">
                                    <xsl:sort select="node"/>
                                </xsl:apply-templates>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:apply-templates select="$sort//root/test">
                                    <xsl:sort select="node"/>
                                </xsl:apply-templates>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:attribute name="source">
                <xsl:value-of select="$nodeID"/>
            </xsl:attribute>
            <xsl:attribute name="target">
                <xsl:value-of select="$neighID"/>
            </xsl:attribute>
            <data>
                <xsl:attribute name="key">edgeTooltip</xsl:attribute>
                <xsl:text disable-output-escaping="yes">&lt;p&gt;&lt;b&gt;</xsl:text><xsl:value-of select="$nodeID"/><xsl:text
                    disable-output-escaping="yes">&lt;/b&gt; </xsl:text>
                <xsl:value-of select="$localInterface"/><xsl:text
                    disable-output-escaping="yes">&lt;b&gt;&lt;/b&gt; ( </xsl:text><xsl:value-of select="$localIP"/> )
                <xsl:text>| </xsl:text><xsl:value-of select="functx:substring-before-last-match($methods,',')"/>
                <xsl:text disable-output-escaping="yes">&lt;/p&gt; </xsl:text>
            </data>
            <data>
                <xsl:attribute name="key">localInterfaceName</xsl:attribute>
                <xsl:value-of select="$localInterface"/>
            </data>
            <data>
                <xsl:attribute name="key">remoteInterfaceName</xsl:attribute>
                <xsl:value-of select="$remoteInterface"/>
            </data>
            <data>
                <xsl:attribute name="key">localIPAddress</xsl:attribute>
                <xsl:value-of select="$localIP"/>
            </data>
            <data>
                <xsl:attribute name="key">remoteIPAddress</xsl:attribute>
                <xsl:value-of select="$remoteIP"/>
            </data>
            <data>
                <xsl:attribute name="key">method</xsl:attribute>
                <xsl:value-of select="distinct-values($methods)"/>
            </data>
            <data>
                <xsl:attribute name="key">ipLink</xsl:attribute>
                <xsl:choose>
                    <xsl:when test="$localIP!=''">YES</xsl:when>
                    <xsl:otherwise>NO</xsl:otherwise>
                </xsl:choose>
            </data>
            <data>
                <xsl:attribute name="key">dataLink</xsl:attribute>
                <xsl:choose>
                    <xsl:when test="$remoteInterface!=''">YES</xsl:when>
                    <xsl:otherwise>
                        <xsl:choose>
                            <xsl:when test="contains($methods,'MAC')">YES</xsl:when>
                            <xsl:otherwise>NO</xsl:otherwise>
                        </xsl:choose>
                    </xsl:otherwise>
                </xsl:choose>
            </data>

        </edge>
    </xsl:template>
    <xsl:function name="functx:is-node-in-sequence" as="xs:boolean" xmlns:functx="http://www.functx.com">
        <xsl:param name="node" as="node()?"/>
        <xsl:param name="seq" as="node()*"/>
        <xsl:sequence select="some $nodeInSeq in $seq satisfies $nodeInSeq is $node"/>
    </xsl:function>
    <xsl:function name="functx:distinct-nodes" as="node()*" xmlns:functx="http://www.functx.com">
        <xsl:param name="nodes" as="node()*"/>
        <xsl:sequence
                select=" for $seq in (1 to count($nodes))return $nodes[$seq][not(functx:is-node-in-sequence(.,$nodes[position() &lt; $seq]))]"/>
    </xsl:function>
</xsl:stylesheet>
