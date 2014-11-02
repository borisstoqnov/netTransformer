package net.itransformers.topologyviewer.diff;

import org.junit.Test;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: niau
 * Date: 10/29/14
 * Time: 3:47 PM
 * To change this template use File | Settings | File Templates.
 */

public class GraphMLDiffTestCase {
    @Test
    public void testDiff(){
        GraphMLFileDiffTool tool = new GraphMLFileDiffTool(new File(path1), new File(path2), new File(path3UndirectedNetwork), getNodeIgnoredKeysFile(), getEdgeIgnoredKeysFile(), xsltTransformator);
        tool.
    }
}
