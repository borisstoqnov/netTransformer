package net.itransformers.utils.graphmledgedefaultresolver;

import org.xml.sax.Locator;
import org.xml.sax.SAXParseException;

import java.util.Map;

public class DataFoundSAXTerminatorException extends SAXParseException {

    private Map<String, Object> data;

    public DataFoundSAXTerminatorException(String message, Locator locator, Map<String, Object> data) {
        super(message, locator);
        this.data = data;
    }

    public Map<String, Object> getData() {
        return data;
    }
}