
package net.itransformers.topologyviewer.rightclick.impl;

import net.itransformers.resourcemanager.config.IPsecPair;
import net.itransformers.topologyviewer.fulfilmentfactory.impl.CLIInterface;
import net.itransformers.topologyviewer.fulfilmentfactory.impl.TelnetCLIInterface;
import net.itransformers.topologyviewer.fulfilmentfactory.impl.TestFulfilmentImpl;


import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.logging.Logger;


/**
 * Created by Bobi on 5/29/2016.
 */

public class ChangeIPSecKeyHandler extends NeighbourFinderByMethod {
    boolean whichrouter=false;
    //We need the neighbours before connecting to them
    @Override
    protected void performIPSecAction(IPsecPair[] ipsecpair) throws IOException {

        String ipseckeyFilename = "";
        String oldkey = "";
        int counter=0;
        String message="";
        for (IPsecPair pair : ipsecpair) {

            if (pair!=null) {
                ipseckeyFilename = pair.getRouterName() + pair.getRouterIP() + pair.getNeighbourName() + pair.getNeighbourIP();
                //empty the counter and pair for the next iteration

                //Generate key and file
                oldkey = KeyFromFile(ipseckeyFilename);
                KeyGenerator(ipseckeyFilename);


                CLIInterface cli;
                cli = new TelnetCLIInterface(pair.getRouterIP(), "nbu", "nbu", "#", 1000, Logger.getAnonymousLogger());
                //Change IP sec shit for first router
                Map<String, String> paramsforfirst = generateConfigMap(pair, oldkey, ipseckeyFilename);

                cli.open();
                TestFulfilmentImpl telnetTorouter = new TestFulfilmentImpl(cli);
                telnetTorouter.execute("E:\\iTransformer\\netTransformer\\iTopologyManager\\fulfilmentFactory\\conf\\templ\\ChangeIPsecKey.templ", paramsforfirst);
                cli.close();

                //Change IP sec shit for second router
                cli = new TelnetCLIInterface(pair.getNeighbourIP(), "nbu", "nbu", "#", 1000, Logger.getAnonymousLogger());
                Map<String, String> paramsforsecond = generateConfigMap(pair, oldkey, ipseckeyFilename);

                cli.open();
                telnetTorouter = new TestFulfilmentImpl(cli);
                telnetTorouter.execute("E:\\iTransformer\\netTransformer\\iTopologyManager\\fulfilmentFactory\\conf\\templ\\ChangeIPsecKey.templ", paramsforsecond);
                cli.close();
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
    }


    private void printMessageToScreen(String message)
    {
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

