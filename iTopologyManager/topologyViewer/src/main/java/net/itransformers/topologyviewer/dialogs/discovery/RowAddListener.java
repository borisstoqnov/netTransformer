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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class RowAddListener implements ActionListener{
	private final JTable table;
	public RowAddListener(JTable table ) {
		this.table = table;
	}
	public void actionPerformed(ActionEvent arg0) {
		DefaultTableModel deviceTableModel = (DefaultTableModel)table.getModel();
		int rowIndex = table.getSelectedRow();
		if (rowIndex == -1) {
			deviceTableModel.addRow(new Object[]{});
		} else {
			deviceTableModel.insertRow(rowIndex+1, new Object[]{});
		}
	}

}
