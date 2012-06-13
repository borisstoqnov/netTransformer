package net.itransformers.topologyviewer.parameterfactory.impl;

import net.itransformers.topologyviewer.parameterfactory.ParameterFactoryElement;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 **
 * 
 * Copyright 
 */
public class XmlParamFactoryElement implements ParameterFactoryElement{
    static Logger logger = Logger.getLogger(XmlParamFactoryElement.class);
    private String contextXmlFileNameKey;
    private Map<String, String> params;

    @Override
    public void init(Map<String, String> config, Map<String, String> params) {
        this.params = params;
        contextXmlFileNameKey = config.get("contextXmlFileNameKey");
    }
    @Override
    public Map<String, String> createParams(Map<String, Object> context, Map<String, String> currentParams) throws Exception {
        String xmlFileName= (String) context.get(contextXmlFileNameKey);
        if (xmlFileName == null) {
            throw new Exception(contextXmlFileNameKey+" key is not found in context");
        }
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        dbf.setValidating(false);
        DocumentBuilder db = dbf.newDocumentBuilder();
        URL url1 = new URL(xmlFileName);
        InputStream in = url1.openStream();
        Document doc = null;
        try {
            doc = db.parse(in);
        } finally {
            if (in != null) in.close();
        }
        Map<String, String> result = new HashMap<String, String>();
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        for (String key : params.keySet()) {
            String expression = params.get(key);
            if (currentParams != null){
                for (String currentKey : currentParams.keySet()) {
                    expression = expression.replaceAll("\\$"+currentKey,currentParams.get(currentKey));
                }
            }
            for (String currentKey : result.keySet()) {
                expression = expression.replaceAll("\\$"+currentKey,result.get(currentKey));
            }
            XPathExpression expr;
            logger.debug("compile expression: "+expression);
            expr = xpath.compile(expression);
            String val = (String) expr.evaluate(doc, XPathConstants.STRING);
            result.put(key,val);
        }
        return result;
    }
}
