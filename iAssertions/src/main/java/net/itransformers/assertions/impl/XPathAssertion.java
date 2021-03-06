/*
 * XPathAssertion.java
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

package net.itransformers.assertions.impl;

import net.itransformers.assertions.Assertion;
import net.itransformers.assertions.AssertionResult;
import net.itransformers.assertions.AssertionType;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.lang.IllegalArgumentException;
import java.lang.String;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: VasilYordanov
 * Date: 6/7/13
 * Time: 11:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class XPathAssertion implements Assertion {
    private final Map<String, String> declareNamespaces = new HashMap<String, String>();
    private final String expectedValue;
    private final Map<String, String> options = new HashMap<String, String>();
    private final XPathExpression xPathExpression;
    private final DocumentBuilder builder;
    private final String assertionName;

    public XPathAssertion(String assertionName, Map<String, String> params) {
        this.assertionName = assertionName;
        String paramNamespaces = params.get("declareNamespaces");
        if (paramNamespaces != null){
            String[] nameSpacesArr = paramNamespaces.split(",");
            for (String nameSpace : nameSpacesArr) {
                String[] entries = nameSpace.split(",");
                if (entries.length != 2)
                    throw new IllegalArgumentException("Invalid parameter declareNamespaces:" + declareNamespaces);
                this.declareNamespaces.put(entries[0], entries[1]);
            }
        }
        this.expectedValue = params.get("expectedValue");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }


        String xPathStr = params.get("xpath");
        XPathFactory xPathFactory = javax.xml.xpath.XPathFactory.newInstance();
        XPath xPath = xPathFactory.newXPath();

        try {
            xPathExpression = xPath.compile(xPathStr);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public AssertionResult doAssert(InputSource source) {

        try {
            Document xmlDocument = builder.parse(source);
            String actualValue = xPathExpression.evaluate(xmlDocument, XPathConstants.STRING).toString();
            if (expectedValue == null && actualValue == null) {
                return new AssertionResult(assertionName, AssertionType.SUCCESS,
                        String.format("Expected value: null matched"));
            } else if (expectedValue == null){
                return new AssertionResult(assertionName, AssertionType.FAILED,
                        String.format("Expected value: null, does not match actual value '%s'", actualValue));
            }
            if (expectedValue.equals(actualValue)){
                return new AssertionResult(assertionName, AssertionType.SUCCESS,
                        String.format("Expected value: '%s', matches actual value", expectedValue));
            } else {
                return new AssertionResult(assertionName, AssertionType.FAILED,
                        String.format("Expected value: '%s', does not match actual value '%s'", expectedValue, actualValue));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
