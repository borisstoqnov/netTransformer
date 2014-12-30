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

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:SnmpForXslt="net.itransformers.idiscover.discoveryhelpers.xml.SnmpForXslt" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:functx="http://www.functx.com">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<!-- IP Templates Start -->
	<xsl:variable name="bit8" select="256"/>
	<xsl:variable name="bit7" select="128"/>
	<xsl:variable name="bit6" select="64"/>
	<xsl:variable name="bit5" select="32"/>
	<xsl:variable name="bit4" select="16"/>
	<xsl:variable name="bit3" select="8"/>
	<xsl:variable name="bit2" select="4"/>
	<xsl:variable name="bit1" select="2"/>
	<xsl:variable name="bit0" select="1"/>
    <xsl:template name="ipv6AddrAnycastFlag">
        <xsl:param name="ipv6AddrAnycastFlag"/>
        <xsl:choose>
            <xsl:when test="$ipv6AddrAnycastFlag = '1'">true</xsl:when>
            <xsl:otherwise>false</xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="ipv6AddrType">
        <xsl:param name="type"/>
        <xsl:choose>
            <xsl:when test="$type='1'">stateless</xsl:when>
            <xsl:when test="$type='2'">statefull</xsl:when>
            <xsl:otherwise>unknown</xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="adminStatus">
        <xsl:param name="status"/>
        <xsl:choose>
            <xsl:when test="$status='1'">UP</xsl:when>
            <xsl:when test="$status='2'">DOWN</xsl:when>
            <xsl:otherwise>TESTING</xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="operStatus">
        <xsl:param name="status"/>
        <xsl:choose>
            <xsl:when test="$status='1'">UP</xsl:when>
            <xsl:when test="$status='2'">DOWN</xsl:when>
            <xsl:when test="$status='3'">TESTING</xsl:when>
            <xsl:when test="$status='4'">UNKNOWN</xsl:when>
            <xsl:when test="$status='5'">DORMANT</xsl:when>
            <xsl:when test="$status='6'">NOTPRESENT</xsl:when>
            <xsl:otherwise>LOWERLAYERDOWN</xsl:otherwise>
        </xsl:choose>
    </xsl:template>
	<xsl:template name="neighIDCommunity">
		<xsl:param name="neighIP"/>
		<xsl:param name="comm"/>
		<xsl:param name="comm2"/>
		<xsl:if test="$neighIP!=''">
		<xsl:variable name="temp">
			<xsl:call-template name="return-hostname">
				<xsl:with-param name="hostname-unformated" select="SnmpForXslt:getName($neighIP, $comm)"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="$temp!=''">
				<xsl:value-of select="$temp"/>+-<xsl:value-of select="$comm"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="temp2">
					<xsl:call-template name="return-hostname">
						<xsl:with-param name="hostname-unformated" select="SnmpForXslt:getName($neighIP, $comm2)"/>
					</xsl:call-template>
				</xsl:variable>
				<xsl:choose>
					<xsl:when test="$temp2=''"/>
					<xsl:otherwise>
						<xsl:value-of select="$temp2"/>+-<xsl:value-of select="$comm2"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:if>
	</xsl:template>
	<xsl:template name="substring-before-last">
		<xsl:param name="value"/>
		<xsl:param name="substring"/>
		<xsl:choose>
			<xsl:when test="contains($value,$substring)">
				<xsl:value-of select="substring-before($value,$substring)"/>
				<xsl:variable name="substring-after" select="substring-after($value,$substring)"/>
				<xsl:if test="contains($substring-after,$substring)">
					<xsl:value-of select="$substring"/>
				</xsl:if>
				<xsl:call-template name="substring-before-last">
					<xsl:with-param name="value" select="$substring-after"/>
					<xsl:with-param name="substring" select="$substring"/>
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="substring-before">
		<xsl:param name="value"/>
		<xsl:param name="substring"/>
		<xsl:choose>
			<xsl:when test="contains($value,$substring)">
				<xsl:variable name="substring-before" select="substring-before($value,$substring)"/>
				<xsl:value-of select="$substring-before"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$value"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="substring-after">
		<xsl:param name="value"/>
		<xsl:param name="substring"/>
		<xsl:choose>
			<xsl:when test="contains($value,$substring)">
				<xsl:variable name="substring-after" select="substring-after($value,$substring)"/>
				<xsl:value-of select="$substring-after"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$value"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="return-hostname">
		<xsl:param name="hostname-unformated"/>
		<xsl:call-template name="substring-before">
			<xsl:with-param name="substring">.</xsl:with-param>
			<xsl:with-param name="value">
				<xsl:value-of select="$hostname-unformated"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	<xsl:template name="Bit">
		<xsl:param name="decimal"/>
		<xsl:param name="bit" select="1"/>
		<xsl:choose>
			<xsl:when test="( $decimal mod ( $bit * 2 ) ) -
                      ( $decimal mod ( $bit     ) )">1</xsl:when>
			<xsl:otherwise>0</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="decimal-to-binary">
		<xsl:param name="decimal" select="0"/>
		<xsl:variable name="binary-digit">
			<xsl:call-template name="Bit">
				<xsl:with-param name="decimal" select="$decimal"/>
				<xsl:with-param name="bit" select="$bit7"/>
			</xsl:call-template>
			<xsl:call-template name="Bit">
				<xsl:with-param name="decimal" select="$decimal"/>
				<xsl:with-param name="bit" select="$bit6"/>
			</xsl:call-template>
			<xsl:call-template name="Bit">
				<xsl:with-param name="decimal" select="$decimal"/>
				<xsl:with-param name="bit" select="$bit5"/>
			</xsl:call-template>
			<xsl:call-template name="Bit">
				<xsl:with-param name="decimal" select="$decimal"/>
				<xsl:with-param name="bit" select="$bit4"/>
			</xsl:call-template>
			<xsl:call-template name="Bit">
				<xsl:with-param name="decimal" select="$decimal"/>
				<xsl:with-param name="bit" select="$bit3"/>
			</xsl:call-template>
			<xsl:call-template name="Bit">
				<xsl:with-param name="decimal" select="$decimal"/>
				<xsl:with-param name="bit" select="$bit2"/>
			</xsl:call-template>
			<xsl:call-template name="Bit">
				<xsl:with-param name="decimal" select="$decimal"/>
				<xsl:with-param name="bit" select="$bit1"/>
			</xsl:call-template>
			<xsl:call-template name="Bit">
				<xsl:with-param name="decimal" select="$decimal"/>
				<xsl:with-param name="bit" select="$bit0"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:value-of select="normalize-space($binary-digit)"/>
	</xsl:template>
	<xsl:template name="ip-to-binary">
		<xsl:param name="ip-address"/>
		<xsl:variable name="octet-1">
			<xsl:value-of select="substring-before($ip-address,'.')"/>
		</xsl:variable>
		<xsl:variable name="octet-2">
			<xsl:value-of select="substring-before(substring-after($ip-address,'.'),'.')"/>
		</xsl:variable>
		<xsl:variable name="octet-3">
			<xsl:value-of select="substring-before(substring-after(substring-after($ip-address,'.'),'.'),'.')"/>
		</xsl:variable>
		<xsl:variable name="octet-4">
			<xsl:value-of select="substring-after(substring-after(substring-after($ip-address,'.'),'.'),'.')"/>
		</xsl:variable>
		<xsl:call-template name="decimal-to-binary">
			<xsl:with-param name="decimal" select="$octet-1"/>
		</xsl:call-template>
		<xsl:call-template name="decimal-to-binary">
			<xsl:with-param name="decimal" select="$octet-2"/>
		</xsl:call-template>
		<xsl:call-template name="decimal-to-binary">
			<xsl:with-param name="decimal" select="$octet-3"/>
		</xsl:call-template>
		<xsl:call-template name="decimal-to-binary">
			<xsl:with-param name="decimal" select="$octet-4"/>
		</xsl:call-template>
	</xsl:template>
	<xsl:template name="get-ip-octet">
		<xsl:param name="binary"/>
		<xsl:variable name="ip-octet">
			<xsl:value-of select="number(substring($binary,1,1))*128+(number(substring($binary,2,1))*64)+(number(substring($binary,3,1))*32)+(number(substring($binary,4,1))*16)+(number(substring($binary,5,1))*8)+(number(substring($binary,6,1))*4)+(number(substring($binary,7,1))*2)+(number(substring($binary,8,1))*1)"/>
		</xsl:variable>
		<xsl:value-of select="$ip-octet"/>
	</xsl:template>
	<xsl:template name="binary-to-ip">
		<xsl:param name="binary-ip-address"/>
		<xsl:variable name="octet-1">
			<xsl:call-template name="get-ip-octet">
				<xsl:with-param name="binary" select="substring($binary-ip-address,1,8)"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="octet-2">
			<xsl:call-template name="get-ip-octet">
				<xsl:with-param name="binary" select="substring($binary-ip-address,9,8)"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="octet-3">
			<xsl:call-template name="get-ip-octet">
				<xsl:with-param name="binary" select="substring($binary-ip-address,17,8)"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="octet-4">
			<xsl:call-template name="get-ip-octet">
				<xsl:with-param name="binary" select="substring($binary-ip-address,25,8)"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:value-of select="concat($octet-1, '.', $octet-2, '.', $octet-3, '.', $octet-4)"/>
	</xsl:template>
	<xsl:template name="subnet-to-bitcount">
		<xsl:param name="subnet"/>
		<xsl:variable name="subnet-ip">
			<xsl:call-template name="ip-to-binary">
				<xsl:with-param name="ip-address" select="$subnet"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:choose>
			<xsl:when test="contains($subnet-ip,'0')">
				<xsl:value-of select="string-length(substring-before($subnet-ip,'0'))"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="string-length($subnet-ip)"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="get-network-range">
		<xsl:param name="ip-address"/>
		<xsl:param name="subnet-mask"/>
		<xsl:variable name="zero-ip">00000000000000000000000000000000</xsl:variable>
		<xsl:variable name="bitcount">
			<xsl:call-template name="subnet-to-bitcount">
				<xsl:with-param name="subnet" select="$subnet-mask"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="binary-ip">
			<xsl:call-template name="ip-to-binary">
				<xsl:with-param name="ip-address" select="$ip-address"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="network-range-binary">
			<xsl:value-of select="concat(substring($binary-ip,1,$bitcount),substring($zero-ip,$bitcount + 1, string-length($zero-ip) - $bitcount))"/>
		</xsl:variable>
		<xsl:variable name="network-start-ip">
			<xsl:call-template name="binary-to-ip">
				<xsl:with-param name="binary-ip-address" select="$network-range-binary"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:value-of select="concat($network-start-ip,'/',$bitcount)"/>
	</xsl:template>
	<xsl:template name="return-neighbor-params">
		<xsl:param name="neighborIP"/>
		<xsl:param name="neighborHostname"/>
		<xsl:param name="comm"/>
		<!--Get Neighbor hostname and format it-->
		<parameter>
			<name>Neighbor IP Address</name>
			<value>
				<xsl:value-of select="$neighborIP"/>
			</value>
		</parameter>
		<parameter>
			<name>Neighbor hostname</name>
			<value>
				<xsl:value-of select="$neighborHostname"/>
			</value>
		</parameter>
		<!--Get Neighbor systemDescription-->
		<xsl:variable name="neighborDeviceType">
			<xsl:choose>
			<xsl:when test="$neighborHostname!='' and $comm!=''">
				<xsl:call-template name="determine-device-Type">
					<xsl:with-param name="sysDescr" select="SnmpForXslt:getByOid($neighborIP,'1.3.6.1.2.1.1.1', $comm)"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>DEFAULT</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<parameter>
			<name>Neighbor Device Type</name>
			<value>
				<xsl:value-of select="$neighborDeviceType"/>
			</value>
		</parameter>
	</xsl:template>
	<xsl:template name="determine-device-Type">
		<xsl:param name="sysDescr"/>
		<xsl:choose>
			<xsl:when test="contains($sysDescr, 'Cisco')">CISCO</xsl:when>
			<xsl:when test="contains($sysDescr, 'Huawei')">HUAWEI</xsl:when>
			<xsl:when test="contains($sysDescr, 'Juniper')">JUNIPER</xsl:when>
			<xsl:when test="contains($sysDescr, 'Riverstone')">RIVERSTONE</xsl:when>
            <xsl:when test="contains($sysDescr, 'SevOne')">SevOne</xsl:when>
            <xsl:when test="contains($sysDescr, 'Tellabs')">TELLABS</xsl:when>
			<xsl:when test="contains($sysDescr, 'ProCurve')">HP</xsl:when>
			<xsl:otherwise>DEFAULT</xsl:otherwise>
		</xsl:choose>
		<!--xsl:when test="$sysDescr!=''">UNKNOWN</xsl:when-->
	</xsl:template>
	<xsl:template name="determine-ifType">
		<xsl:param name="ifType"/>
		<xsl:choose>
			<xsl:when test="$ifType='150'">mplsTunnel</xsl:when>
			<xsl:when test="$ifType='166'">mpls</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$ifType"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="ipCidrProtocolResolver">
		<xsl:param name="number"/>
		<xsl:choose>
			<xsl:when test="$number='2'">LOCAL</xsl:when>
			<xsl:when test="$number='3'">STATIC_ROUTE</xsl:when>
			<xsl:when test="$number='4'">ICMP</xsl:when>
			<xsl:when test="$number='5'">EGP</xsl:when>
			<xsl:when test="$number='6'">GGP</xsl:when>
			<xsl:when test="$number='7'">HELLO</xsl:when>
			<xsl:when test="$number='8'">RIP</xsl:when>
			<xsl:when test="$number='9'">ISIS</xsl:when>
			<xsl:when test="$number='10'">ESLS</xsl:when>
			<xsl:when test="$number='11'">IGRP</xsl:when>
			<xsl:when test="$number='12'">BBN</xsl:when>
			<xsl:when test="$number='13'">OSPF</xsl:when>
			<xsl:when test="$number='14'">BGP</xsl:when>
			<xsl:when test="$number='15'">IDPR</xsl:when>
			<xsl:when test="$number='15'">EIGRP</xsl:when>
			<xsl:otherwise>OTHER</xsl:otherwise>
		</xsl:choose>
  </xsl:template>
	<xsl:template name="HexToDecimal">
		<xsl:param name="hexNumber"/>
		<xsl:param name="decimalNumber">0</xsl:param>
		<!-- If there is zero hex digits left, output-->
		<xsl:choose>
			<xsl:when test="$hexNumber">
				<xsl:call-template name="HexToDecimal">
					<xsl:with-param name="decimalNumber" select="($decimalNumber*16)+number(substring-before(substring-after('00/11/22/33/44/55/66/77/88/99/A10/B11/C12/D13/E14/F15/a10/b11/c12/d13/e14/f15/',substring($hexNumber,1,1)),'/'))"/>
					<xsl:with-param name="hexNumber" select="substring($hexNumber,2)"/>
				</xsl:call-template>
			</xsl:when>
			<!-- otherwise multiply, and add the next digit, and recurse -->
			<xsl:otherwise>
				<xsl:value-of select="$decimalNumber"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<!-- If it begins with 0x then parse it for sure, else return it -->
	<xsl:template name="asDecimal">
		<xsl:param name="number"/>
		<xsl:choose>
			<xsl:when test="substring($number,1,2)='0x'">
				<xsl:call-template name="HexToDecimal">
					<xsl:with-param name="hexNumber" select="substring($number,3)"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$number"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:function name="functx:substring-before-last-match" as="xs:string?" xmlns:functx="http://www.functx.com">
		<xsl:param name="arg" as="xs:string?"/>
		<xsl:param name="regex" as="xs:string"/>
		<xsl:sequence select="
   replace($arg,concat('^(.*)',$regex,'.*'),'$1')
 "/>
	</xsl:function>
  <xsl:function name="functx:substring-after-last-match" as="xs:string"
              xmlns:functx="http://www.functx.com" >
  <xsl:param name="arg" as="xs:string?"/>
  <xsl:param name="regex" as="xs:string"/>

  <xsl:sequence select="
   replace($arg,concat('^.*',$regex),'')
 "/>

</xsl:function>
  <xsl:function name="functx:decimal-to-hex" as="xs:string">
  <xsl:param name="decimalNumber"/>
  <xsl:variable name="hexDigits" select="'0123456789ABCDEF'"/>
  <xsl:variable name="upperDigits">
    <xsl:if test="$decimalNumber &gt;= 16">
      <xsl:sequence select="string-join(functx:decimal-to-hex(floor($decimalNumber div 16)), '')"/>
    </xsl:if>
  </xsl:variable>
  <xsl:sequence select="string-join(($upperDigits,substring($hexDigits, ($decimalNumber mod 16) + 1, 1)), '')"/>
</xsl:function>
<xsl:function name="functx:replace-multi" as="xs:string?"
                  xmlns:functx="http://www.functx.com" >
      <xsl:param name="arg" as="xs:string?"/>
      <xsl:param name="changeFrom" as="xs:string*"/>
      <xsl:param name="changeTo" as="xs:string*"/>

      <xsl:sequence select="
       if (count($changeFrom) > 0)
       then functx:replace-multi(
              replace($arg, $changeFrom[1],
                         functx:if-absent($changeTo[1],'')),
              $changeFrom[position() > 1],
              $changeTo[position() > 1])
       else $arg
     "/>

</xsl:function>
<xsl:function name="functx:if-absent" as="item()*"
                  xmlns:functx="http://www.functx.com" >
      <xsl:param name="arg" as="item()*"/>
      <xsl:param name="value" as="item()*"/>

      <xsl:sequence select="
        if (exists($arg))
        then $arg
        else $value
     "/>

</xsl:function>

</xsl:stylesheet>
