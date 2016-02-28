/*
 * GraphMLFileDiffTool.java
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

import javax.swing.*;
import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: niau
 * Date: 1/25/14
 * Time: 7:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class GraphMLFileDiffTool extends SwingWorker<Void, Void> {
    private File graphmlAFile;
    private File graphmlBFile;
    private File graphmlCFile;
    private File ignoredNodeKeysFile;
    private File ignoredEdgeKeysFile;
    private File xsltTransformerFile;

    public GraphMLFileDiffTool(File graphmlAFile, File graphmlBFile, File graphmlCFile, File ignoredNodeKeysFile, File ignoredEdgeKeysFile, File xsltTransformerFile) {
        this.graphmlAFile = graphmlAFile;
        this.graphmlBFile = graphmlBFile;
        this.graphmlCFile = graphmlCFile;
        this.ignoredNodeKeysFile = ignoredNodeKeysFile;
        this.ignoredEdgeKeysFile = ignoredEdgeKeysFile;
        this.xsltTransformerFile = xsltTransformerFile;
    }


    @Override
    protected Void doInBackground() throws Exception {

        setProgress(1); // dummy to show progress window
        //"iTopologyManager/topologyViewer/conf/xslt/graphml_diff.xslt"
        GraphMLDiff graphmDiffer = new GraphMLDiff(graphmlAFile.toURI(),graphmlBFile.toURI(),graphmlCFile,xsltTransformerFile,ignoredNodeKeysFile,ignoredEdgeKeysFile);

        Thread.sleep(2000);
        graphmDiffer.createDiffGraphml();
        setProgress(100);
        if (isCancelled()) return null;
        return null;
    }

}
