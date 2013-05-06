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


    <xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions">
        <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
        <xsl:template match="/">
            <xsl:message>Number of nodes: <xsl:value-of select="count(/graphml/graph/node)"/> </xsl:message>
            <xsl:message>Number of edges: <xsl:value-of select="count(/graphml/graph/edge)"/> </xsl:message>

            <xsl:for-each select="/graphml/graph/node">
                <xsl:copy-of select="."/>
            </xsl:for-each>
            <xsl:for-each select="/graphml/graph/edge">
                <xsl:copy-of select="."/>
            </xsl:for-each>
        </xsl:template>
    </xsl:stylesheet>