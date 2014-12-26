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

import net.itransformers.idiscover.v2.core.*;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.resourcemanager.ResourceManager;
import net.itransformers.resourcemanager.config.ConnectionParamsType;
import net.itransformers.resourcemanager.config.ParamType;
import net.itransformers.resourcemanager.config.ResourceType;
import net.itransformers.resourcemanager.config.ResourcesType;
import net.itransformers.topologyviewer.gui.TopologyManagerFrame;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.swing.*;
import javax.xml.bind.JAXBException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DiscoveryManagerDialogV2 extends JDialog {
    static Logger logger = Logger.getLogger(DiscoveryManagerDialogV2.class);
    public static final String DISCOVERED_DEVICES = "Discovered devices \n";
    public static final String VERSION_LABEL = "version";
    private JTextField addressTextField;
    private JFrame frame;
    private File projectDir;
    private JComboBox depthComboBox;
    private DiscoveryManagerThread managerThread;
    private JTextArea loggerConsole;
    final JButton pauseResumeButton = new JButton("Pause");
    private int discoveredDevices;
    private JTextArea lblDiscoveredDevices;
    private JTextField labelTextField;
    private JCheckBox autoLabelCheckBox;
    private JCheckBox postDiscoveryCheckBox;


    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            DiscoveryManagerDialogV2 dialog = new DiscoveryManagerDialogV2(null, new File("."));
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the dialog.
     */
    public DiscoveryManagerDialogV2(TopologyManagerFrame frame) {
        this(frame, getProjPath(frame));
    }

    public DiscoveryManagerDialogV2(JFrame frame, File projectDir) {
        this.frame = frame;
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
                        depthComboBox = new JComboBox();
                        depthComboBox.setModel(new DefaultComboBoxModel(new Integer[]{0,1, 2, 3, 4, 5, 6, 7,8,9,10}));
                        depthComboBox.setBounds(46, 11, 70, 20);
                        panel.add(depthComboBox);
                    }

                    JLabel lblMode = new JLabel("Depth:");
                    lblMode.setBounds(6, 14, 46, 14);
                    panel.add(lblMode);

                    JLabel lblAddress = new JLabel("Address:");
                    lblAddress.setBounds(172, 14, 56, 14);
                    panel.add(lblAddress);

                    addressTextField = new JTextField();
                    addressTextField.setBounds(230, 11, 113, 20);
                    panel.add(addressTextField);
                    addressTextField.setColumns(10);

                    JLabel lblLabel = new JLabel("Label:");
                    lblLabel.setBounds(360, 14, 56, 14);
                    panel.add(lblLabel);

                    labelTextField = new JTextField();
                    labelTextField.setBounds(400, 11, 113, 20);
                    panel.add(labelTextField);
                    labelTextField.setColumns(10);

                    autoLabelCheckBox = new JCheckBox("auto-label");
                    autoLabelCheckBox.setBounds(520, 11, 113, 20);
                    autoLabelCheckBox.setSelected(true);
                    panel.add(autoLabelCheckBox);
                    postDiscoveryCheckBox = new JCheckBox("Post Discovery");
                    postDiscoveryCheckBox.setBounds(620, 11, 153, 20);
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
            lblDiscoveredDevices = new JTextArea();
            JScrollPane scrolltxt = new JScrollPane(lblDiscoveredDevices);
            lblDiscoveredDevices.append("Discovery process output");
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
                //panel.setSize(100:100);
                {
                    loggerConsole = new JTextArea();
                    JScrollPane scrolltxt = new JScrollPane(loggerConsole);
                    loggerConsole.append("Discovery logger console");
                    panel.add(scrolltxt,BorderLayout.CENTER);
                }
            }
        }
        {
            Logger logger = Logger.getRootLogger();
            logger.setLevel(Level.INFO);
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
        depthComboBox.setEditable(true);
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
        depthComboBox.setEditable(false);
        addressTextField.setEditable(false);
    }

    private void onResumeDiscovery() {
        managerThread.resumeDiscovery();
    }

    private void onPauseDiscovery() {
        managerThread.pauseDiscovery();
    }

    private static File getProjPath(TopologyManagerFrame viewer) {
        File projectDir = viewer.getPath();
        return projectDir;
    }

    private void onStopDiscovery() {
        managerThread.stopDiscovery();
    }

    private boolean onStartDiscovery() {

        String label = labelTextField.getText().trim();
        if (autoLabelCheckBox.isSelected()) {
            label = createAutoLabel();
            labelTextField.setText(label);
        } else {
            if (!isValidLabel(label)) return false;
        }

        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext("discovery.xml", "connectionsDetails.xml");
        NetworkNodeDiscovererImpl nodeDiscovererImpl = applicationContext.getBean("discovery", NetworkNodeDiscovererImpl.class);


        Map<String, String> resourceSelectionParams = new HashMap<String, String>();
        resourceSelectionParams.put("protocol", "SNMP");

        ConnectionDetails connectionDetails = new ConnectionDetails();
        connectionDetails.setConnectionType("SNMP");
        connectionDetails.put("ipAddress", addressTextField.getText());

        ResourceManager resourceManager;
        {
            String xml = null;
            try {
                xml = FileUtils.readFileToString(new File(projectDir, "resourceManager/conf/xml/resource.xml"));
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            InputStream is1 = new ByteArrayInputStream(xml.getBytes());
            ResourcesType deviceGroupsType = null;
            try {
                deviceGroupsType = net.itransformers.resourcemanager.util.JaxbMarshalar.unmarshal(ResourcesType.class, is1);
            } catch (JAXBException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            resourceManager = new ResourceManager(deviceGroupsType);
        }
        ResourceType resourceType = resourceManager.getResource("DEFAULT");

        List<ConnectionParamsType> connectionParams = resourceType.getConnectionParams();
        for (ConnectionParamsType connectionParam : connectionParams) {
            if (connectionParam.getConnectionType().equals("snmp")) {
                List<ParamType> params = connectionParam.getParam();
                for (ParamType param : params) {
                    connectionDetails.put(param.getName(), param.getValue());
                }
            }
        }
        int depth = (Integer) depthComboBox.getSelectedItem();
        NodeDiscoveryListener nodeListener = new NodeDiscoveryListener() {
            @Override
            public void nodeDiscovered(NodeDiscoveryResult discoveryResult) {
                discoveredDevices++;
                lblDiscoveredDevices.append(discoveryResult.getNodeId() + " has been discovered. " + "Total number of " + DISCOVERED_DEVICES + discoveredDevices + "\n");
            }
        };
        List<NodeDiscoveryListener> nodeListeners = nodeDiscovererImpl.getNodeDiscoveryListeners();
        nodeListeners.add(nodeListener);

        nodeDiscovererImpl.setNodeDiscoveryListeners(nodeListeners);


        NetworkDiscoveryListener networkListener = new NetworkDiscoveryListener() {
            @Override
            public void networkDiscovered(NetworkDiscoveryResult result) {

                lblDiscoveredDevices.append("Network Discovered!!!");
            }
        };
        List<NetworkDiscoveryListener> networkListeners = nodeDiscovererImpl.getNetworkDiscoveryListeners();
        networkListeners.add(networkListener);
        nodeDiscovererImpl.setNetworkDiscoveryListeners(networkListeners);
        managerThread = new DiscoveryManagerThread(nodeDiscovererImpl, depth, connectionDetails);
        lblDiscoveredDevices.setText("");
        loggerConsole.setText("");
        managerThread.start();
        return true;
    }

    private String createAutoLabel() {
        if (!new File(projectDir, "network").exists()) {
            return "version1";
        }
        String[] fileList = new File(projectDir, "network").list();
        int max = 0;
        for (String fName : fileList) {
            if (fName.matches(VERSION_LABEL + "\\d+")) {
                int curr = Integer.parseInt(fName.substring(VERSION_LABEL.length()));
                if (max < curr) max = curr;
            }
        }
        return VERSION_LABEL + (max + 1);
    }

    private boolean isValidLabel(String label) {
        if (new File(new File(projectDir, "network"), label).exists()) {
            JOptionPane.showMessageDialog(this, "The specified label already exists");
            return false;
        }
        return true;
    }

}
