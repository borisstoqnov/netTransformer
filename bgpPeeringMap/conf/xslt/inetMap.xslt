<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ inetMap.xslt
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

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
    <xsl:param name="as-numbers"/>
	<xsl:template match="/">


		<xsl:variable name="root" select="/root"/>
        <xsl:message>DEBUG:<xsl:value-of select="$as-numbers"/></xsl:message>

        <graphml>
			<graph edgedefault="undirected">
				<key id="diffs" for="edge" attr.name="diffs" attr.type="string"/>
				<key id="diffs" for="node" attr.name="diffs" attr.type="string"/>      
				<key id="diff" for="edge" attr.name="diff" attr.type="string"/>
				<key id="diff" for="node" attr.name="diff" attr.type="string"/>
                <key id="ASName" for="node" attr.name="ASName" attr.type="string"/>
                <key id="IPv4PrefixCount" for="node" attr.name="IPv4PrefixCount" attr.type="int"/>
                <key id="IPv6PrefixCount" for="node" attr.name="IPv6PrefixCount"  attr.type="int"/>
                <key id="IPv4Flag" for="node" attr.name="IPv4Flag" attr.type="string"/>
                <key id="IPv6Flag" for="node" attr.name="IPv6Flag" attr.type="string"/>
                <key id="IPv4AddressSpace" for="node" attr.name="IPv4AddressSpace" attr.type="int"/>
                <key id="IPv6AddressSpace" for="node" attr.name="IPv6AddressSpace" attr.type="int"/>
                <key id="Country" for="node" attr.name="Country" attr.type="string"/>
                <key id="Description" for="node" attr.name="Description" attr.type="string"/>
                <key id="IPv4Prefixes" for="node" attr.name="IPv4Prefixes" attr.type="string"/>
                <key id="IPv6Prefixes" for="node" attr.name="IPv6Prefixes" attr.type="string"/>
                <key id="weight" for="edge" attr.name="weight" attr.type="int"/>


				<xsl:variable name="asNumberess" select="document($as-numbers)/root/AS"/>

				<xsl:for-each select="distinct-values($root//prefix/ASes/AS)">
					<xsl:variable name="AS" select="."/>
					<xsl:variable name="asCount" select="count($root//prefix/ASes[AS=$AS])"/>
					<xsl:variable name="description" select="$asNumberess[number=concat('AS',$AS)]/description"/>
					<xsl:variable name="countOriginatedPrefixes" select="count($root//prefix[lastAS=$AS])"/>
					<xsl:variable name="countASTransitAppearances" select="count($root//prefix/ASes[AS=$AS]/..[lastAS!=$AS])"/>
                    <xsl:variable name="node">
					<node>
						<xsl:attribute name="id"><xsl:value-of select="$AS"/></xsl:attribute>
						<data key="ASName">
							<xsl:value-of select="$description"/>
						</data>
						<data key="IPv4PrefixCount">
								<xsl:value-of select="$countOriginatedPrefixes"/>
						</data>
						<data key="countASTransitAppearances">
								<xsl:value-of select="$countASTransitAppearances"/>
						</data>
						<xsl:variable name="RoutePrefixes">
							<xsl:for-each select="distinct-values($root//prefix[lastAS=$AS]/@id)">
									<xsl:value-of select="."/>,
							</xsl:for-each>
						</xsl:variable>
						<data key="ASInfo">
							<xsl:text disable-output-escaping="yes">&lt;![CDATA[ &lt;html&gt;</xsl:text>
							<xsl:text disable-output-escaping="yes">&lt;b&gt;AS: &lt;/b&gt;</xsl:text>
							<xsl:value-of select="."/>
							<xsl:text disable-output-escaping="yes">&lt;br/&gt;&lt;b&gt;Description: &lt;/b&gt;</xsl:text>
							<xsl:value-of select="$description"/>
							<xsl:text disable-output-escaping="yes">&lt;br/&gt;&lt;b&gt;Transit AS appearance: &lt;/b&gt;</xsl:text>
							<xsl:value-of select="$countASTransitAppearances"/>
							<xsl:text disable-output-escaping="yes">&lt;br/&gt;&lt;b&gt;Originated AS prefixes: &lt;/b&gt;</xsl:text>
							<xsl:value-of select="$countOriginatedPrefixes"/>
							<xsl:text disable-output-escaping="yes">&lt;/html&gt;]]&gt;</xsl:text>
						</data>
						<data key="transit">
							<xsl:choose>
								<xsl:when test="$countASTransitAppearances >0">YES</xsl:when>
								<xsl:otherwise>NO</xsl:otherwise>
							</xsl:choose>
						</data>
						<data key="description">
							<xsl:text disable-output-escaping="yes">&lt;![CDATA[ &lt;html&gt;</xsl:text>
							<xsl:value-of select="$description"/>
							<xsl:text disable-output-escaping="yes">&lt;/html&gt;]]&gt;</xsl:text>
						</data>
                        <data key="IPv4Prefixes"><xsl:copy-of select="$RoutePrefixes"/></data>
					</node>
                    </xsl:variable>
                    <xsl:message>DEBUG:<xsl:value-of select="$AS"/></xsl:message>
                    <xsl:message>TRACE:<xsl:copy-of select="$node"/></xsl:message>
                    <!--xsl:message>Done! <xsl:value-of  select="current-dateTime()"/></xsl:message-->
                    <xsl:copy-of select="$node"/>

                    <xsl:variable name="nextASes" select="$root//prefix/ASes/AS[.=$AS]/following-sibling::AS[1]"/>
                    <xsl:for-each select="distinct-values($nextASes)">
                        <xsl:variable name="nextAS" select="."/>
                        <xsl:variable name="weigth"><xsl:value-of select="distinct-values($nextASes[. = $nextAS]/@weigth)[1]"/></xsl:variable>
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
                            <data key="color">
                                <xsl:choose>
                                    <xsl:when test="$weigth=2">FF0000</xsl:when>
                                    <xsl:when test="$weigth=1">0000FF</xsl:when>
                                    <xsl:when test="$weigth=3">FFFFFF</xsl:when>
                                    <xsl:otherwise>000000</xsl:otherwise>
                                </xsl:choose>

                            </data>
                        </edge>
                        </xsl:variable>
                        <xsl:message>DEBUG:<xsl:value-of select="$AS"/><xsl:text>-</xsl:text><xsl:value-of select="$nextAS"/>/></xsl:message>
                        <xsl:message>TRACE:<xsl:copy-of select="$edge"/></xsl:message>
                        <!--xsl:message>Done <xsl:value-of  select="current-dateTime()"/> </xsl:message-->
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
</xsl:stylesheet>
