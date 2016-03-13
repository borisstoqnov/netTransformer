<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ bgpAsPath.xslt
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
	<xsl:output method="xml" omit-xml-declaration="yes"/>
	<xsl:template match="/">
		<xsl:variable name="bgp4PathAttrEntries" select="//root/iso/org/dod/internet/mgmt/mib-2/bgp/bgp4PathAttrTable/bgp4PathAttrEntry"/>
		<xsl:variable name="root">
			<root>
				<xsl:for-each select="//root/iso/org/dod/internet/mgmt/mib-2/bgp/bgp4PathAttrTable/bgp4PathAttrEntry/bgp4PathAttrASPathSegment">
					<xsl:variable name="prefix" select="concat(../bgp4PathAttrIpAddrPrefix, '/' ,../bgp4PathAttrIpAddrPrefixLen)"/>
					<xsl:variable name="type" select="substring-before(.,':')"/>
					<xsl:variable name="lengthValue" select="substring-after(.,':')"/>
					<xsl:variable name="length" select="substring-before($lengthValue,':')"/>
					<xsl:variable name="value" select="substring-after($lengthValue,':')"/>
					<prefix>
						<xsl:attribute name="id"><xsl:value-of select="$prefix"/></xsl:attribute>
						<xsl:variable name="ASes">
							<Autonomus-Systems>
								<as-path>
									<xsl:value-of select="."/>
								</as-path>
								<type>
									<xsl:value-of select="$type"/>
								</type>
								<length>
									<xsl:value-of select="$length"/>
								</length>
								<xsl:choose>
									<xsl:when test="matches($value,':')">
										<xsl:variable name="test" select="replace($value,':','')"/>
										<value>
											<xsl:value-of select="$test"/>
										</value>
										<xsl:call-template name="groupByTwo">
											<xsl:with-param name="string" select="$test"/>
										</xsl:call-template>
									</xsl:when>
									<xsl:otherwise>
										<AS>
											<xsl:call-template name="HexToDecimal">
												<xsl:with-param name="hexNumber" select="$value"/>
											</xsl:call-template>
										</AS>
									</xsl:otherwise>
								</xsl:choose>
							</Autonomus-Systems>
						</xsl:variable>
						<xsl:variable name="lastAS" select="$ASes/Autonomus-Systems/AS[last()]"/>
						<lastAS>
							<xsl:choose>
								<xsl:when test="$lastAS = '0'">
									<xsl:value-of select="$ASes/Autonomus-Systems/AS[1]"/>
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="$lastAS"/>
								</xsl:otherwise>
							</xsl:choose>
						</lastAS>
						<ASes>
							<xsl:for-each select="distinct-values($ASes/Autonomus-Systems/AS)">
								<xsl:variable name="ASs" select="."/>
								<xsl:variable name="count" select="count($ASes/Autonomus-Systems/AS[. = $ASs])"/>
								
										<AS>
											<xsl:attribute name="weigth"><xsl:value-of select="$count"/></xsl:attribute>
											<xsl:value-of select="."/>
										</AS>
							</xsl:for-each>
						</ASes>
					</prefix>
				</xsl:for-each>
			</root>
		</xsl:variable>
		<xsl:copy-of select="$root"/>
	</xsl:template>
	<xsl:template name="groupByTwo">
		<xsl:param name="string"/>
		<xsl:if test="string-length($string) > 0">
			<xsl:variable name="AS" select="substring($string,1, 4)"/>
			<AS>
				<xsl:call-template name="HexToDecimal">
					<xsl:with-param name="hexNumber" select="$AS"/>
				</xsl:call-template>
			</AS>
			<xsl:call-template name="groupByTwo">
				<xsl:with-param name="string" select="substring-after($string,$AS)"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<xsl:template name="HexToDecimal">
		<xsl:param name="hexNumber"/>
		<xsl:param name="decimalNumber">0</xsl:param>
		<!-- If there is zero hex digits left, output-->
		<xsl:choose>
			<xsl:when test="$hexNumber">
				<xsl:call-template name="HexToDecimal">
					<xsl:with-param name="decimalNumber" select="($decimalNumber*16)+number(substring-before(substring-after('00/11/22/33/44/55/66/77/88/99/A10/B11/C12/D13/E14/F15/a10/b11/c12/d13/e14/f15/',substring($hexNumber,1,1)),'/'))"/>
					<xsl:with-param name="hexNumber" select="substring($hexNumber,2)"/>
				</xsl:call-template>
			</xsl:when>
			<!-- otherwise multiply, and add the next digit, and recurse -->
			<xsl:otherwise>
				<xsl:value-of select="$decimalNumber"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>
