/*
 * TopologyViewerLauncher.java
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

package net.itransformers.topologyvierwer.gui.launcher;

import net.itransformers.topologyviewer.gui.TopologyManagerFrame;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TopologyViewerLauncher {
    public static void main(String[] args) throws Exception {

        Map<String, String> params = new HashMap<String, String>();
        String key = null;
        for (String arg : args) {
            if (key == null && arg.startsWith("-")) {
                key = arg;
            } else {
                params.put(key, arg);
                key = null;
            }
        }
        if (!params.containsKey("-t")) {
            params.put("-t", "directed");
        }
        String initialNode = params.get("-n");

        String graphmlRelDir = params.get("-g");
        String dirStr = params.get("-d");
        String urlStr = params.get("-u");
        if (dirStr != null && urlStr != null) {
            printUsage("Can not specify -d or -u options simultaneously");
            return;
        }
        File baseUrl = null;
        if (dirStr != null) {
            File dir = new File(dirStr);
            if (!dir.exists()) {
                System.out.println(String.format("The specified directory '%s' does not exists", dirStr));
                return;
            }
            baseUrl = dir;
        } else if (urlStr != null) {
            baseUrl = new File(urlStr);
        } else {
            baseUrl = null;
        }
        String viewerConfig = params.get("-f");
        File viewerConfigFile = null;
        if (viewerConfig != null) {
            viewerConfigFile = new File(viewerConfig);
        }
        new TopologyManagerFrame(null);
//        if (params.get("-t").equals("undirected")) {
//            new TopologyManagerFrame(baseUrl, graphmlRelDir, UndirectedSparseGraph.<String, String>getFactory(), viewerConfigFile, initialNode);
//        } else if(params.get("-t").equals("multi")){
//            new TopologyManagerFrame<UndirectedGraph<String, String>>(baseUrl, graphmlRelDir, UndirectedSparseMultigraph.<String, String>getFactory(), viewerConfigFile,initialNode);
//        }else if (params.get("-t").equals("directed")) {
//            new TopologyManagerFrame<DirectedGraph<String, String>>(baseUrl, graphmlRelDir, DirectedSparseGraph.<String, String>getFactory(), viewerConfigFile,initialNode);
//        }else{
//
//        }
    }

    private static void printUsage(String msg) {
        System.out.println("Error: "+msg);
        System.out.println("Usage:   topoManager.bat [-t <directed|undirected>] -d <local_dir> -u <remote_url> -g <graphml_dir> -f <viewer_config]");
        System.out.println(
                "-f <viewer-config>          # viewer configuration file\n" +
                        "-d <local_dir>              # relative or absolute path to local dir with 'graphml-dir' and 'device-data' dirs.\n"
//                        "-g <graphml_dir>            # relative path to the 'graphml-dir' (relateive to the 'local_dir' or 'remote_url').\n" +
//                        "-u <remote_url>             # URL to site with 'graphml-dir' and 'device-data' dirs.\n" +
//                        "-t <directed|undirected>    # graph type - directed or undirected\n"+
//                        "-n <Initial Node>           # Initial Node"
        );
    }
}
