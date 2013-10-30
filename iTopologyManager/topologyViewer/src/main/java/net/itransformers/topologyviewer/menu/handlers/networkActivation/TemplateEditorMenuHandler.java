/*
 * netTransformer is an open source tool able to discover and transform
 *  IP network infrastructures.
 *  Copyright (C) 2012  http://itransformers.net
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.itransformers.topologyviewer.menu.handlers.networkActivation;

import net.itransformers.topologyviewer.gui.TopologyManagerFrame;
import net.itransformers.utils.JEditorPane;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;


public class TemplateEditorMenuHandler implements ActionListener {

    private TopologyManagerFrame frame;
    String pathToResource;
    String extension;
    public TemplateEditorMenuHandler(TopologyManagerFrame frame, String path, String extension) throws HeadlessException {

        this.frame = frame;
        this.pathToResource = path;
        this.extension = extension;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser(frame.getPath()+ File.separator+pathToResource);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                return  (f.isFile() && f.getName().endsWith(extension) || f.isDirectory());
            }

            @Override
            public String getDescription() {
                return "(netTransformer Files) *"+extension;
            }
        });

        chooser.setMultiSelectionEnabled(false);
        int result = chooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            chooser.getSelectedFile().getAbsolutePath();

        JEditorPane templateEditor = new net.itransformers.utils.JEditorPane(chooser.getSelectedFile().getAbsolutePath(),chooser.getSelectedFile().getParent(),extension);
        try {
            templateEditor.init();
        } catch (IOException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (BadLocationException e1) {
            e1.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        }

    }


}
