
<?xml version="1.0" encoding="UTF-8"?>

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

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:exslt="http://exslt.org/common" extension-element-prefixes="exslt" xmlns:functx="http://www.functx.com">
    <xsl:output method="text"  encoding="UTF-8"/>

    <xsl:template match="/">

        <xsl:for-each select="//assembly/moduleSets/moduleSet/sources/fileSets/fileSet/outputDirectory/.">
            <xsl:if test="contains(.,'conf')">
                <xsl:copy-of select="."/><xsl:text>
</xsl:text>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>
