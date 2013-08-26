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

package net.itransformers.topologyviewer.dialogs.snmpDiscovery;

import net.itransformers.idiscover.discoveryhelpers.xml.discoveryParameters.DeviceType;
import net.itransformers.idiscover.discoveryhelpers.xml.discoveryParameters.DiscoveryHelperType;
import net.itransformers.idiscover.discoveryhelpers.xml.discoveryParameters.DiscoveryMethodType;
import net.itransformers.utils.JaxbMarshalar;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.xml.bind.JAXBException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class DiscoveryParametersPanel extends JPanel {
    private DiscoveryHelperType discoveryHelperType;
    private DefaultTableModel oidsTableModel;
    private DefaultTableModel discoveryMethodTableModel;
    private DefaultTableModel devicesTableModel;
    private int currentDeviceIndex = -1;
    private int currentDiscoveryMethodIndex = -1;

    /**
	 * Create the panel.
	 */
	public DiscoveryParametersPanel() {
		setLayout(null);
        discoveryHelperType = new DiscoveryHelperType();
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(294, 49, 125, 240);
		add(scrollPane);

        JTable oidsTable = new JTable();
        oidsTableModel = new DefaultTableModel(new Object[][]{},new String[]{"OIDs"});
        oidsTable.setModel(oidsTableModel);
		oidsTable.getColumnModel().getColumn(0).setPreferredWidth(115);
		scrollPane.setViewportView(oidsTable);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(156, 49, 128, 240);
		add(scrollPane_1);

        final JTable discoveryMethodTable = new JTable();
        discoveryMethodTableModel = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Discovery Method"}
        );
        discoveryMethodTable.setModel(discoveryMethodTableModel);
        discoveryMethodTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        discoveryMethodTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = discoveryMethodTable.getSelectedRow();
                    onSelectedDiscoveryMethod(selectedRow);
                }
            }
        });
        discoveryMethodTable.getColumnModel().getColumn(0).setPreferredWidth(108);
		scrollPane_1.setViewportView(discoveryMethodTable);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(20, 49, 126, 240);
		add(scrollPane_2);

        final JTable deviceTable = new JTable();
		scrollPane_2.setViewportView(deviceTable);
		devicesTableModel = new DefaultTableModel(
				new Object[][] {},
				new String[] {"Device"}
			);
		deviceTable.setModel(devicesTableModel);
        deviceTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        deviceTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    int selectedRow = deviceTable.getSelectedRow();
                    onSelectedDevice(selectedRow);
                }
            }
        });
		JButton button = new JButton("+");
		button.addActionListener(new RowAddListener(deviceTable));
		button.setBounds(20, 293, 46, 23);
		add(button);
		
		JButton button_1 = new JButton("-");
		button_1.addActionListener(new RowRemoveListener(deviceTable));
		button_1.setBounds(76, 293, 46, 23);
		add(button_1);
		
		JButton button_2 = new JButton("+");
		button_2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onAddDiscoveryMethod();
            }
        });
		button_2.setBounds(156, 293, 46, 23);
		add(button_2);
		
		JButton button_3 = new JButton("-");
		button_3.addActionListener(new RowRemoveListener(discoveryMethodTable));
		button_3.setBounds(212, 293, 46, 23);
		add(button_3);
		
		JButton button_4 = new JButton("+");
		button_4.addActionListener(new RowAddListener(oidsTable));
		button_4.setBounds(294, 293, 46, 23);
		add(button_4);
		
		JButton button_5 = new JButton("-");
		button_5.addActionListener(new RowRemoveListener(oidsTable));
		button_5.setBounds(350, 293, 46, 23);
		add(button_5);

	}

    private void onAddDiscoveryMethod() {
        DeviceType device = getCurrentDevice();
        List<DiscoveryMethodType> discoveryMethods = device.getDiscoveryMethod();
        DiscoveryMethodType discoveryMethodType = new DiscoveryMethodType();
        discoveryMethodType.setName("");
        discoveryMethodType.setValue("");
        discoveryMethods.add(discoveryMethodType);
        onSelectedDevice(currentDeviceIndex);
    }

    private void onSelectedDevice(int index) {
        if (currentDeviceIndex != -1) {
            updateCurrentDiscoveryMethod();
            updateCurrentOids();
        }
        currentDeviceIndex = index;
        currentDiscoveryMethodIndex = -1;
        updateDiscoveryMethodTable();
        clearOidsTable();
    }

    private void updateCurrentDiscoveryMethod() {
        DeviceType device = getCurrentDevice();
        if (device != null) {
            Vector discoveryMethodsRows = discoveryMethodTableModel.getDataVector();
            for (int i=0;i < discoveryMethodsRows.size(); i++) {
                String discoveryMethodName = (String) ((Vector) discoveryMethodsRows.get(i)).get(0);
                device.getDiscoveryMethod().get(i).setName(discoveryMethodName);
            }
        }
    }

    private void onSelectedDiscoveryMethod(int index) {
        if (currentDiscoveryMethodIndex != -1) updateCurrentOids();
        currentDiscoveryMethodIndex = index;
        updateOidsTable();
    }

    private void updateDeviceTable() {
        DeviceType currentDevice = getCurrentDevice();
    }

    private DiscoveryMethodType getCurrentDiscoveryMethod() {
        if (currentDiscoveryMethodIndex < 0 ) return null;
        DeviceType currentDevice = getCurrentDevice();
        return currentDevice.getDiscoveryMethod().get(currentDiscoveryMethodIndex);
    }

    private DeviceType getCurrentDevice() {
        return discoveryHelperType.getDevice().get(currentDeviceIndex);
    }

    private void updateOidsTable() {
        DiscoveryMethodType discoveryMethod = getCurrentDiscoveryMethod();
        if (discoveryMethod != null) {
            String discoveryMethodValueList = discoveryMethod.getValue();
            String[] oids = discoveryMethodValueList.split(",");
            Vector oidsTableModelData = oidsTableModel.getDataVector();
            oidsTableModelData.removeAllElements();
            for (String oid : oids) {
                Vector vec = new Vector();
                vec.add(oid);
                oidsTableModelData.add(vec);
            }
            oidsTableModel.fireTableDataChanged();
        }
    }

    private void clearOidsTable() {
        Vector oidsTableModelData = oidsTableModel.getDataVector();
        oidsTableModelData.removeAllElements();
        oidsTableModel.fireTableDataChanged();
    }

    private void updateDiscoveryMethodTable() {
        DeviceType deviceType = getCurrentDevice();
        List<DiscoveryMethodType> discoveryMethodTypeList = deviceType.getDiscoveryMethod();
        Vector discoveryMethodTableModelData = discoveryMethodTableModel.getDataVector();
        discoveryMethodTableModelData.removeAllElements();
        for (DiscoveryMethodType discoveryMethodType : discoveryMethodTypeList) {
            Vector vec = new Vector();
            vec.add(discoveryMethodType.getName());
            discoveryMethodTableModelData.add(vec);
        }
        discoveryMethodTableModel.fireTableDataChanged();
    }

    public DiscoveryHelperType getDiscoveryHelperType() {
        updateCurrentDiscoveryMethod();
        updateCurrentOids();
        return discoveryHelperType;
    }

    public void setDiscoveryHelperType(DiscoveryHelperType discoveryHelperType) {
        this.discoveryHelperType = discoveryHelperType;
        bindFrom(discoveryHelperType);
    }

//    public void bindTo(DiscoveryHelperType  discoveryHelperType){
//        DefaultTableModel model = (DefaultTableModel) deviceTable.getModel();
//        Vector dataVec = model.getDataVector();
//        discoveryHelperType.getDevice().clear();
//        for (Object data : dataVec) {
//            DeviceType deviceType = new DeviceType();
//            deviceType.setType((String) data);
//            discoveryHelperType.getDevice().add(deviceType);
//        }

//        for (DeviceType  : discoveryHelperType.getDevice()) {
//
//        }
//    }

    private void updateCurrentOids(){
        DiscoveryMethodType discoveryMethod = getCurrentDiscoveryMethod();
        if (discoveryMethod != null) {
            StringBuilder sbs = new StringBuilder();
            Vector oidsTableModelData = oidsTableModel.getDataVector();
            for (int i=0; i< oidsTableModelData.size();  i++) {
                Vector row = (Vector) oidsTableModelData.get(i);
                sbs.append(row.get(0));
                if (i < oidsTableModelData.size()) sbs.append(",");
            }
            discoveryMethod.setValue(sbs.toString());
        }
    }

    public void bindFrom(DiscoveryHelperType discoveryHelperType) {
        List<DeviceType> deviceTypeList = discoveryHelperType.getDevice();
        Vector devicesTableModelData = devicesTableModel.getDataVector();
        devicesTableModelData.removeAllElements();
        for (DeviceType deviceType : deviceTypeList) {
            Vector vec = new Vector();
            vec.add(deviceType.getType());
            devicesTableModelData.add(vec);
        }
    }


    public static void main(String[] args) throws IOException, JAXBException {
        FileInputStream is = null;
        try {
            is = new FileInputStream("iDiscover/conf/xml/discoveryParameters.xml");

            DiscoveryHelperType discoveryHelperType1 = JaxbMarshalar.unmarshal(DiscoveryHelperType.class, is);
            final DiscoveryParametersPanel panel = new DiscoveryParametersPanel();
            panel.setDiscoveryHelperType(discoveryHelperType1);

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
