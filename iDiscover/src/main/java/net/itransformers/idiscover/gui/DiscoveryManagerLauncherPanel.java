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
import net.itransformers.resourcemanager.config.ResourcesType;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JTabbedPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


public class DiscoveryManagerLauncherPanel extends JPanel {
	private JTextField textField;

	/**
	 * Create the panel.
	 */
	public DiscoveryManagerLauncherPanel() {
		setLayout(null);
		
		JLabel lblMode = new JLabel("Mode:");
		lblMode.setBounds(245, 27, 46, 14);
		add(lblMode);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"network", "node"}));
		comboBox.setBounds(301, 24, 74, 20);
		add(comboBox);
		
		textField = new JTextField();
		textField.setBounds(85, 24, 133, 20);
		add(textField);
		textField.setColumns(10);
		
		JLabel lblHost = new JLabel("Host:");
		lblHost.setBounds(29, 27, 46, 14);
		add(lblHost);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(29, 73, 500, 428);
		add(tabbedPane);

        ResourcesType resources = null;
        DiscoveryResourcePanel discoveryResourcePanel = new DiscoveryResourcePanel();
		tabbedPane.addTab("Resources", null, discoveryResourcePanel, null);
		
		DiscoveryParametersPanel discoveryParametersPanel = new DiscoveryParametersPanel();
		tabbedPane.addTab("Discovery Parameters", null, discoveryParametersPanel, null);
		
		JButton btnNewButton = new JButton("Start");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnNewButton.setBounds(440, 11, 89, 23);
		add(btnNewButton);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnCancel.setBounds(440, 45, 89, 23);
		add(btnCancel);

	}
}
