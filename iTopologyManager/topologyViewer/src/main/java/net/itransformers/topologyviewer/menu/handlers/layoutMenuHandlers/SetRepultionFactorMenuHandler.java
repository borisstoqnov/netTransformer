package net.itransformers.topologyviewer.menu.handlers.layoutMenuHandlers;

import net.itransformers.topologyviewer.gui.TopologyManagerFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by niau on 5/22/15.
 */
public class SetRepultionFactorMenuHandler implements ActionListener{
    private TopologyManagerFrame frame;
    double repultionFactor = 0.75f;

    public SetRepultionFactorMenuHandler(TopologyManagerFrame frame) {

        this.frame = frame;
        this.repultionFactor = 0.75f;
    }

    @Override
    public void actionPerformed(ActionEvent e) {



    }
}
