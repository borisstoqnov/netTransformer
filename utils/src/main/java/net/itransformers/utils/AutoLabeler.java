/*
 * AutoLabeler.java
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

package net.itransformers.utils;

import java.io.File;

/**
 * Created by niau on 2/29/16.
 */
public class AutoLabeler {

    private static String projectPath;
    private static String networkFolderName;
    private static String versionLabel;


    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public AutoLabeler(String projectPath, String networkFolderName, String versionLabel) {
        this.projectPath = projectPath;
        this.networkFolderName = networkFolderName;
        this.versionLabel = versionLabel;

    }

    public String getProjectPath() {
        return projectPath;
    }

    public String getNetworkFolderName() {
        return networkFolderName;
    }

    public void setNetworkFolderName(String networkFolderName) {
        this.networkFolderName = networkFolderName;
    }

    public static String autolabel() {

        String[] fileList = new File(projectPath, networkFolderName).list();
        int max = 0;
        for (String fName : fileList) {
            if (fName.matches(versionLabel + "\\d+")) {
                int curr = Integer.parseInt(fName.substring(versionLabel.length()));
                if (max < curr) max = curr;
            }
        }
        return networkFolderName + File.separator + versionLabel + (max + 1);
    }
}
