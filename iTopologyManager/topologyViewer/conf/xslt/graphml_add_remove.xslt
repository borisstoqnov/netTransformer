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

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:cmp="http://xsltsl.org/cmp">
	<xsl:output omit-xml-declaration="yes" indent="yes"/>
	<xsl:param name="file"/>
	<xsl:param name="status"/>
	<xsl:strip-space elements="*"/>
	<xsl:variable name="fileA" select="document($file)"/>
	<!--Add diff status to graphml file1. -->
	<xsl:template match="/">
		<graphml>
			<graph>
				<xsl:copy-of select="$fileA/graphml/graph/@*" copy-namespaces="no"/>
				<xsl:copy-of select="$fileA/graphml/graph/descr" copy-namespaces="no"/>
				<xsl:copy-of select="$fileA/graphml/graph/key" copy-namespaces="no"/>
                <xsl:if test="count($fileA/graphml/graph/key[@id='diff'])=0">
                    <key id="diff" diff="ADDED" for="node" attr.name="diff" attr.type="string"/>
                    <key id="diff" diff="ADDED" for="edge" attr.name="diff" attr.type="string"/>
                    <key id="diffs" diff="ADDED" for="node" attr.name="diffs" attr.type="string"/>
                    <key id="diffs" diff="ADDED" for="edge" attr.name="diffs" attr.type="string"/>
                </xsl:if>
				<xsl:variable name="id" select="$fileA/graphml/graph/node[1]/@id"></xsl:variable>
				<xsl:for-each select="$fileA/graphml/graph/node[@id=$id]">
					<xsl:variable name="node" select="."/>
					<node>
						<xsl:attribute name="id" select="@id"/>
						<xsl:for-each select="data[@key]">
							<xsl:copy-of select="." copy-namespaces="no"/>
							
						</xsl:for-each>
						<data>
									<xsl:attribute name="key">diff</xsl:attribute>
									<xsl:value-of select="$status"/>
					    </data>
						<data><xsl:attribute name="key">diffs</xsl:attribute><!--xsl:value-of select="data[@key='nodeInfo']" &lt;br/&gt;--> Node: <xsl:value-of select="$status"/></data>
					</node>
				</xsl:for-each>
				<xsl:for-each select="$fileA/graphml/graph/node[@id!=$id]">
					<xsl:copy-of select="." copy-namespaces="no"/>
				</xsl:for-each>
				<xsl:for-each select="$fileA/graphml/graph/edge[@source=$id]">
					<xsl:variable name="edge" select="."/>
					<xsl:variable name="diff" select="@diff"/>
					<edge>
						<xsl:attribute name="id" select="@id"/>
						<xsl:copy-of select="@source"/>
						<xsl:copy-of select="@target"/>
						<xsl:for-each select="data[@key]">
							<xsl:copy-of select="." copy-namespaces="no"/>
						</xsl:for-each>
						<data>
								<xsl:attribute name="key">diff</xsl:attribute>
								<xsl:value-of select="$status"/>
						</data>
						<data><xsl:attribute name="key">diffs</xsl:attribute><!--<xsl:value-of select="data[@key='edgeTooltip']"/> &lt;br/&gt;--> Edge: <xsl:value-of select="$status"/></data>
					</edge>
				</xsl:for-each>
			</graph>
		</graphml>
	</xsl:template>
</xsl:stylesheet>
