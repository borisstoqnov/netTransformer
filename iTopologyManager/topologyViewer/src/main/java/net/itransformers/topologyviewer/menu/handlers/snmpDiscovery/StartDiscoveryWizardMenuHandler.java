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
