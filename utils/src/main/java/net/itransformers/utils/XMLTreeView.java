/*
 * XMLTreeView.java
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

package net.itransformers.utils;

import com.sun.org.apache.xerces.internal.parsers.SAXParser;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.helpers.DefaultHandler;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.net.MalformedURLException;


public class XMLTreeView {
    private SAXTreeBuilder saxTree = null;
    private static String file = "";
    private static File url;


    public XMLTreeView(String deviceName, File url) throws MalformedURLException {
              JFrame frame = new JFrame("Object Tree Browser: [ " + deviceName + " ]");
              frame.setSize(400,400);
//              file = File;


                frame.getContentPane().setLayout(new BorderLayout());
                DefaultMutableTreeNode top = new DefaultMutableTreeNode(file);
//                DefaultMutableTreeNode top1 = new DefaultMutableTreeNode(url);

                saxTree = new SAXTreeBuilder(top);

                try {
                SAXParser saxParser = new SAXParser();
                saxParser.setContentHandler(saxTree);
//                saxParser.parse(new InputSource(new FileInputStream(file)));
                saxParser.parse(new InputSource(new FileInputStream(url)));
                }catch(Exception ex){
                   top.add(new DefaultMutableTreeNode(ex.getMessage()));
                }
                JTree tree = new JTree(saxTree.getTree());
                JScrollPane scrollPane = new JScrollPane(tree);

                frame.getContentPane().add("Center",scrollPane);
                frame.setVisible(true);

          }


  }
  class SAXTreeBuilder extends DefaultHandler{

         private DefaultMutableTreeNode currentNode = null;
         private DefaultMutableTreeNode previousNode = null;
         private DefaultMutableTreeNode rootNode = null;

         public SAXTreeBuilder(DefaultMutableTreeNode root){
                rootNode = root;
         }
         public void startDocument(){
                currentNode = rootNode;
         }
         public void endDocument(){
         }
         public void characters(char[] data,int start,int end){
                String str = new String(data,start,end);
                if (!str.equals("") && (Character.isLetter(str.charAt(0)) || Character.isDigit(str.charAt(0))))
//                if (!str.equals(""))
                    currentNode.add(new DefaultMutableTreeNode(str));
         }
         public void startElement(String uri,String qName,String lName,Attributes atts){
                previousNode = currentNode;
                currentNode = new DefaultMutableTreeNode(lName);
                // Add attributes as child nodes //
                attachAttributeList(currentNode,atts);
                previousNode.add(currentNode);
         }
         public void endElement(String uri,String qName,String lName){
                if (currentNode.getUserObject().equals(lName))
                    currentNode = (DefaultMutableTreeNode)currentNode.getParent();
         }
         public DefaultMutableTreeNode getTree(){
                return rootNode;
         }

         private void attachAttributeList(DefaultMutableTreeNode node,Attributes atts){
                 for (int i=0;i<atts.getLength();i++){
                      String name = atts.getLocalName(i);
                      String value = atts.getValue(name);
                      node.add(new DefaultMutableTreeNode(name + " = " + value));
                 }
         }

}
