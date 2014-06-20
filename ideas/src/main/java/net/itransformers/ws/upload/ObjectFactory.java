
package net.itransformers.ws.upload;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.itransformers.ws.upload package. 
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

    private final static QName _ImportNodeResponse_QNAME = new QName("http://upload.ws.itransformers.net/", "importNodeResponse");
    private final static QName _Exception_QNAME = new QName("http://upload.ws.itransformers.net/", "Exception");
    private final static QName _ImportNode_QNAME = new QName("http://upload.ws.itransformers.net/", "importNode");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.itransformers.ws.upload
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Node }
     * 
     */
    public Node createNode() {
        return new Node();
    }

    /**
     * Create an instance of {@link Node.Attributes.Entry }
     * 
     */
    public Node.Attributes.Entry createNodeAttributesEntry() {
        return new Node.Attributes.Entry();
    }

    /**
     * Create an instance of {@link ImportNodeResponse }
     * 
     */
    public ImportNodeResponse createImportNodeResponse() {
        return new ImportNodeResponse();
    }

    /**
     * Create an instance of {@link ImportNode }
     * 
     */
    public ImportNode createImportNode() {
        return new ImportNode();
    }

    /**
     * Create an instance of {@link Exception }
     * 
     */
    public Exception createException() {
        return new Exception();
    }

    /**
     * Create an instance of {@link Node.Attributes }
     * 
     */
    public Node.Attributes createNodeAttributes() {
        return new Node.Attributes();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ImportNodeResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://upload.ws.itransformers.net/", name = "importNodeResponse")
    public JAXBElement<ImportNodeResponse> createImportNodeResponse(ImportNodeResponse value) {
        return new JAXBElement<ImportNodeResponse>(_ImportNodeResponse_QNAME, ImportNodeResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Exception }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://upload.ws.itransformers.net/", name = "Exception")
    public JAXBElement<Exception> createException(Exception value) {
        return new JAXBElement<Exception>(_Exception_QNAME, Exception.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ImportNode }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://upload.ws.itransformers.net/", name = "importNode")
    public JAXBElement<ImportNode> createImportNode(ImportNode value) {
        return new JAXBElement<ImportNode>(_ImportNode_QNAME, ImportNode.class, null, value);
    }

}
