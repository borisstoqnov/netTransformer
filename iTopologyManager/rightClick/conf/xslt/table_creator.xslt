<?xml version="1.0"?>
<!--
  ~ table_creator.xslt
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