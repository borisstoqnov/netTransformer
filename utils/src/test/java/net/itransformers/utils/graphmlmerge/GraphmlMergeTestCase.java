package net.itransformers.utils.graphmlmerge;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: VasilYordanov
 * Date: 12/5/13
 * Time: 3:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class GraphmlMergeTestCase {
    @Test
    public void testMerge() throws IOException {
        File f1 = new File("utils/src/test/java/net/itransformers/utils/graphmlmerge/1.graphml");
        File f2 = new File("utils/src/test/java/net/itransformers/utils/graphmlmerge/2.graphml");
        File f3 = new File("utils/src/test/java/net/itransformers/utils/graphmlmerge/3.graphml");
        File f3Actual = File.createTempFile("graphml_merge",".xml");
        new GrahmlMerge().merge(new File[]{f1, f2}, f3Actual);
        String xml3 = FileUtils.readFileToString(f3);
        String xml3Actual = FileUtils.readFileToString(f3Actual);
        Assert.assertEquals(xml3, xml3Actual);
    }

}
