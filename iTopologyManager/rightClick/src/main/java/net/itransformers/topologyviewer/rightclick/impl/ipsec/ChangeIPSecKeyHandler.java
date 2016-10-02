
package net.itransformers.topologyviewer.rightclick.impl.ipsec;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * Created by Bobi on 5/29/2016.
 */

public class ChangeIPSecKeyHandler extends NeighbourFinderByMethod {


    ChangeIPSecKeyWorker worker;
    private ProgressMonitor progressMonitor;
    List<String> userInput = null;

    @Override
    protected String performIPSecAction(IPsecPair[] ipsecpair) throws IOException {

        String userAnswer = getUserAnswer();
        if(userAnswer == "Yes") {
            userInput = firstTimeConfigurationCheck(ipsecpair);
        }
        else if(userAnswer == "Cancel"){
            return"";
        }

        progressMonitor = new ProgressMonitor(this, "Running routers", "", 0, 100);

        progressMonitor.setMillisToPopup(0);
        worker = new ChangeIPSecKeyWorker(ipsecpair, progressMonitor, userInput);
        worker.addPropertyChangeListener(this);
        worker.execute();
        return "";

    }

    private void printMessageToScreen(String message) {

        if(message == null || message.equals("")) {

            return;
        }

        JFrame frame = new JFrame("IPsec Neighbours");
        frame.setSize(300, 200);
        frame.getContentPane().setLayout(new BorderLayout());
        JTextPane text = new JTextPane();
        text.setEditable(false);
        text.setContentType("text/html");
        text.setText(message);
        frame.getContentPane().add("Center",text);
        frame.setVisible(true);
    }
    private String getUserAnswer(){
        String userAnswer = "";
        int reply = JOptionPane.showConfirmDialog(null, "First time configurtion?", "Close?",  JOptionPane.YES_NO_OPTION);
        if (reply == JOptionPane.YES_OPTION) {
            userAnswer = "Yes";
        }
        if (reply == JOptionPane.NO_OPTION) {
            userAnswer = "No";
        }
        if (reply == JOptionPane.CANCEL_OPTION) {
            userAnswer = "Cancel";
        }
        return userAnswer;
    }

    private List<String> firstTimeConfigurationCheck(IPsecPair[] ipsecpair) {

            userInput = new ArrayList<>();
            for(int i = 0; i < ipsecpair.length; i++)
            {
                if (ipsecpair[i] != null) {
                    String message = ipsecpair[i].toString();

                    JPasswordField p = new JPasswordField(5);
                    JLabel label  = new JLabel();
                    label.setText(message);
                    p.setEchoChar('*');

                    JPanel input = new JPanel();
                    input.setLayout(new BoxLayout(input, BoxLayout.Y_AXIS));
                    input.add(new JLabel("Please input current key"));
                    input.add(Box.createVerticalStrut(25));
                    input.add(label);
                    input.add(Box.createVerticalStrut(10));
                    input.add(p);

                    JOptionPane.showConfirmDialog(null, input, "Login", JOptionPane.DEFAULT_OPTION);
                    userInput.add(new String(p.getPassword()));
                }
            }

        return userInput;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {

        if ("progress".equals(evt.getPropertyName())) {

            Number progress = (Number) evt.getNewValue();
            if (progress != null) {

                progressMonitor.setProgress(progress.intValue());
            }
            if (progressMonitor.isCanceled() || worker.isDone()) {
                if (progressMonitor.isCanceled()) {
                    worker.cancel(true);
                } else {
                    progressMonitor.close();
                    try {
                        printMessageToScreen(worker.get());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

