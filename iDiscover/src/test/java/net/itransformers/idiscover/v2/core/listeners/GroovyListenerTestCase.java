package net.itransformers.idiscover.v2.core.listeners;

import org.junit.Assert;
import org.junit.Test;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: niau
 * Date: 2/2/15
 * Time: 4:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class GroovyListenerTestCase {
    @Test
    public void testTransformRawData() throws IOException {
        GraphmlFileLogGroovyDiscoveryListener listener = new GraphmlFileLogGroovyDiscoveryListener();
        InputStream in = this.getClass().getResourceAsStream("/bfogal54-peer.xml");

        StringReader reader = new StringReader(readInputStreamToString(in));
        StringWriter writer = new StringWriter();
        listener.transformRawDataToGraphml(reader, writer);

        InputStream expectedResultStream = this.getClass().getResourceAsStream("/expected.graphml");
        String expectedResult = readInputStreamToString(expectedResultStream);
        String actualResult = writer.toString();
        Assert.assertEquals(expectedResult, actualResult);
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
