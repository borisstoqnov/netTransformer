/*
 * ConnectionDetailsListener.java
 *
 * This work is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * This work is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * Copyright (c) 2010-2016 iTransformers Labs. All rights reserved.
 */

package net.itransformers.idiscover.v2.core.listeners.node;

import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetails;
import net.itransformers.idiscover.api.NodeDiscoveryListener;
import net.itransformers.idiscover.api.NodeDiscoveryResult;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.Map;
import java.util.Set;

/**
 * Created by niau on 4/25/15.
 */
public class ConnectionDetailsListener implements NodeDiscoveryListener {
    String projectPath;
    String connectionDetailsPath;
    static Logger logger = Logger.getLogger(ConnectionDetailsListener.class);


    @Override
    public void nodeDiscovered(NodeDiscoveryResult discoveryResult) {
        File connectionDetailsFile = new File(projectPath, connectionDetailsPath);


        PrintWriter out = null;
        try {
            out = new PrintWriter(new BufferedWriter(new FileWriter(connectionDetailsFile, true)));


            Set<ConnectionDetails> neighboursConnDetails = discoveryResult.getNeighboursConnectionDetails();
            for (ConnectionDetails neighboursConnDetail : neighboursConnDetails) {
                StringBuffer connDetails = new StringBuffer();
                String connectionDetailsType = neighboursConnDetail.getConnectionType();


                Map<String, String> connectionDetailsParams = neighboursConnDetail.getParams();
                String connectionDetailsName = connectionDetailsParams.get("deviceName");
                connDetails.append("name=" + connectionDetailsName + ",type=" + connectionDetailsType+":");

                int size =0;
                for ( String s : connectionDetailsParams.keySet()) {
                   if (connectionDetailsParams.size()!=size)
                        connDetails.append(s+"="+connectionDetailsParams.get(s)+",");
                    else
                       connDetails.append(s+"="+connectionDetailsParams.get(s));
                   size++;

                }

                out.println(connDetails.toString());


            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {

            out.close();
        }
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
