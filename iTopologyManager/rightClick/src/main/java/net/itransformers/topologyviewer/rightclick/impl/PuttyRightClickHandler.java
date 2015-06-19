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
import net.itransformers.topologyviewer.rightclick.impl.putty.Putty;

import javax.swing.*;
import java.io.File;
import java.net.URL;
import java.util.Map;

public class PuttyRightClickHandler implements RightClickHandler {
    public <G> void handleRightClick(JFrame parent, String v,
                                     Map<String, String> graphMLParams,
                                     Map<String, String> rightClickParams,
                                     File projectPath,
                                     java.io.File s){
        Map<String,String> connParams;
        try {
            connParams = ResourceResolver.getResource(graphMLParams, new File(projectPath, rightClickParams.get("resource")));
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parent, "Can not find resource : "+e.getMessage());
            return;
        }
        String session = rightClickParams.get("saved_session");
//        connParams.put("discoveredIPv4Address",graphMLParams.get("discoveredIPv4Address"));
        connParams.put("session", session);

        handleConnParams(parent, connParams, rightClickParams);
    }

    protected void handleConnParams(JFrame parent, Map<String, String> connParams, Map<String, String> rightClickParams) {
        Putty putty = new Putty(rightClickParams);
        putty.openSession(connParams);
    }

}
