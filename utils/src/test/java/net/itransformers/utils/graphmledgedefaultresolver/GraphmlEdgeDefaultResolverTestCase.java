/*
 * GraphmlEdgeDefaultResolverTestCase.java
 *
 * This work is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * This work is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * Copyright (c) 2010-2016 iTransformers Labs. All rights reserved.
 */

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
