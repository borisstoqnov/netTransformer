<?xml version="1.0" encoding="UTF-8"?>
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

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:functx="http://www.functx.com">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
    <xsl:param name="url"/>
    <xsl:variable name="url1">file:////Users/niau/svn/1_small/undirected?select=*.graphml</xsl:variable>
	<xsl:template match="/">
		<Document>
			<name>Google Maps Network Topology View</name>
			<description>Google Maps Network Topology View</description>
			<Style id="blueLine">
				<LineStyle>
					<color>ffff0000</color>
					<width>3</width>
				</LineStyle>
			</Style>
            <Style id="redLine">
				<LineStyle>
					<color>FF0000FF</color>
					<width>4</width>
				</LineStyle>
			</Style>
            <Style id="brownLine">
				<LineStyle>
					<color>FF3C14DC</color>
					<width>4</width>
				</LineStyle>
			</Style>
            <Style id="greenLine">
				<LineStyle>
					<color>FF00FF00</color>
					<width>2</width>
				</LineStyle>
			</Style>
            <Style id="greyLine">
				<LineStyle>
					<color>FFC0C0C0</color>
					<width>2</width>
				</LineStyle>
			</Style>
			<Style id="CISCO">
		<IconStyle>
			<scale>1</scale>
			<Icon>
				<href>http://niau.org/images/large_router.png</href>
			</Icon>
		</IconStyle>
	</Style>
    <Style id="HUAWEI">
		<IconStyle>
			<scale>1</scale>
			<Icon>
				<href>http://niau.org/images/medium_router.png</href>
			</Icon>
		</IconStyle>
	</Style>
     <Style id="JUNIPER">
		<IconStyle>
			<scale>1</scale>
			<Icon>
				<href>http://niau.org/images/very_big_router.png</href>
			</Icon>
		</IconStyle>
	</Style>
    <Style id="RIVERSTONE">
		<IconStyle>
			<scale>1</scale>
			<Icon>
				<href>http://niau.org/images/very_big_router.png</href>
			</Icon>
		</IconStyle>
	</Style>
    <Style id="HP">
		<IconStyle>
			<scale>1</scale>
			<Icon>
				<href>http://niau.org/images/workgroup_switch.png</href>
			</Icon>
		</IconStyle>
	</Style>
			<xsl:variable name="nodes">
				<xsl:for-each select="collection(iri-to-uri($url1))">
					<xsl:variable name="id" select="//graphml/graph/node/data[@key='geoCoordinates']/../@id"/>
					<xsl:variable name="geoCoordinates" select="//graphml/graph/node/data[@key='geoCoordinates']"/>
					<xsl:variable name="deviceInfo" select="//graphml/graph/node/data[@key='deviceInfo']"/>
                    <xsl:variable name="deviceType" select="//graphml/graph/node/data[@key='deviceType']"/>
                    <xsl:variable name="deviceModel" select="//graphml/graph/node/data[@key='deviceModel']"/>
					<xsl:if test="$id !='' and $geoCoordinates!='' and $geoCoordinates!=','">
						<node>
							<id>
								<xsl:value-of select="$id"/>
							</id>
							<geoCoordinates>
								<xsl:value-of select="$geoCoordinates"/>
							</geoCoordinates>
                            <type><xsl:value-of select="$deviceType"/></type>
                            <model><xsl:value-of select="$deviceModel"/></model>
							<description><xsl:value-of select = "$deviceInfo"/></description>
						</node>
					</xsl:if>
				</xsl:for-each>
			</xsl:variable>
			<!--xsl:copy-of select="$nodes"/-->
			<xsl:variable name="edges">
				<xsl:for-each select="collection(iri-to-uri($url1))">
					<xsl:for-each select="//graphml/graph/edge">
						<xsl:variable name="source" select="@source"/>
						<xsl:variable name="target" select="@target"/>
                        <xsl:variable name="color" select="data[@key='color']"/>
						<xsl:if test="$nodes/node/id[.=$source]!='' and $nodes/node/id[.=$target]!=''">
							<edge>
								<id>
									<xsl:value-of select="$source"/>,<xsl:value-of select="$target"/> 
								</id>
								<source>
									<id>
										<xsl:value-of select="$source"/>
									</id>
									<coordinates>
										<xsl:value-of select="$nodes//id[.=$source]/../geoCoordinates"/>
									</coordinates>
								</source>
								<target>
									<id>
										<xsl:value-of select="$target"/>
									</id>
									<coordinates>
										<xsl:value-of select="$nodes/node/id[.=$target]/../geoCoordinates"/>
									</coordinates>
								</target>
                                <color>
                                    <xsl:value-of select="$color"/>
                                </color>
								<description>Link between <xsl:value-of select="$source"/> and <xsl:value-of select="$target"/>  </description>
							</edge>
						</xsl:if>
					</xsl:for-each>
				</xsl:for-each>
			</xsl:variable>
			<!--xsl:copy-of select="$edges"/-->
			<xsl:for-each select="functx:distinct-nodes($nodes//node)">
				<xsl:call-template name="DeviceMarker">
					<xsl:with-param name="name" select="id"/>
					<xsl:with-param name="model" select="model"/>
                    <xsl:with-param name="type" select="type"/>
					<xsl:with-param name="YX" select="geoCoordinates"/>
					<xsl:with-param name="description">
						<xsl:value-of select="description"/>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:for-each>
			<xsl:for-each select="functx:distinct-nodes($edges//edge)">
				<xsl:call-template name="LinkLine">
					<xsl:with-param name="name" select="id"/>
					<xsl:with-param name="color" select="color"/>
					<xsl:with-param name="YX1" select="source/coordinates"/>
					<xsl:with-param name="YX2" select="target/coordinates"/>
					<xsl:with-param name="description">
						<xsl:value-of select="description"/>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:for-each>
		</Document>
	</xsl:template>
	<xsl:template name="DeviceMarker">
		<xsl:param name="name"/>
		<xsl:param name="description"/>
		<xsl:param name="YX"/>
        <xsl:param name="model"/>
        <xsl:param name="type"/>

		<Placemark>
			<name>
				<xsl:value-of select="$name"/>
			</name>
			<description>
				<xsl:value-of select="$description"/>
			</description>
			<styleUrl>
                <xsl:choose>
                    <xsl:when test="contains($type,'CISCO')">#CISCO</xsl:when>
                    <xsl:when test="contains($type,'JUNIPER')">#JUNIPER</xsl:when>
                     <xsl:when test="contains($type,'RIVERSTONE')">#RIVERSTONE</xsl:when>
                    <xsl:when test="contains($type,'HP')">#HP</xsl:when>
                    <xsl:when test="contains($type,'HUAWEI')">#HUAWEI</xsl:when>
                    <xsl:otherwise>#HP</xsl:otherwise>
                </xsl:choose>
			</styleUrl>
			<Point>
				<coordinates>
					<xsl:value-of select="$YX"/>
				</coordinates>
			</Point>
		</Placemark>
	</xsl:template>
	<xsl:template name="LinkLine">
		<xsl:param name="name"/>
		<xsl:param name="YX1"/>
		<xsl:param name="YX2"/>
		<xsl:param name="description"/>
        <xsl:param name="color"/>
		<Placemark>
			<name>
				<xsl:value-of select="$name"/>
			</name>
            <COLOR><xsl:value-of select="$color"/></COLOR>
            <styleUrl>
            <xsl:choose>
                <xsl:when test="contains($color,'0000FF')">#blueLine</xsl:when>
                 <xsl:when test="contains($color,'FF0000')">#redLine</xsl:when>
                 <xsl:when test="contains($color,'DC143C')">#brownLine</xsl:when>
                 <xsl:when test="contains($color,'00FF00')">#greenLine</xsl:when>
                 <xsl:otherwise>#greyLine</xsl:otherwise>
            </xsl:choose>
			</styleUrl>
			<LineString>
				<altitudeMode>relative</altitudeMode>
				<coordinates>
					<xsl:text>&#xa;</xsl:text>
					<xsl:value-of select="$YX1"/>
					<xsl:text>&#xa;</xsl:text>
					<xsl:value-of select="$YX2"/>
					<xsl:text>&#xa;</xsl:text>
				</coordinates>
			</LineString>
		</Placemark>
	</xsl:template>
	<xsl:function name="functx:is-node-in-sequence" as="xs:boolean" xmlns:functx="http://www.functx.com">
		<xsl:param name="node" as="node()?"/>
		<xsl:param name="seq" as="node()*"/>
		<xsl:sequence select="some $nodeInSeq in $seq satisfies $nodeInSeq is $node"/>
	</xsl:function>
	<xsl:function name="functx:distinct-nodes" as="node()*" xmlns:functx="http://www.functx.com">
		<xsl:param name="nodes" as="node()*"/>
		<xsl:sequence select=" for $seq in (1 to count($nodes))return $nodes[$seq][not(functx:is-node-in-sequence(.,$nodes[position() &lt; $seq]))]"/>
	</xsl:function>
</xsl:stylesheet>
