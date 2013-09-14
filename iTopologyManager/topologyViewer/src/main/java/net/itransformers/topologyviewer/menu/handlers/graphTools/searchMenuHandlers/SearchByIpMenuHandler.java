/*
 * iMap is an open source tool able to upload Internet BGP peering information
 *  and to visualize the beauty of Internet BGP Peering in 2D map.
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

package net.itransformers.topologyviewer.menu.handlers.graphTools.searchMenuHandlers;

import net.itransformers.topologyviewer.gui.GraphViewerPanel;
import net.itransformers.topologyviewer.gui.TopologyManagerFrame;
import org.apache.commons.net.util.SubnetUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * Date: 12-4-27
 * Time: 23:30
 * To change this template use File | Settings | File Templates.
 */
public class SearchByIpMenuHandler implements ActionListener {
    static Logger logger = Logger.getLogger(SearchByIpMenuHandler.class);

    private TopologyManagerFrame frame;

    public SearchByIpMenuHandler(TopologyManagerFrame frame) throws HeadlessException {

        this.frame = frame;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

//                Map<String, Map<String, GraphMLMetadata<G>>> test1 = graphmlLoader();

//        String key = JOptionPane.showInputDialog(frame, "Choose IP", JOptionPane.QUESTION_MESSAGE);
        final String ip = JOptionPane.showInputDialog(frame, "Enter IP", "Value", JOptionPane.QUESTION_MESSAGE);

        GraphViewerPanel viewerPanel = (GraphViewerPanel) frame.getTabbedPane().getSelectedComponent();
        Set<String> foundVertexes;
        foundVertexes = viewerPanel.FindNodeByKey("RoutePrefixes",new Object(){
            @Override
            public boolean equals(Object obj) {
                String s = (String) obj;
                String[] ipRanges = s.split(",");
                for (String ipRangeNotTrimmed : ipRanges) {
                    String ipRange = ipRangeNotTrimmed.trim();
                    if (ipRange.equals("") || ipRange.equals("0.0.0.0/0")) continue;
                    try {
                        SubnetUtils subnet = new SubnetUtils(ipRange);
                        if (subnet.getInfo().isInRange(ip)){
                            return true;
                        }
                    } catch (IllegalArgumentException iae){
                        logger.error("Can not parse ip or ip range:"+ipRange+", ip:"+ip);
                        System.out.println("Can not parse ip or ip range:"+ipRange+", ip:"+ip);
                        iae.printStackTrace();
                        continue;
                    }
                }
                return false;
            }
        });
        if (!foundVertexes.isEmpty()){
            Iterator it = foundVertexes.iterator();
            if (foundVertexes.size()==1){
                Object element = it.next();
                System.out.println("Redrawing around "+element.toString());
                viewerPanel.SetPickedState(element.toString());
                viewerPanel.Animator(element.toString());
            }else{
                JOptionPane.showMessageDialog(frame, "Multiple Nodes with ip " + ip +" found :\n"+foundVertexes, "Error", JOptionPane.ERROR_MESSAGE);
            }
        }  else{
            JOptionPane.showMessageDialog(frame, "Can not find node with ip " + ip , "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

}
