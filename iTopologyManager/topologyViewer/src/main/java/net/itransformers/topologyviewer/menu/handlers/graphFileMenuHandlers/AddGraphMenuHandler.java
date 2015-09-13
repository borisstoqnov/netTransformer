package net.itransformers.topologyviewer.menu.handlers.graphFileMenuHandlers;

import net.itransformers.topologyviewer.gui.TopologyManagerFrame;
import org.jfree.ui.ExtensionFileFilter;
import org.springframework.util.FileCopyUtils;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * Created by niau on 9/13/15.
 */
public class AddGraphMenuHandler implements ActionListener {

    private TopologyManagerFrame frame;
    public static final String VERSION_LABEL = "version";


    public AddGraphMenuHandler(TopologyManagerFrame frame) {
        this.frame = frame;
    }





    @Override
    public void actionPerformed(ActionEvent e) {
        File projectPath = frame.getPath();

        if (projectPath == null) {
            JOptionPane.showMessageDialog(frame, "Can not open graph before project has been opened.");
            return;
        }
        final File networkPath = new File(projectPath+File.separator+"network");
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select a graphml file to load into your project");
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        chooser.setFileFilter(new ExtensionFileFilter("Graphml File chooser", "graphml"));

        chooser.setMultiSelectionEnabled(false);
        int result = chooser.showOpenDialog(frame);
        if (result == JFileChooser.APPROVE_OPTION) {
            File fileResult = chooser.getSelectedFile();



            if(fileResult.exists()){
                System.out.println(fileResult);
                File version = autolabel(projectPath.getAbsolutePath());

                File graphmlDir =  new File (version,"graphml-undirected");
                graphmlDir.mkdir();

                try {
                    File outputFile = new File(graphmlDir,"network.graphml");
                    FileCopyUtils.copy(fileResult,outputFile);
                    JOptionPane.showMessageDialog(frame, "Graph file successfully imported in "+ version.getName() + " of your current project!");

                    frame.doOpenGraph(outputFile);

                } catch (IOException e1) {
                    e1.printStackTrace();
                }

            }
        }

    }

    public static File autolabel(String projectPath){
        File networkPath = new File(projectPath,"network");

        if (!networkPath.exists()) {
            networkPath.mkdir();

            File labelDir = new File(networkPath,"version1");
            labelDir.mkdir();
            return labelDir;
        }
        String[] fileList = new File(projectPath,"network").list();
        int max = 0;
        for (String fName : fileList) {
            if (fName.matches(VERSION_LABEL+"\\d+")){
                int curr = Integer.parseInt(fName.substring(VERSION_LABEL.length()));
                if (max < curr ) max = curr;
            }
        }
        File labelDir = new File(networkPath,VERSION_LABEL +(max+1));
        labelDir.mkdir();
        return labelDir;
    }

}

