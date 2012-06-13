/*
 * iDiscovery is an open source tool able to discover IP networks
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

package net.itransformers.topologyviewer.fulfilmentfactory.impl;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class XsltTransform {
    String xsl = null; //input xsl


    public XsltTransform(String pathToXSL) {
        this.xsl=pathToXSL;
    }
    public OutputStream myTransformer (String XML) throws TransformerException, IOException {

            // Create a transform factory instance.
            TransformerFactory tfactory = TransformerFactory.newInstance();
             // Create a transformer for the stylesheet.
            Transformer transformer = tfactory.newTransformer(new StreamSource(new File(this.xsl)));


            // Transform the source XML to System.out.
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        Result result = new StreamResult(baos);
//         String result;
             StreamResult result = new StreamResult(baos);
//           transformer.transform(new StreamSource(new URL(this.xml).openStream()),new StreamResult(result));

            transformer.transform(new StreamSource(XML),result);

            System.out.println(result.toString());
            return result.getOutputStream();

        }

}
