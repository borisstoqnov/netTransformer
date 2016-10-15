package net.itransformers.topologyviewer.rightclick.impl.ipsec;


import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import net.itransformers.resourcemanager.ResourceManager;
import net.itransformers.resourcemanager.config.ResourceType;
import net.itransformers.topologyviewer.gui.GraphViewerPanel;
import net.itransformers.topologyviewer.gui.MyVisualizationViewer;
import net.itransformers.topologyviewer.gui.TopologyManagerFrame;
import net.itransformers.topologyviewer.rightclick.RightClickHandler;
import net.itransformers.topologyviewer.rightclick.impl.ResourceResolver;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class NeighbourFinderByMethod extends JPanel implements RightClickHandler, PropertyChangeListener {

    protected ResourceManager resourceManager;
    protected ResourceResolver resourceResolver;

    public NeighbourFinderByMethod(ResourceManager resourceManager, ResourceResolver resourceResolver) {
        this.resourceManager = resourceManager;
        this.resourceResolver = resourceResolver;
    }

    protected String performIPSecAction(IPsecPair[] ipsecpair) throws IOException, InterruptedException {


        StringBuilder entiremessage = new StringBuilder();
        for(int i = 0; i < ipsecpair.length; i++)
        {
            if (ipsecpair[i] != null) {
                String message = ipsecpair[i].toString();
                entiremessage.append(message + "\n");
            }
        }
        return entiremessage.toString();
    }

    protected void showmessage (String message){
        if(message == null || message.equals("")) {
            return;
        }
        JFrame frame = new JFrame("IPsec Neighbours");
        JTextArea text;
        frame.setSize(300, 200);
        frame.getContentPane().setLayout(new BorderLayout());
        text = new JTextArea();
        text.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(text);
        frame.getContentPane().add("Center", scrollPane);
        text.setText(message.toString());
        frame.setVisible(true);
    }


    public void handleRightClick(JFrame parent, String v, Map graphMLParams, Map rightClickParams, File projectPath, File s)
            throws Exception, IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {

        TopologyManagerFrame viewer = (TopologyManagerFrame) parent;
        final GraphViewerPanel viewerPanel = (GraphViewerPanel) viewer.getTabbedPane().getSelectedComponent();
        final MyVisualizationViewer vv = (MyVisualizationViewer) viewerPanel.getVisualizationViewer();
        Graph currentGraph = viewerPanel.getCurrentGraph();

        String method = (String) rightClickParams.get("Discovery Method");

        int i = 0;
        IPsecPair[] ipsecpair = new IPsecPair[100];

        Collection<String> outedges = currentGraph.getEdges();

        for (String edge : outedges) {

            HashMap<String, String> edgeParamsMetadata = (HashMap<String, String>) viewerPanel.getEdgeParams(edge);
            String discoveryMethod = edgeParamsMetadata.get("discoveryMethod");

            if (discoveryMethod != null && discoveryMethod.contains(method)) {
                Pair pair = viewerPanel.getEdgeVertexes(edge);

                String first = pair.getFirst().toString();
                System.out.printf("First is " + first + " is an IPSEC neighbour");
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
                } else if ((v == first || v == second) && (second !=null || first !=null)) {

                    //Get the password based on deviceType for now
                    ResourceType resource = resourceManager.findFirstResourceBy(graphMLParams);
                    Map <String,String> connectionparameters = resourceResolver.getConnectionParams(resource, graphMLParams, "telnet");
                    String pass = connectionparameters.get("password");
                    String enablepass = connectionparameters.get("enable-password");
                    String username = connectionparameters.get("username");

                    IPsecPair p = new IPsecPair(first,thisDeviceIPAddress,second,neighbourDeviceIPAddress,username,pass,enablepass);
                    ipsecpair[i] = p;
                    i++;
                }
            }

        }
        //Show a message only if more than 1 neighbours are found
        if (i > 0) {
            showmessage(performIPSecAction(ipsecpair));
        }
        else {
            showmessage("No IPsec neighbours found");
        }
    }

    //TODO add a progress bar to ChangeIPSecKeyHandler as well
    @Override
    public void propertyChange(PropertyChangeEvent evt) {

    }
}
