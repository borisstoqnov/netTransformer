

/*
 * XmlEditor.java
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

package net.itransformers.utils.XmlTextEditor;

import javax.swing.*;
import java.awt.*;


 public class XmlEditor extends JFrame {

     private static final long serialVersionUID = 2623631186455160679L;

     public static void main(String[] args) {
         XmlEditor xmlEditor = new XmlEditor();
         xmlEditor.setVisible(true);
     }

     public XmlEditor() {

         super(" Text Editor Demo");
         setSize(800, 600);

         JPanel panel = new JPanel();
         panel.setLayout(new GridLayout());

         XmlEditorPane xmlEditorPane = new XmlEditorPane("File");
         panel.add(xmlEditorPane);

         add(panel);
     }
 }
