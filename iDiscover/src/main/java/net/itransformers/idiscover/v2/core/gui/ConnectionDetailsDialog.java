package net.itransformers.idiscover.v2.core.gui;
import net.itransformers.idiscover.v2.core.CvsConnectionDetailsFactory;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.ColorUIResource;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ConnectionDetailsDialog extends JDialog  implements TableModelListener, ListSelectionListener {

    private DefaultListModel<String> listModel;
    private final JList<String> list;
    private DefaultTableModel tableModel;
    private final JTable table;

    private Map<String,ConnectionDetails> connDetails;
    private String selectedConnection;
    private int option = JOptionPane.CLOSED_OPTION;
    private JTextField connTypeTextField;

    /**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
            UIManager.put("Table.gridColor", new ColorUIResource(Color.gray));
            java.util.Map<String,ConnectionDetails> connectionDetailsList = CvsConnectionDetailsFactory.createConnectionDetail(new File("iDiscover/src/main/resources/connection-details.txt"));
			ConnectionDetailsDialog dialog = new ConnectionDetailsDialog(connectionDetailsList,true);
            int option = dialog.showDialog();
            if (option == JOptionPane.OK_OPTION) {
                System.out.println(dialog.getConnDetails());
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
     * @param connDetails from which the dialog will be filled
     */
	public ConnectionDetailsDialog(final java.util.Map<String, ConnectionDetails> connDetails, boolean modal) {
        this.setTitle("Connection Details");
        this.setModal(modal);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.connDetails = connDetails;
        setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
        JPanel contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
        initListModel();
		contentPanel.setLayout(new BorderLayout(0, 0));

        JPanel listPanel = new JPanel();
        JPanel tablePanel = new JPanel();
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,listPanel,tablePanel);
		getContentPane().add(splitPane, BorderLayout.CENTER);
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
                        JOptionPane.showMessageDialog(ConnectionDetailsDialog.this, "Connection name already exists");
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
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        option = JOptionPane.OK_OPTION;
                        ConnectionDetailsDialog.this.setVisible(false);
                    }
                });
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        option = JOptionPane.CANCEL_OPTION;
                        ConnectionDetailsDialog.this.setVisible(false);
                    }
                });
			}
		}
        if (listModel.getSize() >0 ) {
            list.setSelectedIndex(0);
            selectedConnection = listModel.get(0);
            updateConnDetails(connDetails.get(selectedConnection));
        }
    }

    public int showDialog(){
        this.setVisible(true);
        this.dispose();
        return option;
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
