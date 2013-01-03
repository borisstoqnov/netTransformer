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

package net.itransformers.idiscover.gui;

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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;


public class DiscoveryResourcePanel extends JPanel {
    private JTable resourceParamsTable;
    private JTable connectionParamsTable;
    private DefaultTableModel resourcesTableModel;
    private DefaultTableModel resourceParamsTableModel;
    private ResourcesType resources;
    private int currentResourceIndex;
    private final DefaultTableModel resourceConnectionParamsTableModel;


    /**
     * Create the panel.
     */
    public DiscoveryResourcePanel() {
        this.resources = new ResourcesType();
        setLayout(null);

        JLabel label_1 = new JLabel("Resource Params:");
        label_1.setBounds(148, 11, 102, 14);
        add(label_1);

        JLabel label_2 = new JLabel("Connection Params:");
        label_2.setBounds(148, 182, 116, 14);
        add(label_2);

        JComboBox comboBox = new JComboBox();
        comboBox.setEditable(true);
        comboBox.setModel(new DefaultComboBoxModel(new String[]{"snmp"}));
        comboBox.setBounds(274, 179, 82, 20);
        add(comboBox);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(149, 36, 261, 88);
        add(scrollPane);

        resourceParamsTable = new JTable();
        resourceParamsTableModel = new DefaultTableModel(
                new Object[][]{}, new String[]{"Name", "Value"});
        resourceParamsTable.setModel(resourceParamsTableModel);
        scrollPane.setViewportView(resourceParamsTable);

        JScrollPane scrollPane_1 = new JScrollPane();
        scrollPane_1.setBounds(148, 207, 267, 143);
        add(scrollPane_1);

        connectionParamsTable = new JTable();
        resourceConnectionParamsTableModel = new DefaultTableModel(
                new Object[][]{}, new String[]{"Name", "Value"});
        connectionParamsTable.setModel(resourceConnectionParamsTableModel);
        connectionParamsTable.getColumnModel().getColumn(0).setPreferredWidth(90);
        scrollPane_1.setViewportView(connectionParamsTable);

        final JTable resourcesTable = new JTable();

        String[] columnNames = {"Resources"};


        Object[][] data = {};
        resourcesTableModel = new DefaultTableModel(data,columnNames);
        resourcesTable.setModel(resourcesTableModel);
        resourcesTable.setSelectionMode(DefaultListSelectionModel.SINGLE_SELECTION);
        resourcesTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
//                int index = e.getLastIndex();
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = resourcesTable.getSelectedRow();
                    onSelectedResource(selectedRow);
                }
            }
        });
        resourcesTableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                    if (e.getType() == TableModelEvent.INSERT){
                        resources.getResource().add(e.getFirstRow(), new ResourceType());
                    } else if (e.getType() == TableModelEvent.DELETE) {
                        resources.getResource().remove(e.getFirstRow());
                    } else {
                        resources.getResource().get(e.getFirstRow()).setName((String) ((DefaultTableModel)e.getSource()).getValueAt(e.getFirstRow(),0));
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

    }

    public ResourcesType getResources() {
        return resources;
    }

    public void setResources(ResourcesType resources) {
        this.resources = resources;
        bindFrom(resources);
    }

    private void onSelectedResource(int index) {
        if (currentResourceIndex >= 0){
            ResourceType currentResource = resources.getResource().get(currentResourceIndex);
            bindTo(currentResource);
        }
        if (index >= 0){
            ResourceType resource = resources.getResource().get(index);
            bindFrom(resource);
        } else {
            resourceParamsTableModel.getDataVector().removeAllElements();
            resourceConnectionParamsTableModel.getDataVector().removeAllElements();
        }
        currentResourceIndex = index;
        resourceParamsTableModel.fireTableDataChanged();
        resourceConnectionParamsTableModel.fireTableDataChanged();
    }

    private void bindFrom(ResourceType resource) {
        List<ParamType> paramsList = resource.getParam();
        Vector resourceParamsTableModelData = resourceParamsTableModel.getDataVector();
        resourceParamsTableModelData.removeAllElements();
        for (ParamType paramType : paramsList) {
            Vector vec = new Vector();
            vec.add(paramType.getName());
            vec.add(paramType.getValue());
            resourceParamsTableModelData.add(0,vec);
        }

        List<ConnectionParamsType> connectionList = resource.getConnectionParams();
        if (connectionList.size() > 0) {
            ConnectionParamsType connectionParamList = connectionList.get(0);
            Vector resourceConnectionParamsTableModelData = resourceConnectionParamsTableModel.getDataVector();
            resourceConnectionParamsTableModelData.removeAllElements();
            for (ParamType connectionParamsType : connectionParamList.getParam()) {
                Vector vec = new Vector();
                vec.add(connectionParamsType.getName());
                vec.add(connectionParamsType.getValue());
                resourceConnectionParamsTableModelData.add(0,vec);
            }
        }
    }
    private void bindTo(ResourceType resource) {
        List<ParamType> paramsList = resource.getParam();
        paramsList.clear();
        for (int i=0; i< resourceParamsTableModel.getDataVector().size(); i++) {
            ParamType paramType = new ParamType();
            Vector rows = (Vector) resourceParamsTableModel.getDataVector().get(i);
            paramType.setName((String) rows.get(0));
            paramType.setValue((String) rows.get(1));
            paramsList.add(paramType);
        }

        List<ConnectionParamsType> connectionParamsList = resource.getConnectionParams();
        if (connectionParamsList.size() > 0 ) {
            ConnectionParamsType connectionParam = connectionParamsList.get(0);
            List<ParamType> paramList = connectionParam.getParam();
            paramList.clear();
            for (int i=0; i< resourceConnectionParamsTableModel.getDataVector().size(); i++) {
                ParamType paramType = new ParamType();
                Vector rows = (Vector) resourceConnectionParamsTableModel.getDataVector().get(i);
                paramType.setName((String) rows.get(0));
                paramType.setValue((String) rows.get(1));
                paramList.add(paramType);
            }
        }

    }

    public void bindFrom(ResourcesType resources) {
        List<ResourceType> resourcesList = resources.getResource();
        Vector resourcesTableModelData = resourcesTableModel.getDataVector();
        resourcesTableModelData.removeAllElements();
        for (ResourceType resourceType : resourcesList) {
            Vector vec = new Vector();
            vec.add(resourceType.getName());
            resourcesTableModelData.add(0,vec);
        }
    }

    public static void main(String[] args) throws IOException, JAXBException {
        FileInputStream is = null;
        try {
            is = new FileInputStream("iDiscover/conf/xml/discoveryResource.xml");

            ResourcesType resources = JaxbMarshalar.unmarshal(ResourcesType.class, is);
            final DiscoveryResourcePanel panel = new DiscoveryResourcePanel();
            panel.setResources(resources);

            EventQueue.invokeLater(new Runnable() {
                public void run() {
                    try {
                        JFrame frame = new JFrame();
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        frame.setBounds(100, 100, 645, 622);
                        JPanel contentPane = new JPanel();
                        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
                        frame.setContentPane(contentPane);
                        contentPane.setLayout(null);

                        panel.setBounds(10, 11, 617, 648);
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
