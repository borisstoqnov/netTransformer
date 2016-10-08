package net.itransformers.idiscover.v2.core.snmpNetworkGraphMerge;

import net.itransformers.idiscover.v2.core.SnmpGraphmlMetadataProvider;
import net.itransformers.utils.graphmlmerge.GrahmlMerge;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Map;

/**
 * Created by niau on 8/25/16.
 */
public class NetworkSnmpGraphMergeTestCase {
    File outputFile;
    File inputVersionDir;
    File[] inputFiles;
    Map<String, String> edgesTypes;
    Map<String, String> vertexTypes;
    private final String baseDir = (String) System.getProperties().get("user.dir");



    @Before
    public void setUp() throws Exception {

         outputFile = new File(baseDir + File.separator +"iDiscover/netDiscoverer/src/test/resources/lab-results/demo/network/version1/generated/network-generated.graphml");

         inputVersionDir = new File(baseDir + File.separator+"iDiscover/netDiscoverer/src/test/resources/lab-results/demo/network/version1/graphml-undirected");



        inputFiles = inputVersionDir.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".graphml");
            }
        });
        if(!outputFile.exists()){
            try {
                outputFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }



        SnmpGraphmlMetadataProvider snmpGraphmlMetadataProvider = new SnmpGraphmlMetadataProvider();

        edgesTypes =         snmpGraphmlMetadataProvider.getEdgesMetadataTypes();
        vertexTypes =         snmpGraphmlMetadataProvider.getVertexMetadataTypes();



    }
    @Test
    public void loadFiles(){

       Assert.assertEquals(7,inputFiles.length);
    }
//    @Test
    public void merge(){
        try {
            new GrahmlMerge().merge(inputFiles, outputFile,vertexTypes, edgesTypes, "undirected");
        } catch (IOException e) {
            e.printStackTrace();
        }

        File expected = new File(baseDir+File.separator+"iDiscover/netDiscoverer/src/test/resources/lab-results/demo/network/version1/expected/network-expected.graphml");
        try {
            Assert.assertEquals(FileUtils.readFileToString(expected,"utf-8"),FileUtils.readFileToString(outputFile,"utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
