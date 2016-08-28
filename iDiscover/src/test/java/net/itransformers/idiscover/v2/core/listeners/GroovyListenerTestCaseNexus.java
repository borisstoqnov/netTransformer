/*
 * GroovyListenerTestCaseNexus.java
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

package net.itransformers.idiscover.v2.core.listeners;

import net.itransformers.idiscover.v2.core.listeners.node.GraphmlFileLogGroovyDiscoveryListener;
import org.junit.Assert;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: niau
 * Date: 2/2/15
 * Time: 4:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class GroovyListenerTestCaseNexus {
//    @Test
    public void testTransformRawData() throws IOException {
        GraphmlFileLogGroovyDiscoveryListener listener = new GraphmlFileLogGroovyDiscoveryListener();
        listener.setProjectPath(".");
        InputStream inn = this.getClass().getClassLoader().getResourceAsStream("/device-data-shit.xml");
        InputStream in = this.getClass().getResourceAsStream("/device-data-shit.xml");

        if (in != null) {
            StringReader reader = new StringReader(readInputStreamToString(in));
            StringWriter writer = new StringWriter();
            listener.transformRawDataToGraphml(reader, writer);

            InputStream expectedResultStream = this.getClass().getResourceAsStream("/graphmlMerge/expected.graphml");
            String expectedResult = readInputStreamToString(expectedResultStream);
            String actualResult = writer.toString();
            Assert.assertEquals(expectedResult, actualResult);
        }else {
            System.out.println("Can't load an resource");
        }
    }

    String readInputStreamToString(InputStream inputStream) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(inputStream);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[256];
        int len;
        while ((len = bis.read(buffer, 0, 256)) != -1){
            bos.write(buffer, 0, len);
        }
        return bos.toString().replace("\r","");// for tests under windows
    }
}

