<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ discovery-methods.xslt
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

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:java="java" xmlns:SnmpForXslt="net.itransformers.idiscover.discoveryhelpers.xml.SnmpForXslt" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:exslt="http://exslt.org/common" extension-element-prefixes="exslt" xmlns:functx="http://www.functx.com">
	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
	<xsl:template name="IPv6">
		<xsl:param name="ipAdEntAddr"/>
		<xsl:param name="ipv6AddrPfxLength"/>
		<xsl:param name="ipv6AddrType"/>
		<xsl:param name="ipv6AddrAnycastFlag"/>
		<xsl:param name="ipv6AddrStatus"/>
		<xsl:variable name="ipv6Addr" select="functx:substring-before-last-match($ipAdEntAddr,'.')"/>
		<xsl:variable name="ipv6Prefixtemp" select="concat($ipv6Addr,'/')"/>
		<xsl:variable name="ipv6Prefix" select="concat($ipv6Prefixtemp,$ipv6AddrPfxLength)"/>

		<object>
			<name>
				<xsl:value-of select="functx:substring-before-last-match($ipAdEntAddr,'.')"/>/<xsl:value-of select="$ipv6AddrPfxLength"/>
			</name>
			<objectType>IPv6 Address</objectType>
			<parameters>
				<parameter>
					<name>IPv6Address</name>
					<value>
						<xsl:value-of select="$ipv6Addr"/>
					</value>
				</parameter>
				<parameter>
					<name>ipv6AddrPfxLength</name>
					<value>
						<xsl:value-of select="$ipv6AddrPfxLength"/>
					</value>
				</parameter>
				<parameter>
					<name>ipv6Subnet</name>
					<value><xsl:value-of select="SnmpForXslt:getSubnetFromPrefix($ipv6Prefix)"/></value>
				</parameter>

				<parameter>
					<name>ipv6AddrType</name>
					<value>
						<xsl:call-template name="ipv6AddrType">
							<xsl:with-param name="type" select="$ipv6AddrType"/>
						</xsl:call-template>
					</value>
				</parameter>
				<parameter>
					<name>ipv6AddrAnycastFlag</name>
					<value>
						<xsl:call-template name="ipv6AddrAnycastFlag">
							<xsl:with-param name="ipv6AddrAnycastFlag" select="$ipv6AddrAnycastFlag"/>
						</xsl:call-template>
					</value>
				</parameter>
			</parameters>
		</object>
	</xsl:template>
	<xsl:template name="CDP">
		<xsl:param name="cdpIfNeighbors"/>
		<xsl:for-each select="exslt:node-set($cdpIfNeighbors)">
			<xsl:variable name="cdpNeighbor">
				<xsl:call-template name="return-hostname">
					<xsl:with-param name="hostname-unformated" select="cdpCacheDeviceId"/>
				</xsl:call-template>
			</xsl:variable>
			<xsl:variable name="cdpNeighborPort" select="cdpCacheDevicePort"/>
			<xsl:variable name="cdpNeighborPlatform" select="cdpCachePlatform"/>
			<xsl:variable name="cdpCachePrimaryMgmtAddrType" select="cdpCachePrimaryMgmtAddrType"/>
			<xsl:variable name="cdpCacheAddressType" select="cdpCacheAddressType"/>
			<xsl:variable name="cdpCacheAddress" select="cdpCacheAddress"/>

			<xsl:variable name="cdpCachePrimaryMgmtAddr" select="cdpCachePrimaryMgmtAddr"/>
			<xsl:variable name="neighborIP">
				<xsl:choose>
				<xsl:when test="$cdpCachePrimaryMgmtAddrType='1'">
					<xsl:variable name="temp">
						<xsl:for-each select="tokenize($cdpCachePrimaryMgmtAddr,':')">
							<xsl:call-template name="HexToDecimal">
								<xsl:with-param name="hexNumber">
									<xsl:value-of select="."/>
								</xsl:with-param>
							</xsl:call-template>.</xsl:for-each>
					</xsl:variable>
					<xsl:value-of select="functx:substring-before-last-match($temp,'.')"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:variable name="temp">
						<xsl:for-each select="tokenize($cdpCacheAddress,':')">
							<xsl:call-template name="HexToDecimal">
								<xsl:with-param name="hexNumber">
									<xsl:value-of select="."/>
								</xsl:with-param>
							</xsl:call-template>.</xsl:for-each>

					</xsl:variable>
					<xsl:value-of select="functx:substring-before-last-match($temp,'.')"/>

				</xsl:otherwise>
				</xsl:choose>

			</xsl:variable>
			<xsl:variable name="neighID">
				<xsl:call-template name="getNeighID">
					<xsl:with-param name="neighIP" select="$neighborIP"/>
				</xsl:call-template>
			</xsl:variable>
			<xsl:if test="$cdpNeighbor !=''">
				<object>
					<name><xsl:value-of select="$cdpNeighbor"/></name>
					<objectType>Discovered Neighbor</objectType>
					<parameters>
						<parameter>
							<name>Reachable</name>
							<value>
								<xsl:choose>
									<xsl:when test="$neighID!='' and $neighID!=$neighborIP">YES</xsl:when>
									<xsl:otherwise>NO</xsl:otherwise>
								</xsl:choose>
							</value>
						</parameter>

						<parameter>
							<name>Discovery Method</name>
							<value>CDP</value>
						</parameter>
						<parameter>
							<name>Neighbor Port</name>
							<value>
								<xsl:value-of select="$cdpNeighborPort"/>
							</value>
						</parameter>
						<parameter>
							<name>Neighbor Platform</name>
							<value>
								<xsl:value-of select="$cdpNeighborPlatform"/>
							</value>
						</parameter>
						<parameter>
							<name>Neighbor IP Address</name>
							<value>
								<xsl:value-of select="$neighborIP"/>
							</value>
						</parameter>
						<parameter>
							<name>Neighbor hostname</name>
							<value>
								<xsl:value-of select="$cdpNeighbor"/>
							</value>
						</parameter>
						<parameter>
							<name>Neighbor Device Type</name>
							<value><xsl:value-of select="SnmpForXslt:getDeviceType($neighborIP, $neighbourIPDryRun)"/></value>
						</parameter>
					</parameters>
				</object>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="LLDP">
		<xsl:param name="lldpIfNeighbors"/>
		<xsl:variable name="lldpRemSysName" select="$lldpIfNeighbors/../lldpRemSysName"/>
		<xsl:variable name="lldpNeighbor-rough">
			<xsl:choose>
				<xsl:when test="$lldpRemSysName!=''">
					<xsl:value-of select="$lldpRemSysName"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="$lldpIfNeighbors/../lldpRemChassisId"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="lldpNeighbor">
			<xsl:call-template name="return-hostname">
				<xsl:with-param name="hostname-unformated" select="$lldpNeighbor-rough"/>
			</xsl:call-template>
		</xsl:variable>


		<xsl:variable name="neighID">
			<xsl:call-template name="getNeighID">
				<xsl:with-param name="neighIP" select="$lldpNeighbor-rough"/>
			</xsl:call-template>
		</xsl:variable>


		<xsl:variable name="lldpNeighborPort">
			<xsl:value-of select="$lldpIfNeighbors/../lldpRemPortId"/>
		</xsl:variable>
		<xsl:variable name="lldpNeighborPlatform">
			<xsl:value-of select="$lldpIfNeighbors/../lldpRemSysDesc"/>
		</xsl:variable>
		<xsl:if test="$lldpNeighbor !=''">
			<object>
				<name>
					<xsl:value-of select="$lldpNeighbor"/>
				</name>
				<objectType>Discovered Neighbor</objectType>
				<parameters>
					<parameter>
						<name>Reachable</name>
						<value>
							<xsl:choose>
								<xsl:when test="$neighID!='' and $neighID!=$lldpNeighbor-rough">YES</xsl:when>
								<xsl:otherwise>NO</xsl:otherwise>
							</xsl:choose>
						</value>
					</parameter>
					<parameter>
						<name>Discovery Method</name>
						<value>LLDP</value>
					</parameter>
					<parameter>
						<name>Neighbor Port</name>
						<value>
							<xsl:value-of select="$lldpNeighborPort"/>
						</value>
					</parameter>
					<parameter>
						<name>Neighbor Platform</name>
						<value>
							<xsl:value-of select="$lldpNeighborPlatform"/>
						</value>
					</parameter>
					<xsl:call-template name="return-neighbor-params">
						<xsl:with-param name="neighborIP"/>
						<xsl:with-param name="neighborHostname" select="$lldpNeighbor"/>
					</xsl:call-template>
				</parameters>
			</object>
		</xsl:if>
	</xsl:template>
	<xsl:template name="SLASH31">
		<xsl:param name="ipAdEntNetMask"/>
		<xsl:param name="ipAdEntAddr"/>
		<xsl:if test="$ipAdEntNetMask='255.255.255.254'">
			<xsl:variable name="firstOctets">
				<xsl:call-template name="substring-before-last">
					<xsl:with-param name="value" select="$ipAdEntAddr"/>
					<xsl:with-param name="substring">.</xsl:with-param>
				</xsl:call-template>
				<xsl:text>.</xsl:text>
			</xsl:variable>
			<!-- Calculate the other IP address on the Point to Point link -->
			<xsl:variable name="lastOctet" select="number(substring-after($ipAdEntAddr,$firstOctets))"/>
			<xsl:variable name="lastOctetSubnet" select="$lastOctet - ($lastOctet mod 2)"/>
			<xsl:variable name="plusOne" select="concat($firstOctets,number($lastOctetSubnet)+1)"/>
			<xsl:variable name="subnetIP" select="concat($firstOctets,number($lastOctetSubnet))"/>
			<xsl:variable name="otherIp">
				<xsl:choose>
					<xsl:when test="$ipAdEntAddr = $plusOne">
						<xsl:value-of select="$subnetIP"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$plusOne"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>

			<object>
				<name><xsl:value-of select="$otherIp"/></name>
				<objectType>Discovered Neighbor</objectType>
				<parameters>

					<parameter>
						<name>Discovery Method</name>
						<value>Slash31</value>
					</parameter>
					<parameter>
						<name>Local IP address</name>
						<value>
							<xsl:value-of select="$ipAdEntAddr"/>
						</value>
					</parameter>
					<xsl:call-template name="return-neighbor-params">
						<xsl:with-param name="neighborIP" select="$otherIp"/>
						<xsl:with-param name="neighborHostname" />
					</xsl:call-template>
				</parameters>
			</object>
		</xsl:if>
	</xsl:template>
	<xsl:template name="SLASH30">
		<xsl:param name="ipAdEntNetMask"/>
		<xsl:param name="ipAdEntAddr"/>
		<xsl:if test="$ipAdEntNetMask='255.255.255.252'">
			<xsl:variable name="firstOctets">
				<xsl:call-template name="substring-before-last">
					<xsl:with-param name="value" select="$ipAdEntAddr"/>
					<xsl:with-param name="substring">.</xsl:with-param>
				</xsl:call-template>
				<xsl:text>.</xsl:text>
			</xsl:variable>
			<!-- Calculate the other IP address on the Point to Point link -->
			<xsl:variable name="lastOctet" select="number(substring-after($ipAdEntAddr,$firstOctets))"/>
			<xsl:variable name="lastOctetSubnet" select="$lastOctet - ($lastOctet mod 4)"/>
			<xsl:variable name="plusOne" select="concat($firstOctets,number($lastOctetSubnet)+1)"/>
			<xsl:variable name="plusTwo" select="concat($firstOctets,number($lastOctetSubnet)+2)"/>
			<xsl:variable name="otherIp">
				<xsl:choose>
					<xsl:when test="$ipAdEntAddr = $plusOne">
						<xsl:value-of select="$plusTwo"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="$plusOne"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<!--Slash 30-->

			<object>
				<name><xsl:value-of select="$otherIp"/></name>
				<objectType>Discovered Neighbor</objectType>
				<parameters>

					<parameter>
						<name>Discovery Method</name>
						<value>Slash30</value>
					</parameter>
					<parameter>
						<name>Local IP address</name>
						<value>
							<xsl:value-of select="$ipAdEntAddr"/>
						</value>
					</parameter>
                    <xsl:message>DEBUG: SLASH30<xsl:value-of select="$otherIp"/>OTHER IP ADDRESS</xsl:message>

                    <xsl:call-template name="return-neighbor-params">
						<xsl:with-param name="neighborIP" select="$otherIp"/>
						<xsl:with-param name="neighborHostname"/>

					</xsl:call-template>


				</parameters>
			</object>
		</xsl:if>
	</xsl:template>
	<xsl:template name="nextHop">
		<xsl:param name="ipRouteTable"/>
		<xsl:param name="sysName"/>
        <xsl:param name="ipv4addresses"/>
		<xsl:for-each select="distinct-values($ipRouteTable/ipRouteNextHop)">
			<xsl:variable name="next-hop-ip" select="."/>
            <xsl:if test="$next-hop-ip!='' and SnmpForXslt:checkBogons($next-hop-ip)=$next-hop-ip and count($ipv4addresses[ipAdEntAddr=$next-hop-ip])=0 and  count($ipv4addresses[ipv4SubnetBroadcast=$next-hop-ip]) = 0">

					<object>
						<name><xsl:value-of select="$next-hop-ip"/></name>
						<objectType>Discovered Neighbor</objectType>
						<parameters>

                            <parameter>
                                <name>Discovery Method</name>
                                <xsl:variable name="test">
                                    <xsl:for-each select="distinct-values($ipRouteTable/../ipRouteEntry[ipRouteNextHop = $next-hop-ip]/ipRouteProto)">
                                        <xsl:call-template name="ipCidrProtocolResolver">
                                            <xsl:with-param name="number">
                                                <xsl:value-of select="."/>
                                            </xsl:with-param>
                                        </xsl:call-template>
                                        <xsl:text>,</xsl:text>
                                    </xsl:for-each>
                                </xsl:variable>
                                <value>r_<xsl:value-of select="functx:substring-before-last-match($test,'.')"/>
                                </value>
                            </parameter>
							<xsl:call-template name="return-neighbor-params">
								<xsl:with-param name="neighborIP" select="$next-hop-ip"/>
								<xsl:with-param name="neighborHostname"/>
							</xsl:call-template>
						</parameters>
					</object>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="cnextHop">
		<xsl:param name="ipCidrRouteTable"/>
		<xsl:param name="sysName"/>
        <xsl:param name="ipv4addresses"/>
        <xsl:variable name="next-hop-ips" select="distinct-values($ipCidrRouteTable/ipCidrRouteNextHop)"/>
		<xsl:for-each select="$next-hop-ips">
			<xsl:variable name="next-hop-ip" select="."/>
			<xsl:if test="SnmpForXslt:checkBogons($next-hop-ip)=$next-hop-ip and count($ipv4addresses[ipAdEntAddr=$next-hop-ip])=0 and  count($ipv4addresses[ipv4SubnetBroadcast=$next-hop-ip]) = 0 ">




				<xsl:if test="not(contains($ipv4addresses,$next-hop-ip))">
					<object>
						<name><xsl:value-of select="$next-hop-ip"/></name>
						<objectType>Discovered Neighbor</objectType>
						<parameters>
							<parameter>
								<name>Discovery Method</name>
								<xsl:variable name="test">
									<xsl:for-each select="distinct-values($ipCidrRouteTable/../ipCidrRouteEntry[ipCidrRouteNextHop = $next-hop-ip]/ipCidrRouteProto)">
										<xsl:call-template name="ipCidrProtocolResolver">
											<xsl:with-param name="number">
												<xsl:value-of select="."/>
											</xsl:with-param>
										</xsl:call-template>
										<xsl:text>,</xsl:text>
									</xsl:for-each>
								</xsl:variable>
								<value>c_<xsl:value-of select="functx:substring-before-last-match($test,'.')"/>
								</value>
							</parameter>
							<xsl:call-template name="return-neighbor-params">
								<xsl:with-param name="neighborIP" select="$next-hop-ip"/>
								<xsl:with-param name="neighborHostname"/>
							</xsl:call-template>
						</parameters>
					</object>
				</xsl:if>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="ARP">
		<xsl:param name="ipNetToMediaIfNeighbors"/>
		<xsl:param name="sysName"/>
        <xsl:param name="ipv4addresses"/>
        <xsl:for-each select="$ipNetToMediaIfNeighbors">
			<xsl:variable name="ipNetToMediaNetAddress">
				<xsl:value-of select="ipNetToMediaNetAddress"/>
			</xsl:variable>
            <xsl:if test="$ipNetToMediaNetAddress">
                <xsl:if test="SnmpForXslt:checkBogons($ipNetToMediaNetAddress)=$ipNetToMediaNetAddress and count($ipv4addresses[ipAdEntAddr=$ipNetToMediaNetAddress])=0 and count($ipv4addresses[ipv4Subnet=$ipNetToMediaNetAddress]) = 0 and  count($ipv4addresses[ipv4SubnetBroadcast=$ipNetToMediaNetAddress]) = 0 ">

                    <!--<xsl:variable name="neighID">-->
                        <!--<xsl:call-template name="getNeighID">-->
                            <!--<xsl:with-param name="neighIP" select="$ipNetToMediaNetAddress"/>-->
                        <!--</xsl:call-template>-->
                    <!--</xsl:variable>-->
                    <xsl:message>DEBUG: ARP<xsl:value-of select="$ipNetToMediaNetAddress"/></xsl:message>
                    <xsl:if test="count($ipv4addresses[ipAdEntAddr=$ipNetToMediaNetAddress])=0">
                        <object>
							<name><xsl:value-of select="$ipNetToMediaNetAddress"/></name>
                            <objectType>Discovered Neighbor</objectType>
                            <parameters>

                                <parameter>
                                    <name>Discovery Method</name>
                                    <value>ARP</value>
                                </parameter>
                                <parameter>
                                    <name>Neighbor MAC Address</name>
                                    <value>
                                        <xsl:value-of select="ipNetToMediaPhysAddress"/>
                                    </value>
                                </parameter><xsl:call-template name="return-neighbor-params">
                                    <xsl:with-param name="neighborIP" select="$ipNetToMediaNetAddress"/>
                                    <xsl:with-param name="neighborHostname" />
                                </xsl:call-template></parameters>
                        </object>
                    </xsl:if>
                  </xsl:if>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="MAC">
		<xsl:param name="neighborMACAddress"/>
		<xsl:param name="neighborIPAddress"/>
        <xsl:if test="$neighborMACAddress !=''">

			<!--<xsl:variable name="neighID">-->
				<!--<xsl:call-template name="getNeighID">-->
					<!--<xsl:with-param name="neighIP" select="$neighborIPAddress"/>-->
				<!--</xsl:call-template>-->
			<!--</xsl:variable>-->
			<object>
				<objectType>Discovered Neighbor</objectType>
				<xsl:choose>
					<xsl:when test="$neighborIPAddress!=''">
						<name><xsl:value-of select="$neighborIPAddress"/></name>
						<parameters>

							<parameter>
								<name>Discovery Method</name>
								<value>MAC</value>
							</parameter>
							<parameter>
								<name>Neighbor MAC Address</name>
								<value>
									<xsl:value-of select="$neighborMACAddress"/>
								</value>
							</parameter>
							<xsl:call-template name="return-neighbor-params">
								<xsl:with-param name="neighborIP" select="$neighborIPAddress"/>
								<xsl:with-param name="neighborHostname" />
							</xsl:call-template>
						</parameters>
					</xsl:when>
					<xsl:otherwise>
						<name><xsl:value-of select="$neighborMACAddress"/></name>
						<parameters>
							<parameter>
								<name>Reachable</name>
								<value>NO</value>
							</parameter>
							<parameter>
								<name>Discovery Method</name>
								<value>MAC</value>
							</parameter>
							<parameter>
								<name>Neighbor MAC Address</name>
								<value>
									<xsl:value-of select="$neighborMACAddress"/>
								</value>
                            </parameter>
							<parameter>
								<name>Neighbor IP Address</name>
								<value/>
							</parameter>
							<parameter>
								<name>Neighbor Hostname</name>
								<value/>
							</parameter>
                            <parameter>
                                <name>Neighbor Device Type</name>
                                <value>UNKNOWN</value>
                            </parameter>
						</parameters>
					</xsl:otherwise>
				</xsl:choose>
			</object>
		</xsl:if>
	</xsl:template>
	<xsl:template name="OSPF">
		<xsl:param name="ospfNbr"/>
		<xsl:variable name="ospfNbrIpAddr">
			<xsl:value-of select="$ospfNbr/ospfNbrIpAddr"/>
		</xsl:variable>
		<xsl:if test="$ospfNbrIpAddr!=''">
			<!--<xsl:variable name="neighID">-->
				<!--<xsl:call-template name="getNeighID">-->
					<!--<xsl:with-param name="neighIP" select="$ospfNbrIpAddr"/>-->
				<!--</xsl:call-template>-->
			<!--</xsl:variable>-->


			<object>

				<name><xsl:value-of select="$ospfNbrIpAddr"/></name>
				<objectType>Discovered Neighbor</objectType>
				<parameters>

					<parameter>
						<name>Local IP address</name>
						<value><xsl:value-of select="$ospfNbr/ospfIfIpAddress"/></value>
					</parameter>
					<parameter>
						<name>OSPF Area</name>
						<value><xsl:value-of select="$ospfNbr/ospfIfAreaId"/></value>
					</parameter>
					<parameter>
						<name>Discovery Method</name>
						<value>OSPF</value>
					</parameter>
					<xsl:call-template name="return-neighbor-params">
						<xsl:with-param name="neighborIP" select="$ospfNbrIpAddr"/>
						<xsl:with-param name="neighborHostname"/>
					</xsl:call-template>
				</parameters>
			</object>
		</xsl:if>
	</xsl:template>
	<xsl:template name="BGP" match="bgpPeerEntry">
		<xsl:param name="bgpPeer"/>
		<xsl:variable name="bgpPeerRemoteAddr">
			<xsl:value-of select="$bgpPeer/bgpPeerRemoteAddr"/>
		</xsl:variable>
		<xsl:if test="$bgpPeerRemoteAddr!=''">

			<object>
			<name><xsl:value-of select="$bgpPeerRemoteAddr"/></name>
			<objectType>Discovered Neighbor</objectType>
			<parameters>

				<parameter>
					<name>Discovery Method</name>
					<value>BGP</value>
				</parameter>
				<parameter>
					<name>bgpPeerRemoteAs</name>
					<value>
						<xsl:value-of select="$bgpPeer/bgpPeerRemoteAs"/>
					</value>
				</parameter>
				<parameter>
					<name>bgpPeerState</name>
					<value>
						<xsl:value-of select="$bgpPeer/bgpPeerState"/>
					</value>
				</parameter>
                <parameter>
                    <name>Local IP address</name>
                    <value><xsl:value-of select="$bgpPeer/bgpPeerLocalAddr"/></value>
                </parameter>
				<parameter>
					<name>bgpPeerAdminStatus</name>
					<value>
						<xsl:value-of select="$bgpPeer/bgpPeerAdminStatus"/>
					</value>
				</parameter><xsl:call-template name="return-neighbor-params">
					<xsl:with-param name="neighborIP" select="$bgpPeerRemoteAddr"/>
					<xsl:with-param name="neighborHostname" />
				</xsl:call-template></parameters>

			</object>
		</xsl:if>
	</xsl:template>

	<xsl:template name="IPSEC-Phase2">
		<xsl:param name="cipSecTunnelTable"/>
		<xsl:param name="sysName"/>
		<xsl:param name="ipv4addresses"/>
		<xsl:for-each select="$cipSecTunnelTable/cipSecTunnelEntry">
			<xsl:copy-of select="."/>

			<xsl:variable name="localAddress"><xsl:variable name="temp"><xsl:for-each select="tokenize(cipSecTunLocalAddr,':')"><xsl:call-template name="HexToDecimal"><xsl:with-param name="hexNumber"><xsl:value-of select="."/></xsl:with-param></xsl:call-template>.</xsl:for-each>
			</xsl:variable><xsl:value-of select="functx:substring-before-last-match($temp,'.')"/>
			</xsl:variable>
			<xsl:variable name="next-hop-ip"><xsl:variable name="temp"><xsl:for-each select="tokenize(cipSecTunRemoteAddr,':')"><xsl:call-template name="HexToDecimal"><xsl:with-param name="hexNumber"><xsl:value-of select="."/></xsl:with-param></xsl:call-template>.</xsl:for-each>
			</xsl:variable>
				<xsl:value-of select="functx:substring-before-last-match($temp,'.')"/>
			</xsl:variable>

			<xsl:variable name="status">
				<xsl:choose>
					<xsl:when test="cipSecTunIkeTunnelAlive = '1'">UP</xsl:when>
					<xsl:otherwise>DOWN</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>

			<xsl:if test="SnmpForXslt:checkBogons($next-hop-ip)=$next-hop-ip and count($ipv4addresses/ipAddrEntry[ipAdEntAddr=$next-hop-ip])=0 and  count($ipv4addresses/ipAddrEntry[ipv4SubnetBroadcast=$next-hop-ip]) = 0">

				<!--<xsl:variable name="neighID">-->
					<!--<xsl:call-template name="getNeighID">-->
						<!--<xsl:with-param name="neighIP" select="$next-hop-ip"/>-->
					<!--</xsl:call-template>-->
				<!--</xsl:variable>     -->

				<xsl:if test="$next-hop-ip!=$sysName">
				<object>
					<name><xsl:value-of select="$next-hop-ip"/></name>
					<objectType>Discovered Neighbor</objectType>
					<parameters>

						<parameter>
							<name>Discovery Method</name>
							<value>IPSEC-Phase2</value>
						</parameter>
						<xsl:call-template name="return-neighbor-params">
							<xsl:with-param name="neighborIP" select="$next-hop-ip"/>
							<xsl:with-param name="neighborHostname" />
						</xsl:call-template>
					</parameters>
					<parameter>
						<name>Local IP address</name>
						<value><xsl:value-of select="$localAddress"/></value>
					</parameter>
					<parameter>
						<name>Tunnel Status</name>
						<value><xsl:value-of select="$status"/></value>
					</parameter>
				</object>
			</xsl:if>
			</xsl:if>
		</xsl:for-each>

	</xsl:template>


	<xsl:template name="IPSEC-Phase1">
		<xsl:param name="cikeTunnelTable"/>
		<xsl:param name="sysName"/>
		<xsl:param name="ipv4addresses"/>
		<xsl:for-each select="$cikeTunnelTable/cikeTunnelEntry">
			<xsl:copy-of select="."/>

			<xsl:variable name="localAddress"><xsl:variable name="temp"><xsl:for-each select="tokenize(cikeTunLocalAddr,':')"><xsl:call-template name="HexToDecimal"><xsl:with-param name="hexNumber"><xsl:value-of select="."/></xsl:with-param></xsl:call-template>.</xsl:for-each>
			</xsl:variable><xsl:value-of select="functx:substring-before-last-match($temp,'.')"/>
			</xsl:variable>
			<xsl:variable name="next-hop-ip"><xsl:variable name="temp"><xsl:for-each select="tokenize(cikeTunRemoteAddr,':')"><xsl:call-template name="HexToDecimal"><xsl:with-param name="hexNumber"><xsl:value-of select="."/></xsl:with-param></xsl:call-template>.</xsl:for-each>
			</xsl:variable>
				<xsl:value-of select="functx:substring-before-last-match($temp,'.')"/>
			</xsl:variable>

			<xsl:variable name="status">
				<xsl:choose>
					<xsl:when test="cikeTunStatus = '1'">UP</xsl:when>
					<xsl:otherwise>DOWN</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>

			<xsl:if test="SnmpForXslt:checkBogons($next-hop-ip)=$next-hop-ip and count($ipv4addresses/ipAddrEntry[ipAdEntAddr=$next-hop-ip])=0 and  count($ipv4addresses/ipAddrEntry[ipv4SubnetBroadcast=$next-hop-ip]) = 0">

				<xsl:variable name="neighID">
					<xsl:call-template name="getNeighID">
						<xsl:with-param name="neighIP" select="$next-hop-ip"/>
					</xsl:call-template>
				</xsl:variable>

				<xsl:if test="$neighID!=$sysName">
					<object>
						<name><xsl:value-of select="$neighID"/></name>
						<objectType>Discovered Neighbor</objectType>
						<parameters>
							<parameter>
								<name>Discovery Method</name>
								<value>IPSEC-Phase1</value>
							</parameter>
							<xsl:call-template name="return-neighbor-params">
								<xsl:with-param name="neighborIP" select="$next-hop-ip"/>
								<xsl:with-param name="neighborHostname" />
							</xsl:call-template>
						</parameters>
						<parameter>
							<name>Local IP address</name>
							<value><xsl:value-of select="$localAddress"/></value>
						</parameter>
						<parameter>
							<name>Tunnel Status</name>
							<value><xsl:value-of select="$status"/></value>
						</parameter>
					</object>
				</xsl:if>
			</xsl:if>
		</xsl:for-each>

	</xsl:template>
</xsl:stylesheet>
