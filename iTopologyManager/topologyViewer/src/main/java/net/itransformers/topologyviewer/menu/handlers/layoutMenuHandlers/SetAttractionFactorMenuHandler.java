/*
 * SetAttractionFactorMenuHandler.java
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
