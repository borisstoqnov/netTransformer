/*
 * iTransformer is an open source tool able to discover and transform
 *  IP network infrastructures.
 *  Copyright (C) 2012  http://itransformers.net
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.itransformers.topologyviewer.dialogs.discovery;
import net.itransformers.idiscover.core.DiscoveryManager;
import net.itransformers.idiscover.core.DiscoveryManagerThread;
import net.itransformers.idiscover.core.IPv4Address;
import net.itransformers.idiscover.core.Resource;
import net.itransformers.resourcemanager.config.ResourceType;
import net.itransformers.topologyviewer.gui.TopologyViewer;
import org.apache.log4j.Appender;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import javax.swing.*;
import javax.xml.bind.JAXBException;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class DiscoveryManagerDialog extends JDialog {
    private JTextField addressTextField;
    private JFrame frame;
    private File projectDir;
    private JComboBox modeComboBox;
    private DiscoveryManagerThread managerThread;
    private JTextArea loggerConsole;
    final JButton pauseResumeButton = new JButton("Pause");
    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            DiscoveryManagerDialog dialog = new DiscoveryManagerDialog(null, new File("."));
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the dialog.
     */
    public DiscoveryManagerDialog(TopologyViewer frame) {
        this(frame, getProjPath(frame));
    }
    public DiscoveryManagerDialog(JFrame frame, File projectDir) {
        this.frame = frame;
        this.projectDir = projectDir;
        if (projectDir == null) return;
        setTitle("Discovery Manager");
        setBounds(100, 100, 528, 364);
        getContentPane().setLayout(new BorderLayout());
        {
            JPanel buttonPane = new JPanel();
            getContentPane().add(buttonPane, BorderLayout.NORTH);
            {
                buttonPane.setLayout(new BorderLayout(0, 0));
                {
                    JPanel panel = new JPanel();
                    buttonPane.add(panel);
                    panel.setLayout(null);
                    {
                        modeComboBox = new JComboBox();
                        modeComboBox.setModel(new DefaultComboBoxModel(new String[]{"Network", "Node"}));
                        modeComboBox.setBounds(46, 11, 76, 20);
                        panel.add(modeComboBox);
                    }

                    JLabel lblMode = new JLabel("Mode:");
                    lblMode.setBounds(6, 14, 46, 14);
                    panel.add(lblMode);

                    JLabel lblAddress = new JLabel("Address:");
                    lblAddress.setBounds(132, 14, 56, 14);
                    panel.add(lblAddress);

                    addressTextField = new JTextField();
                    addressTextField.setBounds(188, 11, 113, 20);
                    panel.add(addressTextField);
                    addressTextField.setColumns(10);
                }
            }
            {
                JPanel panel = new JPanel();
                buttonPane.add(panel, BorderLayout.EAST);
                final JButton stopStartButton = new JButton("Start");
                panel.add(stopStartButton);
                stopStartButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        if ("Start".equals(stopStartButton.getText())) {
                            stopStartButton.setEnabled(false);
                            modeComboBox.setEditable(false);
                            addressTextField.setEditable(false);
                            onStartDiscovery();
                            stopStartButton.setText("Stop");
                            pauseResumeButton.setEnabled(true);
                            stopStartButton.setEnabled(true);
                        } else {
                            stopStartButton.setEnabled(false);
                            onStopDiscovery();
                            modeComboBox.setEditable(true);
                            addressTextField.setEditable(true);
                            stopStartButton.setText("Start");
                            pauseResumeButton.setEnabled(false);
                            stopStartButton.setEnabled(true);

                        }
                    }
                });
                stopStartButton.setActionCommand("Start");
                getRootPane().setDefaultButton(stopStartButton);
                {
                    pauseResumeButton.setEnabled(false);
                    panel.add(pauseResumeButton);
                    pauseResumeButton.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            if ("Pause".equals(pauseResumeButton.getText())) {
                                pauseResumeButton.setEnabled(false);
                                onPauseDiscovery();
                                pauseResumeButton.setText("Resume");
                                pauseResumeButton.setEnabled(true);
                            } else {
                                pauseResumeButton.setEnabled(false);
                                onResumeDiscovery();
                                pauseResumeButton.setText("Pause");
                                pauseResumeButton.setEnabled(true);
                            }
                        }
                    });
                }
            }
        }
        {
            loggerConsole = new JTextArea();
            JScrollPane scrolltxt = new JScrollPane(loggerConsole);
            getContentPane().add(scrolltxt, BorderLayout.CENTER);
        }
        {
            JPanel statusPanel = new JPanel();
            getContentPane().add(statusPanel, BorderLayout.SOUTH);
            statusPanel.setLayout(new BorderLayout(0, 0));
            {
                JPanel panel = new JPanel();
                statusPanel.add(panel);
                panel.setLayout(new BorderLayout(0, 0));
                {
                    JLabel lblDiscoveredDevices = new JLabel("Discovered Devices:");
                    panel.add(lblDiscoveredDevices, BorderLayout.WEST);
                }
            }
        }
        {
            Logger logger = Logger.getRootLogger();
            logger.addAppender(new AppenderSkeleton() {
                @Override
                protected void append(final LoggingEvent loggingEvent) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            loggerConsole.append(loggingEvent.getMessage().toString());
                            loggerConsole.append("\n");
                        }
                    });
                }
                @Override
                public void close() {
                }
                @Override
                public boolean requiresLayout() {
                    return false;
                }
            });
        }
    }

    private void onResumeDiscovery() {
        managerThread.resumeDiscovery();
    }

    private void onPauseDiscovery() {
        managerThread.pauseDiscovery();
    }

    private static File getProjPath(TopologyViewer viewer){
        URL projectURL = viewer.getPath();
        File projectDir = null;
        try {
            projectDir = new File(projectURL.toURI().toString());
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            JOptionPane.showConfirmDialog(viewer,"Cannot start discovery. See error log for more info");
            return null;
        }
        return projectDir;
    }
    private void onStopDiscovery() {
        managerThread.stopDiscovery();
    }

    private void onStartDiscovery() {
        DiscoveryManager manager;
        try {
            manager = DiscoveryManager.createDiscoveryManager(new File(projectDir, "iDiscover/conf/xml/discoveryManager.xml"));
        } catch (Exception e) {
            JOptionPane.showConfirmDialog(frame, "Cannot start discovery. See error log for more info");
            return;
        }
        String[] discoveryTypes = new String[]{"PHYSICAL","NEXT_HOP","OSPF","ISIS","BGP","RIP","ADDITIONAL","IPV6"};
        Map<String,String> resourceSelectionParams = new HashMap<String, String>();
        resourceSelectionParams.put("protocol","SNMP");
        ResourceType snmp = manager.discoveryResource.ReturnResourceByParam(resourceSelectionParams);

        Map<String, String> snmpConnParams = new HashMap<String, String>();
        snmpConnParams = manager.discoveryResource.getParamMap(snmp);

        IPv4Address initialIPaddress= new IPv4Address(addressTextField.getText(),null);
        snmpConnParams.put("status", "initial");
        snmpConnParams.put("mibDir", "snmptoolkit/mibs");

        snmpConnParams.get("port");
        Resource resource = new Resource(initialIPaddress,null, Integer.parseInt(snmpConnParams.get("port")), snmpConnParams);

        managerThread = new DiscoveryManagerThread(manager,resource, modeComboBox.getSelectedItem().toString(),discoveryTypes);
        managerThread.start();
    }

}
