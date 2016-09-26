package net.itransformers.idiscover.v2.core.parallel;

import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetails;
import net.itransformers.idiscover.v2.core.NodeDiscoverer;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by Vasil Yordanov on 1/9/2016.
 */
public class DiscoveryWorker implements Callable<NodeDiscoveryResult> {
    static Logger logger = Logger.getLogger(DiscoveryWorker.class);
    protected Map<String, NodeDiscoverer> discoverers;
    protected ConnectionDetails connectionDetails;
    protected String parentId;

    public DiscoveryWorker(Map<String, NodeDiscoverer> discoverers, ConnectionDetails connectionDetails, String parentId) {
        this.discoverers = discoverers;
        this.connectionDetails = connectionDetails;
        this.parentId = parentId;
    }

    @Override
    public NodeDiscoveryResult call()  {
            logger.debug("Discovery worker: " + Thread.currentThread().getName() + " started. connectionDetails = " + connectionDetails);

            String connectionType = connectionDetails.getConnectionType();
            NodeDiscoverer nodeDiscoverer = discoverers.get(connectionType);
            NodeDiscoveryResult nodeDiscoveryResult = null;
            try {

                if (nodeDiscoverer == null) {
                    logger.debug("No node discoverer can be found for connectionType: " + connectionDetails);
                    logger.debug("Trying with the discoverer with connectionType any");

                    nodeDiscoverer = discoverers.get("any");

                    nodeDiscoveryResult = nodeDiscoverer.discover((ConnectionDetails) connectionDetails.clone());
                } else {
                    nodeDiscoveryResult = nodeDiscoverer.discover((ConnectionDetails) connectionDetails.clone());
                }
            }catch (Exception e){
                logger.error(e.getMessage(),e);
            }
            if (nodeDiscoveryResult == null) {
                nodeDiscoveryResult = new NodeDiscoveryResult(null, null);
            }
            nodeDiscoveryResult.setParentId(parentId);
            nodeDiscoveryResult.setDiscoveryConnectionDetails(connectionDetails);
            return nodeDiscoveryResult;


        }


}
