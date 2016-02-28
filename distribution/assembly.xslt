<?xml version="1.0" encoding="UTF-8"?>

<!--
  ~ assembly.xslt
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



<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:exslt="http://exslt.org/common">
    <xsl:template match="/">
        <xsl:for-each select="/*/*/*/*/*/*/*">
            <xsl:if test="contains(name(.),'output') and contains(.,'conf')">
                <xsl:copy-of select="."/><xsl:text>
</xsl:text>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    <!--<xsl:template match="assembly">-->
        <!--&lt;!&ndash;xsl:copy-of select="/assembly/moduleSets/."/&ndash;&gt;-->
        <!--<xsl:for-each select="moduleSets/moduleSet/sources/fileSets/fileSet/outputDirectory/.">-->
            <!--<xsl:if test="contains(.,'conf')">-->
                <!--<xsl:copy-of select="."/><xsl:text>-->
<!--</xsl:text>-->
            <!--</xsl:if>-->
        <!--</xsl:for-each>-->
    <!--</xsl:template>-->

</xsl:stylesheet>
