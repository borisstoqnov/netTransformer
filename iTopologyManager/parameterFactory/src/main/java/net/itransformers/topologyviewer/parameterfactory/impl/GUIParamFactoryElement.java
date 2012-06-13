package net.itransformers.topologyviewer.parameterfactory.impl;

import net.itransformers.topologyviewer.parameterfactory.ParameterFactoryElement;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 **
 * 
 * Copyright 
 */
public class GUIParamFactoryElement implements ParameterFactoryElement{

    private Map<String, String> params;

    public void init(Map<String, String> config, Map<String, String> params) {
        this.params = params;
    }

    public Map<String, String> createParams(Map<String, Object> context,Map<String, String> currentParams) {
        JFrame parent = context == null ? null : context.get("parentFrame") == null? null : (JFrame) context.get("parentFrame");
        final JDialog jDialog = new JDialog(parent, "Parameter Editor",true);

        jDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                jDialog.dispose();
            }
        });

        final MapTableModel tableModel = new MapTableModel(params);

        final JTable jTable = new JTable(tableModel);
        jTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        jTable.setPreferredScrollableViewportSize(new Dimension(500, 145));
        jTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(jTable);

        jDialog.getContentPane().add(BorderLayout.CENTER, scrollPane);
        final boolean[] okChoice = new boolean[]{false};
        JPanel buttonPanel = new JPanel();
        final JButton okButton = new JButton("OK");
        buttonPanel.add(okButton);
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                okChoice[0] = true;
                jDialog.dispose();
            }
        });
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jDialog.dispose();
            }
        });
        buttonPanel.add(cancelButton);

        jDialog.add(BorderLayout.SOUTH, buttonPanel);
        jDialog.pack();
        jDialog.setVisible(true);
        if (okChoice[0]) {
            return tableModel.getMap();
        } else {
            return null;
        }

    }

    class MapTableModel extends AbstractTableModel {
        protected Map map;
        protected String[] columnNames;
        public MapTableModel(Map map) {
            this(map, "Entry", "Value");
        }
        public MapTableModel(Map map, String keyName, String valueName) {
            setMap(map);
            setColumnNames(keyName, valueName);
        }
        public int getRowCount() {
            return map.size();
        }
        public int getColumnCount() {
            return 2;
        }
        public Object getValueAt(int row, int column) {
            Object[] entries = map.entrySet().toArray();
            Map.Entry entry = (Map.Entry) entries[row];
            if (column == 0) {
                return entry.getKey();
            } else if (column == 1) { // column==1
                return entry.getValue();
            } else {
                throw new IndexOutOfBoundsException("MapTableModel provides a 2-column table, column-index " + column + " is illegal.");
            }
        }
        public String getColumnName(int column) {
            return columnNames[column];
        }
        public void setColumnNames(String keyName, String valueName) {
            String[] names = {keyName, valueName};
            columnNames = names;
        }
        public Map getMap() {
            return map;
        }
        public void setMap(Map _map) {
            map = _map;
        }
        public boolean isCellEditable(int row, int column)
        {
            return (column == 1);
        }

        public void setValueAt(Object value, int row, int column) {
            Object[] entries = map.entrySet().toArray();
            Map.Entry entry = (Map.Entry) entries[row];
            if (column == 1) {
                entry.setValue(value);
            } else {
                throw new IndexOutOfBoundsException("MapTableModel provides a 2-column table, column-index " + column + " is illegal.");
            }
            fireTableCellUpdated(row, column);
        }
    } // end MapTableModel

    public static void main(String[] args) {
        GUIParamFactoryElement factoryElement = new GUIParamFactoryElement();
        Map<String, String> params = new HashMap<String, String>();
        params.put("test123","test");
        params.put("test12345","test125");
        factoryElement.init(null,params);
        Map<String,String> result = factoryElement.createParams(null, null);
        System.out.println(result);
    }
}
