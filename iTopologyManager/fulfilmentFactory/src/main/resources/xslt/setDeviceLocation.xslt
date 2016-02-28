<!--
  ~ setDeviceLocation.xslt
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

<xsl:stylesheet version="2.0"
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
 xmlns:xs="http://www.w3.org/2001/XMLSchema"
 xmlns:fn="http://www.w3.org/2005/xpath-functions"
 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 xmlns:functx="http://www.functx.com">
 <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template match="/">
	<xsl:variable name="ManagementIPAddress" select="/root/parameters[name='ManagementIPAddress']/value"/>
	<xsl:variable name="community" select="/root/parameters[name='community']/value"/>
	<!--xsl:variable name="SnmpSetResponse" select="snmpSet($ManagementIPAddress,$community)"/-->
	<test>
	   <name><xsl:value-of select="$ManagementIPAddress"/></name>
	   <name><xsl:value-of select="$community"/></name>
	</test>
	</xsl:template>
</xsl:stylesheet>