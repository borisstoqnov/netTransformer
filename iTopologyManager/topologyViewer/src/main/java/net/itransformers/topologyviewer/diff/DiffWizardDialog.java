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

package net.itransformers.topologyviewer.diff;

import net.itransformers.topologyviewer.gui.PreferencesKeys;

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
import java.net.MalformedURLException;
import java.util.Properties;


public class DiffWizardDialog extends JDialog implements PropertyChangeListener {
    private File baseDir;
    private File networkDir;

    public enum Result {
        CANCELED,
        DONE
    }
    private JButton startButton;
    private JButton cancelButton;
    private JTextField diffPathTextField1;
    private JButton openButton1;
    private final JTextField diffPathTextField2;
    private File ignoredNodeKeysFile;
    private File ignoredEdgeKeysFile;
//    = new JTextField("iTopologyManager/topologyViewer/conf/xml/ignored_node_keys.xml");
//    private final String ignoredEdgeKeysTextField = new JTextField("iTopologyManager/topologyViewer/conf/xml/ignored_edge_keys.xml");

    private final JButton openButton2;
    private final JTextField diffPathTextField3;
    private final JButton openButton3;
    private Result result = Result.CANCELED;
    private ProgressMonitor progressMonitor;
    private GraphMLFileDiffTool task;

    public DiffWizardDialog(final JFrame owner, File baseDir) throws MalformedURLException {
        super(owner,"Diff Dialog",true);
        this.baseDir = baseDir;
        this.networkDir = new File(baseDir,"network");
        this.ignoredNodeKeysFile =  new File(baseDir,"iTopologyManager/topologyViewer/conf/xml/ignored_node_keys.xml");
        this.ignoredEdgeKeysFile = new File(baseDir,"iTopologyManager/topologyViewer/conf/xml/ignored_edge_keys.xml");
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

        String diffPath1 = networkDir.getAbsolutePath();
        JPanel row1 = new JPanel(new BorderLayout(10,10));
        row1.add(new JLabel("   Graph1"),BorderLayout.WEST);
        diffPathTextField1 = new JTextField(diffPath1);
        row1.add(diffPathTextField1,BorderLayout.CENTER);
        openButton1 = new JButton("Open");
        row1.add(openButton1,BorderLayout.EAST);
        centralPanel.add(row1);
        openButton1.addActionListener(new FileSelector(owner,diffPathTextField1));

        String diffPath2 = networkDir.getAbsolutePath();
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
                updateDiffPath1();
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
                updateDiffPath2();
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

    private void updateDiffPath1() {
        System.out.println("INSERT UPDATE");
        final String path1 = diffPathTextField1.getText();
        File file1 = new File(path1);


            String versionADir = new File(new File(file1.getParent()).getParent()).getName();
            String versionCdir = networkDir+File.separator+versionADir+"-";

            diffPathTextField3.setText(versionCdir);


    }

    private void updateDiffPath2() {
        System.out.println("INSERT UPDATE");
        final String path1 = diffPathTextField1.getText();
        final String path2 = diffPathTextField2.getText();
//                final String ignoredKeysFile = ignoredKeysTextField.getText();
        File file1 = new File(path1);
        File file2 = new File(path2);
        String graphType = null;

        if (file1.getName().contains("directed")){

            graphType = "directed";
        } else {
            graphType = "undirected";

        }
        if (graphType != null) {
            String versionADir = new File(new File(file1.getParent()).getParent()).getName();
            String versionCdir = networkDir+File.separator+versionADir+"-";
            if (file2!=null){
                String versionBDir = new File(new File(file2.getParent()).getParent()).getName();
                versionCdir = networkDir+File.separator+versionADir+"-"+versionBDir;
            }

            diffPathTextField3.setText(versionCdir);

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
    public File getNodeIgnoredKeysFile(){
        return ignoredNodeKeysFile;
    }
    public File getEdgeIgnoredKeysFile(){
        return ignoredEdgeKeysFile;
    }

    public Result getResult() {
        return result;
    }

    private void doDiff() {
        progressMonitor = new ProgressMonitor(this,"Running diff tool","", 0, 1000);
        progressMonitor.setMillisToPopup(0);
        new File(getDiffPath3()).mkdirs();
        String path1 = getDiffPath1();

        String path2 = getDiffPath2();
        String path3 = getDiffPath3();
        File path3File = new File (path3);
        File path3Undirected = new File (path3+File.separator+"device-centric");
        String path3UndirectedNetwork = path3Undirected.getAbsolutePath()+File.separator+ "network.graphml";

        if (!path3File.exists()){
            path3File.mkdir();

        }
        if (!path3Undirected.exists()){
            path3Undirected.mkdir();
        }
        if (!path3Undirected.exists()){
            path3Undirected.mkdir();
        }
        File xsltTransformator =  new File(baseDir, "iTopologyManager/topologyViewer/conf/xslt/graphml_diff.xslt");

        task = new GraphMLFileDiffTool(new File(path1),new File(path2),new File(path3UndirectedNetwork), getNodeIgnoredKeysFile(),getEdgeIgnoredKeysFile(),xsltTransformator);
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
        private  File undirectedDir;
        private File networkGraphml;

        FileSelector(JFrame owner, JTextField diffPathTextField) {
            this.owner = owner;
            this.diffPathTextField = diffPathTextField;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser(networkDir);
            chooser.setDialogTitle("Choose Graph version");

            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.setFileFilter(new FileFilter() {
                @Override
                public boolean accept(File f) {
                    if (f.exists() && f.isDirectory()){
                        undirectedDir = new File(f+File.separator+"device-centric");
                        networkGraphml = new File(undirectedDir+File.separator+ "network.graphml");
                        if (undirectedDir.exists() && networkGraphml.exists()){
                            return true;
                        }

                    }
                    return false;

//                    return  (f.isFile() && f.getName().endsWith(".graphml") || f.isDirectory());
                }

                @Override
                public String getDescription() {
                    return "(Network version folders)";
                }
            });
            chooser.setMultiSelectionEnabled(false);
            int result = chooser.showOpenDialog(owner);
            if (result == JFileChooser.APPROVE_OPTION) {
                diffPathTextField.setText(chooser.getSelectedFile().getAbsolutePath());
            }
            File fileResult = new File(chooser.getSelectedFile()+File.separator+"device-centric"+File.separator+ "network.graphml");
            if(fileResult.exists()){
                diffPathTextField.setText(fileResult.getAbsolutePath());
            }else {
                JOptionPane.showMessageDialog(owner, "You are trying to open a version that does not contain a network graph!");

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
