/*
 * XmlFormatter.java
 *
 * This work is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * This work is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * Copyright (c) 2010-2016 iTransformers Labs. All rights reserved.
 */

package net.itransformers.utils;

/**
 * Created with IntelliJ IDEA.
 * User: niau
 * Date: 10/26/13
 * Time: 5:47 PM
 * To change this template use File | Settings | File Templates.
 */

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

public class XmlFormatter {

    public XmlFormatter() {
    }

    public String format(String unformattedXml) throws IOException, ParserConfigurationException, SAXException {

            final Document document = parseXmlFile(unformattedXml);

            OutputFormat format = new OutputFormat(document);
            format.setLineWidth(65);
            format.setIndenting(true);
            format.setIndent(2);
            Writer out = new StringWriter();
            XMLSerializer serializer = new XMLSerializer(out, format);
            serializer.serialize(document);

            return out.toString();

    }

    private Document parseXmlFile(String in) throws ParserConfigurationException, IOException, SAXException {

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(in));
            return db.parse(is);

    }

    public static void main(String[] args) throws ParserConfigurationException, SAXException {
        String unformattedXml =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "        <DiscoveredDevice>\n" +
                "        <name>R8</name>\n" +
                "        <parameters>\n" +
                "        <parameter>\n" +
                "        <name>Device State</name>\n" +
                "        <value>discovered</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>sysDescr</name>\n" +
                "        <value>Cisco IOS Software, 3700 Software (C3725-ADVENTERPRISEK9-M), Version 12.4(23), RELEASE SOFTWARE (fc1)\n" +
                "        Technical Support: http://www.cisco.com/techsupport\n" +
                "        Copyright (c) 1986-2008 by Cisco Systems, Inc.\n" +
                "                Compiled Sun 09-Nov-08 01:11 by prod_rel_team</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Device Type</name>\n" +
                "        <value>CISCO</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Device Model</name>\n" +
                "        <value>cisco3620</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Management IP Address</name>\n" +
                "        <value>10.11.12.108</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>ipv6Forwarding</name>\n" +
                "        <value>\n" +
                "                YES\n" +
                "                </value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Device Model Oid</name>\n" +
                "        <value>1.3.6.1.4.1.9.1.122</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>BGPLocalASInfo</name>\n" +
                "        <value>ff:c0:00:40</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>baseBridgeAddress</name>\n" +
                "        <value>00:00:00:00:00:00</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>stpDesignatedRoot</name>\n" +
                "        <value>Fa0/0</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>siteID</name>\n" +
                "        <value>R8</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>X Coordinate</name>\n" +
                "        <value></value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Y Coordinate</name>\n" +
                "        <value></value>\n" +
                "        </parameter>\n" +
                "        </parameters>\n" +
                "        <object>\n" +
                "        <name>FastEthernet0/0</name>\n" +
                "        <objectType>Discovery Interface</objectType>\n" +
                "        <parameters>\n" +
                "        <parameter>\n" +
                "        <name>ifIndex</name>\n" +
                "        <value>1</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>ifDescr</name>\n" +
                "        <value>FastEthernet0/0</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>ifType</name>\n" +
                "        <value>6</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>ifSpeed</name>\n" +
                "        <value></value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>ifAdminStatus</name>\n" +
                "        <value>UP</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>ifOperStatus</name>\n" +
                "        <value>UP</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>ifPhysAddress</name>\n" +
                "        <value>c2:07:02:44:00:00</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>CableCut</name>\n" +
                "        <value>NO</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>vrfForwarding</name>\n" +
                "        <value></value>\n" +
                "        </parameter>\n" +
                "        </parameters>\n" +
                "        <object>\n" +
                "        <name>172.16.48.8/255.255.255.240</name>\n" +
                "        <objectType>IPv4 Address</objectType>\n" +
                "        <parameters>\n" +
                "        <parameter>\n" +
                "        <name>IPv4Address</name>\n" +
                "        <value>172.16.48.8</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>ipSubnetMask</name>\n" +
                "        <value>255.255.255.240</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>ipv4Subnet</name>\n" +
                "        <value>172.16.48.0/28</value>\n" +
                "        </parameter>\n" +
                "        </parameters>\n" +
                "        </object>\n" +
                "        <object>\n" +
                "        <name>R4</name>\n" +
                "        <objectType>Discovered Neighbor</objectType>\n" +
                "        <parameters>\n" +
                "        <parameter>\n" +
                "        <name>Reachable</name>\n" +
                "        <value>NO</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>SNMP Community</name>\n" +
                "        <value></value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Discovery Method</name>\n" +
                "        <value>CDP</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor Port</name>\n" +
                "        <value>FastEthernet2/0</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor Platform</name>\n" +
                "        <value></value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor IP Address</name>\n" +
                "        <value></value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor hostname</name>\n" +
                "        <value>R4</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor Device Type</name>\n" +
                "        <value>CISCO</value>\n" +
                "        </parameter>\n" +
                "        </parameters>\n" +
                "        </object>\n" +
                "        </object>\n" +
                "        <object>\n" +
                "        <name>FastEthernet0/1</name>\n" +
                "        <objectType>Discovery Interface</objectType>\n" +
                "        <parameters>\n" +
                "        <parameter>\n" +
                "        <name>ifIndex</name>\n" +
                "        <value>2</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>ifDescr</name>\n" +
                "        <value>FastEthernet0/1</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>ifType</name>\n" +
                "        <value>6</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>ifSpeed</name>\n" +
                "        <value></value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>ifAdminStatus</name>\n" +
                "        <value>UP</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>ifOperStatus</name>\n" +
                "        <value>UP</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>ifPhysAddress</name>\n" +
                "        <value>c2:07:02:44:00:01</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>CableCut</name>\n" +
                "        <value>NO</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>vrfForwarding</name>\n" +
                "        <value></value>\n" +
                "        </parameter>\n" +
                "        </parameters>\n" +
                "        <object>\n" +
                "        <name>10.11.12.108/255.255.255.0</name>\n" +
                "        <objectType>IPv4 Address</objectType>\n" +
                "        <parameters>\n" +
                "        <parameter>\n" +
                "        <name>IPv4Address</name>\n" +
                "        <value>10.11.12.108</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>ipSubnetMask</name>\n" +
                "        <value>255.255.255.0</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>ipv4Subnet</name>\n" +
                "        <value>10.11.12.0/24</value>\n" +
                "        </parameter>\n" +
                "        </parameters>\n" +
                "        </object>\n" +
                "        <object>\n" +
                "        <name>R1</name>\n" +
                "        <objectType>Discovered Neighbor</objectType>\n" +
                "        <parameters>\n" +
                "        <parameter>\n" +
                "        <name>Reachable</name>\n" +
                "        <value>YES</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>SNMP Community</name>\n" +
                "        <value>iTransformer-r</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Discovery Method</name>\n" +
                "        <value>r_OSPF,ARP,CDP</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor Port</name>\n" +
                "        <value>FastEthernet0/1</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor IP Address</name>\n" +
                "        <value>10.11.12.11</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor hostname</name>\n" +
                "        <value>R1</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor Device Type</name>\n" +
                "        <value>CISCO</value>\n" +
                "        </parameter>\n" +
                "        </parameters>\n" +
                "        </object>\n" +
                "        <object>\n" +
                "        <name>R2</name>\n" +
                "        <objectType>Discovered Neighbor</objectType>\n" +
                "        <parameters>\n" +
                "        <parameter>\n" +
                "        <name>Reachable</name>\n" +
                "        <value>YES</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>SNMP Community</name>\n" +
                "        <value>iTransformer-r</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Discovery Method</name>\n" +
                "        <value>r_OSPF,ARP,CDP</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor Port</name>\n" +
                "        <value>FastEthernet0/1</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor IP Address</name>\n" +
                "        <value>10.11.12.12</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor hostname</name>\n" +
                "        <value>R2</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor Device Type</name>\n" +
                "        <value>CISCO</value>\n" +
                "        </parameter>\n" +
                "        </parameters>\n" +
                "        </object>\n" +
                "        <object>\n" +
                "        <name>R5</name>\n" +
                "        <objectType>Discovered Neighbor</objectType>\n" +
                "        <parameters>\n" +
                "        <parameter>\n" +
                "        <name>Reachable</name>\n" +
                "        <value>YES</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>SNMP Community</name>\n" +
                "        <value>iTransformer-r</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Discovery Method</name>\n" +
                "        <value>r_OSPF,ARP,CDP</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor Port</name>\n" +
                "        <value>FastEthernet0/1</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor IP Address</name>\n" +
                "        <value>10.11.12.15</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor hostname</name>\n" +
                "        <value>R5</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor Device Type</name>\n" +
                "        <value>CISCO</value>\n" +
                "        </parameter>\n" +
                "        </parameters>\n" +
                "        </object>\n" +
                "        <object>\n" +
                "        <name>R7</name>\n" +
                "        <objectType>Discovered Neighbor</objectType>\n" +
                "        <parameters>\n" +
                "        <parameter>\n" +
                "        <name>Reachable</name>\n" +
                "        <value>YES</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>SNMP Community</name>\n" +
                "        <value>iTransformer-r</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Discovery Method</name>\n" +
                "        <value>r_OSPF,ARP,CDP</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor Port</name>\n" +
                "        <value>FastEthernet0/1</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor IP Address</name>\n" +
                "        <value>10.11.12.107</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor hostname</name>\n" +
                "        <value>R7</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor Device Type</name>\n" +
                "        <value>CISCO</value>\n" +
                "        </parameter>\n" +
                "        </parameters>\n" +
                "        </object>\n" +
                "        <object>\n" +
                "        <name>R6</name>\n" +
                "        <objectType>Discovered Neighbor</objectType>\n" +
                "        <parameters>\n" +
                "        <parameter>\n" +
                "        <name>Reachable</name>\n" +
                "        <value>YES</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>SNMP Community</name>\n" +
                "        <value>iTransformer-r</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Discovery Method</name>\n" +
                "        <value>r_OSPF,ARP,CDP</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor Port</name>\n" +
                "        <value>FastEthernet0/1</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor IP Address</name>\n" +
                "        <value>10.11.12.106</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor hostname</name>\n" +
                "        <value>R6</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor Device Type</name>\n" +
                "        <value>CISCO</value>\n" +
                "        </parameter>\n" +
                "        </parameters>\n" +
                "        </object>\n" +
                "        <object>\n" +
                "        <name>10.11.12.150</name>\n" +
                "        <objectType>Discovered Neighbor</objectType>\n" +
                "        <parameters>\n" +
                "        <parameter>\n" +
                "        <name>Reachable</name>\n" +
                "        <value>NO</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>SNMP Community</name>\n" +
                "        <value></value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Discovery Method</name>\n" +
                "        <value>ARP</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor Port</name>\n" +
                "        <value></value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor Platform</name>\n" +
                "        <value></value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor IP Address</name>\n" +
                "        <value>10.11.12.150</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor hostname</name>\n" +
                "        <value></value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor Device Type</name>\n" +
                "        <value>DEFAULT</value>\n" +
                "        </parameter>\n" +
                "        </parameters>\n" +
                "        </object>\n" +
                "        <object>\n" +
                "        <name>telecom-01</name>\n" +
                "        <objectType>Discovered Neighbor</objectType>\n" +
                "        <parameters>\n" +
                "        <parameter>\n" +
                "        <name>Reachable</name>\n" +
                "        <value>NO</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>SNMP Community</name>\n" +
                "        <value></value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Discovery Method</name>\n" +
                "        <value>CDP</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor Port</name>\n" +
                "        <value>FastEthernet0/9</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor Platform</name>\n" +
                "        <value></value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor IP Address</name>\n" +
                "        <value></value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor hostname</name>\n" +
                "        <value>telecom-01</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor Device Type</name>\n" +
                "        <value>CISCO</value>\n" +
                "        </parameter>\n" +
                "        </parameters>\n" +
                "        </object>\n" +
                "        </object>\n" +
                "        <object>\n" +
                "        <name>Null0</name>\n" +
                "        <objectType>Discovery Interface</objectType>\n" +
                "        <parameters>\n" +
                "        <parameter>\n" +
                "        <name>ifIndex</name>\n" +
                "        <value>4</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>ifDescr</name>\n" +
                "        <value>Null0</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>ifType</name>\n" +
                "        <value>1</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>ifSpeed</name>\n" +
                "        <value></value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>ifAdminStatus</name>\n" +
                "        <value>UP</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>ifOperStatus</name>\n" +
                "        <value>UP</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>ifPhysAddress</name>\n" +
                "        <value></value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>CableCut</name>\n" +
                "        <value>NO</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>vrfForwarding</name>\n" +
                "        <value></value>\n" +
                "        </parameter>\n" +
                "        </parameters>\n" +
                "        </object>\n" +
                "        <object>\n" +
                "        <name>Loopback0</name>\n" +
                "        <objectType>Discovery Interface</objectType>\n" +
                "        <parameters>\n" +
                "        <parameter>\n" +
                "        <name>ifIndex</name>\n" +
                "        <value>5</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>ifDescr</name>\n" +
                "        <value>Loopback0</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>ifType</name>\n" +
                "        <value>24</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>ifSpeed</name>\n" +
                "        <value></value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>ifAdminStatus</name>\n" +
                "        <value>UP</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>ifOperStatus</name>\n" +
                "        <value>UP</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>ifPhysAddress</name>\n" +
                "        <value></value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>CableCut</name>\n" +
                "        <value>NO</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>vrfForwarding</name>\n" +
                "        <value></value>\n" +
                "        </parameter>\n" +
                "        </parameters>\n" +
                "        <object>\n" +
                "        <name>192.168.8.8/255.255.255.255</name>\n" +
                "        <objectType>IPv4 Address</objectType>\n" +
                "        <parameters>\n" +
                "        <parameter>\n" +
                "        <name>IPv4Address</name>\n" +
                "        <value>192.168.8.8</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>ipSubnetMask</name>\n" +
                "        <value>255.255.255.255</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>ipv4Subnet</name>\n" +
                "        <value>192.168.8.8/32</value>\n" +
                "        </parameter>\n" +
                "        </parameters>\n" +
                "        </object>\n" +
                "        </object>\n" +
                "        <object>\n" +
                "        <name>FastEthernet0/0-mpls layer</name>\n" +
                "        <objectType>Discovery Interface</objectType>\n" +
                "        <parameters>\n" +
                "        <parameter>\n" +
                "        <name>ifIndex</name>\n" +
                "        <value>6</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>ifDescr</name>\n" +
                "        <value>FastEthernet0/0-mpls layer</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>ifType</name>\n" +
                "        <value>mpls</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>ifSpeed</name>\n" +
                "        <value></value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>ifAdminStatus</name>\n" +
                "        <value>UP</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>ifOperStatus</name>\n" +
                "        <value>UP</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>ifPhysAddress</name>\n" +
                "        <value></value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>CableCut</name>\n" +
                "        <value>NO</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>vrfForwarding</name>\n" +
                "        <value></value>\n" +
                "        </parameter>\n" +
                "        </parameters>\n" +
                "        </object>\n" +
                "        <object>\n" +
                "        <name>DeviceLogicalData</name>\n" +
                "        <objectType>DeviceLogicalData</objectType>\n" +
                "        <parameters/>\n" +
                "        <object>\n" +
                "        <name>R1</name>\n" +
                "        <objectType>Discovered Neighbor</objectType>\n" +
                "        <parameters>\n" +
                "        <parameter>\n" +
                "        <name>Reachable</name>\n" +
                "        <value>YES</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>SNMP Community</name>\n" +
                "        <value>iTransformer-r</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Discovery Method</name>\n" +
                "        <value>OSPF</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor IP Address</name>\n" +
                "        <value>10.11.12.11</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor hostname</name>\n" +
                "        <value>R1</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor Device Type</name>\n" +
                "        <value>CISCO</value>\n" +
                "        </parameter>\n" +
                "        </parameters>\n" +
                "        </object>\n" +
                "        <object>\n" +
                "        <name>R2</name>\n" +
                "        <objectType>Discovered Neighbor</objectType>\n" +
                "        <parameters>\n" +
                "        <parameter>\n" +
                "        <name>Reachable</name>\n" +
                "        <value>YES</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>SNMP Community</name>\n" +
                "        <value>iTransformer-r</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Discovery Method</name>\n" +
                "        <value>OSPF</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor IP Address</name>\n" +
                "        <value>10.11.12.12</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor hostname</name>\n" +
                "        <value>R2</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor Device Type</name>\n" +
                "        <value>CISCO</value>\n" +
                "        </parameter>\n" +
                "        </parameters>\n" +
                "        </object>\n" +
                "        <object>\n" +
                "        <name>R5</name>\n" +
                "        <objectType>Discovered Neighbor</objectType>\n" +
                "        <parameters>\n" +
                "        <parameter>\n" +
                "        <name>Reachable</name>\n" +
                "        <value>YES</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>SNMP Community</name>\n" +
                "        <value>iTransformer-r</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Discovery Method</name>\n" +
                "        <value>OSPF</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor IP Address</name>\n" +
                "        <value>10.11.12.15</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor hostname</name>\n" +
                "        <value>R5</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor Device Type</name>\n" +
                "        <value>CISCO</value>\n" +
                "        </parameter>\n" +
                "        </parameters>\n" +
                "        </object>\n" +
                "        <object>\n" +
                "        <name>R6</name>\n" +
                "        <objectType>Discovered Neighbor</objectType>\n" +
                "        <parameters>\n" +
                "        <parameter>\n" +
                "        <name>Reachable</name>\n" +
                "        <value>YES</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>SNMP Community</name>\n" +
                "        <value>iTransformer-r</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Discovery Method</name>\n" +
                "        <value>OSPF</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor IP Address</name>\n" +
                "        <value>10.11.12.106</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor hostname</name>\n" +
                "        <value>R6</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor Device Type</name>\n" +
                "        <value>CISCO</value>\n" +
                "        </parameter>\n" +
                "        </parameters>\n" +
                "        </object>\n" +
                "        <object>\n" +
                "        <name>R7</name>\n" +
                "        <objectType>Discovered Neighbor</objectType>\n" +
                "        <parameters>\n" +
                "        <parameter>\n" +
                "        <name>Reachable</name>\n" +
                "        <value>YES</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>SNMP Community</name>\n" +
                "        <value>iTransformer-r</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Discovery Method</name>\n" +
                "        <value>OSPF</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor IP Address</name>\n" +
                "        <value>10.11.12.107</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor hostname</name>\n" +
                "        <value>R7</value>\n" +
                "        </parameter>\n" +
                "        <parameter>\n" +
                "        <name>Neighbor Device Type</name>\n" +
                "        <value>CISCO</value>\n" +
                "        </parameter>\n" +
                "        </parameters>\n" +
                "        </object>\n" +
                "        </object>\n" +
                "        <object>\n" +
                "        <name>mplsL3VPNs</name>\n" +
                "        <objectType>mplsL3VPNs</objectType>\n" +
                "        <parameters/>\n" +
                "        </object>\n" +
                "        <object>\n" +
                "        <name>IPSEC VPNS</name>\n" +
                "        <objectType>IPSECVPNs</objectType>\n" +
                "        <parameters/>\n" +
                "        </object>\n" +
                "        </DiscoveredDevice>";

        try {
            System.out.println(new XmlFormatter().format(unformattedXml));
        } catch (IOException e) {

            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

}