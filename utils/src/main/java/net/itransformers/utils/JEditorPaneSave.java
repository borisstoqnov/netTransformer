package net.itransformers.utils;


import net.itransformers.utils.XmlTextEditor.XmlEditorPane;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JEditorPaneSave implements ActionListener {
   JFrame myFrame = null;
   XmlEditorPane myPane = null;
   String fileName = null;

    public JEditorPaneSave(String s) {
        this.fileName = s;
    }

    public static void main(String[] a) throws IOException, BadLocationException {
      (new JEditorPaneSave("/Users/niau/trunk/iDiscover/conf/xml/discoveryResource.xml")).init();
   }
   public void init() throws IOException, BadLocationException {
      myFrame = new JFrame("JEditorPane Save Test");
      myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      myFrame.setSize(640,480);

      myPane = new XmlEditorPane();
       JScrollPane editorScrollPane = new JScrollPane(myPane);
       editorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
       editorScrollPane.setPreferredSize(new Dimension(250, 145));
       editorScrollPane.setMinimumSize(new Dimension(10, 10));

       myPane.setContentType("text/xml");

       FileReader in = new FileReader(fileName);
       myPane.getEditorKit().read(in,myPane.getDocument(),0);

       in.close();
      myFrame.setContentPane(editorScrollPane);

      JMenuBar myBar = new JMenuBar();
      JMenu myMenu = getFileMenu();
      myBar.add(myMenu); 
      myFrame.setJMenuBar(myBar);


      myFrame.setVisible(true);
   }
   private JMenu getFileMenu() {
      JMenu myMenu = new JMenu("File");

      JMenuItem myItem = new JMenuItem("Save");
      myItem.addActionListener(this);
      myMenu.add(myItem);
       myItem = new JMenuItem("Exit");
       myItem.addActionListener(this);
       myMenu.add(myItem);

       return myMenu;
   }
   public void actionPerformed(ActionEvent e) {
      String cmd = ((AbstractButton) e.getSource()).getText();
      try {
         if (cmd.equals("Open")) {
            FileReader in = new FileReader(fileName);
            myPane.getEditorKit().read(in,myPane.getDocument(),0);
             in.close();


         } else if (cmd.equals("Save")) {
            FileWriter out = new FileWriter(fileName);
            out.write(myPane.getText());
            out.close();
         }  else{
             myFrame.dispose();
         }

         } catch (FileNotFoundException e1) {
          e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      } catch (BadLocationException e1) {
          e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      } catch (IOException e1) {
          e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
      }
   }
   }