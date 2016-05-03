/*
 * XsltTransformer.java
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

package net.itransformers.utils;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.Map;

public class XsltTransformer {
    public void transformXML(InputStream xmlIn, File xslt, OutputStream xmlOut, Map<String, String> params) throws ParserConfigurationException, IOException, SAXException, TransformerException {

        Source xmlSource = new StreamSource(xmlIn);

        Transformer trans = StylesheetCache.newTransformer(xslt);
        if (params != null) {
            for (String param : params.keySet()) {
                trans.setParameter(param, params.get(param));
            }
        }
        trans.transform(xmlSource, new StreamResult(xmlOut));
    }

    public void transformXML(InputStream xmlIn, File xslt, OutputStream xmlOut) throws ParserConfigurationException, IOException, SAXException, TransformerException {

        Source xmlSource = new StreamSource(xmlIn);

        Transformer trans = StylesheetCache.newTransformer(xslt);
        trans.transform(xmlSource, new StreamResult(xmlOut));
    }

    public static ByteArrayOutputStream transformXML(File xslt,ByteArrayInputStream inputStream,  Map<String, String> params) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();


        Transformer trans = StylesheetCache.newTransformer(xslt);
        if (params != null) {
            for (String param : params.keySet()) {
                trans.setParameter(param, params.get(param));
            }
        }
        Source xmlSource = new StreamSource(inputStream);
        trans.transform(xmlSource, new StreamResult(outputStream));

        return outputStream;

    }

    public static ByteArrayOutputStream transformXML(File xslt, ByteArrayInputStream inputStream) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();


        Transformer trans = StylesheetCache.newTransformer(xslt);
        Source xmlSource = new StreamSource(inputStream);
        trans.transform(xmlSource, new StreamResult(outputStream));

        return outputStream;
    }

}
