package net.itransformers.topologyviewer.dialogs.discovery;

import net.itransformers.idiscover.v2.core.model.ConnectionDetails;

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
import java.util.Map;


public class ConnectionDetailsPanel extends JPanel implements ListSelectionListener, TableModelListener {
    private DefaultListModel<String> listModel;
    private final JList<String> list;
    private DefaultTableModel tableModel;
    private final JTable table;

    private Map<String,ConnectionDetails> connDetails;
    private String selectedConnection;

    private JTextField connTypeTextField;


    public ConnectionDetailsPanel(final java.util.Map<String, ConnectionDetails> connDetails) {

        this.connDetails = connDetails;
        this.setLayout(new BorderLayout());

        this.setBorder(new EmptyBorder(5, 5, 5, 5));

        initListModel();
        this.setLayout(new BorderLayout(0, 0));

        JPanel description= new JPanel();
        this.add(description,BorderLayout.EAST);
        description.setSize(50, 50);
        TextArea label = new TextArea();

        label.setText(
                "Please enter your initial devices for discovery.\n"+
                "If your devices are SNMP enabled each initial\n" +
                "device should have a valid IP address, SNMP\n" +
                "version, community and port!\n"+
                "If you have SDN enabled network please enter\n" +
                "also the parameters and credentials of your\n" +
                "SDN controller!");

        label.setEditable(false);
       // label.setSize(100,100);

        description.add(label);
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
                connDetails.put(connectionName,new ConnectionDetails());
            }
        });
        listButtonsPanel.add(addListButton);

        JButton removeListButton = new JButton("-");
        removeListButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = list.getSelectedIndex();
                if (selectedIndex != -1){
                    listModel.remove(selectedIndex);
                }
            }
        });
        listButtonsPanel.add(removeListButton);

        list = new JList<String>(listModel);
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
                ConnectionDetails connectionDetail = connDetails.get(selectedConnection);
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

        if (listModel.getSize() >0 ) {
            list.setSelectedIndex(0);
            selectedConnection = listModel.get(0);
            updateConnDetails(connDetails.get(selectedConnection));
        }
    }


    public Map<String, ConnectionDetails> getConnDetails() {
        return connDetails;
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
        for (String name : connDetails.keySet()) {
            listModel.addElement(name);
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
//        int col = e.getColumn();
//        int row = e.getFirstRow();
//        tableModel.getValueAt(row,col);
        ConnectionDetails connectionDetail = connDetails.get(selectedConnection);
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
            updateConnDetails(connDetails.get(selectedConnection));
        }
    }

}
