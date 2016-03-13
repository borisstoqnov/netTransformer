/*
 * DiscoveryWizardDialog.java
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

package net.itransformers.topologyviewer.dialogs.discovery;

import net.itransformers.utils.ProjectConstants;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DiscoveryWizardDialog extends JDialog {

    private int option = JOptionPane.CLOSED_OPTION;
    private JPanel contentPanel = null;
    private JButton prevButton;
    private JButton nextButton;
    private Frame frame;
    private String projectPath;
    private String discoveryBeanName;


    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            UIManager.put("Table.gridColor", new ColorUIResource(Color.gray));

            DiscoveryWizardDialog dialog = new DiscoveryWizardDialog(null, ".", ProjectConstants.snmpProjectType);
            int option = dialog.showDialog();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public DiscoveryWizardDialog(Frame parentFrame, String projectPath, String projectType) {

        super(parentFrame, "Discovery Wizard", true);
        this.frame = parentFrame;
        this.projectPath = projectPath;
        //Discoverer Beans are equal to the ProjectTYpes
        this.discoveryBeanName = projectType;

        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 1000, 600);
        getContentPane().setLayout(new BorderLayout());


        init();

        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                prevButton = new JButton("BACK");
                prevButton.setActionCommand("BACK");
                buttonPane.add(prevButton);
                getRootPane().setDefaultButton(prevButton);
                prevButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        DiscoveryWizardDialog.this.prev();
                    }
                });
                prevButton.setEnabled(false);
            }
            {
                nextButton = new JButton("NEXT");
                nextButton.setActionCommand("NEXT");
                buttonPane.add(nextButton);
                nextButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        DiscoveryWizardDialog.this.next();
                    }
                });


            }
        }

    }

    private void init() {
        File file = new File(projectPath, "iDiscover/conf/txt/connection-details.txt");
        ConnectionDetailsPanel connectionDetailsPanel = new ConnectionDetailsPanel();
        try {
            connectionDetailsPanel.load(file);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(DiscoveryWizardDialog.this, "Error loading connection details file");

        }
        updateCurrentPanel(connectionDetailsPanel);
    }

    private void prev() {
        prevButton.setEnabled(true);
        nextButton.setEnabled(true);
        if (contentPanel instanceof DiscoveryResourcePanel) {
            File resourceFile = new File(projectPath, "resourceManager/conf/xml/resource.xml");
            try {
                ((DiscoveryResourcePanel) contentPanel).save(resourceFile);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(DiscoveryWizardDialog.this, "Error saving resources file");
            }
            File file = new File(projectPath, "iDiscover/conf/txt/connection-details.txt");
            ConnectionDetailsPanel panel = new ConnectionDetailsPanel();
            try {
                panel.load(file);
            } catch (IOException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(DiscoveryWizardDialog.this, "Error loading connection file");
            }
            updateCurrentPanel(panel);
            prevButton.setEnabled(false);

            nextButton.setText("NEXT");
            nextButton.setEnabled(true);
            nextButton.setActionCommand("NEXT");

        }

    }

    private void next() {
        prevButton.setEnabled(true);
        nextButton.setEnabled(true);
        if (contentPanel instanceof ConnectionDetailsPanel) {
            File connectionFile = new File(projectPath, "iDiscover/conf/txt/connection-details.txt");
            try {
                ((ConnectionDetailsPanel) contentPanel).save(connectionFile);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(DiscoveryWizardDialog.this, "Error saving connection details file");
            }

        }

        final DiscoveryResourcePanel panel = new DiscoveryResourcePanel();
        File resourceFile = new File(projectPath, "resourceManager/conf/xml/resource.xml");
        try {
            panel.load(resourceFile);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(DiscoveryWizardDialog.this, "Error loading resources details file");
        }

        updateCurrentPanel(panel);
        nextButton.setText("GO!");
        nextButton.setEnabled(true);
        nextButton.setActionCommand("GO");

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (nextButton.getActionCommand().equals("GO")) {
                    DiscoveryWizardDialog.this.go();
                }
            }
        });
    }


    private void go() {
        File resourceFile = new File(projectPath, "resourceManager/conf/xml/resource.xml");
        try {
            ((DiscoveryResourcePanel) contentPanel).save(resourceFile);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(DiscoveryWizardDialog.this, "Error saving resources file");
        }
        try {
            DiscoveryManagerDialogV2 discoveryManagerDialogV2 = new DiscoveryManagerDialogV2(DiscoveryWizardDialog.this.frame, new File(projectPath), discoveryBeanName);
            DiscoveryWizardDialog.this.setVisible(false);
            discoveryManagerDialogV2.setVisible(true);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    private void updateCurrentPanel(JPanel panel) {
        if (contentPanel != null) {
            getContentPane().remove(contentPanel);
        }
        contentPanel = panel;
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        this.validate();
    }


    public int showDialog() {
        this.setVisible(true);
        this.dispose();
        return option;
    }

}
