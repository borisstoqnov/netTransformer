package net.itransformers.idiscover.v2.core.gui;
import net.itransformers.idiscover.v2.core.CvsConnectionDetailsFactory;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ConnectionDetailsDialog extends JDialog  {

    private int option = JOptionPane.CLOSED_OPTION;

    /**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
            UIManager.put("Table.gridColor", new ColorUIResource(Color.gray));
            java.util.Map<String,ConnectionDetails> connectionDetailsList = CvsConnectionDetailsFactory.createConnectionDetail(new File("iDiscover/src/main/resources/connection-details.txt"));
			ConnectionDetailsDialog dialog = new ConnectionDetailsDialog(connectionDetailsList,true);
            int option = dialog.showDialog();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
     * @param connDetails from which the dialog will be filled
     */
	public ConnectionDetailsDialog(final java.util.Map<String, ConnectionDetails> connDetails, boolean modal) {
        this.setTitle("Connection Details");
        this.setModal(modal);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
        JPanel contentPanel = new ConnectionDetailsPanel(connDetails);
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);


		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
                okButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        option = JOptionPane.OK_OPTION;
                        ConnectionDetailsDialog.this.setVisible(false);
                    }
                });
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
                cancelButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        option = JOptionPane.CANCEL_OPTION;
                        ConnectionDetailsDialog.this.setVisible(false);
                    }
                });
			}
		}

    }

    public int showDialog(){
        this.setVisible(true);
        this.dispose();
        return option;
    }

}
