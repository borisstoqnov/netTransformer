<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ iMap is an open source tool able to upload Internet BGP peering information
  ~  and to visualize the beauty of Internet BGP Peering in 2D map.
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

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:IPv6formatConvertor="net.itransformers.idiscover.util.IPv6formatConvertor" xmlns:saxon="http://saxon.sf.net/"
                xmlns:math="http://exslt.org/math" extension-element-prefixes="saxon">
    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
    <xsl:param name="as-numbers"/>
    <xsl:variable name="ipv4counter" select="0" saxon:assignable="yes"/> <!-- declare value -->
    <xsl:variable name="ipv6counter" select="0" saxon:assignable="yes"/> <!-- declare value -->
    <xsl:variable name="ipv6flag" select="0" saxon:assignable="yes"/> <!-- declare value -->
    <xsl:variable name="ipv4flag" select="0" saxon:assignable="yes"/> <!-- declare value -->
    <xsl:variable name="asPathWeight" select="0" saxon:assignable="yes"/>
    <xsl:variable name="asCounter" select="0" saxon:assignable="yes"/>


    <xsl:template match="/">


        <xsl:variable name="root" select="/root"/>
        <xsl:message>DEBUG:<xsl:value-of select="$as-numbers"/></xsl:message>

        <graphml>
            <key id="diffs" for="node" attr.name="diffs" attr.type="string"/>
            <key id="diff" for="edge" attr.name="diff" attr.type="string"/>
            <key id="diff" for="node" attr.name="diff" attr.type="string"/>
            <key id="ASflag" for="node" attr.name="AS" attr.type="string"/>
            <key id="ASInfo" for="node" attr.name="ASInfo" attr.type="string"/>
            <key id="ASName" for="node" attr.name="ASInfo" attr.type="string"/>
            <key id="ASId" for="node" attr.name="ASN" attr.type="string"/>
            <key id="description" for="node" attr.name="description" attr.type="string"/>
            <key id="RoutePrefixes" for="node" attr.name="RoutePrefixes" attr.type="string"/>
            <key id="countOriginatedPrefixes" for="node" attr.name="countOriginatedPrefixes" attr.type="integer"/>
            <key id="countASTransitAppearances" for="node" attr.name="countASTransitAppearances" attr.type="integer"/>
            <key id="transit" for="node" attr.name="transit" attr.type="string"/>
            <key id="edgeID" for="edge" attr.name="edgeID" attr.type="string"/>
            <key id="edge" for="edge" attr.name="edge" attr.type="string"/>
            <key id="prefix" for="edge" attr.name="prefix" attr.type="string"/>
            <key id="color" for="edge" attr.name="color" attr.type="string"/>
            <key id="weigth" for="edge" attr.name="weigth" attr.type="string"/>
            <key id="IPv4AddressCount" for="node" attr.name="IPv4AddressCount" attr.type="string"/>
            <key id="IPv6AddressCount" for="node" attr.name="IPv6AddressCount" attr.type="string"/>
            <key id="IPv4Flag" for="node" attr.name="IPv4Flag" attr.type="string"/>
            <key id="IPv6Flag" for="node" attr.name="IPv6Flag" attr.type="string"/>

            <graph edgedefault="undirected">
                <key id="diffs" for="edge" attr.name="diffs" attr.type="string"/>



                <xsl:variable name="asNumberess" select="document($as-numbers)/root/AS"/>
                <xsl:variable name="asCount" select="count(distinct-values($root//tableDump/attributes/attribute[@type = 'ASPath']/AS))"/>
                <xsl:variable name="debug_asCount">AS Count <xsl:value-of select="$asCount"/> !</xsl:variable>
                <xsl:message>DEBUG:<xsl:value-of select="$debug_asCount"/></xsl:message>

                <xsl:variable name="prefixCount" select="count($root//tableDump)"/>
                <xsl:variable name="debug_prefixCount">Prefix Count <xsl:value-of select="$prefixCount"/>!</xsl:variable>
                <xsl:message>DEBUG:<xsl:value-of select="$debug_prefixCount"/></xsl:message>

                <xsl:for-each select="distinct-values($root//tableDump/attributes/attribute[@type = 'ASPath']/AS)">
                    <saxon:assign name="asCounter" select="$asCounter + 1"/>
                    <xsl:variable name="AS" select="."/>
                    <xsl:variable name="debug_AS"><xsl:text>Starting to work on ASN: </xsl:text><xsl:value-of select="$AS"/><xsl:text> which is </xsl:text><xsl:value-of select="$asCounter"/><xsl:text> out of </xsl:text><xsl:value-of select="$asCount"/><xsl:text>!</xsl:text></xsl:variable>
                    <xsl:message>DEBUG: <xsl:value-of select="$debug_AS"/></xsl:message>

                    <!--<xsl:variable name="asCount" select="count($root//tableDump/attributes/attribute[@type = 'ASPath' and AS=$AS])"/>-->
                    <xsl:variable name="description" select="$asNumberess[number=concat('AS',$AS)]/description"/>
                    <!--<xsl:variable name="countOriginatedPrefixes" select="count($root//tableDump/attributes/attribute[@type = 'ASPath']/AS[@last='true' and . = $AS])"/>-->
                    <xsl:variable name="countOriginatedPrefixes" select="count($root//tableDump/attributes/attribute[@type = 'ASPath']/AS[@last='true' and . = $AS])"/>
                    <xsl:variable name="debug_countOriginatedPrefixes">countOriginatedPrefixes  <xsl:value-of select="$countOriginatedPrefixes"/></xsl:variable>
                    <xsl:message>DEBUG: <xsl:value-of select="$debug_countOriginatedPrefixes"/></xsl:message>

                    <xsl:variable name="countASTransitAppearances" select="count($root//tableDump/attributes/attribute[@type = 'ASPath']/AS[ not(fn:exists(@last)) and . = $AS])"/>
                    <xsl:variable name="debug_countASTransitAppearances">countASTransitAppearances  <xsl:value-of select="$countASTransitAppearances"/></xsl:variable>
                    <xsl:message>DEBUG: <xsl:value-of select="$debug_countASTransitAppearances"/></xsl:message>

                    <xsl:variable name="node">
                        <node>
                            <xsl:attribute name="id"><xsl:value-of select="$AS"/></xsl:attribute>
                            <data key="ASflag">YES</data>
                            <data key="ASId">
                                <xsl:value-of select="$AS"/>
                            </data>
                            <data key="ASName">
                                <xsl:value-of select="$description"/>
                            </data>
                            <data key="countOriginatedPrefixes">
                                <xsl:value-of select="$countOriginatedPrefixes"/>
                            </data>
                            <data key="countASTransitAppearances">
                                <xsl:value-of select="$countASTransitAppearances"/>
                            </data>

                            <xsl:for-each select="distinct-values($root//tableDump/attributes/attribute[@type = 'ASPath']/AS[@last='true' and . = $AS]/../../../@prefix)">
                                <xsl:variable name="prefix" select="."/>
                                <xsl:variable name="subnetMask" select="fn:substring-after($prefix,'/')"/>
                                <xsl:choose>
                                    <xsl:when test="not(contains($prefix,'::'))">
                                        <xsl:variable name="IPv4AddressCount"><xsl:call-template name="prefixIPv4AddressSpaceCounter"><xsl:with-param name="subnetMask" select="xs:integer($subnetMask)"/></xsl:call-template></xsl:variable>
                                        <xsl:if test="$IPv4AddressCount!=''">
                                            <saxon:assign name="ipv4counter" select="$ipv4counter+$IPv4AddressCount"/>
                                            <xsl:variable name="debug_IPv4Prefix"><xsl:text>IPv4Prefix: </xsl:text><xsl:value-of select="$prefix"/><xsl:text> count: </xsl:text><xsl:value-of select="xs:integer($IPv4AddressCount)"/><xsl:text> counter: </xsl:text><xsl:value-of select="xs:integer($ipv4counter)"/></xsl:variable>
                                            <xsl:message>DEBUG: <xsl:value-of select="$debug_IPv4Prefix"/></xsl:message>
                                        </xsl:if>
                                        <saxon:assign name="ipv4flag" select="1"/>

                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:variable name="IPv6AddressCount"><xsl:call-template name="prefixIPv6AddressSpaceCounter"><xsl:with-param name="subnetMask" select="xs:integer($subnetMask)"/></xsl:call-template></xsl:variable>
                                        <xsl:if test="$IPv6AddressCount!=''">
                                            <saxon:assign name="ipv6counter" select="$ipv6counter+$IPv6AddressCount"/>
                                        </xsl:if>
                                        <saxon:assign name="ipv6flag" select="1"/>
                                        <xsl:variable name="debug_IPv6Prefix"><xsl:text>IPv6Prefix: </xsl:text><xsl:value-of select="$prefix"/><xsl:text> count: </xsl:text><xsl:value-of select="xs:integer($IPv6AddressCount)"/><xsl:text> counter:  </xsl:text><xsl:value-of select="xs:integer($ipv6counter)"/></xsl:variable>
                                        <xsl:message>DEBUG: <xsl:value-of select="$debug_IPv6Prefix"/></xsl:message>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </xsl:for-each>
                            <data key="IPv4AddressCount"><xsl:value-of select="xs:integer($ipv4counter)"/></data>
                            <data key="IPv6AddressCount"><xsl:value-of select="xs:integer($ipv6counter)"/></data>

                            <xsl:variable name="RoutePrefixes">

                                <xsl:for-each select="distinct-values($root//tableDump/attributes/attribute[@type = 'ASPath']/AS[@last='true' and . = $AS]/../../../@prefix)">
                                    &lt;prefix&gt;<xsl:value-of select="."/>&lt;/prefix&gt;
                                </xsl:for-each>

                            </xsl:variable>


                            <xsl:variable name="asInfo"><data key="ASInfo">
                                <xsl:text disable-output-escaping="yes">&lt;html&gt;</xsl:text>
                                <xsl:text disable-output-escaping="yes">&lt;b&gt;AS: &lt;/b&gt;</xsl:text>
                                <xsl:value-of select="."/>
                                <xsl:text disable-output-escaping="yes">&lt;br/&gt;&lt;b&gt;Organization Name: &lt;/b&gt;</xsl:text>
                                <xsl:value-of select="$description"/>
                                <xsl:text disable-output-escaping="yes">&lt;br/&gt;&lt;b&gt;Advertised IPv4 Address Space: &lt;/b&gt;</xsl:text>
                                <xsl:value-of select="xs:integer($ipv4counter)"/>
                                <xsl:text disable-output-escaping="yes">&lt;br/&gt;&lt;b&gt;Advertised IPv6 Address Space: &lt;/b&gt;</xsl:text>
                                <xsl:value-of select="xs:integer($ipv6counter)"/>
                                <xsl:text disable-output-escaping="yes">&lt;/html&gt;</xsl:text>
                            </data></xsl:variable>
                            <xsl:copy-of select="$asInfo"/>
                            <xsl:message>INFO: <xsl:value-of select="$asInfo"/></xsl:message>
                            <data key="IPv4Flag">
                            <xsl:choose>
                                <xsl:when test="$ipv4flag=1">TRUE</xsl:when>
                                <xsl:otherwise>FALSE</xsl:otherwise>
                            </xsl:choose>
                            </data>
                            <data key="IPv6Flag">
                                <xsl:choose>
                                    <xsl:when test="$ipv6flag=1">TRUE</xsl:when>
                                    <xsl:otherwise>FALSE</xsl:otherwise>
                                </xsl:choose>
                            </data>
                            <data key="RoutePrefixes"><xsl:copy-of select="$RoutePrefixes"/></data>
                        </node>

                    </xsl:variable>
                    <saxon:assign name="ipv4counter" select="0"/>
                    <saxon:assign name="ipv6counter" select="0"/>

                    <saxon:assign name="ipv4flag" select="0"/>
                    <saxon:assign name="ipv6flag" select="0"/>

                    <!--xsl:message>Done! </xsl:message-->

                    <xsl:copy-of select="$node"/>
                    <xsl:variable name="debug_AS_final"><xsl:value-of select="$AS"/> done!</xsl:variable>
                    <xsl:message>DEBUG: <xsl:value-of select="$debug_AS_final"/></xsl:message>

                </xsl:for-each>

                <xsl:for-each select="distinct-values($root//tableDump/attributes/attribute[@type = 'ASPath']/AS)">
                    <xsl:variable name="AS" select="."/>
                    <!--<xsl:message>DEBUG:<xsl:value-of select="$AS"/><xsl:text>-</xsl:text></xsl:message>-->

                    <xsl:variable name="nextASes" select="$root//tableDump/attributes/attribute[@type = 'ASPath' ]/AS[.=$AS]/following-sibling::AS[1]"/>
                    <xsl:for-each select="distinct-values($nextASes)">
                        <xsl:variable name="nextAS" select="."/>
                        <xsl:variable name="weigth"><xsl:value-of select="$asPathWeight"/></xsl:variable>
                        <xsl:variable name="edge">
                            <edge>
                                <xsl:attribute name="id"><xsl:value-of select="$AS"/><xsl:text>-</xsl:text><xsl:value-of select="$nextAS"/></xsl:attribute>
                                <xsl:attribute name="source"><xsl:value-of select="$AS"/></xsl:attribute>
                                <xsl:attribute name="target"><xsl:value-of select="$nextAS"/></xsl:attribute>
                                <data key="weigth">
                                    <xsl:value-of select="$weigth"/>
                                </data>
                                <data key="edgeID">
                                    <xsl:value-of select="$AS"/>
                                    <xsl:text>-</xsl:text>
                                    <xsl:value-of select="."/>
                                </data>
                                <data key="edge">YES</data>
                            </edge>
                        </xsl:variable>
                        <xsl:variable name="debug_edge"><xsl:value-of select="$AS"/><xsl:text>-</xsl:text><xsl:value-of select="$nextAS"/></xsl:variable>
                        <xsl:message>DEBUG:<xsl:value-of select="$debug_edge"/></xsl:message>
                        <xsl:message>TRACE:<xsl:copy-of select="$edge"/></xsl:message>
                        <!--xsl:message>Done  </xsl:message-->
                        <xsl:copy-of select="$edge"/>
                    </xsl:for-each>
                </xsl:for-each>
                <!--<xsl:for-each select="distinct-values(/root//prefix/ASes/AS)">-->
                <!--<xsl:variable name="AS" select="."/>-->
                <!--<xsl:variable name="nextASes" select="$root//prefix/ASes/AS[.=$AS]/following-sibling::AS[1]"/>-->
                <!--<xsl:for-each select="distinct-values($nextASes)">-->
                <!--<xsl:variable name="nextAS" select="."/>-->
                <!--<xsl:variable name="weigth"><xsl:value-of select="distinct-values($nextASes[. = $nextAS]/@weigth)[1]"/></xsl:variable>-->
                <!--<edge>-->
                <!--<xsl:attribute name="id"><xsl:value-of select="$AS"/><xsl:text>-</xsl:text><xsl:value-of select="$nextAS"/></xsl:attribute>-->
                <!--<xsl:attribute name="source"><xsl:value-of select="$AS"/></xsl:attribute>-->
                <!--<xsl:attribute name="target"><xsl:value-of select="$nextAS"/></xsl:attribute>-->
                <!--<data key="weigth">-->
                <!--<xsl:value-of select="$weigth"/>-->
                <!--</data>-->
                <!--<data key="edgeID">-->
                <!--<xsl:value-of select="$AS"/>-->
                <!--<xsl:text>-</xsl:text>-->
                <!--<xsl:value-of select="."/>-->
                <!--</data>-->
                <!--<data key="edge">YES</data>-->
                <!--<data key="color">-->
                <!--<xsl:choose>-->
                <!--<xsl:when test="$weigth=2">FF0000</xsl:when>-->
                <!--<xsl:when test="$weigth=1">0000FF</xsl:when>-->
                <!--<xsl:when test="$weigth=3">FFFFFF</xsl:when>-->
                <!--<xsl:otherwise>000000</xsl:otherwise>-->
                <!--</xsl:choose>-->

                <!--</data>-->
                <!--</edge>-->
                <!--<xsl:message>Edge id: <xsl:value-of select="$AS"/><xsl:text>-</xsl:text><xsl:value-of select="$nextAS"/> done! </xsl:message>-->
                <!--</xsl:for-each>-->
                <!--</xsl:for-each>-->
            </graph>
        </graphml>
    </xsl:template>
    <xsl:template name="prefixIPv4AddressSpaceCounter">
        <xsl:param name="subnetMask"/>
        <xsl:choose>
            <xsl:when test="$subnetMask=32">0</xsl:when>
            <xsl:when test="$subnetMask=31">2</xsl:when>
            <xsl:when test="$subnetMask=30">4</xsl:when>
            <xsl:when test="$subnetMask=29">8</xsl:when>
            <xsl:when test="$subnetMask=28">16</xsl:when>
            <xsl:when test="$subnetMask=27">32</xsl:when>
            <xsl:when test="$subnetMask=26">64</xsl:when>
            <xsl:when test="$subnetMask=25">128</xsl:when>
            <xsl:when test="$subnetMask=24">256</xsl:when>
            <xsl:when test="$subnetMask=23">512</xsl:when>
            <xsl:when test="$subnetMask=22">1024</xsl:when>
            <xsl:when test="$subnetMask=21">2048</xsl:when>
            <xsl:when test="$subnetMask=20">4096</xsl:when>
            <xsl:when test="$subnetMask=19">8192</xsl:when>
            <xsl:when test="$subnetMask=18">16384</xsl:when>
            <xsl:when test="$subnetMask=17">32768</xsl:when>
            <xsl:when test="$subnetMask=16">65536</xsl:when>
            <xsl:when test="$subnetMask=15">131072</xsl:when>
            <xsl:when test="$subnetMask=14">262144</xsl:when>
            <xsl:when test="$subnetMask=13">524288</xsl:when>
            <xsl:when test="$subnetMask=12">1048576</xsl:when>
            <xsl:when test="$subnetMask=11">2097152</xsl:when>
            <xsl:when test="$subnetMask=10">4194304</xsl:when>
            <xsl:when test="$subnetMask=9">8388608</xsl:when>
            <xsl:when test="$subnetMask=8">16777216</xsl:when>
            <xsl:when test="$subnetMask=7">33554432</xsl:when>
            <xsl:when test="$subnetMask=6">67108864</xsl:when>
            <xsl:when test="$subnetMask=5">134217728</xsl:when>
            <xsl:when test="$subnetMask=4">268435456</xsl:when>
            <xsl:when test="$subnetMask=3">536870912</xsl:when>
            <xsl:when test="$subnetMask=2">1073741824</xsl:when>
            <xsl:when test="$subnetMask=1">2147483648</xsl:when>
            <xsl:when test="$subnetMask=0">4294967296</xsl:when>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="prefixIPv6AddressSpaceCounter">
        <xsl:param name="subnetMask"/>
        <xsl:variable name="power" select="128-$subnetMask" />
        <xsl:variable name="addresscount"><xsl:call-template name="math:power"><xsl:with-param name="base" select="2"/><xsl:with-param name="power" select="xs:integer($power)"/></xsl:call-template></xsl:variable>
        <xsl:value-of select="xs:integer($addresscount)"/>
    </xsl:template>
    <xsl:template name="math:power">
        <xsl:param name="base" select="0" />
        <xsl:param name="power" select="1" />
        <xsl:choose>
            <xsl:when test="$power &lt; 0 or contains(string($power), '.')">
                <xsl:message terminate="yes">
                    The XSLT template math:power doesn't support negative or
                    fractional arguments.
                </xsl:message>
                <xsl:text>NaN</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="math:_power">
                    <xsl:with-param name="base" select="$base" />
                    <xsl:with-param name="power" select="$power" />
                    <xsl:with-param name="result" select="1" />
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="math:_power">
        <xsl:param name="base" select="0" />
        <xsl:param name="power" select="1" />
        <xsl:param name="result" select="1" />
        <xsl:choose>
            <xsl:when test="$power = 0">
                <xsl:value-of select="$result" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="math:_power">
                    <xsl:with-param name="base" select="$base" />
                    <xsl:with-param name="power" select="$power - 1" />
                    <xsl:with-param name="result" select="$result * $base" />
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>
