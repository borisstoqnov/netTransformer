/*
 * NodeDiscoveryResult.java
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

package net.itransformers.idiscover.v2.core;/*
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

import net.itransformers.idiscover.v2.core.model.ConnectionDetails;

import java.util.*;

public class NodeDiscoveryResult implements Cloneable{
    String parentId;
    String nodeId;
    Set<ConnectionDetails> neighboursConnectionDetails = new HashSet<ConnectionDetails>();
    Map<String,Object> discoveredData = new HashMap<String,Object>();

    public NodeDiscoveryResult(String nodeId, Set<ConnectionDetails> neighboursConnectionDetails) {
        this.nodeId = nodeId;
        if (neighboursConnectionDetails != null) {
            this.neighboursConnectionDetails.addAll(neighboursConnectionDetails);
        }
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getNodeId() {
        return nodeId;
    }

    public void setNodeId(String nodeId) {
        this.nodeId = nodeId;
    }

    public Set<ConnectionDetails> getNeighboursConnectionDetails() {
        return Collections.unmodifiableSet(neighboursConnectionDetails);
    }

    public Object getDiscoveredData(String key){
        return discoveredData.get(key);
    }
    public void setDiscoveredData(String key, Object data){
        discoveredData.put(key,data);
    }

    @Override
    public String toString() {
        return "NodeDiscoveryResult{" +
                "nodeId='" + nodeId + '\'' +
                ", neighboursConnectionDetails=" + neighboursConnectionDetails +
                ", discoveredData=" + discoveredData.toString() +
                '}';
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        // TODO do deep copy of discovered data
        return super.clone();
    }

}
