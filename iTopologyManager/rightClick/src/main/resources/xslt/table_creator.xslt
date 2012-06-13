<?xml version="1.0"?>
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

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
 
<xsl:template match="/">
<HTML>
<BODY>
    <xsl:apply-templates/>
</BODY>
</HTML>
</xsl:template>

<xsl:template match="/*">
<TABLE BORDER="1" frame="vsides" rules="cols">
<TR>
        <xsl:for-each select="*[position() = 1]/*">
          <TD><b>
              <xsl:value-of select="local-name()"/>
              </b>
          </TD>
        </xsl:for-each>
</TR>
      <xsl:apply-templates/>
</TABLE>
</xsl:template>

<xsl:template match="/*/*">
<TR>
    <xsl:apply-templates/>
</TR>
</xsl:template>

<xsl:template match="/*/*/*">
<TD>
    <xsl:value-of select="."/>
</TD>
</xsl:template>

</xsl:stylesheet>