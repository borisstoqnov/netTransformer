

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

         XmlEditorPane xmlEditorPane = new XmlEditorPane();
         panel.add(xmlEditorPane);

         add(panel);
     }
 }
