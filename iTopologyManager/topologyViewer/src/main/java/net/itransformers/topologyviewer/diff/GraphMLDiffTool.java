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

package net.itransformers.topologyviewer.diff;

import net.itransformers.utils.XsltTransformer ;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.net.URI;
import java.util.*;


public class GraphMLDiffTool extends SwingWorker<Void, Void> {
    private String dirAPath;
    private String dirBPath;
    private String dirCPath;
    private String ignoredKeysPath;

    public GraphMLDiffTool(String dirAPath, String dirBPath, String dirCPath, String ignoredKeysPath) {
        this.dirAPath = dirAPath;
        this.dirBPath = dirBPath;
        this.dirCPath = dirCPath;
        this.ignoredKeysPath = ignoredKeysPath;
    }

    private static final FilenameFilter FILTER = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(".graphml");
        }
    };

    public static void main(String[] args) throws IOException {
        Map<String, String> params = new HashMap<String, String>();
        String key = null;
        for (String arg : args) {
            if (key == null && arg.startsWith("-")) {
                key = arg;
            } else {
                params.put(key, arg);
                key = null;
            }
        }
        String dirAPath = params.get("-a");
        String dirBPath = params.get("-b");
        String dirCPath = params.get("-c");
        String ignoredKeysPath = params.get("-i");
        GraphMLDiffTool graphMLDiffTool = new GraphMLDiffTool(dirAPath, dirBPath, dirCPath, ignoredKeysPath);
        graphMLDiffTool.execute();
        long time=System.currentTimeMillis();
        graphMLDiffTool.doInBackground();
        System.out.println("Executed in "+(System.currentTimeMillis()-time)+ "ms");
    }

    @Override
    protected Void doInBackground() {
        try {
            File dira = new File(dirAPath);
            File dirb = new File(dirBPath);
            File dirc = new File(dirCPath);
            File ignoredKeysFile = new File(ignoredKeysPath);
            String[] lista = dira.list(FILTER);
            String[] listb = dirb.list(FILTER);
            Set<String> seta = new HashSet<String>(Arrays.asList(lista));
            Set<String> setb = new HashSet<String>(Arrays.asList(listb));
            System.out.println("list a: " + seta);
            System.out.println("list b: " + setb);
            Set<String> newfiles = new HashSet<String>(setb);
            Set<String> deletedfiles = new HashSet<String>(seta);
            Set<String> modifiedfiles = new HashSet<String>(seta);
            newfiles.removeAll(seta);
            deletedfiles.removeAll(setb);
            modifiedfiles.retainAll(setb);
            System.out.println("new : " + newfiles);
            System.out.println("deleted : " + deletedfiles);
            System.out.println("modify : " + modifiedfiles);
            int total = modifiedfiles.size() + newfiles.size() + deletedfiles.size();
            int current = 0;
            if (!dirc.exists()) {
                if (!dirc.mkdir()) {
                    throw new IOException("Can not create dir: " + dirc);
                }
            }
            setProgress(1); // dummy to show progress window
            PrintWriter pw = null;
            try {
                pw = new PrintWriter(new FileOutputStream(new File(dirc, "nodes-file-list.txt")));
                for (String modifiedfile : modifiedfiles) {
                    Thread.sleep(2000);
                    File file1 = new File(dira, modifiedfile);
                    File file2 = new File(dirb, modifiedfile);
                    final File outputFile = new File(dirc, modifiedfile);
                    createDiffGraphml(file1.toURI(), file2.toURI(), outputFile, ignoredKeysFile);
                    pw.println(outputFile.getName());
                    final int progress = 100 * (++current) / total;
                    setProgress(progress);
                    if (isCancelled()) return null;
                }
                for (String newfile : newfiles) {
                    Thread.sleep(2000);
                    File file = new File(dirb, newfile);
                    final File outputFile = new File(dirc, newfile);
                    createNewGraphml(newfile, "ADDED", file.toURI(), outputFile);
                    setProgress(100 * (++current) / total);
                    if (isCancelled()) return null;
                    pw.println(outputFile.getName());
                }
                for (String newfile : deletedfiles) {
                    Thread.sleep(2000);
                    File file = new File(dira, newfile);
                    final File outputFile = new File(dirc, newfile);
                    createNewGraphml(newfile, "REMOVED", file.toURI(), outputFile);
                    setProgress(100 * (++current) / total);
                    if (isCancelled()) return null;
                    pw.println(outputFile.getName());
                }
            } finally {
                if (pw != null) pw.close();
            }


        } catch (Exception rte) {
            rte.printStackTrace();
        }
        return null;
    }

    private static void createDiffGraphml(URI file1, URI file2, File OutputFile, File ignoredKeysFile) throws FileNotFoundException {
        File transformator = new File("iTopologyManager/topologyViewer/conf/xslt/graphml_diff.xslt");
        ByteArrayInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
//    fileInputStream = new FileInputStream(graphml);
        System.out.println("test1");
        String dummyXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<root>\n</root>";
            System.out.println("test1'");
        fileInputStream = new ByteArrayInputStream(dummyXml.getBytes());
        System.out.println("test11"+OutputFile.getAbsolutePath());

        fileOutputStream = new FileOutputStream(OutputFile);
            System.out.println("test11'");
            XsltTransformer transformer = null;

            try {
                 transformer = new XsltTransformer();
            } catch (Error e) {
                e.printStackTrace();
            }

        System.out.println("test111");
        HashMap<String, String> xsltParams;
        xsltParams = new HashMap<String, String>();
        xsltParams.put("file1", file1.toString());
        xsltParams.put("file2", file2.toString());
        xsltParams.put("ignored_keys_file", ignoredKeysFile.toURI().toString());
//        params.put("url",graphml);
        System.out.println("test2");


            System.out.println(fileInputStream.toString()+"|"+transformator.toString()+"|"+fileOutputStream.toString());
            transformer.transformXML(fileInputStream, transformator, fileOutputStream, xsltParams, null);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    private static void createNewGraphml(String newfile, String status, URI file, File OutputFile) throws FileNotFoundException {
        File transformator = new File("topology-viewer/conf/xslt/graphml_add_remove.xslt");
        ByteArrayInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;

//    fileInputStream = new FileInputStream(graphml);
        String dummyXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<root>\n</root>";
        fileInputStream = new ByteArrayInputStream(dummyXml.getBytes());
        fileOutputStream = new FileOutputStream(OutputFile);
        XsltTransformer transformer = new XsltTransformer();
        HashMap<String, String> xsltParams;
        xsltParams = new HashMap<String, String>();
        xsltParams.put("file", file.toString());
        xsltParams.put("status", status);
        xsltParams.put("id", "ID");
        try {
            transformer.transformXML(fileInputStream, transformator, fileOutputStream, xsltParams, null);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
