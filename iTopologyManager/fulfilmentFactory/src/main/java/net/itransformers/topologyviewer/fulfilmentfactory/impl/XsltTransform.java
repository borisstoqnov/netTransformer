/*
 * XsltTransform.java
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
