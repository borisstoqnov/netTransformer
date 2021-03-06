//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.26 at 11:15:12 PM EEST 
//


package net.itransformers.assertions.config;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.itransformers.assertions.config package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Assertions_QNAME = new QName("", "assertions");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.itransformers.assertions.config
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AssertionsType }
     * 
     */
    public AssertionsType createAssertionsType() {
        return new AssertionsType();
    }

    /**
     * Create an instance of {@link AssertType }
     * 
     */
    public AssertType createAssertType() {
        return new AssertType();
    }

    /**
     * Create an instance of {@link ParameterType }
     * 
     */
    public ParameterType createParameterType() {
        return new ParameterType();
    }

    /**
     * Create an instance of {@link AssertTypeType }
     * 
     */
    public AssertTypeType createAssertTypeType() {
        return new AssertTypeType();
    }

    /**
     * Create an instance of {@link AssertTypesType }
     * 
     */
    public AssertTypesType createAssertTypesType() {
        return new AssertTypesType();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AssertionsType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "assertions")
    public JAXBElement<AssertionsType> createAssertions(AssertionsType value) {
        return new JAXBElement<AssertionsType>(_Assertions_QNAME, AssertionsType.class, null, value);
    }

}
