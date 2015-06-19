package net.itransformers.topologyviewer.dialogs.discovery;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class ConnectionDetailsDialog extends JDialog  {

    private int option = JOptionPane.CLOSED_OPTION;

    /**   RawData2GraphmlTransformer.groovy
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
            UIManager.put("Table.gridColor", new ColorUIResource(Color.gray));
            File file = new File("iDiscover/src/main/resources/connection-details.txt");
            ConnectionDetailsDialog dialog = new ConnectionDetailsDialog(file,true);
            int option = dialog.showDialog();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
     * @param file from which the dialog will be filled
     */
	public ConnectionDetailsDialog(File file, boolean modal) {
        this.setTitle("Connection Details");
        this.setModal(modal);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
        ConnectionDetailsPanel contentPanel = new ConnectionDetailsPanel();
        try {
            contentPanel.load(file);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading connection details file");

        }
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
