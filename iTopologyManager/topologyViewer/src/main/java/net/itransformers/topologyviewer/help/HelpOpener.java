/*
 * HelpOpener.java
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

package net.itransformers.topologyviewer.help;/*
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

import javax.swing.*;
import java.io.File;
import java.net.URI;

public class HelpOpener {
    public static void showHelp(JFrame frame, String path){
        File docRoot = new File("iTopologyManager/topologyViewer/src/main/resources/helpdocs");
        File docFile = new File(docRoot, path);
        URI docURI = docFile.toURI();
        if( !java.awt.Desktop.isDesktopSupported() ) {
            JOptionPane.showConfirmDialog(frame, "Can not open default browser. See the documentation in: " +docURI);
        }

        java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
        if( !desktop.isSupported( java.awt.Desktop.Action.BROWSE ) ) {
            JOptionPane.showConfirmDialog(frame, "Can not open default browser. See the documentation in: " +docURI);
        }

        try {
            desktop.browse( docURI );
        }
        catch ( Exception e ) {
            JOptionPane.showConfirmDialog(frame, "Can not open default browser. See the documentation in: " +docURI);
        }
    }
}
