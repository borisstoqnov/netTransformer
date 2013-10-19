/*
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

package net.itransformers.topologyviewer.rightclick.impl;

import net.itransformers.topologyviewer.gui.TopologyManagerFrame;
import net.itransformers.topologyviewer.rightclick.RightClickHandler;
import net.itransformers.utils.JEditorPane;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class XmlFileEditorHandler implements RightClickHandler {
     public <G> void handleRightClick(JFrame parent, String v,
                                     Map<String, String> graphMLParams,
                                     Map<String, String> rightClickParams,
                                     File projectPath,
                                     File deviceDataXmlFileName) throws Exception {

//      JOptionPane.showMessageDialog(parent, "deviceDataXmlFileName: " + deviceDataXmlFileName );

        // JEditorPane settingsEditor = new JEditorPane(deviceDataXmlFileName.getAbsolutePath());
         TopologyManagerFrame frame = (TopologyManagerFrame) parent;
         JEditorPane graphmlEditor = null;

         if(rightClickParams.get("type").equals("deviceXml")){
             graphmlEditor = new net.itransformers.utils.JEditorPane(deviceDataXmlFileName.getAbsolutePath(),deviceDataXmlFileName.getParent(),".xml");
         }else {
             String graphmlPath = frame.getCurrentGraphViewerManager().getVersionDir().getAbsolutePath()+File.separator+frame.getCurrentGraphViewerManager().getGraphType().toString().toLowerCase()+File.separator+"node-"+v+".graphml";
             String graphmlDir =  frame.getCurrentGraphViewerManager().getVersionDir().getAbsolutePath()+File.separator+frame.getCurrentGraphViewerManager().getGraphType().toString().toLowerCase();
             graphmlEditor = new JEditorPane(graphmlPath,graphmlDir,".graphml");

         }
         try {
             graphmlEditor.init();
         } catch (IOException e1) {
             e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
         } catch (BadLocationException e1) {
             e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
         }

     }
}
