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
//    private final JTextField diffConfigPathTextField;
//    private final JButton openConfigButton;
//    private final JButton ignoredKeysButton;
    private Properties preferences;
    private Result result = Result.CANCELED;
    private ProgressMonitor progressMonitor;
    private GraphMLDiffTool task;

    public DiffWizardDialog(final JFrame owner, final Properties preferences) throws MalformedURLException {
        super(owner,"Diff Dialog",true);
        this.preferences = preferences;
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

        String diffPath1 = (String) preferences.get(PreferencesKeys.DIFF_PATH1.name());
        if (diffPath1 == null){
            diffPath1 = (String) preferences.get(PreferencesKeys.PATH.name());
        }
        JPanel row1 = new JPanel(new BorderLayout(10,10));
        row1.add(new JLabel("   Graph1"),BorderLayout.WEST);
        diffPathTextField1 = new JTextField(diffPath1);
        row1.add(diffPathTextField1,BorderLayout.CENTER);
        openButton1 = new JButton("Open");
        row1.add(openButton1,BorderLayout.EAST);
        centralPanel.add(row1);
        openButton1.addActionListener(new FileSelector(owner,diffPathTextField1,PreferencesKeys.DIFF_PATH1.name()));

        String diffPath2 = (String) preferences.get(PreferencesKeys.DIFF_PATH2.name());
        JPanel row2 = new JPanel(new BorderLayout(10,10));
        row2.add(new JLabel("   Graph2"),BorderLayout.WEST);
        diffPathTextField2 = new JTextField(diffPath2);
        row2.add(diffPathTextField2,BorderLayout.CENTER);
        openButton2 = new JButton("Open");
        row2.add(openButton2, BorderLayout.EAST);
        centralPanel.add(row2);
        openButton2.addActionListener(new FileSelector(owner, diffPathTextField2, PreferencesKeys.DIFF_PATH2.name()));
        diffPathTextField2.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                System.out.println("INSERT UPDATE");
                final String path1 = diffPathTextField1.getText();
                final String path2 = diffPathTextField2.getText();
//                final String ignoredKeysFile = ignoredKeysTextField.getText();
                File file1 = new File(path1);
                File file2 = new File(path2);
                String graphType = null;
                if (path1.endsWith("undirected") && path1.endsWith("undirected")){
                    graphType = "undirected";
                } else if (path1.endsWith("directed") && path1.endsWith("directed")){
                    graphType = "directed";
                }
                if (graphType != null) {
                    diffPathTextField3.setText(
                        new File(new File(new File(file1.getParent()).getParent(),
                            new File(file1.getParent()).getName() + "-" + new File(file2.getParent()).getName()),"diff-"+graphType).getAbsolutePath());
                }
                preferences.setProperty(PreferencesKeys.DIFF_PATH3.name(),diffPathTextField3.getText());
                try {
                    preferences.store(new FileOutputStream(TopologyManagerFrame.VIEWER_PREFERENCES_PROPERTIES), "");
                } catch (IOException e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(owner, "Can not Store preferences: " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }

            }

            public void removeUpdate(DocumentEvent e) {
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });

        String diffPath3 = (String) preferences.get(PreferencesKeys.DIFF_PATH3.name());
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
//        openButton3.addActionListener(new FileSelector(owner, diffPathTextField3, PreferencesKeys.DIFF_PATH3.name()));

//        String diffConfigPath = (String) preferences.get(PreferencesKeys.DIFF_CONFIG.name());
//        JPanel row4 = new JPanel(new BorderLayout(10, 10));
//        row4.add(new JLabel("config"),BorderLayout.WEST);
//        diffConfigPathTextField = new JTextField(diffConfigPath);
//        row4.add(diffConfigPathTextField,BorderLayout.CENTER);
//        openConfigButton = new JButton("Open");
//        row4.add(openConfigButton,BorderLayout.EAST);
//        centralPanel.add(row4);
//        openConfigButton.addActionListener(new FileSelector(owner, diffConfigPathTextField, PreferencesKeys.DIFF_CONFIG.name(),false));

//        String ignoredKeysPath = (String) preferences.get(PreferencesKeys.IGNORED_KEYS_PATH.name());

//        JPanel row5 = new JPanel(new BorderLayout(10, 10));
//        row5.add(new JLabel("ignore"),BorderLayout.WEST);
//        ignoredKeysTextField = new JTextField(ignoredKeysPath);
//        row5.add(ignoredKeysTextField,BorderLayout.CENTER);
//        ignoredKeysButton = new JButton("Open");
//        row5.add(ignoredKeysButton,BorderLayout.EAST);
//        centralPanel.add(row5);
//        ignoredKeysButton.addActionListener(new FileSelector(owner, ignoredKeysTextField, PreferencesKeys.IGNORED_KEYS_PATH.name(),false));

        cp.add(centralPanel,BorderLayout.NORTH);
        setPreferredSize(new Dimension(550, 200));
        this.pack();
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
//    public String getDiffConfigPath(){
//        return diffConfigPathTextField.getText();
//    }
    public String getIgnoredKeysPath(){
        return ignoredKeysTextField.getText();
    }

    public Result getResult() {
        return result;
    }

    private void doDiff() {
        progressMonitor = new ProgressMonitor(this,"Running diff tool","", 0, 1000);
        progressMonitor.setMillisToPopup(0);
        task = new GraphMLDiffTool(getDiffPath1(), getDiffPath2(), getDiffPath3(), getIgnoredKeysPath());
        task.addPropertyChangeListener(this);
        task.execute();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("progress".equals(evt.getPropertyName())) {

            int progress = (Integer) evt.getNewValue();
            progressMonitor.setProgress(progress);
//            String message = String.format("Completed %d%%.\n", progress);
//            progressMonitor.setNote(message);
//            taskOutput.append(message);
            if (progressMonitor.isCanceled() || task.isDone()) {
                if (progressMonitor.isCanceled()) {
                    task.cancel(true);
//                    taskOutput.append("Task canceled.\n");
                    result = Result.CANCELED;
                } else {
                    JOptionPane.showMessageDialog(this,"Diff completed","Diff tool info",JOptionPane.INFORMATION_MESSAGE);
//                    taskOutput.append("Task completed.\n");
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
        private String diffPathKey;
        private boolean selectDir;

        FileSelector(JFrame owner, JTextField diffPathTextField, String diffPathKey) {
            this.owner = owner;
            this.diffPathTextField = diffPathTextField;
            this.diffPathKey = diffPathKey;
            this.selectDir = true;
        }
        FileSelector(JFrame owner, JTextField diffPathTextField, String diffPathKey, boolean selectDir) {
            this.owner = owner;
            this.diffPathTextField = diffPathTextField;
            this.diffPathKey = diffPathKey;
            this.selectDir = selectDir;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser("");
            if (selectDir) {
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            } else {
                chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            }
            chooser.setMultiSelectionEnabled(false);
            int result = chooser.showOpenDialog(owner);
            if (result == JFileChooser.APPROVE_OPTION) {
                diffPathTextField.setText(chooser.getSelectedFile().getAbsolutePath());
                preferences.setProperty(diffPathKey,diffPathTextField.getText());
                try {
                    preferences.store(new FileOutputStream(TopologyManagerFrame.VIEWER_PREFERENCES_PROPERTIES), "");
                } catch (IOException e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(owner, "Can not Store preferences: " + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public static void main(String[] args) throws MalformedURLException {
        Properties props = new Properties();
        props.put(PreferencesKeys.PATH.name(),new File("C:\\Documents and Settings\\Administrator\\Desktop\\state2\\initial\\undirected").getAbsolutePath());
        DiffWizardDialog dialog = new DiffWizardDialog(null, props);
        dialog.setVisible(true);
        System.out.println(dialog.getDiffPath1());
        System.out.println(dialog.getDiffPath2());
        System.out.println(dialog.getDiffPath3());
        System.out.println(dialog.result);
//        System.exit(-1);
    }
}
