/*
 * NewProjectDialog.java
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

package net.itransformers.topologyviewer.dialogs;

import net.itransformers.topologyviewer.gui.TopologyManagerFrame;
import net.itransformers.utils.ProjectConstants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class NewProjectDialog extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private JTextField projetNameTextField;
    private JTextField baseFilePathTextField;
    private boolean isOkPressed;
    private File projectDir;
    private String projectType;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            NewProjectDialog dialog = new NewProjectDialog(null);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public NewProjectDialog(final TopologyManagerFrame frame) {
        setModal(true);
        setTitle("Create New Project");
        //Setting the default projectType
        projectType = ProjectConstants.snmpProjectType;
        setBounds(100, 100, 564, 165);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);
        {
            JLabel label = new JLabel("Project Name:");
            label.setBounds(10, 14, 88, 14);
            contentPanel.add(label);
        }
        {
            projetNameTextField = new JTextField();
            projetNameTextField.setColumns(10);
            projetNameTextField.setBounds(108, 14, 277, 20);
            contentPanel.add(projetNameTextField);
        }
        {
            JLabel label = new JLabel("Project Base:");
            label.setBounds(10, 82, 98, 14);
            contentPanel.add(label);
        }

        {
            baseFilePathTextField = new JTextField();
            if (frame.getPath() == null) {
                File dir = new File(new File(System.getProperty("user.dir")).getParent()+File.separator+"Projects");
                if(!dir.exists()){
                    dir.mkdir();
                }
                baseFilePathTextField.setText(dir.getAbsolutePath());
            }
            baseFilePathTextField.setColumns(10);
            baseFilePathTextField.setBounds(108, 82, 277, 20);
            contentPanel.add(baseFilePathTextField);
        }
        {
            JLabel label = new JLabel("Project Type:");
            label.setBounds(10, 47, 98, 14);
            contentPanel.add(label);
        }
        {
      //      projectType = ProjectConstants.snmpProjectType;
            String[] projectNames = {ProjectConstants.snmpProjectName, ProjectConstants.mrtBgpDiscovererName, ProjectConstants.snmpBgpDiscovererName, ProjectConstants.freeGraphDiscovererName};
            final JComboBox comboBox = new JComboBox(projectNames);
            comboBox.setBounds(108, 47, 277, 22);
            //Set default project type
            comboBox.setSelectedItem(projectNames[0]);
            contentPanel.add(comboBox);
            comboBox.addActionListener(new ActionListener() {

                public void actionPerformed(ActionEvent arg0) {
                    JComboBox cb = (JComboBox) arg0.getSource();
                    String projectName = (String) cb.getSelectedItem();

                    projectType = ProjectConstants.getProjectType(projectName);
                }

            });
            }

            {
                JButton button = new JButton("Choose");
                button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        File dir = new File(".");
                        if (frame.getPath() != null) {
                            dir = new File(frame.getPath().toURI());
                        }
                        JFileChooser chooser = new JFileChooser(dir);
                        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                        chooser.setMultiSelectionEnabled(false);
                        int result = chooser.showOpenDialog(NewProjectDialog.this);
                        if (result == JFileChooser.APPROVE_OPTION) {
                            baseFilePathTextField.setText(chooser.getSelectedFile().getAbsolutePath());
                        }

                    }
                });
                button.setBounds(395, 82, 89, 23);
                contentPanel.add(button);
            }
            {
                JPanel buttonPane = new JPanel();
                buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
                getContentPane().add(buttonPane, BorderLayout.SOUTH);
                {
                    JButton okButton = new JButton("OK");
                    okButton.setActionCommand("OK");
                    okButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            onOK();
                        }
                    });
                    buttonPane.add(okButton);
                    getRootPane().setDefaultButton(okButton);
                }
                {
                    JButton cancelButton = new JButton("Cancel");
                    cancelButton.setActionCommand("Cancel");
                    cancelButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            projectDir = null;
                            isOkPressed = false;
                            setVisible(false);
                            dispose();
                        }
                    });
                    buttonPane.add(cancelButton);
                }
            }

        }

    private void onOK() {
        if (baseFilePathTextField.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "Can not create project. Parent directory is not specified");
            return;
        }
        File parentDir = new File(baseFilePathTextField.getText());
        if (parentDir.exists()) {
            File projectDir = new File(parentDir, projetNameTextField.getText());
            if (projectDir.exists()) {
                JOptionPane.showMessageDialog(this,
                        String.format("Can not create project '%s'. The project already exists in dir: %s", projectDir.getName(), parentDir.getAbsolutePath()));
                return;
            }
            if (!projectDir.mkdir()) {
                JOptionPane.showMessageDialog(this, "Can not create project. Unable to create directory: " + projectDir.getAbsolutePath());
                return;
            }
            this.projectDir = projectDir;
            isOkPressed = true;
            setVisible(false);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Can not create project. Parent directory does not exist: " + parentDir.getAbsolutePath());
            return;
        }
    }

    public File getProjectDir() {
        return projectDir;
    }
    public String getProjectType() {
        return projectType;
    }
    public boolean isOkPressed() {
        return isOkPressed;
    }
}
