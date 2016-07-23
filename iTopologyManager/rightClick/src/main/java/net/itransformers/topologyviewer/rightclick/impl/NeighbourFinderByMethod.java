package net.itransformers.topologyviewer.rightclick.impl;


import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import net.itransformers.resourcemanager.config.IPsecPair;
import net.itransformers.topologyviewer.gui.GraphViewerPanel;
import net.itransformers.topologyviewer.gui.MyVisualizationViewer;
import net.itransformers.topologyviewer.gui.TopologyManagerFrame;
import net.itransformers.topologyviewer.rightclick.RightClickHandler;


import javax.swing.*;
import java.awt.*;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;


public class NeighbourFinderByMethod implements RightClickHandler {

    protected void performIPSecAction(IPsecPair[] ipsecpair) throws IOException {
        JFrame frame = new JFrame("IPsec Neighbours");
        frame.setSize(300, 200);
        frame.getContentPane().setLayout(new BorderLayout());
        JTextPane text = new JTextPane();
        text.setEditable(false);
        text.setContentType("text/html");
        StringBuilder entiremessage = new StringBuilder();
        for(int i = 0; i < ipsecpair.length; i++)
        {
            if (ipsecpair[i] != null) {
                String message = ipsecpair[i].toString();
                entiremessage.append(message + "\n");
            }
        }
        JScrollPane scrollPane = new JScrollPane(text);
        frame.getContentPane().add("Center", scrollPane);
        text.setText(entiremessage.toString());
        frame.setVisible(true);
    }



    @Override
    public void handleRightClick(JFrame parent, String v, Map graphMLParams, Map rightClickParams, File projectPath, File s)
            throws Exception, IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {

        TopologyManagerFrame viewer = (TopologyManagerFrame) parent;
        final GraphViewerPanel viewerPanel = (GraphViewerPanel) viewer.getTabbedPane().getSelectedComponent();
        final MyVisualizationViewer vv = (MyVisualizationViewer) viewerPanel.getVisualizationViewer();
        Graph currentGraph = viewerPanel.getCurrentGraph();
        //Keep count of the neighbours

        String method = (String) rightClickParams.get("Discovery Method");
        int i = 0;
        IPsecPair[] ipsecpair = new IPsecPair[100];

        HashMap<String, String> ipSecNeighbours = new HashMap<>();

        Collection<String> outedges = currentGraph.getEdges();

        for (String edge : outedges) {

            HashMap<String, String> edgeParamsMetadata = (HashMap<String, String>) viewerPanel.getEdgeParams(edge);
            String discoveryMethod = edgeParamsMetadata.get("Discovery Method");

            if (discoveryMethod != null && discoveryMethod.contains(method)) {
                Pair pair = viewerPanel.getEdgeVertexes(edge);

                String first = pair.getFirst().toString();
                String second = pair.getSecond().toString();
                System.out.printf("Second is " + second + " is an IPSEC neighbour");
                HashMap<String, String> neighbourMetaData = (HashMap<String, String>) viewerPanel.getVertexParams(second);
                HashMap<String, String> thisrouterMetaData = (HashMap<String, String>) viewerPanel.getVertexParams(first);
                String neighbourDeviceType = neighbourMetaData.get("deviceType");
                String neighbourDeviceIPAddress = neighbourMetaData.get("discoveredIPv4Address");
                String thisDeviceType = thisrouterMetaData.get("deviceType");

                String thisDeviceIPAddress = thisrouterMetaData.get("discoveredIPv4Address");
                System.out.println(second + " has an IP address of " + neighbourDeviceIPAddress);

                if ((neighbourDeviceType != null && neighbourDeviceType.equals("Subnet")) && ((thisDeviceType != null && thisDeviceType.equals("Subnet")))) {
                    System.out.println("Neighbour " + second + " is a subnet");
                } else {
                    System.out.println("Neighbour " + second + " is a not a subnet");

                    IPsecPair p = new IPsecPair(first,thisDeviceIPAddress,second,neighbourDeviceIPAddress);
                    ipsecpair[i] = p;
                    i++;
                }
            }

        }
        //Show a message only if more than 1 neighbours are found
        if (i > 0) {
            performIPSecAction(ipsecpair);
        }


    }


}
