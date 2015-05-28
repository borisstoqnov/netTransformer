package net.itransformers.idiscover.v2.core.listeners;

import net.itransformers.idiscover.networkmodel.DiscoveredDeviceData;
import net.itransformers.idiscover.util.JaxbMarshalar;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import org.junit.Assert;
import org.parboiled.common.FileUtils;

import javax.xml.bind.JAXBException;
import java.io.*;

/**
 * Created by vasko on 1/29/2015.
 */
public class GraphmlFileLogGroovyDiscoveryListenerTestCase {
    //@Test
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

  //  @Test
    public void nodeDiscoverer() throws IOException, JAXBException {
        System.out.println("->>>>>>>"+new File(".").getAbsolutePath());
        File projectPath = createTempDirectory();
        GraphmlFileLogGroovyDiscoveryListener listener = new GraphmlFileLogGroovyDiscoveryListener();
        listener.setProjectPath(projectPath.getAbsolutePath());
        String labelDirName = "network";
        listener.setLabelDirName(labelDirName);
        String graphmlDirName = "undirected";
        listener.setGraphmDirName(graphmlDirName);
        InputStream groovyIs = new FileInputStream("iDiscover/conf/groovy/RawData2GraphmlTransformer.groovy");
        File groovyPath = new File(projectPath, "iDiscover/conf/groovy");
        groovyPath.mkdirs();
        FileUtils.copyAll(groovyIs, new FileOutputStream(new File(groovyPath, "RawData2GraphmlTransformer.groovy")));
        listener.setRawData2GraphmlGroovyTransformer("iDiscover/conf/groovy/RawData2GraphmlTransformer.groovy");
        NodeDiscoveryResult discoveryResult = new NodeDiscoveryResult();
        discoveryResult.setNodeId("123");
        InputStream is = this.getClass().getResourceAsStream("/bfogal54-peer.xml");
        DiscoveredDeviceData deviceData = JaxbMarshalar.unmarshal(DiscoveredDeviceData.class, is);
        discoveryResult.setDiscoveredData("deviceData",deviceData);
        listener.nodeDiscovered(discoveryResult);
        File labelDir = new File(projectPath, labelDirName);
        File graphmlDir = new File(labelDir, graphmlDirName);
        String[] generatedFiles = graphmlDir.list();
        Assert.assertEquals(generatedFiles.length, 1);


    }

    public static File createTempDirectory()  throws IOException
    {
        final File temp = File.createTempFile("temp", Long.toString(System.nanoTime()));
        if(!(temp.delete())) {
            throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
        }

        if(!(temp.mkdir())) {
            throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
        }

        return temp;
    }
}
