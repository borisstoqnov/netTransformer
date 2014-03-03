package net.itransformers.utils.graphmlmerge;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;


public class GraphmlMergeTestCase {
    @Test
    public void testMerge() throws IOException {

        File f1 = new File("utils/src/test/java/net/itransformers/utils/graphmlmerge/1.graphml");
        File f2 = new File("utils/src/test/java/net/itransformers/utils/graphmlmerge/2.graphml");
        File f3 = new File("utils/src/test/java/net/itransformers/utils/graphmlmerge/3.graphml");
        File f3Actual = File.createTempFile("graphml_merge",".xml");
        Map<String, String> edgesTypes  = null;
        Map<String, String> vertexTypes = null;
        new GrahmlMerge().merge(new File[]{f1, f2}, f3Actual, vertexTypes, edgesTypes, "undirected");
        String xml3 = FileUtils.readFileToString(f3);
        String xml3Actual = FileUtils.readFileToString(f3Actual);
        Assert.assertEquals(xml3, xml3Actual);
    }
    @Test
    public void testMerge2() throws IOException {
        File f1 = new File("utils/src/test/java/net/itransformers/utils/graphmlmerge/1.graphml");
        File f2 = new File("utils/src/test/java/net/itransformers/utils/graphmlmerge/2.graphml");
        File f3 = new File("utils/src/test/java/net/itransformers/utils/graphmlmerge/4.graphml");
        File f3Actual = File.createTempFile("graphml_merge",".xml");

        Map<String, MergeConflictResolver> vertexConflictResolver = new HashMap<String, MergeConflictResolver>();
        vertexConflictResolver.put("name", new MergeConflictResolver(){
            @Override
            public Object resolveConflict(Object srcValue, Object targetValue) {
                return srcValue + "-" + targetValue;
            }
        });
        Map<String, MergeConflictResolver> edgeConflictResolver = new HashMap<String, MergeConflictResolver>();
        edgeConflictResolver.put("weight", new MergeConflictResolver() {
            @Override
            public Object resolveConflict(Object srcValue1, Object targetValue) {
                if (srcValue1 instanceof Float && targetValue instanceof Float) {
                    return (Float)srcValue1 + (Float)targetValue;
                } else {
                    return srcValue1 + "#" + targetValue;
                }
            }
        });
        Map<String, String> edgesTypes = null;
        Map<String, String> vertexTypes = null;
        new GrahmlMerge(edgeConflictResolver, vertexConflictResolver).merge(new File[]{f1, f2}, f3Actual, vertexTypes, edgesTypes, "undirected");
        String xml3 = FileUtils.readFileToString(f3);
        String xml3Actual = FileUtils.readFileToString(f3Actual);
        Assert.assertEquals(xml3, xml3Actual);
    }

    @Test
    public void testMerge3() throws IOException {
        File f1 = new File("utils/src/test/java/net/itransformers/utils/graphmlmerge/version5/device-centric/node-R11.graphml");
        File f2 = new File("utils/src/test/java/net/itransformers/utils/graphmlmerge/version5/device-centric/node-R2.graphml");
        File f3 = new File("utils/src/test/java/net/itransformers/utils/graphmlmerge/version5/device-centric/expected.graphml");
        File f3Actual = File.createTempFile("graphml_merge",".xml");

        Map<String, MergeConflictResolver> vertexConflictResolver = new HashMap<String, MergeConflictResolver>();
        vertexConflictResolver.put("name", new MergeConflictResolver(){
            @Override
            public Object resolveConflict(Object srcValue, Object targetValue) {
                return srcValue + "-" + targetValue;
            }
        });
        Map<String, MergeConflictResolver> edgeConflictResolver = new HashMap<String, MergeConflictResolver>();
        edgeConflictResolver.put("weight", new MergeConflictResolver() {
            @Override
            public Object resolveConflict(Object srcValue1, Object targetValue) {
                if (srcValue1 instanceof Float && targetValue instanceof Float) {
                    return (Float)srcValue1 + (Float)targetValue;
                } else {
                    return srcValue1 + "#" + targetValue;
                }
            }
        });
        Map<String, String> edgesTypes = new HashMap<String, String>();
        edgesTypes.put("name","string");
        edgesTypes.put("method","string");
        edgesTypes.put("dataLink","string");
        edgesTypes.put("ipLink","string");
        edgesTypes.put("IPv4Forwarding","string");
        edgesTypes.put("IPv6Forwarding","string");
        edgesTypes.put("InterfaceNameA","string");
        edgesTypes.put("InterfaceNameB","string");
        edgesTypes.put("IPv4AddressA","string");
        edgesTypes.put("IPv4AddressB","string");
        edgesTypes.put("edgeTooltip","string");
        edgesTypes.put("diff","string");
        edgesTypes.put("diffs","string");

        Map<String, String> vertexTypes = new HashMap<String, String>();
        vertexTypes.put("deviceModel","string");
        vertexTypes.put("deviceType","string");
        vertexTypes.put("nodeInfo","string");
        vertexTypes.put("hostname","string");
        vertexTypes.put("deviceStatus","string");
        vertexTypes.put("ManagementIPAddress","string");
        vertexTypes.put("geoCoordinates","string");
        vertexTypes.put("site","string");
        vertexTypes.put("diff","string");
        vertexTypes.put("diffs","string");
        vertexTypes.put("diffs","string");
        vertexTypes.put("IPv6Forwarding","string");
        vertexTypes.put("IPv4Forwarding","string");
        new GrahmlMerge(edgeConflictResolver, vertexConflictResolver).merge(new File[]{f1, f2}, f3Actual, vertexTypes, edgesTypes, "undirected");
        String xml3 = FileUtils.readFileToString(f3);
        String xml3Actual = FileUtils.readFileToString(f3Actual);
        Assert.assertEquals(xml3, xml3Actual);
    }
    @Test
    public void testMerge4() throws IOException {

    File f1 = new File("utils/src/test/java/net/itransformers/utils/graphmlmerge/1.graphml");
    File f2 = new File("utils/src/test/java/net/itransformers/utils/graphmlmerge/2.graphml");
    File f3 = new File("utils/src/test/java/net/itransformers/utils/graphmlmerge/4.graphml");
    File f3Actual = File.createTempFile("graphml_merge",".xml");

    Map<String, MergeConflictResolver> edgeConflictResolver = new HashMap<String, MergeConflictResolver>();
        Map<String, MergeConflictResolver> vertexConflictResolver = new HashMap<String, MergeConflictResolver>();
        vertexConflictResolver.put("name", new MergeConflictResolver(){
            @Override
            public Object resolveConflict(Object srcValue, Object targetValue) {
                return srcValue + "-" + targetValue;
            }
        });

        edgeConflictResolver.put("method", new MergeConflictResolver(){
        @Override
        public Object resolveConflict(Object srcValue, Object targetValue) {
           // if (srcValue instanceof String && targetValue instanceof String) {
                String[] srcArray = ((String) srcValue).split(",");
                String[] targetArray = ((String) targetValue).split(",");


                String[] both = (String[]) ArrayUtils.addAll(srcArray, targetArray);
                Arrays.sort(both);
            LinkedHashSet<String> m = new LinkedHashSet<String>();
            Collections.addAll(m, both);

                return StringUtils.join(m, ',');

        }
    });

    Map<String, String> edgesTypes = null;
    Map<String, String> vertexTypes = null;
    new GrahmlMerge(edgeConflictResolver, vertexConflictResolver).merge(new File[]{f1, f2}, f3Actual, vertexTypes, edgesTypes, "undirected");
    String xml3 = FileUtils.readFileToString(f3);
    String xml3Actual = FileUtils.readFileToString(f3Actual);
    Assert.assertEquals(xml3, xml3Actual);
}

}
