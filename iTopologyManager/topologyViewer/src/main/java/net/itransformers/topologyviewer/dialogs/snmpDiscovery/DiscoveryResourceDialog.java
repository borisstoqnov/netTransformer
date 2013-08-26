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

import net.itransformers.resourcemanager.config.ResourcesType;
import net.itransformers.topologyviewer.gui.TopologyManagerFrame;
import net.itransformers.topologyviewer.help.HelpOpener;
import net.itransformers.utils.JaxbMarshalar;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.xml.bind.JAXBException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class DiscoveryResourceDialog extends JDialog {

	private final DiscoveryResourcePanel contentPanel;
    private TopologyManagerFrame frame;

    /**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			DiscoveryResourceDialog dialog = new DiscoveryResourceDialog(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public DiscoveryResourceDialog(final TopologyManagerFrame frame) {
        this.frame = frame;
        setModal(true);
		setTitle("Configure Discovery Resource");
		setBounds(100, 100, 580, 500);
		getContentPane().setLayout(new BorderLayout());
        contentPanel = new DiscoveryResourcePanel();
        ResourcesType resource = loadResource();
        if (resource == null) return;
        contentPanel.setResources(resource);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        onOK();
                    }
                });
                buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        setVisible(false);
                        dispose();
                    }
                });
                buttonPane.add(cancelButton);
			}
			{
				JButton helpButton = new JButton("?");
                helpButton.setActionCommand("Cancel");
                helpButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        HelpOpener.showHelp(frame, "net/itransformers/topologyviewer/dialogs/snmpDiscovery/DiscoveryResourceDialog.html");
                    }
                });
                buttonPane.add(helpButton);
			}
		}

	}

    private void onOK() {
        ResourcesType resource = contentPanel.getResources();
        storeResource(resource);
        setVisible(false);
        dispose();
    }

    private void storeResource(ResourcesType resource) {
        FileOutputStream os = null;
        File path = getPath();
       // File file = new File(path, "iDiscover/conf/xml/discoveryResource.xml");
        File file = new File(path, "resourceManager/conf/xml/resource.xml");

        try {
            os = new FileOutputStream(file);
            JaxbMarshalar.marshal(resource, os,"resources");
            JOptionPane.showMessageDialog(this.frame, "Configuration Resource stored successful.");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this.frame,"Can not open file: "+file.getAbsolutePath());
        } catch (JAXBException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this.frame,"Invalid file content: "+file.getAbsolutePath());
        } finally {
            if (os != null) try { os.close(); } catch (IOException e) {}
        }
    }

    private File getPath()  {
        if (frame == null) {
            return null;
        }
        return frame.getPath();
    }

    private ResourcesType loadResource(){
        FileInputStream is = null;
        File file;
        file = new File(getPath(), "resourceManager/conf/xml/resource.xml");
        try {
            is = new FileInputStream(file);

            ResourcesType resources = JaxbMarshalar.unmarshal(ResourcesType.class, is);
            return resources;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this.frame,"Can not open file: "+file.getAbsolutePath());
        } catch (JAXBException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this.frame,"Invalid file content: "+file.getAbsolutePath());
        } finally {
            if (is != null) try { is.close(); } catch (IOException e) {}
        }
        return null;
    }
}
