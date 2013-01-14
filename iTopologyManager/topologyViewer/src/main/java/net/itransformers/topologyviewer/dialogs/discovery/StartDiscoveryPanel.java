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

package net.itransformers.topologyviewer.dialogs.discovery;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;

public class StartDiscoveryPanel extends JPanel {
	private JTextField addressTextField;
	private JTextField portTextField;

	/**
	 * Create the panel.
	 */
	public StartDiscoveryPanel() {
		setLayout(null);
		
		addressTextField = new JTextField();
		addressTextField.setBounds(88, 11, 100, 20);
		add(addressTextField);
		addressTextField.setColumns(10);
		
		JLabel lblAddress = new JLabel("Address:");
		lblAddress.setBounds(10, 14, 46, 14);
		add(lblAddress);
		
		JLabel lblPort = new JLabel("Port:");
		lblPort.setBounds(10, 52, 46, 14);
		add(lblPort);
		
		portTextField = new JTextField();
		portTextField.setBounds(88, 49, 100, 20);
		add(portTextField);
		portTextField.setColumns(10);
		
		JLabel lblMode = new JLabel("Mode:");
		lblMode.setBounds(10, 95, 46, 14);
		add(lblMode);
		
		JComboBox modeComboBox = new JComboBox();
		modeComboBox.setModel(new DefaultComboBoxModel(new String[] {"Network", "Node"}));
		modeComboBox.setBounds(88, 92, 100, 20);
		add(modeComboBox);

	}
}
