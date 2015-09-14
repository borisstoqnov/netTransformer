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

package net.itransformers.topologyviewer.dialogs.discovery;

import net.itransformers.resourcemanager.config.ConnectionParamsType;
import net.itransformers.resourcemanager.config.ParamType;
import net.itransformers.resourcemanager.config.ResourceType;
import net.itransformers.resourcemanager.config.ResourcesType;
import net.itransformers.utils.JaxbMarshalar;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.xml.bind.JAXBException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.List;
import java.util.Vector;


public class DiscoveryResourcePanel extends JPanel {
    private DefaultTableModel resourcesTableModel;
    private DefaultTableModel resourceParamsTableModel;
    private ResourcesType resources;
    private final DefaultTableModel resourceConnectionParamsTableModel;
    private JComboBox comboBox;
    private JTable resourcesTable;
    private int currentResourceIndex;
    private int mCurrentConnectionTypeIndex;


    /**
     * Create the panel.
     */
    public DiscoveryResourcePanel() {
        this.resources = new ResourcesType();

        this.setLayout(new BorderLayout());

        this.setBorder(new EmptyBorder(5, 5, 5, 5));


        JLabel label_1 = new JLabel("Resource Parameters");
        label_1.setBounds(148, 11, 140, 14);
        add(label_1);

        JLabel label_2 = new JLabel("Connection Parameters");
        label_2.setBounds(148, 182, 140, 14);
        add(label_2);
        JSeparator separator = new JSeparator();
        add(separator);

        comboBox = new JComboBox();
        comboBox.setEditable(true);
        comboBox.setModel(new DefaultComboBoxModel(new String[]{}));
        comboBox.setBounds(254, 179, 82, 20);
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = comboBox.getSelectedIndex();
                if (index < 0) return;
                onConnectionComboBoxChanged(index);
            }
        });
        add(comboBox);
        //This has to be visible on the right side of the panel!
        JPanel description= new JPanel();
//        TextArea helpArea = new TextArea();
//        helpArea.setEditable(false);
//        helpArea.setText("Please configure your discovery resources.\n"+
//                        "Discovery process will use them to discover each of\n" +
//                        "each of the \"unknown\" neighbours found during its run.\n"+
//                        "Therefore each resource is associated with a number\n" +
//                        "of resource parameters that are used for resource matching.\n"+
//                        "Such could be device hostname, type, protocol, model \n+" +
//                        "or IP address range!");
//
//        description.add(helpArea);



        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(149, 36, 293, 88);
        add(scrollPane);

        JTable resourceParamsTable = new JTable();
        resourceParamsTableModel = new DefaultTableModel(
                new Object[][]{}, new String[]{"Name", "Value"});
        resourceParamsTable.setModel(resourceParamsTableModel);
        scrollPane.setViewportView(resourceParamsTable);

        JScrollPane scrollPane_1 = new JScrollPane();
        scrollPane_1.setBounds(148, 207, 294, 143);
        add(scrollPane_1);

        JTable connectionParamsTable = new JTable();
        resourceConnectionParamsTableModel = new DefaultTableModel(
                new Object[][]{}, new String[]{"Name", "Value"});
        connectionParamsTable.setModel(resourceConnectionParamsTableModel);
        connectionParamsTable.getColumnModel().getColumn(0).setPreferredWidth(90);
        scrollPane_1.setViewportView(connectionParamsTable);

        resourcesTable = new JTable();

        String[] columnNames = {"Resources"};


        Object[][] data = {};
        resourcesTableModel = new DefaultTableModel(data,columnNames);
        resourcesTable.setModel(resourcesTableModel);
        resourcesTable.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        resourcesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = resourcesTable.getSelectedRow();
                    onSelectedResource(selectedRow);
                }
            }
        });
        resourcesTableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                int index = e.getFirstRow();
                if (e.getType() == TableModelEvent.INSERT) {
                    resources.getResource().add(index, new ResourceType());
                } else if (e.getType() == TableModelEvent.DELETE) {
                    resources.getResource().remove(index);
                } else {
                    resources.getResource().get(index).setName((String) ((DefaultTableModel) e.getSource()).getValueAt(index, 0));
                }
            }
        });
        JScrollPane scrollPane_2 = new JScrollPane();
        scrollPane_2.setBounds(20, 36, 102, 314);
        add(scrollPane_2);

        scrollPane_2.setViewportView(resourcesTable);

        JButton button = new JButton("+");
        button.addActionListener(new RowAddListener(resourcesTable));
        button.setBounds(10, 361, 46, 23);
        add(button);

        JButton button_1 = new JButton("-");
        button_1.addActionListener(new RowRemoveListener(resourcesTable));
        button_1.setBounds(74, 361, 46, 23);
        add(button_1);

        JButton button_2 = new JButton("+");
        button_2.addActionListener(new RowAddListener(connectionParamsTable));
        button_2.setBounds(148, 361, 46, 23);
        add(button_2);

        JButton button_3 = new JButton("-");
        button_3.addActionListener(new RowRemoveListener(connectionParamsTable));
        button_3.setBounds(204, 361, 46, 23);
        add(button_3);

        JButton button_4 = new JButton("+");
        button_4.addActionListener(new RowAddListener(resourceParamsTable));
        button_4.setBounds(148, 130, 46, 23);
        add(button_4);

        JButton button_5 = new JButton("-");
        button_5.addActionListener(new RowRemoveListener(resourceParamsTable));
        button_5.setBounds(204, 130, 46, 23);
        add(button_5);

        JButton button_6 = new JButton("+");
        button_6.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                onAddConnection();
            }
        });
        button_6.setBounds(346, 178, 46, 23);
        add(button_6);

        JButton btnNewButton = new JButton("-");
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onRemoveConnection();
            }
        });
        btnNewButton.setBounds(396, 178, 46, 23);
        add(btnNewButton);

        // Dirty hack for linux
        add(new Label());

        String text =
                "<html>"+
                        "Please configure your discovery resources. <p>Discovery process will " +
                        "use them to discover each of devices found during its run." +
                        "<p><p>Each resource is associated with a number of \"<b>Resource " +
                        "parameters</b>\" that are used for resource matching. " +
                        "<p>Such kind of parameters are deviceName, deviceType, protocol," +
                        "deviceModel and ipAddress others."+
                        "<p><p>\"<b>Connection parameters</b>\" are the actual parameters that will be used " +
                        "by netTransformer to communicate with your device. Example for such parameters"+
                        "are port, timeout, retries, version and authentication parameters such as "+
                        "usernames, passwords and community strings."+
                "</html>";
        JLabel help = new JLabel(text) {
            public Dimension getPreferredSize() {
                return new Dimension(400, 400);
            }
            public Dimension getMinimumSize() {
                return new Dimension(400, 400);
            }
            public Dimension getMaximumSize() {
                return new Dimension(400, 400);
            }
        };
        help.setVerticalAlignment(SwingConstants.TOP);
        help.setHorizontalAlignment(SwingConstants.LEFT);

        description.add(help);

        add(description,BorderLayout.EAST);

    }

    private void onAddConnection() {
        ResourceType resource = getCurrentResource();
        if (resource == null){
            JOptionPane.showMessageDialog(this, "Can not insert new connection until resource is not selected","Error",JOptionPane.OK_OPTION);
            return;
        }
        String newConnectionType = (String)comboBox.getSelectedItem();
        if (newConnectionType == null || newConnectionType.trim().equals("")) {
            JOptionPane.showMessageDialog(this, "Can not insert empty connection","Error",JOptionPane.OK_OPTION);
            return;
        }
        List<ConnectionParamsType> connParamList = resource.getConnectionParams();
        boolean found = false;
        for (ConnectionParamsType connectionParamsType : connParamList) {
            if (newConnectionType.equals(connectionParamsType.getConnectionType())){
                found = true;
                break;
            }
        }
        if (!found){
            ConnectionParamsType newConnParamType = new ConnectionParamsType();
            newConnParamType.setConnectionType(newConnectionType);
            connParamList.add(newConnParamType);
            mCurrentConnectionTypeIndex = connParamList.size()-1; // the last added
            updateConnectionCombo();
            updateConnectionParamsTable();
        } else {
            JOptionPane.showMessageDialog(this, "Can not insert duplicated connection. Already exists","Error",JOptionPane.OK_OPTION);
        }
    }
    private void onRemoveConnection() {
        ResourceType resource = getCurrentResource();
        if (resource == null){
            JOptionPane.showMessageDialog(this, "Can not remove a connection until resource is not selected","Error",JOptionPane.OK_OPTION);
            return;
        }
        String newConnectionType = (String)comboBox.getSelectedItem();
        if (newConnectionType == null || newConnectionType.trim().equals("")) {
            JOptionPane.showMessageDialog(this, "Can not insert empty connection","Error",JOptionPane.OK_OPTION);
            return;
        }
        List<ConnectionParamsType> connParamList = resource.getConnectionParams();
        ConnectionParamsType found = null;
        for (ConnectionParamsType connectionParamsType : connParamList) {
            if (newConnectionType.equals(connectionParamsType.getConnectionType())){
                found = connectionParamsType;
                break;
            }
        }
        if (found != null){
            connParamList.remove(found);
            mCurrentConnectionTypeIndex = connParamList.size()-1; // the last added
            updateConnectionCombo();
            updateConnectionParamsTable();
        } else {
            JOptionPane.showMessageDialog(this, "Can find connection type for remove","Error",JOptionPane.OK_OPTION);
        }
    }

    private void onConnectionComboBoxChanged(int index) {
        updateCurrentConnectionParams();
        mCurrentConnectionTypeIndex = index;
        updateConnectionParamsTable();
    }

    public ResourcesType getResources() {
        updateCurrentResource();
        return resources;
    }

    public void setResources(ResourcesType resources) {
        this.resources = resources;
        updateResourcesTable(resources);
    }

    private void onSelectedResource(int index) {
        if (index != -1) {
            updateCurrentResource();
        }
        currentResourceIndex = index;
        mCurrentConnectionTypeIndex = 0;  // the zero is default
        updateResourceTable();
    }

    private void updateResourceTable() {
        updateResourceParamsTable();
        updateConnectionCombo();
        updateConnectionParamsTable();
    }

    private void updateResourceParamsTable() {
        ResourceType resource = getCurrentResource();
        if (resource != null){
            List<ParamType> paramsList = resource.getParam();
            Vector resourceParamsTableModelData = resourceParamsTableModel.getDataVector();
            resourceParamsTableModelData.removeAllElements();
            for (ParamType paramType : paramsList) {
                Vector vec = new Vector();
                vec.add(paramType.getName());
                vec.add(paramType.getValue());
                resourceParamsTableModelData.add(0,vec);
            }
        } else {
            resourceParamsTableModel.getDataVector().removeAllElements();
        }
        resourceParamsTableModel.fireTableDataChanged();
    }

    private void updateConnectionCombo() {
        ResourceType resource = getCurrentResource();
        ActionListener[] listeners = comboBox.getActionListeners();
        for (ActionListener listener : listeners) {
            comboBox.removeActionListener(listener);
        }
        comboBox.removeAllItems();
        if (resource != null) {
            List<ConnectionParamsType> connectionList = resource.getConnectionParams();
            for (ConnectionParamsType connectionParamsType : connectionList) {
                comboBox.addItem(connectionParamsType.getConnectionType());
            }
            if (connectionList.size() > 0){
                comboBox.setSelectedIndex(mCurrentConnectionTypeIndex);
            }
        }
        for (ActionListener listener : listeners) {
            comboBox.addActionListener(listener);
        }
    }

    private ResourceType getCurrentResource() {
        if (currentResourceIndex <0){
            return null;
        } else {
            return resources.getResource().get(currentResourceIndex);
        }
    }


    private void updateConnectionParamsTable() {
        ResourceType resource = getCurrentResource();
        if (resource != null) {
            List<ConnectionParamsType> connectionList = resource.getConnectionParams();
            if (connectionList.size() > 0){
                ConnectionParamsType connectionParamList = connectionList.get(mCurrentConnectionTypeIndex);

                Vector resourceConnectionParamsTableModelData = resourceConnectionParamsTableModel.getDataVector();
                resourceConnectionParamsTableModelData.removeAllElements();
                for (ParamType connectionParamsType : connectionParamList.getParam()) {
                    Vector vec = new Vector();
                    vec.add(connectionParamsType.getName());
                    vec.add(connectionParamsType.getValue());
                    resourceConnectionParamsTableModelData.add(0,vec);
                }
            } else {
                resourceConnectionParamsTableModel.getDataVector().removeAllElements();
            }
        } else {
            resourceConnectionParamsTableModel.getDataVector().removeAllElements();
        }
        resourceConnectionParamsTableModel.fireTableDataChanged();
    }

    public void updateResourcesTable(ResourcesType resources) {
        List<ResourceType> resourcesList = resources.getResource();
        Vector resourcesTableModelData = resourcesTableModel.getDataVector();
        resourcesTableModelData.removeAllElements();
        for (ResourceType resourceType : resourcesList) {
            Vector vec = new Vector();
            vec.add(resourceType.getName());
            resourcesTableModelData.add(vec);
        }
    }

    private void updateCurrentResource() {
        updateCurrentResourceParams();
        updateCurrentConnectionParams();
    }

    private void updateCurrentConnectionParams() {
        ResourceType resource = getCurrentResource();
        if (resource != null){
            List<ConnectionParamsType> connectionParamsList = resource.getConnectionParams();
            if (connectionParamsList.size() > 0 ) {
                ConnectionParamsType connectionParam = connectionParamsList.get(mCurrentConnectionTypeIndex);
                List<ParamType> paramList = connectionParam.getParam();
                paramList.clear();
                for (int i=0; i< resourceConnectionParamsTableModel.getDataVector().size(); i++) {
                    ParamType paramType = new ParamType();
                    Vector rows = (Vector) resourceConnectionParamsTableModel.getDataVector().get(i);
                    paramType.setName((String) rows.get(0));
                    paramType.setValue((String) rows.get(1));
                    paramList.add(0,paramType);
                }
            }
        }
    }

    private void updateCurrentResourceParams() {
        ResourceType resource = getCurrentResource();
        if (resource != null){
            List<ParamType> paramsList = resource.getParam();
            paramsList.clear();
            for (int i=0; i< resourceParamsTableModel.getDataVector().size(); i++) {
                ParamType paramType = new ParamType();
                Vector rows = (Vector) resourceParamsTableModel.getDataVector().get(i);
                paramType.setName((String) rows.get(0));
                paramType.setValue((String) rows.get(1));
                paramsList.add(0,paramType);
            }
        }
    }

    public void load(File file) throws Exception{
        FileInputStream is = null;
        is = new FileInputStream(file);
        ResourcesType resources = null;
        resources = JaxbMarshalar.unmarshal(ResourcesType.class, is);
        this.setResources(resources);
    }

    public void save(File file) throws Exception{
        ResourcesType resources = this.getResources();
        FileOutputStream os = new FileOutputStream(file);
        JaxbMarshalar.marshal(resources, os, "resources");
    }

    public static void main(String[] args) throws IOException, JAXBException {
        FileInputStream is = null;
        try {
            is = new FileInputStream("resourceManager/conf/xml/resource.xml");

            ResourcesType resources = JaxbMarshalar.unmarshal(ResourcesType.class, is);
            final DiscoveryResourcePanel panel = new DiscoveryResourcePanel();
            panel.setResources(resources);

            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    try {
                        JFrame frame = new JFrame();
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        frame.setBounds(100, 100, 945, 622);
                        JPanel contentPane = new JPanel();
                        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
                        frame.setContentPane(contentPane);
                        contentPane.setLayout(null);

                        panel.setBounds(10, 11, 917, 648);
                        contentPane.add(panel);
                        frame.setVisible(true);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

        } finally {
            if (is != null) is.close();
        }
    }
}
