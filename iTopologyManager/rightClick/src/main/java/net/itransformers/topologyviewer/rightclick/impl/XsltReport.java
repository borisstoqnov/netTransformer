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

package net.itransformers.topologyviewer.rightclick.impl;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;


public class XsltReport {
    File xml = null; //input xml
    String xsl = null; //input xsl
    String tableXSL = null; //input xsl

    public XsltReport(String pathToXSL, File pathToXML) {
           xml=pathToXML;
           xsl=pathToXSL;

    }
     public XsltReport(String pathToXSL, String pathToTableXSL, File pathToXML) {
           xml=pathToXML;
           xsl=pathToXSL;
           tableXSL = pathToTableXSL;
    }
    public OutputStream myTransformer () throws TransformerException, TransformerConfigurationException, IOException {
//          System.setProperty("javax.xml.transform.TransformerFactory","net.sf.saxon.TransformerFactoryImpl");

        // Create a transform factory instance.
        TransformerFactory tfactory = TransformerFactory.newInstance();

         // Create a transformer for the stylesheet.
           Transformer transformer = tfactory.newTransformer(new StreamSource(new File(this.xsl)));

        // Transform the source XML to System.out.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        Result result = new StreamResult(baos);
//         String result;
    StreamResult    result = new StreamResult(baos);
//           transformer.transform(new StreamSource(new URL(this.xml).openStream()),new StreamResult(result));
        transformer.transform(new StreamSource(new FileInputStream(this.xml)),result);
        if (tableXSL!=null){
            ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
            StreamResult    result1 = new StreamResult(baos1);
            Transformer transformer1 = tfactory.newTransformer(new StreamSource(new File(this.tableXSL)));
            StringReader test = new StringReader(result.getOutputStream().toString());
            transformer1.transform(new StreamSource(test),result1);
            return result1.getOutputStream();
        }
//        System.out.println(result.toString());
        return result.getOutputStream();

    }
    public static void handleException(Exception ex) {

        System.out.println("EXCEPTION: " + ex);
        ex.printStackTrace();
    }
    public static void main(String args[]) throws MalformedURLException {

        // set the TransformFactory to use the Saxon TransformerFactoryImpl method
//         System.setProperty("javax.xml.transform.TransformerFactory",
//         "net.sf.saxon.TransformerFactoryImpl");
          XsltReport testReport = new XsltReport("rightclick/conf/xslt/table_creator.xslt", new File("device-data-R111.xml"));
         try {
                 System.out.print(testReport.myTransformer().toString());
         } catch (Exception ex) {
              handleException(ex);
         }

    }
}
