/*
 * DiffMenuHandler.java
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

package net.itransformers.topologyviewer.menu.handlers.graphFileMenuHandlers;

import net.itransformers.topologyviewer.diff.DiffWizardDialog;
import net.itransformers.topologyviewer.gui.TopologyManagerFrame;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.MalformedURLException;

/**
 * Created by IntelliJ IDEA.
 * Date: 12-4-27
 * Time: 23:30
 * To change this template use File | Settings | File Templates.
 */
public class DiffMenuHandler implements ActionListener {

    private TopologyManagerFrame frame;

    public DiffMenuHandler(TopologyManagerFrame frame) throws HeadlessException {

        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DiffWizardDialog wizardDialog;
        try {
            File baseDir = frame.getPath();

            wizardDialog = new DiffWizardDialog(frame,baseDir);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
            return;
        }
        wizardDialog.setVisible(true);
        System.out.println(frame.getPath());

        System.out.println(wizardDialog.getDiffPath1());
        System.out.println(wizardDialog.getDiffPath2());
        System.out.println(wizardDialog.getDiffPath3());
        System.out.println(wizardDialog.getResult() == DiffWizardDialog.Result.DONE);
            if (wizardDialog.getResult() == DiffWizardDialog.Result.DONE) {
//                frame.setConfigUri(new File(wizardDialog.getDiffConfigPath()));
                //TODO remove hardcore
                frame.doOpenGraph(new File(wizardDialog.getDiffPath3()+File.separator+"graphml-undirected"+File.separator + "network.graphml"));
            }
    }
}
