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

package net.itransformers.utils;/*
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class RecursiveCopy {
    public static void copyFile(File srcFile, File destDir) throws IOException {
        File destFile = new File(destDir, srcFile.getName());

        FileInputStream source = null;
        FileOutputStream destination = null;
        try {
            source = new FileInputStream(srcFile);
            destination = new FileOutputStream(destFile);

            FileChannel sourceFileChannel = source.getChannel();
            FileChannel destinationFileChannel = destination.getChannel();

            long size = sourceFileChannel.size();
            sourceFileChannel.transferTo(0, size, destinationFileChannel);
        } finally {
            if (source != null) source.close();
            if (destination != null) destination.close();
        }
    }
    public static void copyDir(File srcDir, File destDir) throws IOException {
        if (!srcDir.isDirectory()) {
            throw new IOException("The source is not a directory: "+srcDir);
        }
        if (!destDir.isDirectory()) {
            throw new IOException("The destination is not a directory: "+srcDir);
        }
        File newDir = new File(destDir, srcDir.getName());
        if (!newDir.mkdir()) {
            throw new IOException("Unable to create directory: "+newDir.getAbsolutePath());
        }
        File[] files = srcDir.listFiles();
        if (files != null){
            for (File file : files) {
                if (file.isFile()){
                    copyFile(file, newDir);
                } else if (file.isDirectory()){
                    copyDir(file, newDir);
                }
            }
        }
    }
}
