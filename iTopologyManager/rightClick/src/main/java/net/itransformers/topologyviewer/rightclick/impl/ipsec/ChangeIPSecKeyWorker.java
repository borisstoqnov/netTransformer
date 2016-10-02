package net.itransformers.topologyviewer.rightclick.impl.ipsec;

//import net.itransformers.resourcemanager.config.IPsecPair;

import net.itransformers.topologyviewer.fulfilmentfactory.impl.CLIInterface;
import net.itransformers.topologyviewer.fulfilmentfactory.impl.TelnetCLIInterface;
import net.itransformers.topologyviewer.fulfilmentfactory.impl.TestFulfilmentImpl;
import net.itransformers.topologyviewer.parameterfactory.ParameterFactoryBuilder;

import javax.swing.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Bobi on 9/23/2016.
 */
public class ChangeIPSecKeyWorker extends SwingWorker<String, Void> {

    boolean whichrouter=false;
    ProgressMonitor progressMonitor;
    IPsecPair[] ipsecpair;
    List<String> userInput;

    public ChangeIPSecKeyWorker(IPsecPair[] ipsecpair, ProgressMonitor progressMonitor, List<String> userInput) {
        this.ipsecpair = ipsecpair;
        this.progressMonitor = progressMonitor;
        this.userInput = userInput;
    }

    @Override
    protected String doInBackground() throws Exception {

        int pairCounter =0;

        for(IPsecPair pair : ipsecpair){

            if(pair != null){
                pairCounter++;
            }
        }
        //If we have already done this continue
        if(userInput != null && userInput.size() != pairCounter) {
            return "Something happened";
        }

        String ipseckeyFilename = "";
        String oldkey = "";
        int counter=0;
        String message="";

        int progress = 0;
        int progressStep = 100 / getLenghtWithoutNulls(ipsecpair);
        setProgress(1);

        for (IPsecPair pair : ipsecpair) {

            if (pair!=null) {
                ipseckeyFilename = pair.getRouterName() + pair.getRouterIP() + pair.getNeighbourName() + pair.getNeighbourIP();

                //Generate key and file
                if (userInput != null) {
                    oldkey = userInput.get(counter);
                } else {
                    oldkey = KeyFromFile(ipseckeyFilename);
                }
                KeyGenerator(ipseckeyFilename);

                Logger.getAnonymousLogger().log(Level.INFO, "Initialising cli" + Thread.currentThread().getName());
                CLIInterface cli;
                cli = new TelnetCLIInterface(pair.getRouterIP(), "nbu", "nbu", "#", 1000, Logger.getAnonymousLogger());
                //Change IP sec key for first router
                Map<String, String> paramsfirst = generateConfigMap(pair, oldkey, ipseckeyFilename);

                progress += progressStep;
                setProgress(progress);
                System.out.println("progress is " + progress + " step is " + progressStep);
                progressMonitor.setNote("working on pair: " + pair.getNeighbourName() + ", " + pair.getRouterName() + ".");
                TestFulfilmentImpl telnetTorouter = new TestFulfilmentImpl(cli);

                cli.open();
                telnetTorouter.execute("iTopologyManager/fulfilmentFactory/conf/templ/ChangeIPsecKey.templ", paramsfirst);
                cli.close();

                Map<String, String> paramssecond = generateConfigMap(pair, oldkey, ipseckeyFilename);
                //Change IP sec key for second router
                cli = new TelnetCLIInterface(pair.getNeighbourIP(), "nbu", "nbu", "#", 1000, Logger.getAnonymousLogger());

                cli.open();
                telnetTorouter = new TestFulfilmentImpl(cli);
                telnetTorouter.execute("iTopologyManager/fulfilmentFactory/conf/templ/ChangeIPsecKey.templ", paramssecond);
                cli.close();


                counter++;

            }
            else if (counter > 0)
            {
                message = "Key change completed";
            }
            else {
                message = "Pair is null";
            }
        }

        if (isCancelled()) return null;
        return message;
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

    private int getLenghtWithoutNulls(IPsecPair[] ipsecpair){
        int result = 0;

        for (IPsecPair p : ipsecpair ) {

            if(p != null ){

                result++;
            }
        }
        return result;
    }
}
