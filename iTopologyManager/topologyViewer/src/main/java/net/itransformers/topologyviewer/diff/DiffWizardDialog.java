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

package net.itransformers.topologyviewer.diff;

import net.itransformers.topologyviewer.gui.PreferencesKeys;
import net.itransformers.topologyviewer.gui.TopologyManagerFrame;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * Date: 12-4-28
 * Time: 22:32
 * To change this template use File | Settings | File Templates.
 */
public class DiffWizardDialog extends JDialog implements PropertyChangeListener {
    private File baseDir;

    public enum Result {
        CANCELED,
        DONE
    }
    private JButton startButton;
    private JButton cancelButton;
    private JTextField diffPathTextField1;
    private JButton openButton1;
    private final JTextField diffPathTextField2;
    private final JTextField ignoredKeysTextField = new JTextField("iTopologyManager/topologyViewer/conf/xml/ignored-keys.xml");
    private final JButton openButton2;
    private final JTextField diffPathTextField3;
    private final JButton openButton3;
    private Result result = Result.CANCELED;
    private ProgressMonitor progressMonitor;
    private GraphMLDiffTool task;

    public DiffWizardDialog(final JFrame owner, File baseDir) throws MalformedURLException {
        super(owner,"Diff Dialog",true);
        this.baseDir = baseDir;
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        JPanel buttonsPanel = new JPanel();
        startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startButton.setEnabled(false);
                doDiff();
            }
        });
        startButton.setPreferredSize(new Dimension(80, 25));
        buttonsPanel.add(startButton);
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                result = Result.CANCELED;
                DiffWizardDialog.this.dispose();
            }
        });
        cancelButton.setPreferredSize(new Dimension(80, 25));
        buttonsPanel.add(cancelButton);
        cp.add(buttonsPanel,BorderLayout.SOUTH);
        JPanel centralPanel = new JPanel();
        centralPanel.setLayout(new BoxLayout(centralPanel,BoxLayout.Y_AXIS));

        String diffPath1 = baseDir.getAbsolutePath();
        JPanel row1 = new JPanel(new BorderLayout(10,10));
        row1.add(new JLabel("   Graph1"),BorderLayout.WEST);
        diffPathTextField1 = new JTextField(diffPath1);
        row1.add(diffPathTextField1,BorderLayout.CENTER);
        openButton1 = new JButton("Open");
        row1.add(openButton1,BorderLayout.EAST);
        centralPanel.add(row1);
        openButton1.addActionListener(new FileSelector(owner,diffPathTextField1));

        String diffPath2 = baseDir.getAbsolutePath();
        JPanel row2 = new JPanel(new BorderLayout(10,10));
        row2.add(new JLabel("   Graph2"),BorderLayout.WEST);
        diffPathTextField2 = new JTextField(diffPath2);
        row2.add(diffPathTextField2,BorderLayout.CENTER);
        openButton2 = new JButton("Open");
        row2.add(openButton2, BorderLayout.EAST);
        centralPanel.add(row2);
        openButton2.addActionListener(new FileSelector(owner, diffPathTextField2));
        diffPathTextField1.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateDiffPath();
            }

            public void removeUpdate(DocumentEvent e) {
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        diffPathTextField2.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateDiffPath();
            }

            public void removeUpdate(DocumentEvent e) {
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        String diffPath3 = "";
        JPanel row3 = new JPanel(new BorderLayout(10,10));
        JLabel labelGraph3 = new JLabel("   Result  ");
        labelGraph3.setBounds(40,0,100,20);
        row3.add(labelGraph3,BorderLayout.WEST);
        diffPathTextField3 = new JTextField(diffPath3);
        diffPathTextField3.setEditable(false);
        row3.add(diffPathTextField3,BorderLayout.CENTER);
        openButton3 = new JButton("Open");
        openButton3.setEnabled(false);
        row3.add(openButton3,BorderLayout.EAST);
        centralPanel.add(row3);

        cp.add(centralPanel,BorderLayout.NORTH);
        setPreferredSize(new Dimension(550, 200));
        this.pack();
    }

    private void updateDiffPath() {
        System.out.println("INSERT UPDATE");
        final String path1 = diffPathTextField1.getText();
        final String path2 = diffPathTextField2.getText();
//                final String ignoredKeysFile = ignoredKeysTextField.getText();
        File file1 = new File(path1);
        File file2 = new File(path2);
        String graphType = null;
        if (file1.getName().startsWith("undirected")){
            graphType = "undirected";
        } else if (file2.getName().startsWith("directed")){
            graphType = "directed";
        }
        if (graphType != null) {
            diffPathTextField3.setText(
                new File(new File(new File(file1.getParent()).getParent(),
                    new File(file1.getParent()).getName() + "-" +
                            new File(file2.getParent()).getName()),graphType).getAbsolutePath());
        }
    }

    public String getDiffPath1(){
        return diffPathTextField1.getText();
    }
    public String getDiffPath2(){
        return diffPathTextField2.getText();
    }
    public String getDiffPath3(){
        return diffPathTextField3.getText();
    }
    public String getIgnoredKeysPath(){
        return ignoredKeysTextField.getText();
    }

    public Result getResult() {
        return result;
    }

    private void doDiff() {
        progressMonitor = new ProgressMonitor(this,"Running diff tool","", 0, 1000);
        progressMonitor.setMillisToPopup(0);
        new File(getDiffPath3()).mkdirs();
        String path1 = getDiffPath1();
        File path1File = new File(path1);
        File diffPath1;
        if (path1File.getName().startsWith("directed")){
            diffPath1 = new File(path1File.getParent(),"directed");
        } else {
            diffPath1 = new File(path1File.getParent(),"undirected");
        }
        String path2 = getDiffPath2();
        File path2File = new File(path2);
        File diffPath2;
        if (path2File.getName().startsWith("directed")){
            diffPath2 = new File(path2File.getParent(),"directed");
        } else {
            diffPath2 = new File(path2File.getParent(),"undirected");
        }
        task = new GraphMLDiffTool(baseDir, diffPath1.getAbsolutePath(), diffPath2.getAbsolutePath(), getDiffPath3(), getIgnoredKeysPath());
        task.addPropertyChangeListener(this);
        task.execute();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {

            int progress = (Integer) evt.getNewValue();
            progressMonitor.setProgress(progress);
            if (progressMonitor.isCanceled() || task.isDone()) {
                if (progressMonitor.isCanceled()) {
                    task.cancel(true);
                    result = Result.CANCELED;
                } else {
                    JOptionPane.showMessageDialog(this,"Diff completed","Diff tool info",JOptionPane.INFORMATION_MESSAGE);
                    result = Result.DONE;
                    this.dispose();
                }
                startButton.setEnabled(true);
            }
        }
    }

    class FileSelector implements ActionListener{
        private JFrame owner;
        private JTextField diffPathTextField;

        FileSelector(JFrame owner, JTextField diffPathTextField) {
            this.owner = owner;
            this.diffPathTextField = diffPathTextField;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser(baseDir);
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            chooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    return  (f.isFile() && f.getName().endsWith(".graphmls") || f.isDirectory());
                }

                @Override
                public String getDescription() {
                    return "(List of graphml files) *.graphmls";
                }
            });
            chooser.setMultiSelectionEnabled(false);
            int result = chooser.showOpenDialog(owner);
            if (result == JFileChooser.APPROVE_OPTION) {
                diffPathTextField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
        }
    }

    public static void main(String[] args) throws MalformedURLException {
        Properties props = new Properties();
        props.put(PreferencesKeys.PATH.name(),new File("C:\\Documents and Settings\\Administrator\\Desktop\\state2\\initial\\undirected").getAbsolutePath());
        DiffWizardDialog dialog = new DiffWizardDialog(null, new File("."));
        dialog.setVisible(true);
        System.out.println(dialog.getDiffPath1());
        System.out.println(dialog.getDiffPath2());
        System.out.println(dialog.getDiffPath3());
        System.out.println(dialog.result);
//        System.exit(-1);
    }
}
