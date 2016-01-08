package net.itransformers.idiscover.v2.core.listeners.node;

import net.itransformers.idiscover.v2.core.NodeDiscoveryListener;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.List;
import java.util.Map;

/**
 * Created by niau on 4/25/15.
 */
public class ConnectionDetailsListener implements NodeDiscoveryListener {
    String projectPath;
    String connectionDetailsPath;
    static Logger logger = Logger.getLogger(ConnectionDetailsListener.class);


    @Override
    public void nodeDiscovered(NodeDiscoveryResult discoveryResult) {
//        File connectionDetailsFile = new File(projectPath, connectionDetailsPath);
//
//
//        PrintWriter out = null;
//        try {
//            out = new PrintWriter(new BufferedWriter(new FileWriter(connectionDetailsFile, true)));
//
//
//            List<ConnectionDetails> neighboursConnDetails = discoveryResult.getNeighboursConnectionDetails();
//            for (ConnectionDetails neighboursConnDetail : neighboursConnDetails) {
//                StringBuffer connDetails = new StringBuffer();
//                String connectionDetailsType = neighboursConnDetail.getConnectionType();
//
//
//                Map<String, String> connectionDetailsParams = neighboursConnDetail.getParams();
//                String connectionDetailsName = connectionDetailsParams.get("deviceName");
//                connDetails.append("name=" + connectionDetailsName + ",type=" + connectionDetailsType+":");
//
//                int size =0;
//                for ( String s : connectionDetailsParams.keySet()) {
//                   if (connectionDetailsParams.size()!=size)
//                        connDetails.append(s+"="+connectionDetailsParams.get(s)+",");
//                    else
//                       connDetails.append(s+"="+connectionDetailsParams.get(s));
//                   size++;
//
//                }
//
//                out.println(connDetails.toString());
//
//
//            }
//
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//
//            out.close();
//        }
    }

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public String getConnectionDetailsPath() {
        return connectionDetailsPath;
    }

    public void setConnectionDetailsPath(String connectionDetailsPath) {
        this.connectionDetailsPath = connectionDetailsPath;
    }
}
