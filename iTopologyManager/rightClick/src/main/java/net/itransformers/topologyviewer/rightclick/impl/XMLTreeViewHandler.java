/*
 * netTransformer is an open source tool able to discover IP networks
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

package net.itransformers.topologyviewer.rightclick.impl;

import net.itransformers.topologyviewer.rightclick.RightClickHandler;
import net.itransformers.utils.XMLTreeView;

import javax.swing.*;
import java.io.File;
import java.util.Map;

public class XMLTreeViewHandler implements RightClickHandler {
     public <G> void handleRightClick(JFrame parent, String v,
                                     Map<String, String> graphMLParams,
                                     Map<String, String> rightClickParams,
                                     File projectPath,
                                     File versionDir) throws Exception {

//      JOptionPane.showMessageDialog(parent, "versionDir: " + versionDir );

         String path =  rightClickParams.get("path");

         if(rightClickParams.get("type").equals("deviceXml")){
             String deviceXmlPath = versionDir.getAbsolutePath()+File.separator+path+File.separator+"device-data-"+v+".xml";
             new XMLTreeView(v,new File(deviceXmlPath));
         }else if (rightClickParams.get("type").equals("graphml")) {
             String graphmlPath = versionDir+File.separator+path+File.separator+"node-"+v+".graphml";
             new XMLTreeView(v,new File(graphmlPath));

         } else {
             String rawDataPath = versionDir+File.separator+path+File.separator+v+".xml";
             new XMLTreeView(v,new File(rawDataPath));

         }
}
}
