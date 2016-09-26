/*
 * JEditorPane.java
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


import net.itransformers.utils.XmlTextEditor.XmlEditorPane;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class JEditorPane implements ActionListener {
    JFrame myFrame = null;
    XmlEditorPane myPane = null;
    String fileName = null;
    String fileExtension = null;
    String dir = null;

    public JEditorPane(String s, String dir, String fileExtension) {
        this.fileName = s;
        this.dir = dir;
        this.fileExtension = fileExtension;
    }

    public static void main(String[] a) throws IOException, BadLocationException {
        (new JEditorPane("iDiscover/resourceManager/conf/xml/resource.xml", "resourceManager/conf/xml", "xml")).init();
    }

    public void init() throws IOException, BadLocationException {
        myFrame = new JFrame("netTransformer configuration editor");
        //myFrame.setDefaultCloseOperation(JFrame.);
        myFrame.setSize(640, 480);
        if (new File(fileName).exists()) {

            myPane = new XmlEditorPane(fileName);
            JScrollPane editorScrollPane = new JScrollPane(myPane);
            editorScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            editorScrollPane.setPreferredSize(new Dimension(250, 145));
            editorScrollPane.setMinimumSize(new Dimension(10, 10));

            myPane.setContentType("text/xml");

            FileReader in = new FileReader(fileName);
            myPane.getEditorKit().read(in, myPane.getDocument(), 0);

            in.close();
            myFrame.setContentPane(editorScrollPane);

            JMenuBar myBar = new JMenuBar();
            JMenu myMenu = getFileMenu();
            myBar.add(myMenu);
            myFrame.setJMenuBar(myBar);


            myFrame.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(myFrame, "File does not exist! Please generate it first!");

            myFrame.dispose();
        }
    }

    private JMenu getFileMenu() {
        JMenu myMenu = new JMenu("File");
        JMenuItem myItem = new JMenuItem("New");
        myItem.addActionListener(this);
        myMenu.add(myItem);
        myItem = new JMenuItem("Open");
        myItem.addActionListener(this);
        myMenu.add(myItem);

        myItem = new JMenuItem("Close");
        myItem.addActionListener(this);
        myMenu.add(myItem);

        myItem = new JMenuItem("Save");
        myItem.addActionListener(this);
        myMenu.add(myItem);
        myItem = new JMenuItem("Save As");
        myItem.addActionListener(this);
        myMenu.add(myItem);

        myItem = new JMenuItem("Save and Exit");
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
            if (cmd.equals("New")) {
                fileName = null;
                myPane.setText("");

            } else if (cmd.equals("Close")) {
                fileName = null;
                myPane.setText("");

            } else if (cmd.equals("Open")) {

                JFileChooser chooser = new JFileChooser(dir);
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                chooser.setFileFilter(new FileFilter() {
                    @Override
                    public boolean accept(File f) {
                        return (f.isFile() && f.getName().endsWith(fileExtension) || f.isDirectory());
                    }

                    @Override
                    public String getDescription() {
                        return "(iTransformer Files) *." + fileExtension;
                    }
                });

                chooser.setMultiSelectionEnabled(false);
                int result = chooser.showOpenDialog(myFrame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    myPane.setText("");
                    fileName = chooser.getSelectedFile().getAbsolutePath();
                    FileReader in = new FileReader(fileName);
                    myPane.getEditorKit().read(in, myPane.getDocument(), 0);

                    in.close();

                }
            } else if (cmd.equals("Save")) {
                if (fileName != null) {
                    SaveFile();
                } else {
                    SaveFileAs();
                }
            } else if (cmd.equals("Save As")) {
                SaveFileAs();
            } else if (cmd.equals("Save and Exit")) {
                if (fileName != null) {
                    SaveFile();
                    myFrame.dispose();

                } else {
                    SaveFileAs();
                    myFrame.dispose();

                }

            } else {
                if (fileName == null) {
                    SaveFileAs();
                    myFrame.dispose();
                } else {
                    myFrame.dispose();

                }
            }

        } catch (FileNotFoundException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (BadLocationException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    private void SaveFile() throws IOException {
        FileWriter out = new FileWriter(fileName);
        out.write(myPane.getText());
        out.close();

    }

    private void SaveFileAs() throws IOException {
        JFileChooser chooser = new JFileChooser(dir);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return (f.isFile() && f.getName().endsWith(fileExtension) || f.isDirectory());
            }

            @Override
            public String getDescription() {
                return "(iTransformer Files) *." + fileExtension;
            }
        });

        chooser.setMultiSelectionEnabled(false);
        int returnVal = chooser.showSaveDialog(myFrame);
        if (returnVal == chooser.APPROVE_OPTION) {
            File newFile = chooser.getSelectedFile();
            FileWriter out = new FileWriter(newFile);
            out.write(myPane.getText());
            out.close();
        }

    }
}