/*
 * ScriptAssertion.java
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

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.util.GroovyScriptEngine;
import net.itransformers.assertions.Assertion;
import net.itransformers.assertions.AssertionResult;
import net.itransformers.assertions.AssertionType;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.lang.String;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: VasilYordanov
 * Date: 6/7/13
 * Time: 11:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class ScriptAssertion implements Assertion {
    private final DocumentBuilder builder;
    private final String assertionName;
    private String script;
    private GroovyScriptEngine gse;

    public ScriptAssertion(String assertionName, Map<String, String> params) {
        this.assertionName = assertionName;
        this.script = params.get("script");
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public AssertionResult doAssert(InputSource source) {
        Document xmlDocument;
        try {
            xmlDocument = builder.parse(source);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Binding binding = new Binding();
        binding.setVariable("document", xmlDocument);
        GroovyShell shell = new GroovyShell(binding);

        Object value = shell.evaluate(script);
        if (value instanceof Boolean){
            if ((Boolean)value){
                return new AssertionResult(assertionName, AssertionType.SUCCESS);
            } else {
                return new AssertionResult(assertionName, AssertionType.FAILED);
            }
        } else {
            throw new RuntimeException("value of type "+value.getClass()+" is returned from groovy script");
        }

    }

}
