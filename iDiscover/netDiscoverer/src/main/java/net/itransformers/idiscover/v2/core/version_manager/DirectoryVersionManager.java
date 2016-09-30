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

package net.itransformers.idiscover.v2.core.version_manager;

import net.itransformers.idiscover.api.VersionManager;

import java.io.File;

/**
 * Created by niau on 2/29/16.
 */
public class DirectoryVersionManager implements VersionManager{

    private String networkFolderName;
    private String versionLabel;
    private String projectPath;


    public DirectoryVersionManager(String networkFolderName, String versionLabel, String projectPath) {
        this.networkFolderName = networkFolderName;
        this.versionLabel = versionLabel;

        this.projectPath = projectPath;
    }

    @Override
    public String createVersion() {
        if (projectPath == null){
            throw new IllegalArgumentException("Parameter ProjectPath is not specified");
        }
        File file = new File(projectPath, networkFolderName);
        if (!file.exists() || !file.isDirectory()){
            throw new IllegalArgumentException("The network folder is not a valid directory: "+file);
        }
        String[] fileList = file.list();
        int max = 0;
        for (String fName : fileList) {
            if (fName.matches(versionLabel + "\\d+")) {
                int curr = Integer.parseInt(fName.substring(versionLabel.length()));
                if (max < curr) max = curr;
            }
        }
        return versionLabel + (max + 1);
    }

    @Override
    public void deleteVersion(String version) {
        if (version.contains(".")){
            throw new IllegalArgumentException("version can not contain '.'");
        }
        File file = new File(projectPath, version);
        if (!file.delete()){
            throw new RuntimeException("Unable to delete the version dir");
        }
    }
}
