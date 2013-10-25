<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ netTransformer is an open source tool able to discover IP networks
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

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:x="http://itransformers.net/xslt/functions" exclude-result-prefixes="x fn xs xsl">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<!--compares 2 graphml - elegant and fast but expects explicitly the same positions for the nodes/keys and edges in source and destination. If the order is somehow scrambled won't work-->
	<xsl:param name="file1"/>
	<xsl:param name="file2"/>
	<xsl:param name="status"/>
	
	<xsl:variable name="fileA" select="sort(document($file1))"/>
	<xsl:variable name="fileB" select="sort(document($file2))"/>
	<xsl:function name="x:getPath">
		<xsl:param name="node"/>
		<xsl:value-of select="
for $i in $node/ancestor-or-self::* return concat(local-name($i), count($i/preceding-sibling::*))
"/>
	</xsl:function>
	
	<xsl:key name="path" match="*" use="x:getPath(.)"/>
	<xsl:template match="/">
		<xsl:variable name="temp">
			<xsl:apply-templates select="$fileB/*"/>
		</xsl:variable>
		<xsl:copy-of select="$temp"/>
		<graphml>
			<graph>
				<xsl:attribute name="edgedefault"><xsl:value-of select="$fileB/graphml/graph/@edgedefault"/></xsl:attribute>
				<xsl:for-each select="$fileA/graphml/graph/key">
					<xsl:copy-of select="." copy-namespaces="no"/>
				</xsl:for-each>
				<xsl:for-each select="$temp/graphml/graph/node">
					<xsl:variable name="node" select="."/>
					<node>
						<xsl:attribute name="id" select="@id"/>
						<xsl:for-each select="data[@key and not(@diff)]">
							<xsl:copy-of select="." copy-namespaces="no"/>
						</xsl:for-each>
						<xsl:if test="count(data[@key and @diff])>0">
							<data><xsl:attribute name="key">diff</xsl:attribute>YES</data>
							<xsl:for-each select="data[@key and @diff]">
								<data>
									<xsl:attribute name="key"><xsl:value-of select="@key"/></xsl:attribute>
									<xsl:value-of select="."/>
								</data>
							</xsl:for-each>
							<xsl:variable name="diffs">
								<data>
									<xsl:attribute name="key">diffs</xsl:attribute><xsl:for-each select="data[@key and @diff]">&lt;br&gt;&lt;b&gt;&lt;i&gt;<xsl:value-of select="@key"/><xsl:text>: </xsl:text>&lt;/i&gt;&lt;/b&gt;<xsl:value-of select="@diff"/><xsl:text> - </xsl:text><xsl:value-of select="."/>&lt;/br&gt;</xsl:for-each>
								</data>
							</xsl:variable>
							<xsl:copy-of select="$diffs"/>
						</xsl:if>
					</node>
				</xsl:for-each>
				<xsl:for-each select="$temp/graphml/graph/edge">
					<xsl:variable name="node" select="."/>
					<edge>
						<xsl:attribute name="id" select="@id"/>
						<xsl:copy-of select="@source"/>
						<xsl:copy-of select="@target"/>
						<xsl:for-each select="data[@key and not(@diff)]">
							<xsl:copy-of select="." copy-namespaces="no"/>
						</xsl:for-each>
						<xsl:if test="count(data[@key and @diff])>0">
							<data><xsl:attribute name="key">diff</xsl:attribute>YES</data>
							<xsl:for-each select="data[@key and @diff]">
								<data>
									<xsl:attribute name="key"><xsl:value-of select="@key"/></xsl:attribute>
									<xsl:value-of select="."/>
								</data>
							</xsl:for-each>
							<xsl:variable name="diffs">
								<data>
									<xsl:attribute name="key">diffs</xsl:attribute><xsl:for-each select="data[@key and @diff]">&lt;br&gt;&lt;b&gt;&lt;i&gt;<xsl:value-of select="@key"/><xsl:text>: </xsl:text>&lt;/i&gt;&lt;/b&gt;<xsl:value-of select="@diff"/><xsl:text> - </xsl:text><xsl:value-of select="."/>&lt;/br&gt;</xsl:for-each>
								</data>
							</xsl:variable>
							<xsl:copy-of select="$diffs"/>
						</xsl:if>
					</edge>
				</xsl:for-each>
			</graph>
		</graphml>
	</xsl:template>
	
	
	<xsl:template match="*">
		<xsl:variable name="id" select="x:getPath(.)"/>
		<xsl:variable name="this" select="."/>
		<xsl:variable name="master" select="$fileA/key('path', $id)"/>
		<xsl:copy>
			<xsl:if test="not($master/text()=$this/text())">
				<xsl:attribute name="diff" select="$master/text()"/>
			</xsl:if>
			<xsl:for-each select="@*">
				<xsl:apply-templates select=".">
					<xsl:with-param name="master" select="$master/@*[name()=current()/name()]"/>
				</xsl:apply-templates>
			</xsl:for-each>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>
	<xsl:template match="@*">
		<xsl:param name="master"/>
		<xsl:copy/>
		<xsl:if test="not(.=$master)">
			<xsl:attribute name="{name()}-diff" select="$master"/>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
