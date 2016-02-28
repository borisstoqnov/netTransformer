/*
 * ASNRightClickOpener.java
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
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.Map;

public class ASNRightClickOpener implements RightClickHandler {
    public <G> void handleRightClick(JFrame parent, String v,
                                     Map<String, String> graphMLParams,
                                     Map<String, String> rightClickParams,
                                     File projectPath,
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
