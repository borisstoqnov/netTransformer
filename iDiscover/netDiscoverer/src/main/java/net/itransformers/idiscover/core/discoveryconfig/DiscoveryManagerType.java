/*
 * DiscoveryManagerType.java
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

//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2011.09.12 at 04:54:56 PM EEST 
//


package net.itransformers.idiscover.core.discoveryconfig;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for snmpDiscovery-managerType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="snmpDiscovery-managerType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="snmpDiscovery-helper" type="{}snmpDiscovery-helperType"/>
 *         &lt;element name="snmpDiscovery-manager-listeners" type="{}snmpDiscovery-manager-listenersType"/>
 *         &lt;element name="management-vlans" type="{}management-vlansType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "discovery-managerType", propOrder = {
    "discoveryHelper",
    "discoveryManagerListeners",
     "postDiscoveryManagerListeners",
     "managementVlans"
})
public class DiscoveryManagerType {

    @XmlElement(name = "discovery-helper", required = true)
    protected DiscoveryHelperType discoveryHelper;
    @XmlElement(name = "discovery-manager-listeners", required = true)
    protected DiscoveryManagerListenersType discoveryManagerListeners;
    @XmlElement(name = "post-discovery-manager-listeners", required = true)
    protected PostDiscoveryManagerListenersType postDiscoveryManagerListeners;

    @XmlElement(name = "management-vlans", required = true)
    protected ManagementVlansType managementVlans;

    /**
     * Gets the value of the discoveryHelper property.
     * 
     * @return
     *     possible object is
     *     {@link DiscoveryHelperType }
     *     
     */
    public DiscoveryHelperType getDiscoveryHelper() {
        return discoveryHelper;
    }

    /**
     * Sets the value of the discoveryHelper property.
     * 
     * @param value
     *     allowed object is
     *     {@link DiscoveryHelperType }
     *     
     */
    public void setDiscoveryHelper(DiscoveryHelperType value) {
        this.discoveryHelper = value;
    }

    /**
     * Gets the value of the discoveryManagerListeners property.
     * 
     * @return
     *     possible object is
     *     {@link DiscoveryManagerListenersType }
     *     
     */
    public DiscoveryManagerListenersType getDiscoveryManagerListeners() {
        return discoveryManagerListeners;
    }

    public PostDiscoveryManagerListenersType getPostDiscoveryManagerListeners() {
        return postDiscoveryManagerListeners;
    }

    /**
     * Sets the value of the discoveryManagerListeners property.
     * 
     * @param value
     *     allowed object is
     *     {@link DiscoveryManagerListenersType }
     *     
     */
    public void setDiscoveryManagerListeners(DiscoveryManagerListenersType value) {
        this.discoveryManagerListeners = value;
    }

    public void setPostDiscoveryManagerListeners(PostDiscoveryManagerListenersType value) {
        this.postDiscoveryManagerListeners = value;
    }

    /**
     * Gets the value of the managementVlans property.
     * 
     * @return
     *     possible object is
     *     {@link ManagementVlansType }
     *     
     */
    public ManagementVlansType getManagementVlans() {
        return managementVlans;
    }

    /**
     * Sets the value of the managementVlans property.
     * 
     * @param value
     *     allowed object is
     *     {@link ManagementVlansType }
     *     
     */
    public void setManagementVlans(ManagementVlansType value) {
        this.managementVlans = value;
    }

}
