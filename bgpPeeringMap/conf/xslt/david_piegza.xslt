<!--
  ~ david_piegza.xslt
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
      <root>
        <xsl:for-each select="/graphml/graph/node">
            <node>
                <xsl:variable name="nodeId" select="@id"/>
                <xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>
                <title><xsl:value-of select="data[@key='ASN']"/></title>
                <xsl:for-each select="/graphml/graph/edge[@source=$nodeId]">
                    <ref><xsl:value-of select="@target"/></ref>
                </xsl:for-each>
            </node>

        </xsl:for-each>

      </root>
        </xsl:template>
        </xsl:stylesheet>