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
import org.springframework.context.support.GenericXmlApplicationContext;

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

        String dirStr = params.get("-d");
        String urlStr = params.get("-u");
        if (dirStr != null && urlStr != null) {
            printUsage("Can not specify -d or -u options simultaneously");
            return;
        }
        if (dirStr != null) {
            File dir = new File(dirStr);
            if (!dir.exists()) {
                System.out.println(String.format("The specified directory '%s' does not exists", dirStr));
                return;
            }
        }
        GenericXmlApplicationContext ctx = new GenericXmlApplicationContext();
        ctx.load("classpath:rightClick/rightClick.xml");
        ctx.load("classpath:rightClickAPI/rightClickAPI.xml");
        ctx.load("classpath:xmlResourceManager/xmlResourceManagerFactory.xml");
        ctx.load("classpath:csvConnectionDetails/csvConnectionDetailsFactory.xml");
        ctx.load("classpath:topologyViewer/topologyViewer.xml");
        ctx.refresh();
        TopologyManagerFrame frame = (TopologyManagerFrame) ctx.getBean("topologyManagerFrame");
        frame.init();
    }

    private static void printUsage(String msg) {
        System.out.println("Error: "+msg);
        System.out.println("Usage:   topoManager.bat [-t <directed|undirected>] -d <local_dir> -u <remote_url> -g <graphml_dir> -f <viewer_config]");
        System.out.println(
                        "-d <local_dir>              # relative or absolute path to local dir with 'graphml-dir' and 'device-data' dirs.\n"
        );
    }
}
