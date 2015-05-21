///*
// * netTransformer is an open source tool able to discover and transform
// *  IP network infrastructures.
// *  Copyright (C) 2012  http://itransformers.net
// *
// *  This program is free software: you can redistribute it and/or modify
// *  it under the terms of the GNU General Public License as published by
// *  the Free Software Foundation, either version 3 of the License, or
// *  any later version.
// *
// *  This program is distributed in the hope that it will be useful,
// *  but WITHOUT ANY WARRANTY; without even the implied warranty of
// *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// *  GNU General Public License for more details.
// *
// *  You should have received a copy of the GNU General Public License
// *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
// */
//
//package net.itransformers.idiscover.v2.core;
//
//import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
//import net.itransformers.idiscover.v2.core.model.Node;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.Map;
//
//public class NetworkNodeDiscovererImplTestCase {
//    private NetworkNodeDiscovererImpl networkNodeDiscovererImpl;
//    private ConnectionDetails connectionDetailsToA;
//    @Before
//    public void setUp(){
//        networkNodeDiscovererImpl = new NetworkNodeDiscovererImpl();
//        MockNetworkDiscoverer mockNetworkDiscoverer = new MockNetworkDiscoverer();
//        Map<String, NodeDiscoverer> nodeDiscoverers = new HashMap<String, NodeDiscoverer>();
//        nodeDiscoverers.put("mock", mockNetworkDiscoverer);
//        networkNodeDiscovererImpl.setNodeDiscoverers(nodeDiscoverers);
//        connectionDetailsToA = new ConnectionDetails();
//        connectionDetailsToA.setConnectionType("mock");
//        connectionDetailsToA.put("deviceName", "A");
//    }
//    @Test
//    public void testDoDiscoverNodes(){
//        Map<String, Node> nodes = new HashMap<String, Node>();
//        NetworkDiscoveryResult result = new NetworkDiscoveryResult();
//        networkNodeDiscovererImpl.doDiscoverNodes(Arrays.asList(connectionDetailsToA), nodes, null, 0, -1, result);
//        System.out.println(nodes.size());
//        Assert.assertEquals("discovered nodes are different",
//                "{D=Node{id='D', connectionDetailsList=[ConnectionDetails{connectionType='mock', params={deviceName=D}}], neighbours=[]}, A=Node{id='A', connectionDetailsList=[ConnectionDetails{connectionType='mock', params={deviceName=A}}], neighbours=[B,C,]}, B=Node{id='B', connectionDetailsList=[ConnectionDetails{connectionType='mock', params={deviceName=B}}], neighbours=[D,]}, C=Node{id='C', connectionDetailsList=[ConnectionDetails{connectionType='mock', params={deviceName=C}}], neighbours=[]}}",
//                nodes.toString());
//    }
//    @Test
//    public void testDoDiscoverNodesOneLevel(){
//        Map<String, Node> nodes = new HashMap<String, Node>();
//        NetworkDiscoveryResult result = new NetworkDiscoveryResult();
//        networkNodeDiscovererImpl.doDiscoverNodes(Arrays.asList(connectionDetailsToA), nodes, null, 0, 1, result);
//        Assert.assertEquals("discovered nodes are different",
//                "{A=Node{id='A', connectionDetailsList=[ConnectionDetails{connectionType='mock', params={deviceName=A}}], neighbours=[]}}",
//                nodes.toString());
//    }
//    @Test
//    public void testDoDiscoverNodesTwoLevel(){
//        Map<String, Node> nodes = new HashMap<String, Node>();
//        NetworkDiscoveryResult result = new NetworkDiscoveryResult();
//        networkNodeDiscovererImpl.doDiscoverNodes(Arrays.asList(connectionDetailsToA), nodes, null, 0, 2, result);
//        Assert.assertEquals("discovered nodes are different",
//                "{A=Node{id='A', connectionDetailsList=[ConnectionDetails{connectionType='mock', params={deviceName=A}}], neighbours=[B,C,]}, B=Node{id='B', connectionDetailsList=[ConnectionDetails{connectionType='mock', params={deviceName=B}}], neighbours=[]}, C=Node{id='C', connectionDetailsList=[ConnectionDetails{connectionType='mock', params={deviceName=C}}], neighbours=[]}}",
//                nodes.toString());
//    }
//}
