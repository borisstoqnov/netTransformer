/*
 * FolderTransformator.java
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

package net.itransformers.idiscover.util;

import net.itransformers.utils.XsltTransformer;

import javax.xml.bind.JAXBException;
import java.io.*;
 @Deprecated
public class FolderTransformator {
    public static void main(String[] args) throws IOException, JAXBException {
        File in_dir = null;
        File out_dir = null;
        File transformator = null;
        if (args.length == 3) {
            in_dir = new File(args[0]);
            out_dir = new File(args[1]);
            if (!out_dir.exists()) {
                out_dir.mkdir();
            } else if (out_dir.isFile()) {
                System.out.println("there is an existing file with name equal to output dir name.");
            }
            transformator = new File(args[2]);
        } else {
            System.out.println("Please provide input and output folder names!!!");
        }

        File[] files = in_dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return (name.endsWith(".graphml"));
                // return (name.startsWith("device") && name.endsWith(".xml"));
            }
        });
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new File(out_dir + File.separator + "nodes-file-list.txt"));
            for (File fileName : files) {
                System.out.println(fileName.getName());
                XsltTransformer transformer = new XsltTransformer();
                final File fileName1 =  new File(out_dir + File.separator +  fileName.getName() + ".graphml");
                FileInputStream fileInputStream = null;
                FileOutputStream fileOutputStream = null;
                try {
                    fileInputStream = new FileInputStream(fileName);
                    fileOutputStream = new FileOutputStream(fileName1);
                    transformer.transformXML(fileInputStream, transformator, fileOutputStream);
                    fileOutputStream.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (fileInputStream != null) fileInputStream.close();
                    if (fileOutputStream != null) fileOutputStream.close();
                }

                writer.append(String.valueOf(fileName.getName())+".graphml").append("\n");
                writer.flush();
            }
        } finally {
            if (writer != null) writer.close();
        }

    }
    private static boolean makeOutputDirIfNotExist(String dir) {
        if (dir == null) {
            dir = "tmp";
            System.out.println("-d option for output dir is not specified. Default 'tmp' dir is used");
        }
        File fileDir = new File(dir);
        if (!fileDir.exists()){
            fileDir.mkdir();
        } else if (fileDir.isFile()){
            System.out.println("there is an existing file with name equal to output dir name. " +
                    "Aborting discovery. Please remove this file or change the output dir name.");
            return true;
        }
        return false;
    }



}

