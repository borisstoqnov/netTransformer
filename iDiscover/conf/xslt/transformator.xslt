<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ transformator.xslt
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
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:fn="http://www.w3.org/2005/xpath-functions"
                xmlns:SnmpForXslt="net.itransformers.idiscover.discoveryhelpers.xml.SnmpForXslt"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:exslt="http://exslt.org/common"
                extension-element-prefixes="exslt" xmlns:functx="http://www.functx.com"
                xmlns:IPv6formatConvertor="net.itransformers.idiscover.util.IPv6formatConvertor"
                xmlns:InterfaceNeighbor="net.itransformers.idiscover.discoveryhelpers.xml.InterfaceNeighbor">
    <xsl:output method="xml" omit-xml-declaration="yes"/>
    <xsl:param name="ipAddress"/>
    <xsl:param name="status"/>
    <xsl:param name="community-ro"/>
    <xsl:param name="community-rw"/>
    <xsl:param name="timeout"/>
    <xsl:param name="retries"/>
    <xsl:param name="neighbourIPDryRun"/>

    <xsl:include href="utils.xslt"/>
    <xsl:include href="discovery-methods.xslt"/>
    <xsl:include href="interfaces.xslt"/>

    <xsl:variable name="comm" select="$community-ro"/>
    <xsl:variable name="comm2" select="$community-rw"/>
    <xsl:variable name="deviceIPv4Address" select="$ipAddress"/>
    <xsl:variable name="dev_state" select="$status"/>


    <xsl:template match="/">
        <!--
    <xsl:output method="xml" omit-xml-declaration="yes"/>
    This file transforms the raw-device data to the common object oriented model used by snmpDiscovery manager for representing
    different devices architecture.
    The current model consist of a device that has:
     1. Several common device parameters
     2. Several device objects including:
        2.1 Objects that represent device interfaces
        2.1.1 Under some of the interfaces there are objects that represent one or more addresses connfigured under the interface.
        2.2.2 Neighbors found by the snmpDiscovery methods under the particular interface. Each time the neighbor address is identified
         a snmp-get is perform in order to obtain neighbor hostname. Currently the following snmpDiscovery methods are supported:

        2.2.2.1 MAC address table neighbors. This table represent L2 Neighbors in Ethernet network. Those neighbors are identified by
        MAC address and physical interface index. Unfortunately it does not contain neighbor IP address. So to find it a cross check
        is performed against the IPv4 ARP table. Neighbors that could not be obtained are marked with the name "Unknown - MAC addreess".

        2.2.2.2 ARP address table neighbors - Neighbors here are identified by a MAC, IP and interface index. It is important to note
        that those indexes represent in many cases represent device logical interfaces (e.g vlan interfaces).

        2.2.2.3 Cisco Discovery Protocol neighbors - Cisco proprietary snmpDiscovery protocol - one of the most reliable methods for physical
        network topology snmpDiscovery. Supported by most of the Cisco devices but also by some others e.g HP Procurve switches. Note that HP is
        able to see Cisco but Cisco is not able to see HP. The good stuff of that protocol is that it provide information about neighbor
        platform and current device interface pointing to the neighbor.
        2.2.2.4 Local Link Discovery Protocol - IEEE standardized snmpDiscovery protocol. Pretty much same as CDP.Note that LLDP MIB is still
         a draft and therefore might cause some problems.
        2.2.2.5 SLASH30/31- This method use Interface IP address to calculate the IP address on the other side of the point to point link.
        2.2.2.6 Next Hops from routing Table
        2.2.2.7  Next Hops from ipCidrRouteTable
        2.3 Device Logical Data - section that represent data related to the current device unrelated to the physical setup of the device.
        Such might be routing protocol neighbors or device configuration or something else.
        2.3.1 OSPF Neighbors - Neighbors found by OSPF routing protocol.
        2.3.2 BGP Neighbors - Neighbors found by BGP routing protocol.
        -->


        <xsl:variable name="dot1dStpDesignatedRoot"
                      select="//root/iso/org/dod/internet/mgmt/mib-2/dot1dBridge/dot1dStp/dot1dStpDesignatedRoot"/>
        <xsl:variable name="baseBridgeAddress"
                      select="//root/iso/org/dod/internet/mgmt/mib-2/dot1dBridge/dot1dBase/dot1dBaseBridgeAddress"/>
        <!--Format hostname. For example if we have R1.test.com it will strip .test.com and will return only R1. This comes form the issue
that there is a bug in CDP and on some Cisco routers we might see the neighbor hostname as R1.test.co so those hostname will mismatch
from the one obtained by snmp or other snmpDiscovery methods.-->
        <xsl:variable name="sysName">
            <xsl:call-template name="return-hostname">
                <xsl:with-param name="hostname-unformated"
                                select="//root/iso/org/dod/internet/mgmt/mib-2/system/sysName"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="hostname">
            <xsl:choose>
                <xsl:when test="$sysName!=''">
                    <xsl:value-of select="$sysName"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:variable name="temp">
                        <xsl:call-template name="return-hostname">
                            <xsl:with-param name="hostname-unformated"
                                            select="SnmpForXslt:getName($deviceIPv4Address,$neighbourIPDryRun)"/>
                        </xsl:call-template>
                    </xsl:variable>
                    <xsl:value-of select="$temp"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <!--<xsl:message>DEBUG:<xsl:value-of select="$hostname"/></xsl:message>-->
        <xsl:variable name="sysDescr">
            <xsl:value-of select="//root/iso/org/dod/internet/mgmt/mib-2/system/sysDescr"/>
        </xsl:variable>
        <xsl:variable name="sysOr" select="//root/iso/org/dod/internet/mgmt/mib-2/system/sysObjectID"/>
        <xsl:variable name="deviceType">
            <xsl:call-template name="determine-device-Type">
                <xsl:with-param name="sysDescr" select="$sysDescr"/>
                <xsl:with-param name="sysOr" select="$sysOr"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="IPv6">
            <xsl:choose>
                <xsl:when test="//root/iso/org/dod/internet/private/enterprises/cisco/ciscoExperiment/ciscoIetfIpMIB/ciscoIetfIpMIBObjects/cIpv6/cIpv6Forwarding = '1'">YES</xsl:when>
                <xsl:when test="count(//root/iso/org/dod/internet/mgmt/mib-2/ipv6MIB/ipv6MIBObjects/ipv6AddrTable/ipv6AddrEntry) > 0">YES</xsl:when>
                <xsl:otherwise>NO</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="firstMacAddress" select="(/root/iso/org/dod/internet/mgmt/mib-2/interfaces/ifTable/ifEntry[ifPhysAddress!='']/ifPhysAddress)[1]"/>
        <xsl:variable name="secondMacAddress" select="(/root/iso/org/dod/internet/mgmt/mib-2/interfaces/ifTable/ifEntry[ifPhysAddress!='']/ifPhysAddress)[2]"/>
        <xsl:variable name="macAddress">
            <xsl:choose>
            <xsl:when test="$firstMacAddress = '00:00:00:00:00:00' or $firstMacAddress=''"><xsl:value-of select="$secondMacAddress"/></xsl:when>
            <xsl:otherwise><xsl:value-of select="$firstMacAddress"/></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:variable name="mplsVRF">
            <root1>
                <xsl:for-each
                        select="//root/iso/org/dod/internet/experimental/mplsVpnMIB/mplsVpnObjects/mplsVpnConf/mplsVpnInterfaceConfTable/mplsVpnInterfaceConfEntry/instance">
                    <xsl:variable name="test">
                        <xsl:call-template name="substring-before-last">
                            <xsl:with-param name="substring">.</xsl:with-param>
                            <xsl:with-param name="value" select="substring-after(.,'.')"/>
                        </xsl:call-template>
                    </xsl:variable>
                    <xsl:variable name="name">
                        <xsl:for-each select="tokenize($test,'\.')">
                            <xsl:value-of select="codepoints-to-string(xs:integer(.))"/>
                        </xsl:for-each>
                    </xsl:variable>
                    <xsl:variable name="index">
                        <xsl:call-template name="substring-after">
                            <xsl:with-param name="substring">
                                <xsl:call-template name="substring-before-last">
                                    <xsl:with-param name="value" select="."/>
                                    <xsl:with-param name="substring">.</xsl:with-param>
                                </xsl:call-template>
                            </xsl:with-param>
                            <xsl:with-param name="value" select="."/>
                        </xsl:call-template>
                    </xsl:variable>
                    <test>
                        <name>
                            <xsl:value-of select="$name"/>
                        </name>
                        <index>
                            <xsl:value-of select="$index"/>
                        </index>
                    </test>
                </xsl:for-each>
            </root1>
        </xsl:variable>
        <!--<key id="hostname" for="node" attr.name="Device Hostname" attr.type="string"/>-->
        <!--<key id="deviceModel" for="node" attr.name="Device Model" attr.type="string"/>-->
        <!--<key id="deviceType" for="node" attr.name="Device Type" attr.type="string"/>-->
        <!--<key id="nodeInfo" for="node" attr.name="Node Information " attr.type="string"/>-->
        <!--<key id="discoveredIPv4Address" for="node" attr.name="Discovered IPv4 Address" attr.type="string"/>-->
        <!--<key id="discoveredState" for="node" attr.name="Discovered State" attr.type="string"/>-->
        <!--<key id="sysLocation" for="node" attr.name="Location by SNMP" attr.type="string"/>-->
        <!--<key id="site" for="node" attr.name="site" attr.type="string"/>-->
        <!--<key id="diff" for="node" attr.name="diff" attr.type="string"/>-->
        <!--<key id="diffs" for="node" attr.name="diffs" attr.type="string"/>-->
        <!--<key id="ipv6Forwarding" for="node" attr.name="IP v6 Forwarding" attr.type="string"/>-->
        <!--<key id="ipv4Forwarding" for="node" attr.name="IP v4 Forwarding" attr.type="string"/>-->
        <!--<key id="subnetPrefix" for="node" attr.name="Subnet Prefix" attr.type="string"/>-->
        <!--<key id="ipProtocolType" for="node" attr.name="IP Protocol Type" attr.type="string"/>-->
        <!--<key id="bgpAS" for="node" attr.name="BGP Autonomous system" attr.type="string"/>-->
        <!--<key id="totalInterfaceCount" for="node" attr.name="Total Interface Count" attr.type="string"/>-->

        <!--<key id="discoveryMethod" for="edge" attr.name="Discovery Method" attr.type="string"/>-->
        <!--<key id="dataLink" for="edge" attr.name="dataLink" attr.type="string"/>-->
        <!--<key id="ipLink" for="edge" attr.name="IP Link" attr.type="string"/>-->
        <!--<key id="MPLS" for="edge" attr.name="MPLS" attr.type="string"/>-->
        <!--<key id="ipv6Forwarding" for="edge" attr.name="IP v6 Forwarding" attr.type="string"/>-->
        <!--<key id="ipv4Forwarding" for="edge" attr.name="IP v4 Forwarding" attr.type="string"/>-->
        <!--<key id="interface" for="edge" attr.name="interface" attr.type="string"/>-->
        <!--<key id="diff" for="edge" attr.name="diff" attr.type="string"/>-->
        <!--<key id="diffs" for="edge" attr.name="diffs" attr.type="string"/>-->
        <!--<key id="encapsulation" for="edge" attr.name="L2 encapsulation" attr.type="string"/>-->
        <!--<key id="speed" for="edge" attr.name="Port Speed" attr.type="string"/>-->


        <DiscoveredDevice>
            <name>
                <xsl:value-of select="$hostname"/>
            </name>
            <!-- Device specific parameters-->
            <parameters>
                <parameter>
                    <name>discoveredState</name>
                    <value>discovered</value>
                </parameter>
                <!--Parameter that contain device sysDescr e.g info about device OS and particular image-->
                <parameter>
                    <name>sysDescr</name>
                    <value>
                        <xsl:value-of select="$sysDescr"/>
                    </value>
                </parameter>
                <parameter>
                    <name>deviceType</name>
                    <value>
                        <xsl:value-of select="$deviceType"/>
                    </value>
                </parameter>
                <!--
                OID that represents exact device model for most of the devices. Once identified the OID shall be identified in the
                VENDOR-PRODUCTS-MIB.
                TODO: Currently only CISCO and Juniper are supported!!!
                -->
                <xsl:variable name="oid">
                    <xsl:value-of select="//root/iso/org/dod/internet/mgmt/mib-2/system/sysObjectID"/>
                </xsl:variable>
                <parameter>
                    <name>deviceModel</name>
                    <value><xsl:choose><xsl:when test="$deviceType='CISCO'"><xsl:value-of select="SnmpForXslt:getSymbolByOid('CISCO-PRODUCTS-MIB', $oid)"/></xsl:when><xsl:when test="$deviceType='JUNIPER'"><xsl:value-of select="SnmpForXslt:getSymbolByOid('JUNIPER-CHASSIS-DEFINES-MIB', $oid)"/></xsl:when><xsl:otherwise>Unknown</xsl:otherwise></xsl:choose></value>
                </parameter>
                <parameter>
                    <name>ipAddress</name>
                    <value>
                        <xsl:value-of select="$deviceIPv4Address"/>
                    </value>
                </parameter>
                <parameter>
                    <name>MacAddress</name>
                    <value><xsl:value-of select="$macAddress"/></value>
                </parameter>
                <parameter>
                    <name>totalInterfaceCount</name>
                    <value><xsl:value-of select="//root/iso/org/dod/internet/mgmt/mib-2/interfaces/ifNumber"/></value>
                </parameter>
                <parameter>
                    <name>ipv6Forwarding</name>
                    <value>
                        <xsl:value-of select="$IPv6"/>
                    </value>
                </parameter>
                <parameter>
                    <name>ipv4Forwarding</name>
                    <value>YES</value>
                </parameter>
                <parameter>
                    <name>deviceModelOid</name>
                    <value><xsl:value-of select="$oid"/></value>
                </parameter>
                <xsl:variable name="bgpAS" select="//root/iso/org/dod/internet/mgmt/mib-2/bgp/bgpLocalAs"/>
                <xsl:if test="$bgpAS !='0' and $bgpAS !='' and not(contains($bgpAS,'days'))">
                    <parameter>
                        <name>bgpASInfo</name>
                        <value>
                            <xsl:value-of select="$bgpAS"/>
                        </value>
                    </parameter>
                </xsl:if>

                <!--Those two addresses has intention to STP process. Some devices do not have STP enables so for those might contain
               some boolshit-->
                <xsl:if test="$baseBridgeAddress!='' and not(contains($baseBridgeAddress,'days'))">
                    <parameter>
                        <name>baseBridgeAddress</name>
                        <value>
                            <xsl:value-of select="$baseBridgeAddress"/>
                        </value>
                    </parameter>
                </xsl:if>
                <xsl:if test="$dot1dStpDesignatedRoot!='' and not(contains($dot1dStpDesignatedRoot,'days'))">

                <parameter>
                    <name>stpDesignatedRoot</name>
                    <value>
                        <xsl:value-of select="$dot1dStpDesignatedRoot"/>
                    </value>
                </parameter>
                </xsl:if>
                <xsl:variable name="sysLocation" select="//root/iso/org/dod/internet/mgmt/mib-2/system/sysLocation"/>
                <parameter>
                    <name>sysLocation</name>
                    <value><xsl:value-of select="$sysLocation"/></value>
                </parameter>
            </parameters>
            <!--Walk over the interface ifTable table.-->
            <xsl:for-each select="//root/iso/org/dod/internet/mgmt/mib-2/interfaces/ifTable/ifEntry">
                <xsl:variable name="ifIndex">
                    <xsl:value-of select="ifIndex"/>
                </xsl:variable>
                <xsl:variable name="ifInstanceIndex" select="instance/@instanceIndex"/>
                <xsl:variable name="ifAdminStatus">
                    <xsl:call-template name="adminStatus">
                        <xsl:with-param name="status" select="ifAdminStatus"/>
                    </xsl:call-template>
                </xsl:variable>
                <xsl:variable name="ifOperStatus">
                    <xsl:call-template name="operStatus">
                        <xsl:with-param name="status" select="ifOperStatus"/>
                    </xsl:call-template>
                </xsl:variable>
                <xsl:variable name="ifName">
                    <xsl:value-of select="/root/iso/org/dod/internet/mgmt/mib-2/ifMIB/ifMIBObjects/ifXTable/ifXEntry[instance=$ifIndex]/ifName"/>
                </xsl:variable>
                <xsl:variable name="ifDescr">
                    <xsl:value-of select="ifDescr"/>
                </xsl:variable>
                <xsl:variable name="ifType">
                    <xsl:value-of select="ifType"/>
                </xsl:variable>
                <xsl:variable name="ifSpeed">
                   <xsl:value-of select="//root/iso/org/dod/internet/mgmt/mib-2/ifMIB/ifMIBObjects/ifXTable/ifXEntry[instance=$ifIndex]/ifHighSpeed"/>
                </xsl:variable>
                <xsl:variable name="ifPhysAddress">
                    <xsl:value-of select="ifPhysAddress"/>
                </xsl:variable>
                <xsl:variable name="IPv4Forwarding">
                    <xsl:choose>
                        <xsl:when test="count(//root/iso/org/dod/internet/mgmt/mib-2/ip/ipAddrTable/ipAddrEntry[ipAdEntIfIndex=$ifIndex])>0">YES</xsl:when>
                        <xsl:otherwise>NO</xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:variable name="IPv6Forwarding">
                    <xsl:choose>
                        <xsl:when test="count(//root/iso/org/dod/internet/mgmt/mib-2/ipv6MIB/ipv6MIBObjects/ipv6AddrTable/ipv6AddrEntry[index=$ifIndex]/instance)>0">YES</xsl:when>
                        <xsl:when test="count(/root/iso/org/dod/internet/private/enterprises/cisco/ciscoExperiment/ciscoIetfIpMIB/ciscoIetfIpMIBObjects/cIpv6/cIpv6InterfaceTable/cIpv6InterfaceEntry[index[@name='cIpv6InterfaceIfIndex']=$ifIndex])>0">YES</xsl:when>
                        <xsl:otherwise>NO</xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <xsl:variable name="vrfForwarding">
                    <xsl:value-of
                            select="$mplsVRF/root1//test[substring-after(index,'.')= $ifIndex]/name"/>
                </xsl:variable>
                <xsl:message>TRACE:>ifDescr<xsl:value-of select="$ifDescr"/> ifIndex<xsl:value-of select="$ifIndex"/> ifType <xsl:value-of select="$ifType"/></xsl:message>
                <!-- Neighbors and IP addresses are obtained only for the interfaces that are up and running.
If the Admin status is UP and Operational is down the interface is marked as Cable CUT !-->
                        <object>
                            <name>
                                <xsl:value-of select="$ifDescr"/>
                            </name>
                            <objectType>Discovery Interface</objectType>
                            <xsl:call-template name="interfaceParameters">
                                <xsl:with-param name="ifDescr" select="$ifDescr"/>
                                <xsl:with-param name="ifIndex" select="$ifIndex"/>
                                <xsl:with-param name="ifName" select="$ifName"/>
                                <xsl:with-param name="ifType" select="$ifType"/>
                                <xsl:with-param name="ifSped" select="$ifSpeed"/>
                                <xsl:with-param name="ifPhysicalAddress" select="$ifPhysAddress"/>
                                <xsl:with-param name="ifAdminStatus" select="$ifAdminStatus"/>
                                <xsl:with-param name="ifOperStatus" select="$ifOperStatus"/>
                                <xsl:with-param name="IPv4Forwarding" select="$IPv4Forwarding"/>
                                <xsl:with-param name="IPv6Forwarding" select="$IPv6Forwarding"/>
                                <xsl:with-param name="vrfForwarding" select="$vrfForwarding"/>
                            </xsl:call-template>
                            <!--Check for  IPv4 IP addresses-->
                            <xsl:variable name="ipv4Addresses">
                                <ipv4>
                                    <xsl:for-each
                                            select="//root/iso/org/dod/internet/mgmt/mib-2/ip/ipAddrTable/ipAddrEntry[ipAdEntIfIndex=$ifIndex]">
                                        <xsl:variable name="ipAdEntAddr" select="ipAdEntAddr"/>
                                        <xsl:variable name="ipAdEntNetMask" select="ipAdEntNetMask"/>
                                        <xsl:variable name="subnetBitCount"><xsl:call-template name="subnet-to-bitcount"><xsl:with-param
                                                name="subnet" select="$ipAdEntNetMask"/></xsl:call-template></xsl:variable>
                                        <xsl:variable name="ipPrefix"><xsl:value-of select="$ipAdEntAddr"/>/<xsl:value-of select="$subnetBitCount"/></xsl:variable>

                                        <ipv4addr>
                                            <ipAdEntAddr><xsl:value-of select="ipAdEntAddr"/></ipAdEntAddr>
                                            <ipAdEntNetMask><xsl:value-of select="ipAdEntNetMask"/></ipAdEntNetMask>
                                            <ipPrefix><xsl:value-of select="$ipAdEntAddr"/>/<xsl:value-of select="$subnetBitCount"/></ipPrefix>
                                            <subnetBitCount><xsl:call-template name="subnet-to-bitcount"><xsl:with-param
                                                    name="subnet" select="$ipAdEntNetMask"/></xsl:call-template></subnetBitCount>
                                            <ipv4Subnet><xsl:value-of select="SnmpForXslt:getSubnetFromPrefix($ipPrefix)"/></ipv4Subnet>
                                            <ipv4SubnetBroadcast><xsl:value-of select="SnmpForXslt:getBroadCastFromPrefix($ipPrefix)"/></ipv4SubnetBroadcast>
                                        </ipv4addr>
                                    </xsl:for-each>
                                </ipv4>
                            </xsl:variable>
                            <xsl:message>TRACE:ipv4Addresses<xsl:value-of select="$ipv4Addresses"/>/<xsl:value-of select="ipAdEntNetMask"/></xsl:message>
                            <xsl:for-each select="$ipv4Addresses/ipv4/ipv4addr">
                                <xsl:variable name="ipAdEntAddr" select="ipAdEntAddr"/>
                                <xsl:variable name="ipAdEntNetMask" select="ipAdEntNetMask"/>
                                <xsl:variable name="subnetBitCount" select="subnetBitCount"/>
                                <xsl:variable name="ipPrefix" select="ipPrefix"/>
                                <xsl:if test="$ipAdEntAddr !=''">
                                    <object>
                                        <name>
                                            <xsl:value-of select="$ipAdEntAddr"/>/<xsl:value-of
                                                select="$subnetBitCount"/>
                                        </name>
                                        <objectType>IPv4 Address</objectType>
                                        <parameters>
                                            <parameter>
                                                <name>IPv4Address</name>
                                                <value>
                                                    <xsl:value-of select="$ipAdEntAddr"/>
                                                </value>
                                            </parameter>
                                            <parameter>
                                                <name>ipSubnetMask</name>
                                                <value>
                                                    <xsl:value-of select="$ipAdEntNetMask"/>
                                                </value>
                                            </parameter>
                                            <parameter>
                                                <name>ipv4Subnet</name>
                                                <value><xsl:value-of select="SnmpForXslt:getSubnetFromPrefix($ipPrefix)"/></value>
                                            </parameter>
                                            <parameter>
                                                <name>ipv4SubnetPrefix</name>
                                                <value><xsl:value-of select="$subnetBitCount"/></value>
                                            </parameter>
                                            <parameter>
                                                <name>ipv4SubnetBroadcast</name>
                                                <value><xsl:value-of select="SnmpForXslt:getBroadCastFromPrefix($ipPrefix)"/></value>
                                            </parameter>
                                        </parameters>
                                    </object>
                                </xsl:if>
                            </xsl:for-each>
                            <!--Check for  IPv6 IP addresses-->
                            <xsl:choose>
                                <xsl:when test="count(/root/iso/org/dod/internet/private/enterprises/cisco/ciscoExperiment/ciscoIetfIpMIB/ciscoIetfIpMIBObjects/cIp/cIpAddressTable/cIpAddressEntry[cIpAddressIfIndex=$ifIndex]/instance)!=0">
                                    <xsl:for-each
                                            select="/root/iso/org/dod/internet/private/enterprises/cisco/ciscoExperiment/ciscoIetfIpMIB/ciscoIetfIpMIBObjects/cIp/cIpAddressTable/cIpAddressEntry[cIpAddressIfIndex=$ifIndex]/instance">
                                        <xsl:variable name="instance" select="substring-after(.,'.')"/>
                                        <xsl:variable name="ipAdEntAddr"><xsl:for-each select="tokenize($instance,'\.')"><xsl:value-of select="functx:decimal-to-hex(xs:integer(.))"/>.</xsl:for-each></xsl:variable>

                                        <xsl:variable name="ipv6AddrPfxLength"
                                                      select="substring-after(substring-before(../cIpAddressPrefix,'.'),'.')"/>
                                        <xsl:variable name="ipv6AddrType" select="../cIpAddressType"/>
                                        <xsl:variable name="cIpAddressOrigin" select="../cIpAddressPrefix"/>
                                        <xsl:variable name="ipv6AddrAnycastFlag" select="../ipv6AddrAnycastFlag"/>
                                        <xsl:variable name="ipv6AddrStatus" select="../ipv6AddrStatus"/>
                                        <xsl:message>DEBUG: cIpAddressTable  <xsl:value-of select="$ipAdEntAddr"/>/<xsl:value-of select="$ipv6AddrPfxLength"/> </xsl:message>

                                        <xsl:call-template name="IPv6">
                                            <xsl:with-param name="ipAdEntAddr" select="IPv6formatConvertor:IPv6Convertor($ipAdEntAddr)"/>
                                            <xsl:with-param name="ipv6AddrPfxLength"
                                                            select="functx:substring-after-last-match($cIpAddressOrigin,'\.')"/>
                                            <xsl:with-param name="ipv6AddrType" select="$ipv6AddrType"/>
                                            <xsl:with-param name="ipv6AddrAnycastFlag" select="$ipv6AddrAnycastFlag"/>
                                            <xsl:with-param name="ipv6AddrStatus" select="$ipv6AddrStatus"/>
                                        </xsl:call-template>
                                    </xsl:for-each>
                                </xsl:when>
                                <xsl:otherwise>
                                        <!--<xsl:when test="count(/root/iso/org/dod/internet/private/enterprises/cisco/ciscoExperiment/ciscoIetfIpMIB/ciscoIetfIpMIBObjects/cIpv6/cIpv6InterfaceTable/cIpv6InterfaceEntry[index[@name='cIpv6InterfaceIfIndex']=$ifIndex]/cIpv6InterfaceIdentifier) > 0">-->
                                            <!--<xsl:for-each-->
                                                    <!--select="/root/iso/org/dod/internet/private/enterprises/cisco/ciscoExperiment/ciscoIetfIpMIB/ciscoIetfIpMIBObjects/cIpv6/cIpv6InterfaceTable/cIpv6InterfaceEntry[index[@name='cIpv6InterfaceIfIndex']=$ifIndex]/cIpv6InterfaceIdentifier">-->
                                                <!--<xsl:variable name="instance" select="."/>-->
                                                <!--<xsl:variable name="ipAdEntAddr" select="IPv6formatConvertor:IPv6Convertor(.)"/>-->
                                                <!--<xsl:variable name="ipv6AddrPfxLength"-->
                                                              <!--select="../cIpv6InterfaceIdentifierLength"/>-->
                                                <!--<xsl:variable name="ipv6AddrType" select="../cIpAddressType"/>-->
                                                <!--<xsl:variable name="cIpAddressOrigin" select="../cIpAddressPrefix"/>-->
                                                <!--<xsl:variable name="ipv6AddrAnycastFlag" select="../ipv6AddrAnycastFlag"/>-->
                                                <!--<xsl:variable name="ipv6AddrStatus" select="../ipv6AddrStatus"/>-->
                                                <!--<xsl:message>DEBUG: cIpv6InterfaceIfIndex<xsl:value-of select="$ipAdEntAddr"/>/<xsl:value-of select="$ipv6AddrPfxLength"/> </xsl:message>-->

                                                <!--<xsl:call-template name="IPv6">-->
                                                    <!--<xsl:with-param name="ipAdEntAddr" select="$ipAdEntAddr"/>-->
                                                    <!--<xsl:with-param name="ipv6AddrPfxLength" select="$ipv6AddrPfxLength"/>-->
                                                    <!--<xsl:with-param name="ipv6AddrType" select="$ipv6AddrType"/>-->
                                                    <!--<xsl:with-param name="ipv6AddrAnycastFlag" select="$ipv6AddrAnycastFlag"/>-->
                                                    <!--<xsl:with-param name="ipv6AddrStatus" select="$ipv6AddrStatus"/>-->
                                                <!--</xsl:call-template>-->
                                            <!--</xsl:for-each>-->
                                        <!--</xsl:when>-->
                                            <xsl:for-each
                                                    select="//root/iso/org/dod/internet/mgmt/mib-2/ipv6MIB/ipv6MIBObjects/ipv6AddrTable/ipv6AddrEntry[index=$ifIndex]/instance">
                                                <xsl:variable name="instance" select="substring-after(.,'.')"/>
                                                <xsl:variable name="ipAdEntAddr"><xsl:for-each select="tokenize($instance,'\.')"><xsl:value-of select="functx:decimal-to-hex(xs:integer(.))"/>.</xsl:for-each></xsl:variable>

                                                <!--<xsl:variable name="fr" select="()"/>-->
                                                <!--<xsl:variable name="to" select="(':')"/>-->
                                                <!--<xsl:variable name="ipAddr" select="functx:replace-multi($ipAdEntAddr,$fr,$to)" />-->
                                                <xsl:variable name="ipv6AddrPfxLength" select="../ipv6AddrPfxLength"/>
                                                <xsl:variable name="ipv6AddrType" select="../ipv6AddrType"/>
                                                <xsl:variable name="ipv6AddrAnycastFlag" select="../ipv6AddrAnycastFlag"/>
                                                <xsl:variable name="ipv6AddrStatus" select="../ipv6AddrStatus"/>
                                                <xsl:message>DEBUG: ipv6MIB<xsl:value-of select="$ipAdEntAddr"/>/<xsl:value-of select="$ipv6AddrPfxLength"/> </xsl:message>

                                                <xsl:call-template name="IPv6">
                                                    <xsl:with-param name="ipAdEntAddr"
                                                                    select="$ipAdEntAddr"/>
                                                    <xsl:with-param name="ipv6AddrPfxLength" select="$ipv6AddrPfxLength"/>
                                                    <xsl:with-param name="ipv6AddrType" select="$ipv6AddrType"/>
                                                    <xsl:with-param name="ipv6AddrAnycastFlag" select="$ipv6AddrAnycastFlag"/>
                                                    <xsl:with-param name="ipv6AddrStatus" select="$ipv6AddrStatus"/>
                                                </xsl:call-template>

                                            </xsl:for-each>
                                </xsl:otherwise>
                                <!--<xsl:otherwise>-->
                                    <!--<xsl:for-each-->
                                            <!--select="//root/iso/org/dod/internet/mgmt/mib-2/ipv6MIB/ipv6MIBObjects/ipv6AddrTable/ipv6AddrEntry[index=$ifIndex]/instance">-->
                                        <!--<xsl:variable name="instance" select="substring-after(.,'.')"/>-->
                                        <!--<xsl:variable name="ipAdEntAddr"><xsl:for-each select="tokenize($instance,'\.')"><xsl:value-of select="functx:decimal-to-hex(xs:integer(.))"/>.</xsl:for-each></xsl:variable>-->
                                        <!--&lt;!&ndash;<xsl:variable name="fr" select="()"/>&ndash;&gt;-->
                                        <!--&lt;!&ndash;<xsl:variable name="to" select="(':')"/>&ndash;&gt;-->
                                        <!--&lt;!&ndash;<xsl:variable name="ipAddr" select="functx:replace-multi($ipAdEntAddr,$fr,$to)" />&ndash;&gt;-->
                                        <!--<xsl:variable name="ipv6AddrPfxLength" select="../ipv6AddrPfxLength"/>-->
                                        <!--<xsl:variable name="ipv6AddrType" select="../ipv6AddrType"/>-->
                                        <!--<xsl:variable name="ipv6AddrAnycastFlag" select="../ipv6AddrAnycastFlag"/>-->
                                        <!--<xsl:variable name="ipv6AddrStatus" select="../ipv6AddrStatus"/>-->
                                        <!--<xsl:call-template name="IPv6">-->
                                            <!--<xsl:with-param name="ipAdEntAddr"-->
                                                            <!--select="IPv6formatConvertor:IPv6Convertor($ipAdEntAddr)"/>-->
                                            <!--<xsl:with-param name="ipv6AddrPfxLength" select="$ipv6AddrPfxLength"/>-->
                                            <!--<xsl:with-param name="ipv6AddrType" select="$ipv6AddrType"/>-->
                                            <!--<xsl:with-param name="ipv6AddrAnycastFlag" select="$ipv6AddrAnycastFlag"/>-->
                                            <!--<xsl:with-param name="ipv6AddrStatus" select="$ipv6AddrStatus"/>-->
                                        <!--</xsl:call-template>-->
                                    <!--</xsl:for-each>-->
                                <!--</xsl:otherwise>-->
                            </xsl:choose>
                            <xsl:variable name="interface-neighbors">
                                <xsl:for-each select="$ipv4Addresses/ipv4/ipv4addr">
                                    <xsl:variable name="ipAdEntAddr" select="ipAdEntAddr"/>
                                    <xsl:variable name="ipAdEntNetMask" select="ipAdEntNetMask"/>
                                    <xsl:call-template name="SLASH30">
                                        <xsl:with-param name="ipAdEntNetMask" select="$ipAdEntNetMask"/>
                                        <xsl:with-param name="ipAdEntAddr" select="$ipAdEntAddr"/>
                                    </xsl:call-template>
                                    <xsl:call-template name="SLASH31">
                                        <xsl:with-param name="ipAdEntNetMask" select="$ipAdEntNetMask"/>
                                        <xsl:with-param name="ipAdEntAddr" select="$ipAdEntAddr"/>
                                    </xsl:call-template>
                                </xsl:for-each>
                                <!--Check for NEXT-HOP neighbors-->
                                <xsl:call-template name="nextHop">
                                    <xsl:with-param name="ipRouteTable"
                                                    select="//root/iso/org/dod/internet/mgmt/mib-2/ip/ipRouteTable/ipRouteEntry[ipRouteIfIndex=$ifIndex]"/>
                                    <xsl:with-param name="sysName" select="$sysName"/>
                                    <xsl:with-param name="ipv4addresses" select="$ipv4Addresses/ipv4/ipv4addr"/>
                                </xsl:call-template>
                                <!--Check for CIDR-NEXT-HOP neighbors-->
                                <xsl:call-template name="cnextHop">
                                    <xsl:with-param name="ipCidrRouteTable"
                                                    select="//root/iso/org/dod/internet/mgmt/mib-2/ip/ipForward/ipCidrRouteTable/ipCidrRouteEntry[ipCidrRouteIfIndex=$ifIndex]"/>
                                    <xsl:with-param name="sysName" select="$sysName"/>
                                    <xsl:with-param name="ipv4addresses" select="$ipv4Addresses/ipv4/ipv4addr"/>
                                </xsl:call-template>
                                <!--Check for ARP neighbors-->
                                <xsl:call-template name="ARP">
                                    <xsl:with-param name="ipNetToMediaIfNeighbors"
                                                    select="//root/iso/org/dod/internet/mgmt/mib-2/ip/ipNetToMediaTable/ipNetToMediaEntry[ipNetToMediaIfIndex = $ifIndex]"/>
                                    <xsl:with-param name="sysName" select="$sysName"/>
                                    <xsl:with-param name="ipv4addresses" select="$ipv4Addresses/ipv4/ipv4addr"/>
                                </xsl:call-template>

                                <!--Check for MAC neighbors-->
                                <xsl:variable name="brdPort">
                                    <xsl:value-of
                                            select="//root/iso/org/dod/internet/mgmt/mib-2/dot1dBridge/dot1dBase/dot1dBasePortTable/dot1dBasePortEntry[dot1dBasePortIfIndex=$ifIndex]/dot1dBasePort"/>
                                </xsl:variable>
                                <xsl:for-each
                                        select="//root/iso/org/dod/internet/mgmt/mib-2/dot1dBridge/dot1dTp/dot1dTpFdbTable/dot1dTpFdbEntry[dot1dTpFdbPort=$brdPort]">
                                    <xsl:variable name="neighborMACAddress">
                                        <xsl:value-of select="dot1dTpFdbAddress"/>
                                    </xsl:variable>
                                    <xsl:variable name="neighborIPAddress">
                                        <xsl:value-of
                                                select="//root/iso/org/dod/internet/mgmt/mib-2/ip/ipNetToMediaTable/ipNetToMediaEntry[ipNetToMediaPhysAddress=$neighborMACAddress][1]/ipNetToMediaNetAddress"/>

                                    </xsl:variable>
                                    <xsl:message>DEBUG: NEIGHBOR MAC: <xsl:copy-of select="$neighborMACAddress"/> </xsl:message>
                                    <xsl:message>DEBUG: NEIGHBOR IP: <xsl:copy-of select="$neighborIPAddress"/> </xsl:message>

                                    <xsl:call-template name="MAC">
                                        <xsl:with-param name="neighborMACAddress" select="dot1dTpFdbAddress"/>

                                        <xsl:with-param name="neighborIPAddress"
                                                        select="$neighborIPAddress"/>
                                    </xsl:call-template>
                                </xsl:for-each>
                                <!--Check for CDP neighbors-->
                                <xsl:call-template name="CDP">
                                    <xsl:with-param name="cdpIfNeighbors"
                                                    select="exslt:node-set(//root/iso/org/dod/internet/private/enterprises/cisco/ciscoMgmt/ciscoCdpMIB/ciscoCdpMIBObjects/cdpCache/cdpCacheTable/cdpCacheEntry[index[@name='cdpCacheIfIndex'] = $ifIndex])"/>
                                </xsl:call-template>
                                <!--Check for LLDP neighbors-->
                                <xsl:call-template name="LLDP">
                                    <xsl:with-param name="lldpIfNeighbors"
                                                    select="//root/iso/std/iso8802/ieee802dot1/ieee802dot1mibs/lldpMIB/lldpObjects/lldpRemoteSystemsData/lldpRemTable/lldpRemEntry/index[@name = 'lldpRemLocalPortNum' and text()=$ifIndex]/../lldpRemSysName"/>
                                </xsl:call-template>
                                <!--Check for Spanning Tree neighbors-->
                                <!--<TEST>brdPort<xsl:value-of select="$brdPort"/>-->
                                <!--</TEST>-->
                                <!--<xsl:for-each-->
                                <!--select="//root/iso/org/dod/internet/mgmt/mib-2/dot1dBridge/dot1dStp/dot1dStpPortTable/dot1dStpPortEntry[dot1dStpPort=$brdPort]">-->
                                <!--<xsl:variable name="designatedBridge" select="dot1dStpPortDesignatedBridge"/>-->
                                <!--<xsl:choose>-->
                                <!--<xsl:when test="contains($designatedBridge,$baseBridgeAddress)">-->
                                <!--<STP>The other switch is the root <xsl:value-of select="$designatedBridge"/>|<xsl:value-of-->
                                <!--select="$baseBridgeAddress"/>-->
                                <!--</STP>-->
                                <!--</xsl:when>-->
                                <!--<xsl:otherwise>-->
                                <!--<STP>I am the root. The root is <xsl:value-of select="$baseBridgeAddress"/>|-->
                                <!--<xsl:value-of select="$designatedBridge"/>-->
                                <!--</STP>-->
                                <!--</xsl:otherwise>-->
                                <!--</xsl:choose>-->
                                <!--</xsl:for-each>-->
                            </xsl:variable>
                            <xsl:message>DEBUG: NEIGHBORS </xsl:message>
                            <xsl:for-each select="distinct-values($interface-neighbors/object/name)">
                                <xsl:variable name="name1" select="."/>
                                <xsl:message>DEBUG: Name: <xsl:value-of select="$name1"/> </xsl:message>

                                <xsl:for-each select="distinct-values($interface-neighbors/object[name=$name1]/parameters/parameter[name='Neighbor IP Address']/value)">
                                    <xsl:variable name="ipAddress666" select="."/>
                                    <xsl:message>DEBUG: IP: <xsl:value-of select="$ipAddress666"/></xsl:message>
                                    <object>
                                            <name><xsl:value-of select="$name1"/></name>
                                            <objectType>Discovered Neighbor</objectType>
                                            <parameters>
                                                <xsl:variable name="Reachable">
                                                    <xsl:for-each
                                                            select="$interface-neighbors/object[name=$name1]/parameters/parameter[name='Reachable']/value">
                                                        <xsl:value-of select="."/>
                                                    </xsl:for-each>
                                                </xsl:variable>
                                                <!--<Reachable>-->
                                                    <!--<xsl:copy-of select="$Reachable"/>-->
                                                <!--</Reachable>-->
                                                <xsl:choose>
                                                    <xsl:when test="contains($Reachable,'YES')">
                                                        <parameter>
                                                            <name>Reachable</name>
                                                            <value>YES</value>
                                                        </parameter>
                                                        <parameter>
                                                            <name>Discovery Method</name>
                                                            <xsl:variable name="discoveryMethods"><xsl:for-each select="distinct-values($interface-neighbors/object[name=$name1 and parameters/parameter[name='Neighbor IP Address' and value=$ipAddress666]]/parameters/parameter[name='Discovery Method']/value)"><xsl:value-of select="."/>,</xsl:for-each></xsl:variable>
                                                            <value><xsl:value-of select="functx:substring-before-last-match($discoveryMethods,',')"/></value>
                                                        </parameter>
                                                        <parameter>
                                                            <name>Neighbor Port</name>
                                                            <value>
                                                                <xsl:value-of select="distinct-values($interface-neighbors/object[name=$name1 and parameters/parameter[name='Neighbor IP Address' and value=$ipAddress666]]/parameters/parameter[name='Neighbor Port']/value)[1]"/>
                                                            </value>
                                                        </parameter>
                                                        <parameter>
                                                            <name>Neighbor IP Address</name>
                                                            <value>
                                                                <xsl:value-of select="$ipAddress666"/>
                                                            </value>
                                                        </parameter>
                                                        <parameter>
                                                            <name>Neighbor hostname</name>
                                                            <value>
                                                                <xsl:value-of select="$name1"/>
                                                            </value>
                                                        </parameter>
                                                        <parameter>
                                                            <name>Neighbor Device Type</name>
                                                            <value>
                                                                <xsl:value-of
                                                                        select="distinct-values($interface-neighbors/object[name=$name1 and parameters/parameter[name='Neighbor IP Address' and value=$ipAddress666]]/parameters/parameter[name='Neighbor Device Type']/value)[1]"/>
                                                            </value>
                                                        </parameter>
                                                        <parameter>
                                                            <name>Neighbor MAC Address</name>
                                                            <value>
                                                                <xsl:value-of
                                                                        select="distinct-values($interface-neighbors/object[name=$name1 and parameters/parameter[name='Neighbor IP Address' and value=$ipAddress666]]/parameters/parameter[name='Neighbor MAC Address']/value)[1]"/>
                                                            </value>
                                                        </parameter>
                                                    </xsl:when>
                                                    <xsl:otherwise>
                                                        <parameter>
                                                            <name>Reachable</name>
                                                            <value>NO</value>
                                                        </parameter>
                                                        <parameter>
                                                            <name>Discovery Method</name>
                                                            <xsl:variable name="discoveryMethods"><xsl:for-each select="distinct-values($interface-neighbors/object[name=$name1 and parameters/parameter[name='Neighbor IP Address' and value=$ipAddress666]]/parameters/parameter[name='Discovery Method']/value)"><xsl:value-of select="."/>,</xsl:for-each></xsl:variable>
                                                            <value><xsl:value-of select="functx:substring-before-last-match($discoveryMethods,',')"/></value>
                                                        </parameter>
                                                        <parameter>
                                                            <name>Neighbor Port</name>
                                                            <value>
                                                                <xsl:value-of
                                                                        select="distinct-values($interface-neighbors/object[name=$name1 and parameters/parameter[name='Neighbor IP Address' and value=$ipAddress666]]/parameters/parameter[name='Neighbor Port']/value)[1]"/>
                                                            </value>
                                                        </parameter>
                                                        <parameter>
                                                            <name>Neighbor IP Address</name>
                                                            <value>
                                                                <xsl:value-of
                                                                        select="$ipAddress666"/>
                                                            </value>
                                                        </parameter>
                                                        <parameter>
                                                            <name>Neighbor hostname</name>
                                                            <value>
                                                                <xsl:value-of
                                                                        select="$name1"/>
                                                            </value>
                                                        </parameter>
                                                        <parameter>
                                                            <name>Neighbor Device Type</name>
                                                            <value>
                                                                <xsl:value-of
                                                                        select="distinct-values($interface-neighbors/object[name=$name1 and parameters/parameter[name='Neighbor IP Address' and value=$ipAddress666]]/parameters/parameter[name='Neighbor Device Type']/value)[1]"/>
                                                            </value>
                                                        </parameter>
                                                        <parameter>
                                                            <name>Neighbor MAC Address</name>
                                                            <value>
                                                                <xsl:value-of
                                                                        select="distinct-values($interface-neighbors/object[name=$name1 and parameters/parameter[name='Neighbor IP Address' and value=$ipAddress666]]/parameters/parameter[name='Neighbor MAC Address']/value)[1]"/>
                                                            </value>
                                                        </parameter>
                                                    </xsl:otherwise>
                                                </xsl:choose>
                                            </parameters>
                                        </object>
                                    </xsl:for-each>

                            </xsl:for-each>
                        </object>

            </xsl:for-each>

            <!--Walk over the ifXTable neighbours and find those that are not alredy in the ifTable-->
            <xsl:for-each select="//root/iso/org/dod/internet/mgmt/mib-2/ifMIB/ifMIBObjects/ifXTable/ifXEntry">
                <xsl:variable name="ifIndex" select="instance"/>
                <xsl:choose>
                    <xsl:when test="not(exists(//root/iso/org/dod/internet/mgmt/mib-2/interfaces/ifTable/ifEntry[ifIndex=$ifIndex]))">
                        <xsl:variable name="ifAdminStatus">
                            <xsl:call-template name="adminStatus">
                                <xsl:with-param name="status" select="ifXAdminStatus"/>
                            </xsl:call-template>
                        </xsl:variable>
                        <xsl:variable name="ifOperStatus">
                            <xsl:call-template name="operStatus">
                                <xsl:with-param name="status" select="ifOperStatus"/>
                            </xsl:call-template>
                        </xsl:variable>
                        <xsl:variable name="ifName">
                            <xsl:value-of select="/root/iso/org/dod/internet/mgmt/mib-2/ifMIB/ifMIBObjects/ifXTable/ifXEntry[instance=$ifIndex]/ifName"/>
                        </xsl:variable>
                        <xsl:variable name="ifDescr">
                            <xsl:value-of select="ifDescr"/>
                        </xsl:variable>
                        <xsl:variable name="ifType">
                            <xsl:value-of select="ifType"/>
                        </xsl:variable>
                        <xsl:variable name="ifSpeed">
                            <xsl:value-of select="//root/iso/org/dod/internet/mgmt/mib-2/ifMIB/ifMIBObjects/ifXTable/ifXEntry[instance=$ifIndex]/ifHighSpeed"/>
                        </xsl:variable>
                        <xsl:variable name="ifPhysAddress">
                            <xsl:value-of select="ifPhysAddress"/>
                        </xsl:variable>
                        <xsl:variable name="IPv4Forwarding">
                            <xsl:choose>
                                <xsl:when test="count(//root/iso/org/dod/internet/mgmt/mib-2/ip/ipAddrTable/ipAddrEntry[ipAdEntIfIndex=$ifIndex])>0">YES</xsl:when>
                                <xsl:otherwise>NO</xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:variable name="IPv6Forwarding">
                            <xsl:choose>
                                <xsl:when test="count(//root/iso/org/dod/internet/mgmt/mib-2/ipv6MIB/ipv6MIBObjects/ipv6AddrTable/ipv6AddrEntry[index=$ifIndex]/instance)>0">YES</xsl:when>
                                <xsl:when test="count(/root/iso/org/dod/internet/private/enterprises/cisco/ciscoExperiment/ciscoIetfIpMIB/ciscoIetfIpMIBObjects/cIpv6/cIpv6InterfaceTable/cIpv6InterfaceEntry[index[@name='cIpv6InterfaceIfIndex']=$ifIndex])>0">YES</xsl:when>
                                <xsl:otherwise>NO</xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
                        <xsl:variable name="vrfForwarding">
                            <xsl:value-of
                                    select="$mplsVRF/root1//test[substring-after(index,'.')= $ifIndex]/name"/>
                        </xsl:variable>
                        <xsl:message>TRACE:>ifDescr<xsl:value-of select="$ifDescr"/> ifIndex<xsl:value-of select="$ifIndex"/> ifType <xsl:value-of select="$ifType"/></xsl:message>
                        <!-- Neighbors and IP addresses are obtained only for the interfaces that are up and running.
        If the Admin status is UP and Operational is down the interface is marked as Cable CUT !-->
                        <object>
                            <name>
                                <xsl:value-of select="$ifDescr"/>
                            </name>
                            <objectType>Discovery Interface</objectType>
                            <xsl:call-template name="interfaceParameters">
                                <xsl:with-param name="ifDescr" select="$ifDescr"/>
                                <xsl:with-param name="ifIndex" select="$ifIndex"/>
                                <xsl:with-param name="ifName" select="$ifName"/>
                                <xsl:with-param name="ifType" select="$ifType"/>
                                <xsl:with-param name="ifSped" select="$ifSpeed"/>
                                <xsl:with-param name="ifPhysicalAddress" select="$ifPhysAddress"/>
                                <xsl:with-param name="ifAdminStatus" select="$ifAdminStatus"/>
                                <xsl:with-param name="ifOperStatus" select="$ifOperStatus"/>
                                <xsl:with-param name="IPv4Forwarding" select="$IPv4Forwarding"/>
                                <xsl:with-param name="IPv6Forwarding" select="$IPv6Forwarding"/>
                                <xsl:with-param name="vrfForwarding" select="$vrfForwarding"/>
                            </xsl:call-template>
                            <!--Check for  IPv4 IP addresses-->
                            <xsl:variable name="ipv4Addresses">
                                <ipv4>
                                    <xsl:for-each
                                            select="//root/iso/org/dod/internet/mgmt/mib-2/ip/ipAddrTable/ipAddrEntry[ipAdEntIfIndex=$ifIndex]">
                                        <xsl:variable name="ipAdEntAddr" select="ipAdEntAddr"/>
                                        <xsl:variable name="ipAdEntNetMask" select="ipAdEntNetMask"/>
                                        <xsl:variable name="subnetBitCount"><xsl:call-template name="subnet-to-bitcount"><xsl:with-param
                                                name="subnet" select="$ipAdEntNetMask"/></xsl:call-template></xsl:variable>
                                        <xsl:variable name="ipPrefix"><xsl:value-of select="$ipAdEntAddr"/>/<xsl:value-of select="$subnetBitCount"/></xsl:variable>

                                        <ipv4addr>
                                            <ipAdEntAddr><xsl:value-of select="ipAdEntAddr"/></ipAdEntAddr>
                                            <ipAdEntNetMask><xsl:value-of select="ipAdEntNetMask"/></ipAdEntNetMask>
                                            <ipPrefix><xsl:value-of select="$ipAdEntAddr"/>/<xsl:value-of select="$subnetBitCount"/></ipPrefix>
                                            <subnetBitCount><xsl:call-template name="subnet-to-bitcount"><xsl:with-param
                                                    name="subnet" select="$ipAdEntNetMask"/></xsl:call-template></subnetBitCount>
                                            <ipv4Subnet><xsl:value-of select="SnmpForXslt:getSubnetFromPrefix($ipPrefix)"/></ipv4Subnet>
                                            <ipv4SubnetBroadcast><xsl:value-of select="SnmpForXslt:getBroadCastFromPrefix($ipPrefix)"/></ipv4SubnetBroadcast>
                                        </ipv4addr>
                                    </xsl:for-each>
                                </ipv4>
                            </xsl:variable>
                            <xsl:message>TRACE:ipv4Addresses<xsl:value-of select="$ipv4Addresses"/>/<xsl:value-of select="ipAdEntNetMask"/></xsl:message>
                            <xsl:for-each select="$ipv4Addresses/ipv4/ipv4addr">
                                <xsl:variable name="ipAdEntAddr" select="ipAdEntAddr"/>
                                <xsl:variable name="ipAdEntNetMask" select="ipAdEntNetMask"/>
                                <xsl:variable name="subnetBitCount" select="subnetBitCount"/>
                                <xsl:variable name="ipPrefix" select="ipPrefix"/>
                                <xsl:if test="$ipAdEntAddr !=''">
                                    <object>
                                        <name>
                                            <xsl:value-of select="$ipAdEntAddr"/>/<xsl:value-of
                                                select="$subnetBitCount"/>
                                        </name>
                                        <objectType>IPv4 Address</objectType>
                                        <parameters>
                                            <parameter>
                                                <name>IPv4Address</name>
                                                <value>
                                                    <xsl:value-of select="$ipAdEntAddr"/>
                                                </value>
                                            </parameter>
                                            <parameter>
                                                <name>ipSubnetMask</name>
                                                <value>
                                                    <xsl:value-of select="$ipAdEntNetMask"/>
                                                </value>
                                            </parameter>
                                            <parameter>
                                                <name>ipv4Subnet</name>
                                                <value><xsl:value-of select="SnmpForXslt:getSubnetFromPrefix($ipPrefix)"/></value>
                                            </parameter>
                                            <parameter>
                                                <name>ipv4SubnetPrefix</name>
                                                <value><xsl:value-of select="$subnetBitCount"/></value>
                                            </parameter>
                                            <parameter>
                                                <name>ipv4SubnetBroadcast</name>
                                                <value><xsl:value-of select="SnmpForXslt:getBroadCastFromPrefix($ipPrefix)"/></value>
                                            </parameter>
                                        </parameters>
                                    </object>
                                </xsl:if>
                            </xsl:for-each>
                            <!--Check for  IPv6 IP addresses-->
                            <xsl:choose>
                                <xsl:when test="count(/root/iso/org/dod/internet/private/enterprises/cisco/ciscoExperiment/ciscoIetfIpMIB/ciscoIetfIpMIBObjects/cIp/cIpAddressTable/cIpAddressEntry[cIpAddressIfIndex=$ifIndex]/instance)!=0">
                                    <xsl:for-each
                                            select="/root/iso/org/dod/internet/private/enterprises/cisco/ciscoExperiment/ciscoIetfIpMIB/ciscoIetfIpMIBObjects/cIp/cIpAddressTable/cIpAddressEntry[cIpAddressIfIndex=$ifIndex]/instance">
                                        <xsl:variable name="instance" select="substring-after(.,'.')"/>
                                        <xsl:variable name="ipAdEntAddr"><xsl:for-each select="tokenize($instance,'\.')"><xsl:value-of select="functx:decimal-to-hex(xs:integer(.))"/>.</xsl:for-each></xsl:variable>

                                        <xsl:variable name="ipv6AddrPfxLength"
                                                      select="substring-after(substring-before(../cIpAddressPrefix,'.'),'.')"/>
                                        <xsl:variable name="ipv6AddrType" select="../cIpAddressType"/>
                                        <xsl:variable name="cIpAddressOrigin" select="../cIpAddressPrefix"/>
                                        <xsl:variable name="ipv6AddrAnycastFlag" select="../ipv6AddrAnycastFlag"/>
                                        <xsl:variable name="ipv6AddrStatus" select="../ipv6AddrStatus"/>
                                        <xsl:message>DEBUG: cIpAddressTable  <xsl:value-of select="$ipAdEntAddr"/>/<xsl:value-of select="$ipv6AddrPfxLength"/> </xsl:message>

                                        <xsl:call-template name="IPv6">
                                            <xsl:with-param name="ipAdEntAddr" select="IPv6formatConvertor:IPv6Convertor($ipAdEntAddr)"/>
                                            <xsl:with-param name="ipv6AddrPfxLength"
                                                            select="functx:substring-after-last-match($cIpAddressOrigin,'\.')"/>
                                            <xsl:with-param name="ipv6AddrType" select="$ipv6AddrType"/>
                                            <xsl:with-param name="ipv6AddrAnycastFlag" select="$ipv6AddrAnycastFlag"/>
                                            <xsl:with-param name="ipv6AddrStatus" select="$ipv6AddrStatus"/>
                                        </xsl:call-template>
                                    </xsl:for-each>
                                </xsl:when>
                                <xsl:otherwise>
                                    <!--<xsl:when test="count(/root/iso/org/dod/internet/private/enterprises/cisco/ciscoExperiment/ciscoIetfIpMIB/ciscoIetfIpMIBObjects/cIpv6/cIpv6InterfaceTable/cIpv6InterfaceEntry[index[@name='cIpv6InterfaceIfIndex']=$ifIndex]/cIpv6InterfaceIdentifier) > 0">-->
                                    <!--<xsl:for-each-->
                                    <!--select="/root/iso/org/dod/internet/private/enterprises/cisco/ciscoExperiment/ciscoIetfIpMIB/ciscoIetfIpMIBObjects/cIpv6/cIpv6InterfaceTable/cIpv6InterfaceEntry[index[@name='cIpv6InterfaceIfIndex']=$ifIndex]/cIpv6InterfaceIdentifier">-->
                                    <!--<xsl:variable name="instance" select="."/>-->
                                    <!--<xsl:variable name="ipAdEntAddr" select="IPv6formatConvertor:IPv6Convertor(.)"/>-->
                                    <!--<xsl:variable name="ipv6AddrPfxLength"-->
                                    <!--select="../cIpv6InterfaceIdentifierLength"/>-->
                                    <!--<xsl:variable name="ipv6AddrType" select="../cIpAddressType"/>-->
                                    <!--<xsl:variable name="cIpAddressOrigin" select="../cIpAddressPrefix"/>-->
                                    <!--<xsl:variable name="ipv6AddrAnycastFlag" select="../ipv6AddrAnycastFlag"/>-->
                                    <!--<xsl:variable name="ipv6AddrStatus" select="../ipv6AddrStatus"/>-->
                                    <!--<xsl:message>DEBUG: cIpv6InterfaceIfIndex<xsl:value-of select="$ipAdEntAddr"/>/<xsl:value-of select="$ipv6AddrPfxLength"/> </xsl:message>-->

                                    <!--<xsl:call-template name="IPv6">-->
                                    <!--<xsl:with-param name="ipAdEntAddr" select="$ipAdEntAddr"/>-->
                                    <!--<xsl:with-param name="ipv6AddrPfxLength" select="$ipv6AddrPfxLength"/>-->
                                    <!--<xsl:with-param name="ipv6AddrType" select="$ipv6AddrType"/>-->
                                    <!--<xsl:with-param name="ipv6AddrAnycastFlag" select="$ipv6AddrAnycastFlag"/>-->
                                    <!--<xsl:with-param name="ipv6AddrStatus" select="$ipv6AddrStatus"/>-->
                                    <!--</xsl:call-template>-->
                                    <!--</xsl:for-each>-->
                                    <!--</xsl:when>-->
                                    <xsl:for-each
                                            select="//root/iso/org/dod/internet/mgmt/mib-2/ipv6MIB/ipv6MIBObjects/ipv6AddrTable/ipv6AddrEntry[index=$ifIndex]/instance">
                                        <xsl:variable name="instance" select="substring-after(.,'.')"/>
                                        <xsl:variable name="ipAdEntAddr"><xsl:for-each select="tokenize($instance,'\.')"><xsl:value-of select="functx:decimal-to-hex(xs:integer(.))"/>.</xsl:for-each></xsl:variable>

                                        <!--<xsl:variable name="fr" select="()"/>-->
                                        <!--<xsl:variable name="to" select="(':')"/>-->
                                        <!--<xsl:variable name="ipAddr" select="functx:replace-multi($ipAdEntAddr,$fr,$to)" />-->
                                        <xsl:variable name="ipv6AddrPfxLength" select="../ipv6AddrPfxLength"/>
                                        <xsl:variable name="ipv6AddrType" select="../ipv6AddrType"/>
                                        <xsl:variable name="ipv6AddrAnycastFlag" select="../ipv6AddrAnycastFlag"/>
                                        <xsl:variable name="ipv6AddrStatus" select="../ipv6AddrStatus"/>
                                        <xsl:message>DEBUG: ipv6MIB<xsl:value-of select="$ipAdEntAddr"/>/<xsl:value-of select="$ipv6AddrPfxLength"/> </xsl:message>

                                        <xsl:call-template name="IPv6">
                                            <xsl:with-param name="ipAdEntAddr"
                                                            select="$ipAdEntAddr"/>
                                            <xsl:with-param name="ipv6AddrPfxLength" select="$ipv6AddrPfxLength"/>
                                            <xsl:with-param name="ipv6AddrType" select="$ipv6AddrType"/>
                                            <xsl:with-param name="ipv6AddrAnycastFlag" select="$ipv6AddrAnycastFlag"/>
                                            <xsl:with-param name="ipv6AddrStatus" select="$ipv6AddrStatus"/>
                                        </xsl:call-template>

                                    </xsl:for-each>
                                </xsl:otherwise>
                                <!--<xsl:otherwise>-->
                                <!--<xsl:for-each-->
                                <!--select="//root/iso/org/dod/internet/mgmt/mib-2/ipv6MIB/ipv6MIBObjects/ipv6AddrTable/ipv6AddrEntry[index=$ifIndex]/instance">-->
                                <!--<xsl:variable name="instance" select="substring-after(.,'.')"/>-->
                                <!--<xsl:variable name="ipAdEntAddr"><xsl:for-each select="tokenize($instance,'\.')"><xsl:value-of select="functx:decimal-to-hex(xs:integer(.))"/>.</xsl:for-each></xsl:variable>-->
                                <!--&lt;!&ndash;<xsl:variable name="fr" select="()"/>&ndash;&gt;-->
                                <!--&lt;!&ndash;<xsl:variable name="to" select="(':')"/>&ndash;&gt;-->
                                <!--&lt;!&ndash;<xsl:variable name="ipAddr" select="functx:replace-multi($ipAdEntAddr,$fr,$to)" />&ndash;&gt;-->
                                <!--<xsl:variable name="ipv6AddrPfxLength" select="../ipv6AddrPfxLength"/>-->
                                <!--<xsl:variable name="ipv6AddrType" select="../ipv6AddrType"/>-->
                                <!--<xsl:variable name="ipv6AddrAnycastFlag" select="../ipv6AddrAnycastFlag"/>-->
                                <!--<xsl:variable name="ipv6AddrStatus" select="../ipv6AddrStatus"/>-->
                                <!--<xsl:call-template name="IPv6">-->
                                <!--<xsl:with-param name="ipAdEntAddr"-->
                                <!--select="IPv6formatConvertor:IPv6Convertor($ipAdEntAddr)"/>-->
                                <!--<xsl:with-param name="ipv6AddrPfxLength" select="$ipv6AddrPfxLength"/>-->
                                <!--<xsl:with-param name="ipv6AddrType" select="$ipv6AddrType"/>-->
                                <!--<xsl:with-param name="ipv6AddrAnycastFlag" select="$ipv6AddrAnycastFlag"/>-->
                                <!--<xsl:with-param name="ipv6AddrStatus" select="$ipv6AddrStatus"/>-->
                                <!--</xsl:call-template>-->
                                <!--</xsl:for-each>-->
                                <!--</xsl:otherwise>-->
                            </xsl:choose>
                            <xsl:variable name="interface-neighbors">
                                <xsl:for-each select="$ipv4Addresses/ipv4/ipv4addr">
                                    <xsl:variable name="ipAdEntAddr" select="ipAdEntAddr"/>
                                    <xsl:variable name="ipAdEntNetMask" select="ipAdEntNetMask"/>
                                    <xsl:call-template name="SLASH30">
                                        <xsl:with-param name="ipAdEntNetMask" select="$ipAdEntNetMask"/>
                                        <xsl:with-param name="ipAdEntAddr" select="$ipAdEntAddr"/>
                                    </xsl:call-template>
                                    <xsl:call-template name="SLASH31">
                                        <xsl:with-param name="ipAdEntNetMask" select="$ipAdEntNetMask"/>
                                        <xsl:with-param name="ipAdEntAddr" select="$ipAdEntAddr"/>
                                    </xsl:call-template>
                                </xsl:for-each>
                                <!--Check for NEXT-HOP neighbors-->
                                <xsl:call-template name="nextHop">
                                    <xsl:with-param name="ipRouteTable"
                                                    select="//root/iso/org/dod/internet/mgmt/mib-2/ip/ipRouteTable/ipRouteEntry[ipRouteIfIndex=$ifIndex]"/>
                                    <xsl:with-param name="sysName" select="$sysName"/>
                                    <xsl:with-param name="ipv4addresses" select="$ipv4Addresses/ipv4/ipv4addr"/>
                                </xsl:call-template>
                                <!--Check for CIDR-NEXT-HOP neighbors-->
                                <xsl:call-template name="cnextHop">
                                    <xsl:with-param name="ipCidrRouteTable"
                                                    select="//root/iso/org/dod/internet/mgmt/mib-2/ip/ipForward/ipCidrRouteTable/ipCidrRouteEntry[ipCidrRouteIfIndex=$ifIndex]"/>
                                    <xsl:with-param name="sysName" select="$sysName"/>
                                    <xsl:with-param name="ipv4addresses" select="$ipv4Addresses/ipv4/ipv4addr"/>
                                </xsl:call-template>
                                <!--Check for ARP neighbors-->
                                <xsl:call-template name="ARP">
                                    <xsl:with-param name="ipNetToMediaIfNeighbors"
                                                    select="//root/iso/org/dod/internet/mgmt/mib-2/ip/ipNetToMediaTable/ipNetToMediaEntry[ipNetToMediaIfIndex = $ifIndex]"/>
                                    <xsl:with-param name="sysName" select="$sysName"/>
                                    <xsl:with-param name="ipv4addresses" select="$ipv4Addresses/ipv4/ipv4addr"/>
                                </xsl:call-template>

                                <!--Check for MAC neighbors-->
                                <xsl:variable name="brdPort">
                                    <xsl:value-of
                                            select="//root/iso/org/dod/internet/mgmt/mib-2/dot1dBridge/dot1dBase/dot1dBasePortTable/dot1dBasePortEntry[dot1dBasePortIfIndex=$ifIndex]/dot1dBasePort"/>
                                </xsl:variable>
                                <xsl:for-each
                                        select="//root/iso/org/dod/internet/mgmt/mib-2/dot1dBridge/dot1dTp/dot1dTpFdbTable/dot1dTpFdbEntry[dot1dTpFdbPort=$brdPort]">
                                    <xsl:variable name="neighborMACAddress">
                                        <xsl:value-of select="dot1dTpFdbAddress"/>
                                    </xsl:variable>
                                    <xsl:variable name="neighborIPAddress">
                                        <xsl:value-of
                                                select="//root/iso/org/dod/internet/mgmt/mib-2/ip/ipNetToMediaTable/ipNetToMediaEntry[ipNetToMediaPhysAddress=$neighborMACAddress][1]/ipNetToMediaNetAddress"/>

                                    </xsl:variable>
                                    <xsl:message>DEBUG: NEIGHBOR MAC: <xsl:copy-of select="$neighborMACAddress"/> </xsl:message>
                                    <xsl:message>DEBUG: NEIGHBOR IP: <xsl:copy-of select="$neighborIPAddress"/> </xsl:message>

                                    <xsl:call-template name="MAC">
                                        <xsl:with-param name="neighborMACAddress" select="dot1dTpFdbAddress"/>
                                        <!--<xsl:with-param name="ipv4addresses" select="$ipv4Addresses/ipv4/ipv4addr"/>-->

                                        <xsl:with-param name="neighborIPAddress"
                                                        select="$neighborIPAddress"/>
                                    </xsl:call-template>
                                </xsl:for-each>
                                <!--Check for CDP neighbors-->
                                <xsl:call-template name="CDP">
                                    <xsl:with-param name="cdpIfNeighbors"
                                                    select="exslt:node-set(//root/iso/org/dod/internet/private/enterprises/cisco/ciscoMgmt/ciscoCdpMIB/ciscoCdpMIBObjects/cdpCache/cdpCacheTable/cdpCacheEntry[index[@name='cdpCacheIfIndex'] = $ifIndex])"/>
                                </xsl:call-template>
                                <!--Check for LLDP neighbors-->
                                <xsl:call-template name="LLDP">
                                    <xsl:with-param name="lldpIfNeighbors"
                                                    select="//root/iso/std/iso8802/ieee802dot1/ieee802dot1mibs/lldpMIB/lldpObjects/lldpRemoteSystemsData/lldpRemTable/lldpRemEntry/index[@name = 'lldpRemLocalPortNum' and text()=$ifIndex]/../lldpRemSysName"/>
                                </xsl:call-template>
                                <!--Check for Spanning Tree neighbors-->
                                <!--<TEST>brdPort<xsl:value-of select="$brdPort"/>-->
                                <!--</TEST>-->
                                <!--<xsl:for-each-->
                                <!--select="//root/iso/org/dod/internet/mgmt/mib-2/dot1dBridge/dot1dStp/dot1dStpPortTable/dot1dStpPortEntry[dot1dStpPort=$brdPort]">-->
                                <!--<xsl:variable name="designatedBridge" select="dot1dStpPortDesignatedBridge"/>-->
                                <!--<xsl:choose>-->
                                <!--<xsl:when test="contains($designatedBridge,$baseBridgeAddress)">-->
                                <!--<STP>The other switch is the root <xsl:value-of select="$designatedBridge"/>|<xsl:value-of-->
                                <!--select="$baseBridgeAddress"/>-->
                                <!--</STP>-->
                                <!--</xsl:when>-->
                                <!--<xsl:otherwise>-->
                                <!--<STP>I am the root. The root is <xsl:value-of select="$baseBridgeAddress"/>|-->
                                <!--<xsl:value-of select="$designatedBridge"/>-->
                                <!--</STP>-->
                                <!--</xsl:otherwise>-->
                                <!--</xsl:choose>-->
                                <!--</xsl:for-each>-->
                            </xsl:variable>
                            <xsl:message>DEBUG: NEIGHBORS </xsl:message>
                            <xsl:for-each select="distinct-values($interface-neighbors/object/name)">
                                <xsl:variable name="name1" select="."/>
                                <xsl:message>DEBUG: Name: <xsl:value-of select="$name1"/> </xsl:message>

                                <xsl:for-each select="distinct-values($interface-neighbors/object[name=$name1]/parameters/parameter[name='Neighbor IP Address']/value)">
                                    <xsl:variable name="ipAddress666" select="."/>
                                    <xsl:message>DEBUG: IP: <xsl:value-of select="$ipAddress666"/></xsl:message>
                                    <object>
                                        <name><xsl:value-of select="$name1"/></name>
                                        <objectType>Discovered Neighbor</objectType>
                                        <parameters>
                                            <xsl:variable name="Reachable">
                                                <xsl:for-each
                                                        select="$interface-neighbors/object[name=$name1]/parameters/parameter[name='Reachable']/value">
                                                    <xsl:value-of select="."/>
                                                </xsl:for-each>
                                            </xsl:variable>
                                            <!--<Reachable>-->
                                            <!--<xsl:copy-of select="$Reachable"/>-->
                                            <!--</Reachable>-->
                                            <xsl:choose>
                                                <xsl:when test="contains($Reachable,'YES')">
                                                    <parameter>
                                                        <name>Reachable</name>
                                                        <value>YES</value>
                                                    </parameter>
                                                    <parameter>
                                                        <name>Discovery Method</name>
                                                        <xsl:variable name="discoveryMethods"><xsl:for-each select="distinct-values($interface-neighbors/object[name=$name1 and parameters/parameter[name='Neighbor IP Address' and value=$ipAddress666]]/parameters/parameter[name='Discovery Method']/value)"><xsl:value-of select="."/>,</xsl:for-each></xsl:variable>
                                                        <value><xsl:value-of select="functx:substring-before-last-match($discoveryMethods,',')"/></value>
                                                    </parameter>
                                                    <parameter>
                                                        <name>Neighbor Port</name>
                                                        <value>
                                                            <xsl:value-of select="distinct-values($interface-neighbors/object[name=$name1 and parameters/parameter[name='Neighbor IP Address' and value=$ipAddress666]]/parameters/parameter[name='Neighbor Port']/value)[1]"/>
                                                        </value>
                                                    </parameter>
                                                    <parameter>
                                                        <name>Neighbor IP Address</name>
                                                        <value>
                                                            <xsl:value-of select="$ipAddress666"/>
                                                        </value>
                                                    </parameter>
                                                    <parameter>
                                                        <name>Neighbor hostname</name>
                                                        <value>
                                                            <xsl:value-of select="$name1"/>
                                                        </value>
                                                    </parameter>
                                                    <parameter>
                                                        <name>Neighbor Device Type</name>
                                                        <value>
                                                            <xsl:value-of
                                                                    select="distinct-values($interface-neighbors/object[name=$name1 and parameters/parameter[name='Neighbor IP Address' and value=$ipAddress666]]/parameters/parameter[name='Neighbor Device Type']/value)[1]"/>
                                                        </value>
                                                    </parameter>
                                                    <parameter>
                                                        <name>Neighbor MAC Address</name>
                                                        <value>
                                                            <xsl:value-of
                                                                    select="distinct-values($interface-neighbors/object[name=$name1 and parameters/parameter[name='Neighbor IP Address' and value=$ipAddress666]]/parameters/parameter[name='Neighbor MAC Address']/value)[1]"/>
                                                        </value>
                                                    </parameter>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <parameter>
                                                        <name>Reachable</name>
                                                        <value>NO</value>
                                                    </parameter>
                                                    <parameter>
                                                        <name>Discovery Method</name>
                                                        <xsl:variable name="discoveryMethods"><xsl:for-each select="distinct-values($interface-neighbors/object[name=$name1 and parameters/parameter[name='Neighbor IP Address' and value=$ipAddress666]]/parameters/parameter[name='Discovery Method']/value)"><xsl:value-of select="."/>,</xsl:for-each></xsl:variable>
                                                        <value><xsl:value-of select="functx:substring-before-last-match($discoveryMethods,',')"/></value>
                                                    </parameter>
                                                    <parameter>
                                                        <name>Neighbor Port</name>
                                                        <value>
                                                            <xsl:value-of
                                                                    select="distinct-values($interface-neighbors/object[name=$name1 and parameters/parameter[name='Neighbor IP Address' and value=$ipAddress666]]/parameters/parameter[name='Neighbor Port']/value)[1]"/>
                                                        </value>
                                                    </parameter>
                                                    <parameter>
                                                        <name>Neighbor IP Address</name>
                                                        <value>
                                                            <xsl:value-of
                                                                    select="$ipAddress666"/>
                                                        </value>
                                                    </parameter>
                                                    <parameter>
                                                        <name>Neighbor hostname</name>
                                                        <value>
                                                            <xsl:value-of
                                                                    select="$name1"/>
                                                        </value>
                                                    </parameter>
                                                    <parameter>
                                                        <name>Neighbor Device Type</name>
                                                        <value>
                                                            <xsl:value-of
                                                                    select="distinct-values($interface-neighbors/object[name=$name1 and parameters/parameter[name='Neighbor IP Address' and value=$ipAddress666]]/parameters/parameter[name='Neighbor Device Type']/value)[1]"/>
                                                        </value>
                                                    </parameter>
                                                    <parameter>
                                                        <name>Neighbor MAC Address</name>
                                                        <value>
                                                            <xsl:value-of
                                                                    select="distinct-values($interface-neighbors/object[name=$name1 and parameters/parameter[name='Neighbor IP Address' and value=$ipAddress666]]/parameters/parameter[name='Neighbor MAC Address']/value)[1]"/>
                                                        </value>
                                                    </parameter>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                        </parameters>
                                    </object>
                                </xsl:for-each>

                            </xsl:for-each>
                        </object>

                    </xsl:when>

                </xsl:choose>

            </xsl:for-each>
            <object>
                <name>DeviceLogicalData</name>
                <objectType>DeviceLogicalData</objectType>
                <parameters/>
                <xsl:for-each select="//org/dod/internet/mgmt/mib-2/ospf/ospfNbrTable/ospfNbrEntry">
                    <xsl:call-template name="OSPF">
                        <xsl:with-param name="ospfNbr" select="."/>
                    </xsl:call-template>
                </xsl:for-each>
                <xsl:for-each select="//org/dod/internet/mgmt/mib-2/bgp/bgpPeerTable/bgpPeerEntry">
                    <xsl:call-template name="BGP">
                        <xsl:with-param name="bgpPeer" select="."/>
                    </xsl:call-template>
                </xsl:for-each>
                <xsl:call-template name="IPSEC-Phase1">
                    <xsl:with-param name="cikeTunnelTable" select="/root/iso/org/dod/internet/private/enterprises/cisco/ciscoMgmt/ciscoIpSecFlowMonitorMIB/cipSecMIBObjects/cipSecPhaseOne/cikeTunnelTable"/>
                    <xsl:with-param name="sysName"/>
                    <xsl:with-param name="ipv4addresses" select="/root/iso/org/dod/internet/mgmt/mib-2/ip/ipAddrTable"/>
                </xsl:call-template>
                <xsl:call-template name="IPSEC-Phase2">
                    <xsl:with-param name="cipSecTunnelTable" select="/root/iso/org/dod/internet/private/enterprises/cisco/ciscoMgmt/ciscoIpSecFlowMonitorMIB/cipSecMIBObjects/cipSecPhaseTwo/cipSecTunnelTable"/>
                    <xsl:with-param name="sysName"/>
                    <xsl:with-param name="ipv4addresses" select="/root/iso/org/dod/internet/mgmt/mib-2/ip/ipAddrTable"/>
                </xsl:call-template>
            </object>
            <object>
                <name>mplsL3VPNs</name>
                <objectType>mplsL3VPNs</objectType>
                <parameters/>

                <xsl:for-each
                        select="//org/dod/internet/experimental/mplsVpnMIB/mplsVpnObjects/mplsVpnConf/mplsVpnVrfTable/mplsVpnVrfEntry">
                    <xsl:variable name="RD" select="mplsVpnVrfRouteDistinguisher"/>
                    <xsl:variable name="instance" select="instance"/>
                    <xsl:variable name="rd_instance" select="substring-after($instance,'.')"/>
                    <object>
                        <name>
                            <xsl:value-of select="$RD"/>
                        </name>
                        <objectType>mplsL3VPN</objectType>
                        <parameters>
                            <parameter>
                                <name>vrfName</name>
                                <value>
                                    <xsl:for-each select="tokenize($rd_instance,'\.')">
                                        <xsl:value-of select="codepoints-to-string(xs:integer(.))"/>
                                        <!--xsl:value-of select="."/-->
                                    </xsl:for-each>
                                </value>
                            </parameter>
                        </parameters>
                        <xsl:for-each
                                select="//org/dod/internet/experimental/mplsVpnMIB/mplsVpnObjects/mplsVpnConf/mplsVpnVrfRouteTargetTable/mplsVpnVrfRouteTargetEntry[contains(instance,$rd_instance)]">
                            <xsl:variable name="rt" select="mplsVpnVrfRouteTarget"/>
                            <object>
                                <name>
                                    <xsl:value-of select="$rt"/>
                                </name>
                                <objectType>RT</objectType>
                                <parameters>
                                    <parameter>
                                        <name>Type</name>
                                        <value>
                                            <xsl:value-of
                                                    select="codepoints-to-string(xs:integer(index[@name='mplsVpnVrfRouteTargetType']))"/>
                                        </value>
                                    </parameter>
                                </parameters>
                            </object>
                        </xsl:for-each>
                    </object>
                </xsl:for-each>
            </object>

                <!--<xsl:for-each-->
                        <!--select="//root/iso/org/dod/internet/private/enterprises/cisco/ciscoMgmt/ciscoIpSecFlowMonitorMIB/cipSecMIBObjects/cipSecPhaseOne/cikeTunnelTable/cikeTunnelEntry">-->
                    <!--<xsl:variable name="localAddress"><xsl:variable name="temp"><xsl:for-each select="tokenize(cikeTunLocalAddr,':')"><xsl:call-template name="HexToDecimal"><xsl:with-param name="hexNumber"><xsl:value-of select="."/></xsl:with-param></xsl:call-template>.</xsl:for-each>-->
                    <!--</xsl:variable><xsl:value-of select="functx:substring-before-last-match($temp,'.')"/>-->
                    <!--</xsl:variable>-->
                    <!--<xsl:variable name="remoteAddress"><xsl:variable name="temp"><xsl:for-each select="tokenize(cikeTunRemoteAddr,':')"><xsl:call-template name="HexToDecimal"><xsl:with-param name="hexNumber"><xsl:value-of select="."/></xsl:with-param></xsl:call-template>.</xsl:for-each>-->
                    <!--</xsl:variable>-->
                        <!--<xsl:value-of select="functx:substring-before-last-match($temp,'.')"/>-->
                    <!--</xsl:variable>-->
                    <!--<xsl:variable name="status">-->
                        <!--<xsl:choose>-->
                            <!--<xsl:when test="cikeTunStatus = '1'">UP</xsl:when>-->
                            <!--<xsl:otherwise>DESTROYING</xsl:otherwise>-->
                        <!--</xsl:choose>-->
                    <!--</xsl:variable>-->
                    <!--<xsl:variable name="cikeTunRemoteValue" select="cikeTunRemoteValue"/>-->

                    <!--<xsl:variable name="instance" select="instance"/>-->
                    <!--<object>-->
                        <!--<name>-->
                            <!--<xsl:value-of select="$localAddress"/>-<xsl:value-of select="$remoteAddress"/>-->
                        <!--</name>-->
                        <!--<objectType>Discovered Neighbor</objectType>-->
                        <!--<parameters>-->
                            <!--<parameter>-->
                                <!--<name>Local IP Address</name>-->
                                <!--<value><xsl:value-of select="$localAddress"/></value>-->
                            <!--</parameter>-->
                            <!--<parameter>-->
                                <!--<name>remotelPeer</name>-->
                                <!--<value><xsl:value-of select="$remoteAddress"/></value>-->
                            <!--</parameter>-->
                            <!--<parameter>-->
                                <!--<name>tunnelStatus</name>-->
                                <!--<value><xsl:value-of select="$status"/></value>-->
                            <!--</parameter>-->
                        <!--</parameters>-->

                    <!--</object>-->
                <!--</xsl:for-each>-->

        </DiscoveredDevice>
        <!--</network> -->
    </xsl:template>
</xsl:stylesheet>
