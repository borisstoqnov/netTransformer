/*
 * ConnectionDetailsPanel.java
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


import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetails;
import net.itransformers.connectiondetails.connectiondetailsapi.IPNetConnectionDetails;
import net.itransformers.connectiondetails.csvconnectiondetails.CsvConnectionDetailsFileManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;


public class ConnectionDetailsPanel extends JPanel implements ListSelectionListener, TableModelListener {
    private DefaultListModel<String> listModel;
    private final JList<String> list;
    private DefaultTableModel tableModel;
    private final JTable table;
    private CsvConnectionDetailsFileManager csvConnectionDetailsFileManager;
  //  private Map<String,ConnectionDetails> connDetails = new HashMap<String, ConnectionDetails>();
    private String selectedConnection;

    private JTextField connTypeTextField;


    public ConnectionDetailsPanel() {

        this.setLayout(new BorderLayout());

        this.setBorder(new EmptyBorder(5, 5, 5, 5));


        this.setLayout(new BorderLayout(0, 0));

        JPanel description= new JPanel();

        description.setBorder(new EmptyBorder(5, 5, 5, 5));

        this.add(description, BorderLayout.EAST);

        String text = "<html>This panel is the place to enter discovery entry points\n" +
                "(devices from where the discovery process will start to discover your network).\n" +
                "<p>Each entry point should have deviceType, deviceName and an ipAddress.</html>\n";
        JLabel help = new JLabel(text) {
            public Dimension getPreferredSize() {
                return new Dimension(400, 200);
            }
            public Dimension getMinimumSize() {
                return new Dimension(400, 200);
            }
            public Dimension getMaximumSize() {
                return new Dimension(400, 200);
            }
        };
        help.setVerticalAlignment(SwingConstants.TOP);
        help.setHorizontalAlignment(SwingConstants.LEFT);

       // help.setForeground(new Color(0xffffdd));


       // label.setSize(100,100);

        description.add(help);
        //description.


        JPanel listPanel = new JPanel();
        JPanel tablePanel = new JPanel();
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,listPanel,tablePanel);
        this.add(splitPane, BorderLayout.CENTER);
        listPanel.setLayout(new BorderLayout(0, 0));

        JPanel listButtonsPanel = new JPanel();
        listPanel.add(listButtonsPanel, BorderLayout.SOUTH);

        final JButton addListButton = new JButton("+");
        addListButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String connectionName;
                do {
                    connectionName = JOptionPane.showInputDialog("Enter Connection name");
                    if (listModel.contains(connectionName)) {
                        JOptionPane.showMessageDialog(ConnectionDetailsPanel.this, "Connection name already exists");
                    }
                } while (listModel.contains(connectionName));
                int selectedIndex = list.getSelectedIndex();
                if (selectedIndex == -1){
                    listModel.addElement(connectionName);
                } else {
                    listModel.insertElementAt(connectionName, selectedIndex+1);
                }
                csvConnectionDetailsFileManager.createConnection(connectionName, new IPNetConnectionDetails());
                //connDetails.put(connectionName,new IPNetConnectionDetails());
            }
        });
        listButtonsPanel.add(addListButton);

        JButton removeListButton = new JButton("-");
        removeListButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = list.getSelectedIndex();
                if (selectedIndex != -1) {
                    String removedRow = listModel.remove(selectedIndex);
                    csvConnectionDetailsFileManager.deleteConnection(removedRow);
                }
            }
        });
        listButtonsPanel.add(removeListButton);

        list = new JList<String>();
        JScrollPane listScrollPane = new JScrollPane(list);
        listPanel.add(listScrollPane);

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.getSelectionModel().addListSelectionListener(this);
        tablePanel.setLayout(new BorderLayout(0, 0));

        JPanel headerPanel = new JPanel();
        tablePanel.add(headerPanel, BorderLayout.NORTH);

        JLabel lblConnectionType = new JLabel("Connection Type:");
        headerPanel.add(lblConnectionType);

        connTypeTextField = new JTextField();
        headerPanel.add(connTypeTextField);
        connTypeTextField.setColumns(10);
        connTypeTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = e.getActionCommand();
                ConnectionDetails connectionDetail = csvConnectionDetailsFileManager.getConnection(selectedConnection);
                connectionDetail.setConnectionType(text);
            }
        });

        JPanel tableButtonsPanel = new JPanel();
        tablePanel.add(tableButtonsPanel, BorderLayout.SOUTH);

        JButton addTableButton = new JButton("+");
        addTableButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                tableModel.addRow(new String[]{"",""});
            }
        });
        tableButtonsPanel.add(addTableButton);

        JButton removeTableButton = new JButton("-");
        removeTableButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selRow = table.getSelectedRow();
                tableModel.removeRow(selRow);
            }
        });
        tableButtonsPanel.add(removeTableButton);

        table = new JTable();
        table.putClientProperty("terminateEditOnFocusLost", true);
        JScrollPane tableScrollPane = new JScrollPane(table);
        tablePanel.add(tableScrollPane);

    }

    public void load(File file) throws IOException {
        csvConnectionDetailsFileManager = new CsvConnectionDetailsFileManager(file.getAbsolutePath());
        csvConnectionDetailsFileManager.load();
        Map<String,ConnectionDetails> connDetails = csvConnectionDetailsFileManager.getConnectionDetails();
       // this.connDetails = connDetails;
        initListModel();
        if (listModel.getSize() >0 ) {
            list.setSelectedIndex(0);
            selectedConnection = listModel.get(0);
            updateConnDetails(csvConnectionDetailsFileManager.getConnection(selectedConnection));
        }
    }

    public void save() throws FileNotFoundException {

        csvConnectionDetailsFileManager.save();

    }

    private void updateConnDetails(ConnectionDetails connDetail) {
        tableModel = new DefaultTableModel();
        tableModel.addColumn("name");
        tableModel.addColumn("value");
        if (connDetail != null) {
            Map<String, String> params = connDetail.getParams();
            for (String key : params.keySet()) {
                tableModel.addRow(new String[]{key, params.get(key)});
            }
            connTypeTextField.setText(connDetail.getConnectionType());
        } else {
            connTypeTextField.setText("");
        }
        tableModel.addTableModelListener(this);
        table.setModel(tableModel);
    }

    private void initListModel() {
        listModel = new DefaultListModel<String>();
        for (String name : csvConnectionDetailsFileManager.getConnectionDetails().keySet()) {
            listModel.addElement(name);
        }
        list.setModel(listModel);
    }

    @Override
    public void tableChanged(TableModelEvent e) {
//        int col = e.getColumn();
//        int row = e.getFirstRow();
//        tableModel.getValueAt(row,col);
        ConnectionDetails connectionDetail = csvConnectionDetailsFileManager.getConnection(selectedConnection);
        connectionDetail.clear();
        int rowCount = tableModel.getRowCount();
        for (int i=0;i<rowCount;i++) {
            String key = (String) tableModel.getValueAt(i,0);
            String val = (String) tableModel.getValueAt(i,1);
            connectionDetail.put(key,val);
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        boolean adjust = e.getValueIsAdjusting();
        if (!adjust) {
            selectedConnection = list.getSelectedValue();
            updateConnDetails(csvConnectionDetailsFileManager.getConnection(selectedConnection));
        }
    }

}
