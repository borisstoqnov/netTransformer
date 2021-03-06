//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2016.09.26 at 11:13:19 PM EEST 
//


package net.itransformers.topologyviewer.config;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the net.itransformers.topologyviewer.config package. 
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

    private final static QName _TopologyViewerConf_QNAME = new QName("", "topology-viewer-conf");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: net.itransformers.topologyviewer.config
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link EdgeColorType }
     * 
     */
    public EdgeColorType createEdgeColorType() {
        return new EdgeColorType();
    }

    /**
     * Create an instance of {@link IconType }
     * 
     */
    public IconType createIconType() {
        return new IconType();
    }

    /**
     * Create an instance of {@link EdgeStrokeType }
     * 
     */
    public EdgeStrokeType createEdgeStrokeType() {
        return new EdgeStrokeType();
    }

    /**
     * Create an instance of {@link TopologyViewerConfType }
     * 
     */
    public TopologyViewerConfType createTopologyViewerConfType() {
        return new TopologyViewerConfType();
    }

    /**
     * Create an instance of {@link IncludeType }
     * 
     */
    public IncludeType createIncludeType() {
        return new IncludeType();
    }

    /**
     * Create an instance of {@link SubMenuType }
     * 
     */
    public SubMenuType createSubMenuType() {
        return new SubMenuType();
    }

    /**
     * Create an instance of {@link DataType }
     * 
     */
    public DataType createDataType() {
        return new DataType();
    }

    /**
     * Create an instance of {@link TooltipType }
     * 
     */
    public TooltipType createTooltipType() {
        return new TooltipType();
    }

    /**
     * Create an instance of {@link HopsType }
     * 
     */
    public HopsType createHopsType() {
        return new HopsType();
    }

    /**
     * Create an instance of {@link ParamType }
     * 
     */
    public ParamType createParamType() {
        return new ParamType();
    }

    /**
     * Create an instance of {@link DataMatcherType }
     * 
     */
    public DataMatcherType createDataMatcherType() {
        return new DataMatcherType();
    }

    /**
     * Create an instance of {@link RightClickItemType }
     * 
     */
    public RightClickItemType createRightClickItemType() {
        return new RightClickItemType();
    }

    /**
     * Create an instance of {@link FiltersType }
     * 
     */
    public FiltersType createFiltersType() {
        return new FiltersType();
    }

    /**
     * Create an instance of {@link FilterType }
     * 
     */
    public FilterType createFilterType() {
        return new FilterType();
    }

    /**
     * Create an instance of {@link ScalingFactorType }
     * 
     */
    public ScalingFactorType createScalingFactorType() {
        return new ScalingFactorType();
    }

    /**
     * Create an instance of {@link EdgeColorType.Data }
     * 
     */
    public EdgeColorType.Data createEdgeColorTypeData() {
        return new EdgeColorType.Data();
    }

    /**
     * Create an instance of {@link IconType.Data }
     * 
     */
    public IconType.Data createIconTypeData() {
        return new IconType.Data();
    }

    /**
     * Create an instance of {@link EdgeStrokeType.Data }
     * 
     */
    public EdgeStrokeType.Data createEdgeStrokeTypeData() {
        return new EdgeStrokeType.Data();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TopologyViewerConfType }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "", name = "topology-viewer-conf")
    public JAXBElement<TopologyViewerConfType> createTopologyViewerConf(TopologyViewerConfType value) {
        return new JAXBElement<TopologyViewerConfType>(_TopologyViewerConf_QNAME, TopologyViewerConfType.class, null, value);
    }

}
