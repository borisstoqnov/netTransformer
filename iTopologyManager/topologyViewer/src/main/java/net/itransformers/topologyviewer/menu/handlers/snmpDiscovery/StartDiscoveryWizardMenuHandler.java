/*
 * StartDiscoveryWizardMenuHandler.java
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

package net.itransformers.topologyviewer.menu.handlers.snmpDiscovery;

import net.itransformers.topologyviewer.dialogs.discovery.DiscoveryWizardDialog;
import net.itransformers.topologyviewer.gui.TopologyManagerFrame;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by niau on 5/26/15.
 */
public class StartDiscoveryWizardMenuHandler implements ActionListener {
    TopologyManagerFrame frame;
    public StartDiscoveryWizardMenuHandler(TopologyManagerFrame frame) {
        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        UIManager.put("Table.gridColor", new ColorUIResource(Color.gray));
        DiscoveryWizardDialog dialog = new DiscoveryWizardDialog(frame, frame.getPath().getAbsolutePath());
        int option = dialog.showDialog();

    }
}
