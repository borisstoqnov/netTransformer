/*
 * TestLog4Jxslt.java
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

import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class TestLog4Jxslt {
    static Logger logger = Logger.getLogger(TestLog4Jxslt.class);
    @Test
    public void test() throws TransformerException, IOException, SAXException, ParserConfigurationException, URISyntaxException {

        XsltTransformer transformer = new XsltTransformer();
        System.out.println(transformer.getClass());
        logger.info("Starting...");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Map<String, String> settings = new HashMap<String, String>();
        File xsltFileName = new File(getClass().getResource("/testLog4jxslt/test.xslt").toURI());
        InputStream inputStream = new FileInputStream(new File(getClass().getResource("/testLog4jxslt/test.xml").toURI()));

        transformer.transformXML(inputStream, xsltFileName, outputStream, settings);
        logger.info("Transformation finished");

        String output = new String(outputStream.toByteArray());
        Assert.assertNotNull(output);

    }

}
