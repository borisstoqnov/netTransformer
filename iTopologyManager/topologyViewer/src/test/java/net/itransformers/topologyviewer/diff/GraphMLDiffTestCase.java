package net.itransformers.topologyviewer.diff;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created with IntelliJ IDEA.
 * User: niau
 * Date: 10/29/14
 * Time: 3:47 PM
 * To change this template use File | Settings | File Templates.
 */

public class GraphMLDiffTestCase {
    @Test
    public void testDiff() throws FileNotFoundException {
        File file1 = new File("/Users/niau/Projects/Projects/test1234/network/bg.version.20021231/undirected/network.graphml");
        File file2 = new File("/Users/niau/Projects/Projects/test1234/network/bg.version.20031231/undirected/network.graphml");
        File outFile = new File("/Users/niau/Projects/Projects/test1234/network/vasko2");
        File xsltFile = new File("iTopologyManager/topologyViewer/conf/xslt/graphml_diff.xslt");
        File nodeIgnores = new File("iTopologyManager/topologyViewer/conf/xml/bgpPeeringMap/ignored_node_keys.xml");
        File edgeIgnores = new File("iTopologyManager/topologyViewer/conf/xml/bgpPeeringMap/ignored_edge_keys.xml");
        GraphMLDiff graphmDiffer = new GraphMLDiff(file1.toURI(),file2.toURI(),outFile,xsltFile,nodeIgnores,edgeIgnores);
        graphmDiffer.createDiffGraphml();
    }
}
