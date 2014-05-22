//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.05.20 at 09:46:58 AM EEST 
//


package net.itransformers.topologyviewer.config;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for includeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="includeType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *       &lt;attribute name="dataKey" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="dataValue" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="for" type="{}forType" default="node" />
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "includeType", propOrder = {
    "value"
})
public class IncludeType {

    @XmlValue
    protected String value;
    @XmlAttribute(name = "dataKey")
    protected String dataKey;
    @XmlAttribute(name = "dataValue")
    protected String dataValue;
    @XmlAttribute(name = "for")
    protected ForType _for;

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * Gets the value of the dataKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataKey() {
        return dataKey;
    }

    /**
     * Sets the value of the dataKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataKey(String value) {
        this.dataKey = value;
    }

    /**
     * Gets the value of the dataValue property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataValue() {
        return dataValue;
    }

    /**
     * Sets the value of the dataValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataValue(String value) {
        this.dataValue = value;
    }

    /**
     * Gets the value of the for property.
     * 
     * @return
     *     possible object is
     *     {@link ForType }
     *     
     */
    public ForType getFor() {
        if (_for == null) {
            return ForType.NODE;
        } else {
            return _for;
        }
    }

    /**
     * Sets the value of the for property.
     * 
     * @param value
     *     allowed object is
     *     {@link ForType }
     *     
     */
    public void setFor(ForType value) {
        this._for = value;
    }

}
