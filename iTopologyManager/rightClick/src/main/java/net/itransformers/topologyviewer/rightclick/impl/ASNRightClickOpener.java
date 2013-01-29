/*
 * iTransformer is an open source tool able to discover IP networks
 * and to perform dynamic data data population into a xml based inventory system.
 * Copyright (C) 2010  http://itransformers.net
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.itransformers.topologyviewer.rightclick.impl;

import net.itransformers.topologyviewer.rightclick.RightClickHandler;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.Map;

public class ASNRightClickOpener implements RightClickHandler {
    public <G> void handleRightClick(JFrame parent, String v,
                                     Map<String, String> graphMLParams,
                                     Map<String, String> rightClickParams,
                                     File deviceDataXmlFileName) throws Exception {

        String fullAddress = null;
        fullAddress = rightClickParams.get("url");
        String url = null;
        String org = rightClickParams.get("org");
        String ASN = graphMLParams.get("ASN");


        System.out.println("ORG: " + org + "ASN: " + ASN);
        if (org.equals("ripe")){
            url = fullAddress+"AS"+ASN;
        }else if (org.equals("arin")){
            url = fullAddress+ASN;
        }
        try {
            Desktop.getDesktop().browse(new URL(url).toURI());
        } catch (Exception e1) {
            e1.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Can not open url:" + url, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
