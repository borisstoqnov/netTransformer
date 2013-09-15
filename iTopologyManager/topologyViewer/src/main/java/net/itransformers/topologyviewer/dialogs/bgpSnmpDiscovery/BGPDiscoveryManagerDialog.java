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

package net.itransformers.topologyviewer.dialogs.bgpSnmpDiscovery;

import net.itransformers.bgpPeeringMap.BgpPeeringMap;
import net.itransformers.bgpPeeringMap.BgpPeeringMapManagerThread;
import net.itransformers.topologyviewer.gui.TopologyManagerFrame;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;


public class BGPDiscoveryManagerDialog extends JDialog {
    static Logger logger = Logger.getLogger(BGPDiscoveryManagerDialog.class);
    public static final String DISCOVERED_DEVICES = "Discovered Devices:";
    public static final String VERSION_LABEL = "version";
    private JTextField addressTextField;
    private JFrame frame;
    private File projectDir;
    private JComboBox modeComboBox;
    private BgpPeeringMapManagerThread managerThread;
    private JTextArea loggerConsole;
    final JButton pauseResumeButton = new JButton("Pause");
    private int discoveredDevices;
    private JLabel lblDiscoveredDevices;
    private JTextField labelTextField;
    private JCheckBox autoLabelCheckBox;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            BGPDiscoveryManagerDialog dialogBGP = new BGPDiscoveryManagerDialog(null, new File("."));
            dialogBGP.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialogBGP.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the dialog.
     */
    public BGPDiscoveryManagerDialog(TopologyManagerFrame frame) {
        this(frame, getProjPath(frame));
    }
    public BGPDiscoveryManagerDialog(JFrame frame, File projectDir) {
        this.frame = frame;
        this.projectDir = projectDir;
        setTitle("Internet BGP Peering Map Manager");
        setBounds(100, 100, 860, 364);
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



                    JLabel lblAddress = new JLabel("Initial Discovery IP Address:");
                    lblAddress.setBounds(10, 14, 220, 14);
                    panel.add(lblAddress);

                    addressTextField = new JTextField();
                    addressTextField.setBounds(220, 11, 113, 20);
                    panel.add(addressTextField);
                    addressTextField.setColumns(10);

                    JLabel lblLabel = new JLabel("Label:");
                    lblLabel.setBounds(350, 14, 56, 14);
                    panel.add(lblLabel);

                    labelTextField = new JTextField();
                    labelTextField.setBounds(400, 11, 113, 20);
                    panel.add(labelTextField);
                    labelTextField.setColumns(10);

                    autoLabelCheckBox = new JCheckBox("auto-label");
                    autoLabelCheckBox.setBounds(520, 11, 113, 20);
                    autoLabelCheckBox.setSelected(true);
                    panel.add(autoLabelCheckBox);

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
//        modeComboBox.setEditable(true);
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

        BgpPeeringMap manager;
        String label = null;
        try {
            label = labelTextField.getText().trim();
            if (autoLabelCheckBox.isSelected()) {
                label = createAutoLabel();
                labelTextField.setText(label);
            } else {
                if (!isValidLabel(label)) return false;
            }
            //manager = DiscoveryManager.createDiscoveryManager(projectDir, "bgpPeeringMap/conf/xml/iMapManager.xml", label);

            manager = new BgpPeeringMap(projectDir, addressTextField.getText(),projectDir +"/" +"bgpPeeringMap/conf/txt/bgpPeeringMap.properties",label);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Cannot start BGP Discovery. See error log for more info");
            e.printStackTrace();
            return false;
        }

//        String[] discoveryTypes = new String[]{"BGPPeering"};


//        Map<String,String> resourceSelectionParams = new HashMap<String, String>();
//        resourceSelectionParams.put("protocol","SNMP");
        discoveredDevices = 0;
        lblDiscoveredDevices.setText(DISCOVERED_DEVICES+discoveredDevices);
//        manager.addDiscoveryManagerListener(new DiscoveryListener() {
//            @Override
//            public void handleDevice(String deviceName, RawDeviceData rawData, DiscoveredDeviceData discoveredDeviceData, Resource resource) {
//                discoveredDevices++;
//                lblDiscoveredDevices.setText(DISCOVERED_DEVICES+discoveredDevices);
//
//            }
//        });
//        Map<String, String> snmpConnParams = new HashMap<String, String>();
//        snmpConnParams = manager.discoveryResource.getParamMap(snmp);

//        IPv4Address initialIPaddress= new IPv4Address(addressTextField.getText(),null);
//        snmpConnParams.put("status", "initial");
//        snmpConnParams.put("mibDir", "snmptoolkit/mibs");

        //snmpConnParams.get("port");
        //Resource resource = new Resource(initialIPaddress,null, Integer.parseInt(snmpConnParams.get("port")), snmpConnParams);

        managerThread = new BgpPeeringMapManagerThread(manager);
                //(manager,resource, "node",discoveryTypes);
        managerThread.start();
        return true;
    }

    private String createAutoLabel() {
        File base1 = new File(projectDir, "imap");

        if (!base1.exists()) {
                base1.mkdir();
        }
        String[] fileList = new File(projectDir,"imap").list();
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
        if (new File(new File(projectDir,"imap"),label).exists()){
            JOptionPane.showMessageDialog(this,"The specified label already exists");
            return false;
        }
        return true;
    }

}
