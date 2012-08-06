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

package net.itransformers.idiscover.util;


import net.itransformers.utils.CmdLineParser;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

import net.itransformers.utils.XsltTransformer;

public class KMLTransformator {
    private static void printUsage(String param) {
        System.out.println("Usage:   java [-g <graphml file folder>] [-t <kml xslt transformator>] [-o <kml file output file>] ");
        System.out.println("Missing parameter: " + param);
    }

    public static void main(String[] args) throws IOException, JAXBException {
        Map<String, String> params = CmdLineParser.parseCmdLine(args);

        final String graphml = params.get("-g");
        if (graphml == null) {
            printUsage("graphml file folder");
            return;
        }
        final String xsltTransformator = params.get("-t");
        if (graphml == null) {
            printUsage("kml xslt transformator");
            return;
        }
        File transformator = new File(xsltTransformator);
        final String kmlOutputFile = params.get("-o");
        if (graphml == null) {
            printUsage("kml file output file");
            return;
        }
        ByteArrayInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;

//    fileInputStream = new FileInputStream(graphml);
        String dummyXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<root>\n</root>";
        fileInputStream = new ByteArrayInputStream(dummyXml.getBytes());
        fileOutputStream = new FileOutputStream(kmlOutputFile);
        XsltTransformer transformer = new XsltTransformer();
        HashMap<String, String> xsltParams;
        xsltParams = new HashMap<String, String>();
        params.put("url",graphml);
        try {
            transformer.transformXML(fileInputStream, transformator, fileOutputStream, xsltParams, null);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (SAXException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (TransformerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            if (fileInputStream != null) fileInputStream.close();
            if (fileOutputStream != null) fileOutputStream.close();
        }

    }
}
