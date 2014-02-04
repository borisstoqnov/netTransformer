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

import org.apache.log4j.Logger;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.MalformedURLException;


public class XsltReport {
 //   File xml = null; //input xml
    File xsl = null; //input xsl
    File tableXSL = null; //input xsl
    StreamSource inputXml;
    static Logger logger = Logger.getLogger(XsltReport.class);


    public XsltReport(File pathToXSL, File pathToXML) throws FileNotFoundException {
       // xml = pathToXML;
        inputXml = new StreamSource(new FileInputStream(pathToXML));
        xsl = pathToXSL;

    }
    public XsltReport(File pathToXSL, File pathToTableXSL, StreamSource inputXML) {
        inputXml = inputXML;
        xsl = pathToXSL;
        tableXSL = pathToTableXSL;

    }

    public XsltReport(File pathToTableXSL, StreamSource inputXML) {
        inputXml = inputXML;
        tableXSL = pathToTableXSL;

    }
    public XsltReport(File pathToXSL, StringBuffer inputXml1) throws FileNotFoundException {
        // xml = pathToXML;
        inputXml = new StreamSource(new ByteArrayInputStream(inputXml1.toString().getBytes()));
        xsl = pathToXSL;

    }

    public XsltReport(File pathToXSL, File pathToTableXSL, File pathToXML) throws FileNotFoundException {
        inputXml = new StreamSource(new FileInputStream(pathToXML));
        xsl = pathToXSL;
        tableXSL = pathToTableXSL;
    }

    public OutputStream doubleTransformer() throws TransformerException, TransformerConfigurationException, IOException {
//          System.setProperty("javax.xml.transform.TransformerFactory","net.sf.saxon.TransformerFactoryImpl");

        // Create a transform factory instance.
        TransformerFactory tfactory = TransformerFactory.newInstance();

        // Create a transformer for the stylesheet.
        Transformer transformer = tfactory.newTransformer(new StreamSource(this.xsl));

        // Transform the source XML to System.out.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        StreamResult result = new StreamResult(baos);

        transformer.transform(inputXml, result);
        if (tableXSL != null) {
            ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
            StreamResult result1 = new StreamResult(baos1);
            Transformer transformer1 = tfactory.newTransformer(new StreamSource(this.tableXSL));
            StringReader test = new StringReader(result.getOutputStream().toString());
            transformer1.transform(new StreamSource(test), result1);
            return result1.getOutputStream();
        }
        return result.getOutputStream();

    }
    public OutputStream singleTransformer() throws TransformerException, TransformerConfigurationException, IOException {
//          System.setProperty("javax.xml.transform.TransformerFactory","net.sf.saxon.TransformerFactoryImpl");

        // Create a transform factory instance.
        TransformerFactory tfactory = TransformerFactory.newInstance();

        // Create a transformer for the stylesheet.
//        Transformer transformer = tfactory.newTransformer(new StreamSource(this.xsl));
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        StreamResult result1 = new StreamResult(baos1);

        if (tableXSL != null) {
            Transformer transformer1 = tfactory.newTransformer(new StreamSource(this.tableXSL));
            transformer1.transform(inputXml, result1);
        }
            return result1.getOutputStream();


    }


    public static void handleException(Exception ex) {

        logger.debug("EXCEPTION in xslt Report generation: ") ;
        logger.debug(ex.getMessage());

    }

    public static void main(String args[]) throws MalformedURLException, FileNotFoundException {

        // set the TransformFactory to use the Saxon TransformerFactoryImpl method
//         System.setProperty("javax.xml.transform.TransformerFactory",
//         "net.sf.saxon.TransformerFactoryImpl");

        XsltReport testReport = new XsltReport(new File("rightclick/conf/xslt/table_creator.xslt"),
                new File("device-data-R111.xml"));
        try {
            System.out.print(testReport.doubleTransformer().toString());
        } catch (Exception ex) {
            handleException(ex);
        }

    }
}
