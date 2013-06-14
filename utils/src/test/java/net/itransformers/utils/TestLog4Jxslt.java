package net.itransformers.utils;/*
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

import net.sf.saxon.TransformerFactoryImpl;
import net.sf.saxon.event.Emitter;
import net.sf.saxon.event.MessageEmitter;
import net.sf.saxon.event.Receiver;
import net.sf.saxon.trans.XPathException;
import org.apache.log4j.Logger;
import org.junit.Test;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class TestLog4Jxslt {
    static Logger logger = Logger.getLogger(TestLog4Jxslt.class);
    @Test
    public void test() throws TransformerException, IOException, SAXException, ParserConfigurationException {

        XsltTransformer transformer = new XsltTransformer();
        System.out.println(transformer.getClass());
        logger.info("Starting...");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Map<String, String> settings = new HashMap<String, String>();
        File xsltFileName = new File(System.getProperty("base.dir"), "utils/src/test/java/test.xslt");
        InputStream inputStream = new FileInputStream(new File(System.getProperty("base.dir"), "utils/src/test/java/test.xml"));

        transformer.transformXML(inputStream, xsltFileName, outputStream, settings, null);
        logger.info("Transformation finished");

        System.out.println(new String(outputStream.toByteArray()));

    }

}
