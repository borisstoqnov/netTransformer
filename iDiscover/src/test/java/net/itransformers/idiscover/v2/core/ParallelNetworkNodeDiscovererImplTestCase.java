/*
 * netTransformer is an open source tool able to discover and transform
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

package net.itransformers.idiscover.v2.core;

import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import net.itransformers.idiscover.v2.core.parallel.ParallelNetworkNodeDiscovererImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

public class ParallelNetworkNodeDiscovererImplTestCase {
    /* Test Network
            A --- B
             \      \
         C ----------D
           \   \   /
            E    F

     */
    private NetworkNodeDiscoverer networkNodeDiscovererImpl;
    private MockNodeDiscoverer mockNodeDiscoverer;
    private MockNodeDiscoveryListener mockNodeDiscoveryListener;
    private MockNetworkDiscoveryListener mockNetworkDiscoveryListener;

    @Before
    public void setUp() {
        networkNodeDiscovererImpl = new ParallelNetworkNodeDiscovererImpl();
        HashMap<String, NodeDiscoveryResult> discoveryResultMap = new HashMap<String, NodeDiscoveryResult>() {{
            put("A", new NodeDiscoveryResult("A", getNeighbourConnDetails("B", "F"), null));
            put("B", new NodeDiscoveryResult("B", getNeighbourConnDetails("B", "A", "D"), null));
            put("C", new NodeDiscoveryResult("C", getNeighbourConnDetails("E", "D", "D"), null));
            put("D", new NodeDiscoveryResult("D", getNeighbourConnDetails("F", "B", "C"), null));
            put("E", new NodeDiscoveryResult("E", getNeighbourConnDetails("C"), null));
            put("F", new NodeDiscoveryResult("F", getNeighbourConnDetails("A", "D"), null));
        }};

        mockNodeDiscoverer = new MockNodeDiscoverer(discoveryResultMap);
        Map<String, NodeDiscoverer> nodeDiscoverers = new HashMap<String, NodeDiscoverer>() {{
            put("mock", mockNodeDiscoverer);
        }};
        mockNodeDiscoveryListener = new MockNodeDiscoveryListener();
        List<NodeDiscoveryListener> nodeDiscoveryListeners = new ArrayList<NodeDiscoveryListener>() {{
            add(mockNodeDiscoveryListener);
        }};
        mockNetworkDiscoveryListener = new MockNetworkDiscoveryListener();
        List<NetworkDiscoveryListener> networkDiscoveryListeners = new ArrayList<NetworkDiscoveryListener>(){{
           add(mockNetworkDiscoveryListener);
        }};
        networkNodeDiscovererImpl.setNodeDiscoverers(nodeDiscoverers);
        networkNodeDiscovererImpl.setNodeDiscoveryListeners(nodeDiscoveryListeners);
        networkNodeDiscovererImpl.setNetworkDiscoveryListeners(networkDiscoveryListeners);
    }

    private ConnectionDetails getConnectionDetailsTo(final String name) {
        return new ConnectionDetails("mock",
                new HashMap<String, String>() {{
                    put("key", name);
                }});
    }

    private HashMap<String, List<ConnectionDetails>> getNeighbourConnDetails(final String... nodes) {
        return new HashMap<String, List<ConnectionDetails>>() {{
            for (final String node : nodes) {
                put(node, new ArrayList<ConnectionDetails>() {{
                    add(getConnectionDetailsTo(node));
                }});
            }
        }};
    }

    @Test
    public void testDiscoverNetwork() {
        networkNodeDiscovererImpl.discoverNetwork(Arrays.asList(getConnectionDetailsTo("A")), -1);
        List<NetworkDiscoveryResult> networkDiscoveryResults = mockNetworkDiscoveryListener.getNetworkDiscoveryResults();
        NetworkDiscoveryResult networkDiscoveryResult = networkDiscoveryResults.get(0);
        String[] expected = {"A", "B", "C", "D", "E", "F"};
        Arrays.sort(expected);
        Object[] actual = networkDiscoveryResult.getNodes().keySet().toArray();
        Arrays.sort(actual);
        Assert.assertArrayEquals(expected, actual);
    }

}
