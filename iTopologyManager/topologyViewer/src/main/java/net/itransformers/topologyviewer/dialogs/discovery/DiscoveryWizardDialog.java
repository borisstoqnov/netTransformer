package net.itransformers.topologyviewer.dialogs.discovery;
import net.itransformers.idiscover.v2.core.CvsConnectionDetailsFactory;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.resourcemanager.config.ResourcesType;
import net.itransformers.utils.JaxbMarshalar;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import javax.xml.bind.JAXBException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class DiscoveryWizardDialog extends JDialog  {

    private int option = JOptionPane.CLOSED_OPTION;
    private JPanel contentPanel = null;
    private JButton prevButton;
    private JButton nextButton;
    private Frame frame;
    private String projectPath;



    /**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
            UIManager.put("Table.gridColor", new ColorUIResource(Color.gray));

			DiscoveryWizardDialog dialog = new DiscoveryWizardDialog(null,".");
            int option = dialog.showDialog();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


    public DiscoveryWizardDialog(Frame parentFrame, String projectPath) {

        super(parentFrame, "Discovery Wizard", true);
        this.frame = parentFrame;
        this.projectPath = projectPath;
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setBounds(100, 100, 1000, 400);
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
        java.util.Map<String,ConnectionDetails> connDetails = CvsConnectionDetailsFactory.createConnectionDetail(new File("iDiscover/conf/txt/connection-details.txt"));
        updateCurrentPanel(new ConnectionDetailsPanel(connDetails));
    }

    private void prev() throws IOException {
        prevButton.setEnabled(true);
        nextButton.setEnabled(true);
//        if (contentPanel instanceof DiscoveryParametersPanel){
//            java.util.Map<String,ConnectionDetails> connDetails = CvsConnectionDetailsFactory.createConnectionDetail(new File("iDiscover/src/main/resources/connection-details.txt"));
//            updateCurrentPanel(new ConnectionDetailsPanel(connDetails));
//            prevButton.setEnabled(false);
        if (contentPanel instanceof DiscoveryResourcePanel) {
            java.util.Map<String,ConnectionDetails> connDetails = CvsConnectionDetailsFactory.createConnectionDetail(new File("iDiscover/src/main/resources/connection-details.txt"));

            updateCurrentPanel(new ConnectionDetailsPanel(connDetails));
            prevButton.setEnabled(false);

        }

    }

    private void next() {
        prevButton.setEnabled(true);
        nextButton.setEnabled(true);
//        if (contentPanel instanceof ConnectionDetailsPanel){
//            updateCurrentPanel(new DiscoveryParametersPanel());
//        } else
        if (contentPanel instanceof ConnectionDetailsPanel) {
            FileInputStream is = null;

            try {
                is = new FileInputStream("resourceManager/conf/xml/resource.xml");
            } catch (FileNotFoundException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            ResourcesType resources = null;
            try {
                resources = JaxbMarshalar.unmarshal(ResourcesType.class, is);
            } catch (JAXBException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
            final DiscoveryResourcePanel panel = new DiscoveryResourcePanel();
                panel.setResources(resources);

            updateCurrentPanel(panel);
            nextButton.setText("GO!");
            nextButton.setEnabled(true);
            nextButton.setActionCommand("GO");

            panel.setResources(resources);
            nextButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (nextButton.getActionCommand().equals("GO")) {
                        try {
                            DiscoveryManagerDialogV2 discoveryManagerDialogV2 = new DiscoveryManagerDialogV2(DiscoveryWizardDialog.this.frame,new File(projectPath));
                            //discoveryManagerDialogV2.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                            DiscoveryWizardDialog.this.setVisible(false);
                            discoveryManagerDialogV2.setVisible(true);

                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }
//                        option = JOptionPane.CANCEL_OPTION;
//                        DiscoveryWizardDialog.this.setVisible(false);
                }
            });



        }

    }
    private void go() {
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