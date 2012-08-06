/*
 * iTransformer is an open source tool able to discover and transform
 *  IP network infrastructures.
 *  Copyright (C) 2012  http://itransformers.net
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
