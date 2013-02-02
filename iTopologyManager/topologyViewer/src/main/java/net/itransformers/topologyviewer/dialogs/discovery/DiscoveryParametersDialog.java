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

import net.itransformers.idiscover.discoveryhelpers.xml.discoveryParameters.DiscoveryHelperType;
import net.itransformers.topologyviewer.gui.TopologyManagerFrame;
import net.itransformers.utils.JaxbMarshalar;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.xml.bind.JAXBException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

public class DiscoveryParametersDialog extends JDialog {

	private final DiscoveryParametersPanel contentPanel;
	private JTextField projetNameTextField;
	private JTextField baseFilePathTextField;
    private boolean isOkPressed;
    private File projectDir;
    private TopologyManagerFrame frame;

    /**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			DiscoveryParametersDialog dialog = new DiscoveryParametersDialog(null);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public DiscoveryParametersDialog(final TopologyManagerFrame frame) {
        this.frame = frame;
        setModal(true);
		setTitle("Configure Discovery Parameters");
		setBounds(100, 100, 564, 565);
		getContentPane().setLayout(new BorderLayout());
        loadDiscoveryParameters();
        contentPanel = new DiscoveryParametersPanel();
        DiscoveryHelperType discoveryParameters = loadDiscoveryParameters();
        if (discoveryParameters == null) return;
        contentPanel.setDiscoveryHelperType(discoveryParameters);
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
                        projectDir = null;
                        isOkPressed = false;
                        setVisible(false);
                        dispose();
                    }
                });
                buttonPane.add(cancelButton);
			}
		}

	}


    private void onOK() {
        DiscoveryHelperType discoveryHelperType = contentPanel.getDiscoveryHelperType();
        storeDiscoveryParameters(discoveryHelperType);
        setVisible(false);
        dispose();

    }

    private void storeDiscoveryParameters(DiscoveryHelperType discoveryHelperType) {
        FileOutputStream os = null;
        File file;
            file = new File(frame.getPath(), "iDiscover/conf/xml/discoveryParameters.xml");
        try {
            os = new FileOutputStream(file);
            JaxbMarshalar.marshal(discoveryHelperType, os,"discovery-helper");
            JOptionPane.showMessageDialog(this.frame, "Discovery parameters stored successful.");
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

    private DiscoveryHelperType loadDiscoveryParameters(){
        FileInputStream is = null;
        File file;
        file = new File(frame.getPath(), "iDiscover/conf/xml/discoveryParameters.xml");
        try {
            is = new FileInputStream(file);

            DiscoveryHelperType discoveryHelperType = JaxbMarshalar.unmarshal(DiscoveryHelperType.class, is);
            return discoveryHelperType;
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
