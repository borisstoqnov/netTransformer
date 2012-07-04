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

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
		<xsl:variable name="root" select="/root"/>
		<graphml>
			<graph edgedefault="undirected">
				<key id="diffs" for="edge" attr.name="diffs" attr.type="string"/>
				<key id="diffs" for="node" attr.name="diffs" attr.type="string"/>      
				<key id="diff" for="edge" attr.name="diff" attr.type="string"/>
				<key id="diff" for="node" attr.name="diff" attr.type="string"/>  			
				<key id="AS" for="node" attr.name="AS" attr.type="string"/>
				<key id="ASInfo" for="node" attr.name="AS" attr.type="string"/>
				<key id="ASN" for="node" attr.name="AS" attr.type="string"/>
				<key id="description" for="node" attr.name="description" attr.type="string"/>
				<key id="RoutePrefixes" for="node" attr.name="RoutePrefixes" attr.type="string"/>
				<key id="countOriginatedPrefixes" for="node" attr.name="countOriginatedPrefixes" attr.type="integer"/>
				<key id="countASTransitAppearances" for="node" attr.name="countASTransitAppearances" attr.type="integer"/>
				<key id="customer" for="node" attr.name="customer" attr.type="string"/>
				<key id="transit" for="node" attr.name="transit" attr.type="string"/>
				<key id="edgeID" for="edge" attr.name="edgeID" attr.type="string"/>
				<key id="edge" for="edge" attr.name="edge" attr.type="string"/>
				<key id="prefix" for="edge" attr.name="prefix" attr.type="string"/>
				<key id="color" for="edge" attr.name="color" attr.type="string"/>
				<key id="weigth" for="edge" attr.name="weigth" attr.type="string"/>
				
				<xsl:for-each select="distinct-values($root//prefix/ASes/AS)">
					<xsl:variable name="AS" select="."/>
					<xsl:variable name="asCount" select="count($root//prefix/ASes[AS=$AS])"/>
					<xsl:variable name="description" select="document('as-numbers.xml')/root/AS[number=concat('AS',$AS)]/description"/>
					<xsl:variable name="countOriginatedPrefixes" select="count($root//prefix[lastAS=$AS])"/>
					<xsl:variable name="countASTransitAppearances" select="count($root//prefix/ASes[AS=$AS]/..[lastAS!=$AS])"/>
					<node>
						<xsl:attribute name="id"><xsl:value-of select="$AS"/></xsl:attribute>
						<data key="AS">YES</data>
						<data key="ASN">
							<xsl:value-of select="$AS"/>
						</data>
						<data key="countOriginatedPrefixes">
								<xsl:value-of select="$countOriginatedPrefixes"/>
						</data>
						<data key="countASTransitAppearances">
								<xsl:value-of select="$countASTransitAppearances"/>
						</data>
						<xsl:variable name="RoutePrefixes">
                            <RoutePrefixes>
							<xsl:for-each select="distinct-values($root//prefix[lastAS=$AS]/@id)">
								<RoutePrefix>
									<xsl:value-of select="."/>
								</RoutePrefix>
							</xsl:for-each>
                            </RoutePrefixes>
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
							<xsl:text disable-output-escaping="yes">&lt;br/&gt;&lt;b&gt;Advertized Prefixes: &lt;/b&gt;</xsl:text>
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
                        <data key="RoutePrefixes"><xsl:value-of select="$RoutePrefixes"/></data>
					</node>
				</xsl:for-each>
				<xsl:for-each select="distinct-values(/root//prefix/ASes/AS)">
					<xsl:variable name="AS" select="."/>
					<xsl:variable name="nextASes" select="$root//prefix/ASes/AS[.=$AS]/following-sibling::AS[1]"/>
					<xsl:for-each select="distinct-values($nextASes)">
						<xsl:variable name="nextAS" select="."/>					
						<xsl:variable name="weigth"><xsl:value-of select="distinct-values($nextASes[. = $nextAS]/@weigth)[1]"/></xsl:variable>
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
					</xsl:for-each>
				</xsl:for-each>
			</graph>
		</graphml>
	</xsl:template>
</xsl:stylesheet>
