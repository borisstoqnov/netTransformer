/*
 * netTransformer is an open source tool able to discover IP networks
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
import java.io.File;
import java.net.URL;
import java.util.Map;

public class CreateTerminalHandler implements RightClickHandler {
    public <G> void handleRightClick(JFrame parent, String v,
                                     Map<String, String> graphMLParams,
                                     Map<String, String> rightClickParams,
                                     File projectPath,
                                     File s){
        Map<String,String> connParams;
        try {
            connParams = ResourceResolver.getResource(graphMLParams, new File(projectPath, rightClickParams.get("resource")));
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Can not find resource : "+e.getMessage());
            return;
        }
        handleConnParams(parent, connParams);
    }

    protected void handleConnParams(JFrame parent, Map<String, String> connParams) {
        Terminal executor = new Terminal();
        if (connParams.get("protocol").equals("ssh")){
            executor.openSession(connParams);
        } else {
            JOptionPane.showMessageDialog(parent, "Protocol is not supported : " + connParams.get("protocol"));
        }
    }

}
