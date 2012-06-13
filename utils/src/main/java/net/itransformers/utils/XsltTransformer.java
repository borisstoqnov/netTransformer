/*
 * iTransformer is an open source tool able to discover IP networks
 * and to perform dynamic data data population into a xml based inventory system.
 * Copyright (C) 2010  http://itransformers.net
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.itransformers.utils;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class XsltTransformer {
    public void transformXML(InputStream xmlIn, File xslt, OutputStream xmlOut, Map<String,String> params, String ipAddress) throws ParserConfigurationException, IOException, SAXException, TransformerException {

        // JAXP reads data using the Source interface
        Source xmlSource = new StreamSource(xmlIn);

        Transformer trans = StylesheetCache.newTransformer(xslt);
        if (params != null) {
            for (String param: params.keySet()){
                trans.setParameter(param, params.get(param));
            }
        }
        if (ipAddress!=null){
            trans.setParameter("ipAddress",ipAddress);
        }
        trans.transform(xmlSource, new StreamResult(xmlOut));
    }
}
