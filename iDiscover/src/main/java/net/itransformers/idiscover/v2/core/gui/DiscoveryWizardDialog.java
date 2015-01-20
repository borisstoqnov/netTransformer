package net.itransformers.idiscover.v2.core.gui;
import net.itransformers.idiscover.v2.core.CvsConnectionDetailsFactory;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class DiscoveryWizardDialog extends JDialog  {

    private int option = JOptionPane.CLOSED_OPTION;
    private JPanel contentPanel = null;
    private JButton prevButton;
    private JButton nextButton;

    /**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
            UIManager.put("Table.gridColor", new ColorUIResource(Color.gray));
			DiscoveryWizardDialog dialog = new DiscoveryWizardDialog();
            int option = dialog.showDialog();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DiscoveryWizardDialog() {
        this.setTitle("Connection Details");
        this.setModal(true);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 1000, 300);
		getContentPane().setLayout(new BorderLayout());


        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        {
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				prevButton = new JButton("BACK");
				prevButton.setActionCommand("BACK");
				buttonPane.add(prevButton);
				getRootPane().setDefaultButton(prevButton);
                prevButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {
                            DiscoveryWizardDialog.this.prev();
                        } catch (IOException e1) {
                            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                        }
//                        option = JOptionPane.OK_OPTION;
//                        DiscoveryWizardDialog.this.setVisible(false);
                    }
                });
                prevButton.setEnabled(false);
			}
			{
				nextButton = new JButton("NEXT");
				nextButton.setActionCommand("NEXT");
				buttonPane.add(nextButton);
                nextButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        DiscoveryWizardDialog.this.next();
//                        option = JOptionPane.CANCEL_OPTION;
//                        DiscoveryWizardDialog.this.setVisible(false);
                    }
                });


            }
		}

    }

    private void init() throws IOException {
        java.util.Map<String,ConnectionDetails> connDetails = CvsConnectionDetailsFactory.createConnectionDetail(new File("iDiscover/src/main/resources/connection-details.txt"));
        updateCurrentPanel(new ConnectionDetailsPanel(connDetails));
    }

    private void prev() throws IOException {
        prevButton.setEnabled(true);
        nextButton.setEnabled(true);
        if (contentPanel instanceof DiscoveryParametersPanel){
            java.util.Map<String,ConnectionDetails> connDetails = CvsConnectionDetailsFactory.createConnectionDetail(new File("iDiscover/src/main/resources/connection-details.txt"));
            updateCurrentPanel(new ConnectionDetailsPanel(connDetails));
            prevButton.setEnabled(false);
        } else if (contentPanel instanceof DiscoveryResourcePanel) {
            updateCurrentPanel(new DiscoveryParametersPanel());
        }

    }

    private void next() {
        prevButton.setEnabled(true);
        nextButton.setEnabled(true);
        if (contentPanel instanceof ConnectionDetailsPanel){
            updateCurrentPanel(new DiscoveryParametersPanel());
        } else if (contentPanel instanceof DiscoveryParametersPanel) {
            updateCurrentPanel(new DiscoveryResourcePanel());
            nextButton.setEnabled(false);
        }

    }

    private void updateCurrentPanel(JPanel panel){
        if (contentPanel != null) {
            getContentPane().remove(contentPanel);
        }
        contentPanel = panel;
        getContentPane().add(contentPanel, BorderLayout.CENTER);

        this.validate();
    }



    public int showDialog(){
        this.setVisible(true);
        this.dispose();
        return option;
    }

}
