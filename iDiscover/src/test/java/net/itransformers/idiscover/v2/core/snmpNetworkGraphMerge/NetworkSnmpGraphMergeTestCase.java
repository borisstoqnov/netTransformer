package net.itransformers.idiscover.v2.core.snmpNetworkGraphMerge;

import net.itransformers.utils.graphmlmerge.GrahmlMerge;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashMap;
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

         outputFile = new File(baseDir + File.separator +"iDiscover/src/test/resources/lab-results/demo/network/version1/generated/network-generated.graphml");

         inputVersionDir = new File(baseDir + File.separator+"iDiscover/src/test/resources/lab-results/demo/network/version1/graphml-undirected");



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



        edgesTypes = new HashMap<String, String>();
        edgesTypes.put("name","string");
//            edgesTypes.put("IPv4Forwarding","string");
//            edgesTypes.put("IPv6Forwarding","string");
        edgesTypes.put("Interface","string");
        edgesTypes.put("Discovery Method","string");
        edgesTypes.put("Neighbor IP Address","string");
        edgesTypes.put("Neighbor Device Type","string");
        edgesTypes.put("diff","string");
        edgesTypes.put("diffs","string");


        vertexTypes = new HashMap<String, String>();
        vertexTypes.put("deviceModel","string");
        vertexTypes.put("deviceType","string");
        vertexTypes.put("nodeInfo","string");
        vertexTypes.put("hostname","string");
        vertexTypes.put("deviceStatus","string");
        vertexTypes.put("discoveredIPv4Address","string");
        vertexTypes.put("geoCoordinates","string");
        vertexTypes.put("site","string");
        vertexTypes.put("diff","string");
        vertexTypes.put("diffs","string");
        vertexTypes.put("diffs","string");
        vertexTypes.put("ipv6Forwarding","string");
        vertexTypes.put("ipv4Forwarding","string");
        vertexTypes.put("subnetPrefix","string");
        vertexTypes.put("totalInterfaceCount","string");
        vertexTypes.put("ipProtocolType","string");
        vertexTypes.put("subnetRangeType","string");


    }
    @Test
    public void loadFiles(){

       Assert.assertEquals(7,inputFiles.length);
    }
    @Test
    public void merge(){
        try {
            new GrahmlMerge().merge(inputFiles, outputFile,vertexTypes, edgesTypes, "undirected");
        } catch (IOException e) {
            e.printStackTrace();
        }

        File expected = new File(baseDir+File.separator+"iDiscover/src/test/resources/lab-results/demo/network/version1/expected/network-expected.graphml");
        try {
            Assert.assertEquals(FileUtils.readFileToString(expected,"utf-8"),FileUtils.readFileToString(outputFile,"utf-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
