package net.itransformers.topologyviewer.diff;

import net.itransformers.utils.XsltTransformer;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.net.URI;
import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: niau
 * Date: 1/25/14
 * Time: 8:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class GraphMLDiff {
    URI file1;
    URI file2;
    File OutputFile;
    File xsltFile;
    File ignoredNodeKeysPath;
    File ignoredEdgeKeysPath;

    public GraphMLDiff(URI file1, URI file2, File outputFile, File xsltFile, File ignoredNodeKeysPath, File ignoredEdgeKeysPath) {
        this.file1 = file1;
        this.file2 = file2;
        OutputFile = outputFile;
        this.xsltFile = xsltFile;
        this.ignoredNodeKeysPath = ignoredNodeKeysPath;
        this.ignoredEdgeKeysPath = ignoredEdgeKeysPath;
    }



    public void createDiffGraphml() throws FileNotFoundException {

  //  xsltFIle = new File(baseDir,"iTopologyManager/topologyViewer/conf/xslt/graphml_diff.xslt");
    ByteArrayInputStream fileInputStream = null;
    FileOutputStream fileOutputStream = null;
    try {
        System.out.println("test1");
        String dummyXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<root>\n</root>";
        System.out.println("test1'");
        fileInputStream = new ByteArrayInputStream(dummyXml.getBytes());
        System.out.println("test11"+OutputFile.getAbsolutePath());

        fileOutputStream = new FileOutputStream(OutputFile);
        System.out.println("test11'");
        XsltTransformer transformer = null;

        try {
            transformer = new XsltTransformer();
        } catch (Error e) {
            e.printStackTrace();
        }

//        System.out.println("test111");
        HashMap<String, String> xsltParams;
        xsltParams = new HashMap<String, String>();
        xsltParams.put("file1", file1.toString());
        xsltParams.put("file2", file2.toString());
        xsltParams.put("ignored_node_keys_file", ignoredNodeKeysPath.toURI().toString());
        xsltParams.put("ignored_edge_keys_file", ignoredEdgeKeysPath.toURI().toString());

//        System.out.println("test2");


        System.out.println(xsltParams.toString());
        transformer.transformXML(fileInputStream, xsltFile, fileOutputStream, xsltParams, null);
    } catch (ParserConfigurationException e) {
        e.printStackTrace();
    } catch (SAXException e) {
        e.printStackTrace();
    } catch (TransformerException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    } finally {
        try {
            fileInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}


}
