/*
 * TabbedViewerOpener.java
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

import net.itransformers.topologyviewer.gui.*;
import net.itransformers.topologyviewer.rightclick.RightClickHandler;

import javax.swing.*;
import java.io.File;
import java.util.Map;

public class TabbedViewerOpener implements RightClickHandler {
     public <G> void handleRightClick(JFrame parent, String v,
                                     Map<String, String> graphMLParams,
                                     Map<String, String> rightClickParams,
                                     File projectPath,
                                     File deviceDataXmlFileName) throws Exception {

         TopologyManagerFrame viewer = (TopologyManagerFrame) parent;
         viewer.getCurrentGraphViewerManager().createAndAddViewerPanel();
     }

}
