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

import net.itransformers.topologyviewer.diff.DiffWizardDialog;
import net.itransformers.topologyviewer.gui.TopologyViewer;

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

    private TopologyViewer frame;

    public DiffMenuHandler(TopologyViewer frame) throws HeadlessException {

        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DiffWizardDialog wizardDialog;
        try {
            wizardDialog = new DiffWizardDialog(frame,frame.getPreferences());
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
            return;
        }
        wizardDialog.setVisible(true);
//        System.out.println(wizardDialog.getDiffPath1());
//        System.out.println(wizardDialog.getDiffPath2());
//        System.out.println(wizardDialog.getDiffPath3());
//        System.out.println(wizardDialog.getResult() == DiffWizardDialog.Result.DONE);
            if (wizardDialog.getResult() == DiffWizardDialog.Result.DONE) {
                frame.setConfigUri(new File(wizardDialog.getDiffConfigPath()));
                frame.doOpen(new File(wizardDialog.getDiffPath3()));
            }
    }
}
