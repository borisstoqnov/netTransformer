/*
 * iTransformer is an open source tool able to discover and transform
 *  IP network infrastructures.
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
import net.itransformers.utils.Pair;

import java.util.*;

public class MockNetworkDiscoverer implements NodeDiscoverer{
    Set<String> networkNodes;
    Set<Pair<String,String>> links;

    public MockNetworkDiscoverer() {
        networkNodes = new HashSet<String>(Arrays.asList("A","B","C","D"));
        links = new HashSet<Pair<String, String>>();
        links.add(new Pair<String,String>("A","B"));
        links.add(new Pair<String,String>("A","C"));
        links.add(new Pair<String,String>("B","D"));
    }

    @Override
    public String probe(ConnectionDetails connectionDetails) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public NodeDiscoveryResult discover(ConnectionDetails connectionDetails) {
        String node = connectionDetails.getParam("node");
        NodeDiscoveryResult result = new NodeDiscoveryResult();
        result.setNodeId(node);
        Set<ConnectionDetails> neighbours = new HashSet<ConnectionDetails>();
        for (Pair<String, String> link : links) {
            String neighbour;
            if (link.getFirst().equals(node)){
                neighbour = link.getSecond();
            } else if (link.getSecond().equals(node)){
                neighbour = link.getFirst();
            } else {
                continue;
            }
            if (neighbour != null){
                ConnectionDetails conn = new ConnectionDetails("mock");
                conn.put("node", neighbour);
                neighbours.add(conn);
            }
        }
        result.setNeighboursConnectionDetails(Arrays.asList(neighbours.toArray(new ConnectionDetails[0])));
        return result;
    }
}
