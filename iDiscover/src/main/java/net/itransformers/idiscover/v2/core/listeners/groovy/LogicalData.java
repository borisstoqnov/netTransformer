/*
 * LogicalData.java
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

package net.itransformers.idiscover.v2.core.listeners.groovy;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: niau
 * Date: 2/18/15
 * Time: 6:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class LogicalData {
    HashMap<String, DeviceNeighbour> neighbours = new HashMap<String, DeviceNeighbour>();

    public HashMap<String, DeviceNeighbour> getNeighbours() {
        return neighbours;
    }

    public void setNeighbours(HashMap<String, DeviceNeighbour> neighbours) {
        this.neighbours = neighbours;
    }
}
