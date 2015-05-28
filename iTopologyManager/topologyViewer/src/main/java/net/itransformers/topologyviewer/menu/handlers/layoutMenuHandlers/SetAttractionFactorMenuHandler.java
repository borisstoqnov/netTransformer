package net.itransformers.topologyviewer.menu.handlers.layoutMenuHandlers;

import net.itransformers.topologyviewer.gui.TopologyManagerFrame;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by niau on 5/22/15.
 */
public class SetAttractionFactorMenuHandler implements ActionListener {
    private TopologyManagerFrame frame;
    double attractionFactor = 0.75f;

    public SetAttractionFactorMenuHandler(TopologyManagerFrame frame) {

        this.frame = frame;
        this.attractionFactor = attractionFactor;

    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
