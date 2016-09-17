/*
 * CreateTerminalHandler.java
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
import java.io.File;
import java.util.Map;

public class CreateTerminalHandler implements RightClickHandler {
    protected ResourceResolver resourceResolver;

    public CreateTerminalHandler(ResourceResolver resourceResolver) {
        this.resourceResolver = resourceResolver;
    }

    public <G> void handleRightClick(JFrame parent, String v,
                                     Map<String, String> graphMLParams,
                                     Map<String, String> rightClickParams,
                                     File projectPath,
                                     File s){
        Map<String,String> connParams;
        try {
            connParams = resourceResolver.getResource(graphMLParams, "ssh");
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
