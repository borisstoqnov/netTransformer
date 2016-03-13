<!--
  ~ reorder.xslt
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
        <xsl:template match="/">
            <xsl:message>Number of nodes: <xsl:value-of select="count(/graphml/graph/node)"/> </xsl:message>
            <xsl:message>Number of edges: <xsl:value-of select="count(/graphml/graph/edge)"/> </xsl:message>
            <graphml>
            <graph edgedefault="undirected">
            <key id="diffs" for="edge" attr.name="diffs" attr.type="string"/>
            <key id="diffs" for="node" attr.name="diffs" attr.type="string"/>
            <key id="diff" for="edge" attr.name="diff" attr.type="string"/>
            <key id="diff" for="node" attr.name="diff" attr.type="string"/>
            <key id="AS" for="node" attr.name="AS" attr.type="string"/>
            <key id="ASInfo" for="node" attr.name="ASInfo" attr.type="string"/>
            <key id="ASN" for="node" attr.name="ASN" attr.type="string"/>
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
            <xsl:for-each select="/graphml/graph/node">
                <xsl:copy-of select="."/>
            </xsl:for-each>
            <xsl:for-each select="/graphml/graph/edge">
                <xsl:copy-of select="."/>
            </xsl:for-each>
            </graph>
            </graphml>
        </xsl:template>
    </xsl:stylesheet>