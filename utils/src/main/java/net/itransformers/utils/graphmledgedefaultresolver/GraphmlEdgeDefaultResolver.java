package net.itransformers.utils.graphmledgedefaultresolver;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Vasil Yordanov on 9/14/2015.
 */
public class GraphmlEdgeDefaultResolver {
    private final SAXParser saxParser;

    public GraphmlEdgeDefaultResolver() throws ParserConfigurationException, SAXException {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        saxParser = factory.newSAXParser();

    }
    public String resolveEdgeDefault(File graphmlFile){
        try {

            DefaultHandler handler = new DefaultHandler() {

                public void startElement(String uri, String localName, String qName,
                                         Attributes attributes) throws SAXException {
                    if (qName.equalsIgnoreCase("graph")) {
                        String edgeDefault = attributes.getValue("edgedefault");
                        Map<String, Object> data = new HashMap<String, Object>();
                        data.put("edgedefault", edgeDefault);
                        throw new DataFoundSAXTerminatorException("Terminate parsing", null, data);
                    }
                }
            };

            saxParser.parse(graphmlFile, handler);

        } catch (DataFoundSAXTerminatorException e) {
            return (String) e.getData().get("edgedefault");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}

