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

package net.itransformers.topologyviewer.menu.handlers;

import net.itransformers.topologyviewer.gui.PreferencesKeys;
import net.itransformers.topologyviewer.gui.TopologyManagerFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * Date: 12-4-27
 * Time: 23:30
 * To change this template use File | Settings | File Templates.
 */
public class ConfigMenuHandler implements ActionListener {

    private TopologyManagerFrame frame;

    public ConfigMenuHandler(TopologyManagerFrame frame) throws HeadlessException {

        this.frame = frame;
    }

    class XmlFileFilter extends javax.swing.filechooser.FileFilter {
        @Override
        public boolean accept(File f) { return f.isFile() && f.getName().endsWith(".xml") || f.isDirectory(); }
        @Override
        public String getDescription() { return "*.xml"; }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
//        JFileChooser chooser = null;
//        File configURL = frame.getConfigURI();
//        File configFilePath = null;
//        if (configURL != null) {
//            configFilePath = configURL.getParentFile();
//        }
//        chooser = new JFileChooser(configFilePath);
//        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
//        chooser.setMultiSelectionEnabled(false);
//        chooser.setFileFilter(new XmlFileFilter());
//        int result = chooser.showOpenDialog(frame);
//        if (result == JFileChooser.APPROVE_OPTION){
//
//            File configURI = chooser.getSelectedFile();
//            frame.setConfigUri(configURI);
//            frame.getPreferences().setProperty(PreferencesKeys.CONFIG_FILE_NAME.name(), frame.getConfigURI().toString());
//            try {
//                frame.getPreferences().store(new FileOutputStream(TopologyManagerFrame.VIEWER_PREFERENCES_PROPERTIES),"");
//            } catch (IOException e1) {
//                e1.printStackTrace();
//                JOptionPane.showMessageDialog(frame,"Can not store preferences. Error: "+e1.getMessage(),"Error", JOptionPane.ERROR_MESSAGE);
//            }
//        }
    }
}
