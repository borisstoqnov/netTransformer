package net.itransformers.idiscover.v2.core.parallel;

/**
 * Created by vasko on 20.06.16.
 */
public class DiscoveryWorkerTestCase {
//    private DiscoveryWorker discoveryWorker;
//    private ConnectionDetails connectionDetails;
//    private Node parentNode;
////    private DiscoveryWorkerContext context;
//    private NodeFactory nodeFactory;
//
//    @Before
//    public void setUp() throws Exception {
//        nodeFactory = mock(NodeFactory.class);
//        discoveryWorker = mock(DiscoveryWorker.class);
//        connectionDetails = mock(ConnectionDetails.class);
//        parentNode = mock(Node.class);
////        context = mock(DiscoveryWorkerContext.class);
//        discoveryWorker = new DiscoveryWorker(nodeFactory, connectionDetails, parentNode, 3, context);
//    }
//
//    @Test
//    public void testComputeIfNodeDiscovererIsNotFoundForTheConnectionTypeInConnectionDetails(){
//        String connectionType = "snmp";
//        when(connectionDetails.getConnectionType()).thenReturn(connectionType);
//        when(context.getNodeDiscoverer(connectionType)).thenReturn(null);
//        discoveryWorker.compute();
//        verify(connectionDetails).getConnectionType();
//        verify(context).getNodeDiscoverer(connectionType);
//        verify(context).fireNodeNotDiscoveredEvent(connectionDetails);
//        verifyNoMoreInteractions(connectionDetails, parentNode, context);
//    }
//
//    @Test
//    public void testComputeIfNoDiscoveryResultForTheGivenConnectionDetails(){
//        String connectionType = "snmp";
//        when(connectionDetails.getConnectionType()).thenReturn(connectionType);
//        NodeDiscoverer nodeDiscoverer = mock(NodeDiscoverer.class);
//        when(context.getNodeDiscoverer(connectionType)).thenReturn(nodeDiscoverer);
//        when(nodeDiscoverer.discover(connectionDetails)).thenReturn(null);
//        discoveryWorker.compute();
//        verify(connectionDetails).getConnectionType();
//        verify(context).getNodeDiscoverer(connectionType);
//        verify(nodeDiscoverer).discover(connectionDetails);
//        verify(context).fireNodeNotDiscoveredEvent(connectionDetails);
//        verifyNoMoreInteractions(nodeDiscoverer, connectionDetails, parentNode, context);
//    }
//
//    @Test
//    public void testComputeIfNodeIsAlreadyDiscovered(){
//        String connectionType = "snmp";
//        when(connectionDetails.getConnectionType()).thenReturn(connectionType);
//        NodeDiscoverer nodeDiscoverer = mock(NodeDiscoverer.class);
//        when(context.getNodeDiscoverer(connectionType)).thenReturn(nodeDiscoverer);
//        NodeDiscoveryResult nodeDiscoveryResult = mock(NodeDiscoveryResult.class);
//        when(nodeDiscoverer.discover(connectionDetails)).thenReturn(nodeDiscoveryResult);
//        String nodeId = "1234";
//        when(nodeDiscoveryResult.getNodeId()).thenReturn(nodeId);
//        Node node = mock(Node.class);
//        when(nodeFactory.createNode(nodeId)).thenReturn(node);
//        Map<String, Node> nodes = mock(Map.class);
//        when(context.getNodes()).thenReturn(nodes);
//        when(nodes.containsKey(nodeId)).thenReturn(true);
//
//        discoveryWorker.compute();
//
//        verify(connectionDetails).getConnectionType();
//        verify(context).getNodeDiscoverer(connectionType);
//        verify(nodeDiscoverer).discover(connectionDetails);
//        verify(nodeDiscoveryResult).getNodeId();
//        verify(context).getNodes();
//        verifyNoMoreInteractions(node, nodeDiscoveryResult, nodeDiscoverer,
//                connectionDetails, parentNode, context);
//    }
//
//    @Test
//    public void testComputeIfNewNodeIsDiscoveredAndIfItHasParent(){
//        String connectionType = "snmp";
//        when(connectionDetails.getConnectionType()).thenReturn(connectionType);
//        NodeDiscoverer nodeDiscoverer = mock(NodeDiscoverer.class);
//        when(context.getNodeDiscoverer(connectionType)).thenReturn(nodeDiscoverer);
//        NodeDiscoveryResult nodeDiscoveryResult = mock(NodeDiscoveryResult.class);
//        when(nodeDiscoverer.discover(connectionDetails)).thenReturn(nodeDiscoveryResult);
//        String nodeId = "1234";
//        when(nodeDiscoveryResult.getNodeId()).thenReturn(nodeId);
//        Node currentNode = mock(Node.class);
//        when(nodeFactory.createNode(nodeId)).thenReturn(currentNode);
//        Map<String, Node> nodes = mock(Map.class);
//        when(context.getNodes()).thenReturn(nodes);
//        when(nodes.containsKey(nodeId)).thenReturn(false);
//
//        discoveryWorker.compute();
//
//
//        verify(connectionDetails).getConnectionType();
//        verify(context).getNodeDiscoverer(connectionType);
//        verify(nodeDiscoverer).discover(connectionDetails);
//        verify(nodeDiscoveryResult).getNodeId();
//        verify(context).getNodes();
//        verify(context).getNodeDiscoverer(connectionType);
//        verify(parentNode).addNeighbour(currentNode);
//        verify(nodes).put(nodeId, currentNode);
//        verify(context).fireNodeDiscoveredEvent(connectionDetails, nodeDiscoveryResult);
//
//
////        verifyNoMoreInteractions(currentNode, nodeDiscoveryResult, nodeDiscoverer,
////                connectionDetails, parentNode, context);
//    }
//
}
