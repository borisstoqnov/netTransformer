package net.itransformers.utils.graphmledgedefaultresolver;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.net.URISyntaxException;

/**
 * Created by Vasil Yordanov on 9/14/2015.
 */
public class GraphmlEdgeDefaultResolverTestCase {
    private GraphmlEdgeDefaultResolver resolver;

    @Before
    public void setUp() throws ParserConfigurationException, SAXException {
        resolver = new GraphmlEdgeDefaultResolver();

    }
    @Test
    public void testResolveEdgeDefaultDirected() throws URISyntaxException {
        File f1 = new File(getClass().getResource("/graphmledgedefaultresolver/simple-directed.graphml").toURI());
        String edgeDefault = resolver.resolveEdgeDefault(f1);
        Assert.assertEquals("directed",edgeDefault);
    }
    @Test
    public void testResolveEdgeDefaultUndirected() throws URISyntaxException {
        File f1 = new File(getClass().getResource("/graphmledgedefaultresolver/simple-undirected.graphml").toURI());
        String edgeDefault = resolver.resolveEdgeDefault(f1);
        Assert.assertEquals("undirected",edgeDefault);
    }
    @Test
    public void testResolveEdgeDefaultNotSpecified() throws URISyntaxException {
        File f1 = new File(getClass().getResource("/graphmledgedefaultresolver/simple-not-defined-edgedefault.graphml").toURI());
        String edgeDefault = resolver.resolveEdgeDefault(f1);
        Assert.assertEquals(null,edgeDefault);
    }
}
