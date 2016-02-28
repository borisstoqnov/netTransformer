/*
 * GraphMLDiffTestCase.java
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
        File file1 = new File("test1234/network/bg.version.20021231/undirected/network.graphml");
        File file2 = new File("/test1234/network/bg.version.20031231/undirected/network.graphml");
        File outFile = new File("/test1234/network/vasko2");
        File xsltFile = new File("iTopologyManager/topologyViewer/conf/xslt/graphml_diff.xslt");
        File nodeIgnores = new File("iTopologyManager/topologyViewer/conf/xml/bgpPeeringMap/ignored_node_keys.xml");
        File edgeIgnores = new File("iTopologyManager/topologyViewer/conf/xml/bgpPeeringMap/ignored_edge_keys.xml");
        GraphMLDiff graphmDiffer = new GraphMLDiff(file1.toURI(),file2.toURI(),outFile,xsltFile,nodeIgnores,edgeIgnores);
        graphmDiffer.createDiffGraphml();

    }
}
