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

package net.itransformers.topologyviewer.menu.handlers;

import net.itransformers.topologyviewer.dialogs.bgpSnmpDiscovery.BGPDiscoveryManagerDialog;
import net.itransformers.topologyviewer.gui.TopologyManagerFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 * Date: 12-4-27
 * Time: 23:30
 * To change this template use File | Settings | File Templates.
 */
public class StartBGPDiscoveryMenuHandler implements ActionListener {

    private TopologyManagerFrame frame;

    public StartBGPDiscoveryMenuHandler(TopologyManagerFrame frame) throws HeadlessException {

        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (frame.getProjectType() != "bgpPeeringMap"){
            JOptionPane.showMessageDialog(frame,"Please open or create new bgpPeeringMap project prior starting the discovery process!");
            return;
        }
        BGPDiscoveryManagerDialog dialogBGP = new BGPDiscoveryManagerDialog(frame);
        dialogBGP.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialogBGP.setVisible(true);

    }


}
