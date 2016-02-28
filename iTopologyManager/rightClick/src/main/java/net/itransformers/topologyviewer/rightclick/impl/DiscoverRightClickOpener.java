/*
 * DiscoverRightClickOpener.java
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

package net.itransformers.topologyviewer.rightclick.impl;

import net.itransformers.topologyviewer.rightclick.RightClickHandler;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;

public class DiscoverRightClickOpener implements RightClickHandler {
    public <G> void handleRightClick(JFrame parent, String v,
                                     Map<String, String> graphMLParams,
                                     Map<String, String> rightClickParams,
                                     File projectPath,
                                     File deviceDataXmlFileName) throws Exception {

        String url = null;
        url = rightClickParams.get("url");
        String fullUrl = null;
        String address=null;
        address = graphMLParams.get("discoveredIPv4Address");
        String port = null;
        port= rightClickParams.get("port");
        String protocol=null;
        protocol= rightClickParams.get("protocol");
        String mode=null;
        mode= rightClickParams.get("mode");
        String action = null;
        action =  rightClickParams.get("action");
        String snmpCommunity1 = null;
        snmpCommunity1 =  rightClickParams.get("snmp-community1");
        String snmpCommunity2 = null;
        snmpCommunity2 =  rightClickParams.get("snmp-community2");
        String discoveryManager = null;
        discoveryManager = rightClickParams.get("discoveryManager");
        String mibs = rightClickParams.get("mibs");

        String FullAddress = null;
        if(action==null || action.isEmpty()){
            JOptionPane.showMessageDialog(parent, "No action specified:" + url, "Error", JOptionPane.ERROR_MESSAGE);
        } else if(action.equals("init_and_start")) {
            FullAddress=protocol+"://"+url+action+"&-h="+address+"&-m="+mibs+"&-c="+snmpCommunity1+"&-c2="+snmpCommunity2+
                    "&-d="+mode+"&-f="+discoveryManager;
            System.out.println(FullAddress);
        }
        else if(action.equals("status")) {
            FullAddress=protocol+"://"+url+action;
            System.out.println(FullAddress);
        }  else if(action.equals("stop")) {
            FullAddress=protocol+"://"+url+action;
            System.out.println(FullAddress);
        }   else if(action.equals("pause")) {
            FullAddress=protocol+"://"+url+action;
            System.out.println(FullAddress);
        }   else if(action.equals("resume")) {
            FullAddress=protocol+"://"+url+action;
            System.out.println(FullAddress);
        }

//                url = protocol+"://"+address+":"+port;
        URL urlToOpen=new URL(FullAddress);
        try {
            URLConnection yc =urlToOpen.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            String inputLine;
            StringBuilder sb = new StringBuilder();
            while ((inputLine = in.readLine()) != null){

                sb.append(inputLine);
            }
            in.close();

            JOptionPane.showMessageDialog(parent,  sb.toString()+JOptionPane.INFORMATION_MESSAGE);


        } catch (Exception e1) {
            e1.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Can not open url:" + urlToOpen, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}


