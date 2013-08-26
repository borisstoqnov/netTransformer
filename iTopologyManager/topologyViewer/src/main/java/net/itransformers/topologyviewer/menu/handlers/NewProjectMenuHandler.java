/*
 * iTransformer is an open source tool able to discover and transform
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

package net.itransformers.topologyviewer.menu.handlers;

import net.itransformers.topologyviewer.dialogs.NewProjectDialog;
import net.itransformers.topologyviewer.gui.TopologyManagerFrame;
import net.itransformers.utils.RecursiveCopy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 * Created by IntelliJ IDEA.
 * Date: 12-4-27
 * Time: 23:30
 * To change this template use File | Settings | File Templates.
 */
public class NewProjectMenuHandler implements ActionListener {

    private TopologyManagerFrame frame;

    public NewProjectMenuHandler(TopologyManagerFrame frame) throws HeadlessException {

        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        NewProjectDialog dialog = new NewProjectDialog(frame);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
        File file;
        if (!dialog.isOkPressed()){ return;
        }else if (dialog.getProjectType().equals("bgpPeeringMap")){
            file = new File("bgpPeeringMap.pfl");
        frame.setProjectType("bgpPeeringMap");
         }
         else {
        file = new File("itransformer.pfl");
        frame.setProjectType("iTransformer");
         }
      //  File file = new File("bgpPeeringMap.txt");
        Scanner s = null;
        try {
            try {
                s = new Scanner(file);
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
                JOptionPane.showMessageDialog(this.frame, "Can not find file:"+file.getAbsolutePath());
                return;
            }
            try {
                RecursiveCopy.copyFile(file, dialog.getProjectDir());
                while (s.hasNextLine()) {
                String text = s.nextLine();
                if (text.startsWith("#") || text.trim().equals("")) continue;
                if (System.getProperty("base.dir") == null) System.setProperty("base.dir", ".");
                    String workDirName = System.getProperty("base.dir");
                    File workDir = new File(workDirName);
                File srcDir = new File(workDir,text);
                File destDir = new File(dialog.getProjectDir(), text).getParentFile();
                destDir.mkdirs();
                RecursiveCopy.copyDir(srcDir, destDir);

                }

            } catch (IOException e1) {
                JOptionPane.showMessageDialog(frame, "Unable to create project the reason is:" + e1.getMessage());
                e1.printStackTrace();
            }
            frame.setPath(dialog.getProjectDir());
        } finally {
            if (s != null) s.close();
        }

    }


}
