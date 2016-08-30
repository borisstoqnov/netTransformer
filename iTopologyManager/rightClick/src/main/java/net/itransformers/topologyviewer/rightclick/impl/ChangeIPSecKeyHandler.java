
package net.itransformers.topologyviewer.rightclick.impl;

import net.itransformers.resourcemanager.config.IPsecPair;
import net.itransformers.topologyviewer.fulfilmentfactory.impl.CLIInterface;
import net.itransformers.topologyviewer.fulfilmentfactory.impl.TelnetCLIInterface;
import net.itransformers.topologyviewer.fulfilmentfactory.impl.TestFulfilmentImpl;


import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by Bobi on 5/29/2016.
 */

public class ChangeIPSecKeyHandler extends NeighbourFinderByMethod {
    boolean whichrouter=false;
    //We need the neighbours before connecting to them



    @Override
    protected String performIPSecAction(IPsecPair[] ipsecpair) throws IOException {
        //If this is first run we want to save the old key before generating new ones
        List<String> userInput = firstTimeConfigurationCheck(ipsecpair);
        int pairCounter =0;
        for(IPsecPair pair : ipsecpair){

            if(pair != null){
                pairCounter++;
            }
        }
        //If we have already done this continue
        if(userInput != null && userInput.size() != pairCounter) {
            printMessageToScreen("Something happened");
            return "";
        }

        String ipseckeyFilename = "";
        String oldkey = "";
        int counter=0;
        String message="";

        final  JTextArea area = initializeWorkerDialog();
        publishProgress("Starting something" ,area);

        for (IPsecPair pair : ipsecpair) {

            if (pair!=null) {
                ipseckeyFilename = pair.getRouterName() + pair.getRouterIP() + pair.getNeighbourName() + pair.getNeighbourIP();
                //empty the counter and pair for the next iteration

                //Generate key and file
                if(userInput != null){
                    oldkey = userInput.get(counter);
                } else {
                    oldkey = KeyFromFile(ipseckeyFilename);
                }
                KeyGenerator(ipseckeyFilename);


                Logger.getAnonymousLogger().log(Level.INFO, "Initialising cli" + Thread.currentThread().getName());
                CLIInterface cli;
                cli = new TelnetCLIInterface(pair.getRouterIP(), "nbu", "nbu", "#", 1000, Logger.getAnonymousLogger());
                publishProgress("Opening connection to router with IP: " + pair.getRouterIP() + " name: " + pair.getRouterName(),area);
                //Change IP sec key for first router
                Map<String, String> paramsfirst = generateConfigMap(pair, oldkey, ipseckeyFilename);


                cli.open();
                TestFulfilmentImpl telnetTorouter = new TestFulfilmentImpl(cli);
                telnetTorouter.execute("E:\\iTransformer\\netTransformer\\iTopologyManager\\fulfilmentFactory\\conf\\templ\\ChangeIPsecKey.templ", paramsfirst);
                cli.close();

                Map<String, String> paramssecond = generateConfigMap(pair, oldkey, ipseckeyFilename);
                //Change IP sec key for second router
                cli = new TelnetCLIInterface(pair.getNeighbourIP(), "nbu", "nbu", "#", 1000, Logger.getAnonymousLogger());
                publishProgress("Opening connection to router with IP: " + pair.getNeighbourIP() + " name: " + pair.getNeighbourName(),area);


                cli.open();
                telnetTorouter = new TestFulfilmentImpl(cli);
                telnetTorouter.execute("E:\\iTransformer\\netTransformer\\iTopologyManager\\fulfilmentFactory\\conf\\templ\\ChangeIPsecKey.templ", paramssecond);
                cli.close();

                publishProgress("Finished work for the router pair.",area);
                counter++;
            }
            else if (counter > 0)
            {
                message = "Key change completed";
            }
            else
            {
                message = "Pair is null";
            }

        }
        printMessageToScreen(message);
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

    private JTextArea initializeWorkerDialog() {

        JFrame frame = new JFrame(" Router configuration...");
        frame.setSize(300, 200);
        frame.getContentPane().setLayout(new BorderLayout());
        JTextArea text = new JTextArea();
        text.setEditable(false);
        text.setText("Starting work on routers...");
        frame.getContentPane().add("Center",text);
        frame.setVisible(true);

        return text;
    }

    private void publishProgress(final String message, final JTextArea area) {

        try{

            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    Logger.getAnonymousLogger().log(Level.INFO, "Calling from another thread. " + area.hashCode() + "  " + message + Thread.currentThread().getName());
                    area.append(message + "\n");
                    area.update(area.getGraphics());
                }
            });
        }
        catch(Exception e) {

            e.printStackTrace();
        }

    }

    private Map<String, String> generateConfigMap(IPsecPair pair, String oldkey, String ipseckeyFilename) {

        Map<String, String> configMap = null;
        try {
            configMap = new HashMap<>();
            configMap.put("username", "nbu");
            configMap.put("password", "nbu");
            if (whichrouter==false) {
                configMap.put("discoveredIPv4address", pair.getRouterIP());
                configMap.put("ipsecneighbour", pair.getNeighbourIP());
                whichrouter = true;
            }
            else
            {
                configMap.put("discoveredIPv4address", pair.getNeighbourIP());
                configMap.put("ipsecneighbour", pair.getRouterIP());
                whichrouter=false;
            }
            configMap.put("prompt", "#");
            configMap.put("enable-password", "nbu");
            //get the IPsec key from file
            configMap.put("oldipseckey", oldkey);
            configMap.put("ipseckey", KeyFromFile(ipseckeyFilename));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return configMap;
    }

    //create file and generate key
    protected void KeyGenerator(String ipseckeyFilename) {

        File file = null;
        try {

            file = new File("E:\\" + ipseckeyFilename);

                PrintWriter writer = new PrintWriter(file);
                writer.print("");
                writer.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Writer writer = null;

        try {
            writer = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file), "utf-8"));
            writer.write(UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        } catch (IOException ex) {
            // report
        } finally {
            try {
                writer.close();
            } catch (Exception ex) {
                System.err.println("Caught Exception: " + ex.getMessage());
            }
        }

    }

    //read key from file
    protected String KeyFromFile(String ipseckeyFilename) throws FileNotFoundException {
        File file = new File("E:\\" + ipseckeyFilename);
        if (file.exists()) {
        String content = new Scanner(file).useDelimiter("\\Z").next();
        return content;
        }
        else
        {
            KeyGenerator(ipseckeyFilename);
            System.out.println("File did not exist, it is now created");
        }
        return "";
    }

    private List<String> firstTimeConfigurationCheck(IPsecPair[] ipsecpair) {

        int reply = JOptionPane.showConfirmDialog(null, "First time configurtion?", "Close?",  JOptionPane.YES_NO_OPTION);

        java.util.List<String> userInput = null;

        if (reply == JOptionPane.YES_OPTION) {

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
        }

        return userInput;
    }

//    private byte[] ipSecKey(int mode) throws Exception {
//        SecretKeySpec key = new SecretKeySpec(keyBytes, "DES");
//        IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);
//// create the cipher with the algorithm you choose
//// see javadoc for Cipher class for more info, e.g.
//        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
//
//        switch (mode) {
//            case Cipher.ENCRYPT_MODE:
//                cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec);
//                byte[] encrypted= new byte[cipher.getOutputSize(input.length)];
//                int enc_len = cipher.update(input, 0, input.length, encrypted, 0);
//                enc_len += cipher.doFinal(encrypted, enc_len);
//                break;
//            case Cipher.DECRYPT_MODE:
//                cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
//                byte[] decrypted = new byte[cipher.getOutputSize(enc_len)];
//                int dec_len = cipher.update(encrypted, 0, enc_len, decrypted, 0);
//                dec_len += cipher.doFinal(decrypted, dec_len);
//                break;
//        }
//
//
//    }
}

