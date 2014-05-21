/*
 * netTransformer is an open source tool able to discover and transform
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

package net.itransformers.topologyviewer.dialogs.snmpDiscovery;
import net.itransformers.idiscover.core.*;
import net.itransformers.idiscover.networkmodel.DiscoveredDeviceData;
import net.itransformers.resourcemanager.config.ResourceType;
import net.itransformers.topologyviewer.gui.TopologyManagerFrame;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.HashMap;
import java.util.Map;


public class DiscoveryManagerDialog extends JDialog {
    static Logger logger = Logger.getLogger(DiscoveryManagerDialog.class);
    public static final String DISCOVERED_DEVICES = "Discovered Devices:";
    public static final String VERSION_LABEL = "version";
    private JTextField addressTextField;
    private JFrame frame;
    private File projectDir;
    private JComboBox modeComboBox;
    private DiscoveryManagerThread managerThread;
    private JTextArea loggerConsole;
    final JButton pauseResumeButton = new JButton("Pause");
    private int discoveredDevices;
    private JLabel lblDiscoveredDevices;
    private JTextField labelTextField;
    private JCheckBox autoLabelCheckBox;
    private JCheckBox postDiscoveryCheckBox;


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
    public DiscoveryManagerDialog(TopologyManagerFrame frame) {
        this(frame, getProjPath(frame));
    }
    public DiscoveryManagerDialog(JFrame frame, File projectDir) {
        this.frame = frame;
//        Container pane = frame.getContentPane();
//        pane.add(this.frame);
        this.projectDir = projectDir;
        setTitle("Discovery Manager");
        setBounds(100, 100, 960, 364);
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
                        modeComboBox.setModel(new DefaultComboBoxModel(new String[]{"network", "node"}));
                        modeComboBox.setBounds(46, 11, 120, 20);
                        panel.add(modeComboBox);
                    }

                    JLabel lblMode = new JLabel("Mode:");
                    lblMode.setBounds(6, 14, 46, 14);
                    panel.add(lblMode);

                    JLabel lblAddress = new JLabel("Address:");
                    lblAddress.setBounds(172, 14, 66, 14);
                    panel.add(lblAddress);

                    addressTextField = new JTextField();
                    addressTextField.setBounds(240, 11, 113, 20);
                    panel.add(addressTextField);
                    addressTextField.setColumns(10);

                    JLabel lblLabel = new JLabel("Label:");
                    lblLabel.setBounds(360, 14, 56, 14);
                    panel.add(lblLabel);

                    labelTextField = new JTextField();
                    labelTextField.setBounds(410, 11, 113, 20);
                    panel.add(labelTextField);
                    labelTextField.setColumns(10);

                    autoLabelCheckBox = new JCheckBox("auto-label");
                    autoLabelCheckBox.setBounds(525, 11, 113, 20);
                    autoLabelCheckBox.setSelected(true);
                    panel.add(autoLabelCheckBox);
                    postDiscoveryCheckBox = new JCheckBox("Post Discovery");
                    postDiscoveryCheckBox.setBounds(640, 11, 153, 20);
                    postDiscoveryCheckBox.setSelected(true);
                    panel.add(postDiscoveryCheckBox);


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
                            onStartDiscoveryPre(stopStartButton);
                            onStartDiscovery();
                            onStartDiscoveryPost(stopStartButton);
                        } else {
                            onStopDiscoveryPre(stopStartButton);
                            onStopDiscovery();
                            onStopDiscoveryPost(stopStartButton);

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
                    lblDiscoveredDevices = new JLabel(DISCOVERED_DEVICES);
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

    private void onStopDiscoveryPost(JButton stopStartButton) {
        modeComboBox.setEditable(true);
        addressTextField.setEditable(true);
        stopStartButton.setText("Start");
        pauseResumeButton.setEnabled(false);
        stopStartButton.setEnabled(true);
    }

    private void onStopDiscoveryPre(JButton stopStartButton) {
        stopStartButton.setEnabled(false);
    }

    private void onStartDiscoveryPost(JButton stopStartButton) {
        stopStartButton.setText("Stop");
        pauseResumeButton.setEnabled(true);
        stopStartButton.setEnabled(true);
    }

    private void onStartDiscoveryPre(JButton stopStartButton) {
        stopStartButton.setEnabled(false);
        modeComboBox.setEditable(false);
        addressTextField.setEditable(false);
    }

    private void onResumeDiscovery() {
        managerThread.resumeDiscovery();
    }

    private void onPauseDiscovery() {
        managerThread.pauseDiscovery();
    }

    private static File getProjPath(TopologyManagerFrame viewer){
        File projectDir = viewer.getPath();
        return projectDir;
    }
    private void onStopDiscovery() {
        managerThread.stopDiscovery();
    }

    private boolean onStartDiscovery() {
        DiscoveryManager manager;
        try {
            String label = labelTextField.getText().trim();
            if (autoLabelCheckBox.isSelected()) {
                label = createAutoLabel();
                labelTextField.setText(label);
            } else {
                if (!isValidLabel(label)) return false;
            }

            manager = DiscoveryManager.createDiscoveryManager(projectDir, "iDiscover/conf/xml/discoveryManager.xml",label,postDiscoveryCheckBox.isSelected());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Cannot start snmpDiscovery. See error log for more info");
            e.printStackTrace();
            return false;
        }
        // TODO remove hardcoded snmpDiscovery parameter groups
        String[] discoveryTypes = new String[]{"PHYSICAL","NEXT_HOP","OSPF","ISIS","BGP","RIP","ADDITIONAL","IPV6"};

        Map<String,String> resourceSelectionParams = new HashMap<String, String>();
        resourceSelectionParams.put("protocol","SNMP");
        ResourceType snmp = manager.discoveryResource.ReturnResourceByParam(resourceSelectionParams);
        discoveredDevices = 0;
        lblDiscoveredDevices.setText(DISCOVERED_DEVICES+discoveredDevices);
        manager.addDiscoveryManagerListener(new DiscoveryListener() {
            @Override
            public void handleDevice(String deviceName, RawDeviceData rawData, DiscoveredDeviceData discoveredDeviceData, Resource resource) {
                discoveredDevices++;
                lblDiscoveredDevices.setText(DISCOVERED_DEVICES+discoveredDevices);

            }
        });
        Map<String, String> snmpConnParams = new HashMap<String, String>();
        snmpConnParams = manager.discoveryResource.getParamMap(snmp);

        IPv4Address initialIPaddress= new IPv4Address(addressTextField.getText(),null);
        snmpConnParams.put("status", "initial");
        snmpConnParams.put("mibDir", "snmptoolkit/mibs");

        snmpConnParams.get("port");
        Resource resource = new Resource(initialIPaddress,null, Integer.parseInt(snmpConnParams.get("port")), snmpConnParams);

        managerThread = new DiscoveryManagerThread(manager,resource, modeComboBox.getSelectedItem().toString(),discoveryTypes);
        managerThread.start();
        return true;
    }

    private String createAutoLabel() {
        if (!new File(projectDir,"network").exists()) {
            return "version1";
        }
        String[] fileList = new File(projectDir,"network").list();
        int max = 0;
        for (String fName : fileList) {
            if (fName.matches(VERSION_LABEL+"\\d+")){
                int curr = Integer.parseInt(fName.substring(VERSION_LABEL.length()));
                if (max < curr ) max = curr;
            }
        }
        return VERSION_LABEL +(max+1);
    }

    private boolean isValidLabel(String label) {
        if (new File(new File(projectDir,"network"),label).exists()){
            int reply = JOptionPane.showConfirmDialog(this,"The specified label already exists! Would you like to merge graph in it?","",JOptionPane.YES_NO_OPTION);
            if(reply==JOptionPane.YES_OPTION){
                return true;
            }   else {
                return false;
            }
        }
        return true;
    }

}
