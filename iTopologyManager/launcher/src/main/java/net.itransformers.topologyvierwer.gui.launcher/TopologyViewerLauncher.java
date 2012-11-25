/*
 * iTransformer is an open source tool able to discover and transform
 *  IP network infrastructures.
 *  Copyright (C) 2012  http://itransformers.net
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.itransformers.topologyvierwer.gui.launcher;/*
 * iTransformer is an open source tool able to discover IP networks
 * and to perform dynamic data data population into a xml based inventory system.
 * Copyright (C) 2010  http://itransformers.net
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import edu.uci.ics.jung.graph.*;
import net.itransformers.topologyviewer.gui.TopologyViewer;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class TopologyViewerLauncher {
    public static void main(String[] args) throws Exception {
        Map<String, String > params = new HashMap<String, String>();
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
        URL baseUrl = null;
        if (dirStr != null) {
            File dir = new File(dirStr);
            if (!dir.exists()) {
                System.out.println(String.format("The specified directory '%s' does not exists",dirStr));
                return;
            }
            baseUrl = dir.toURI().toURL();
        } else if (urlStr != null) {
            baseUrl = new URL(urlStr);
        } else {
            baseUrl = new File(".").toURI().toURL();
        }
        String viewerConfig = params.get("-f");
        URL viewerConfigFile = null;
        if (viewerConfig != null) {
            String fName = new File(viewerConfig).toURI().toString();
            viewerConfigFile = new URL(fName);
        }
        if (params.get("-t").equals("undirected")) {
            new TopologyViewer<UndirectedGraph<String, String>>(baseUrl, graphmlRelDir, UndirectedSparseGraph.<String, String>getFactory(), viewerConfigFile, initialNode);
        } else if(params.get("-t").equals("multi")){
            new TopologyViewer<UndirectedGraph<String, String>>(baseUrl, graphmlRelDir, UndirectedSparseMultigraph.<String, String>getFactory(), viewerConfigFile,initialNode);
        }else if (params.get("-t").equals("directed")) {
            new TopologyViewer<DirectedGraph<String, String>>(baseUrl, graphmlRelDir, DirectedSparseGraph.<String, String>getFactory(), viewerConfigFile,initialNode);
        }else{

        }
    }

    private static void printUsage(String msg) {
        System.out.println("Error: "+msg);
        System.out.println("Usage:   topoManager.bat [-t <directed|undirected>] -d <local_dir> -u <remote_url> -g <graphml_dir> -f <viewer_config]");
        System.out.println(
                "-f <viewer-config>          # viewer configuration file\n" +
                        "-d <local_dir>              # relative or absolute path to local dir with 'graphml-dir' and 'device-data' dirs.\n" +
                        "-g <graphml_dir>            # relative path to the 'graphml-dir' (relateive to the 'local_dir' or 'remote_url').\n" +
                        "-u <remote_url>             # URL to site with 'graphml-dir' and 'device-data' dirs.\n" +
                        "-t <directed|undirected>    # graph type - directed or undirected\n"+
                        "-n <Initial Node>           # Initial Node");
    }
}
