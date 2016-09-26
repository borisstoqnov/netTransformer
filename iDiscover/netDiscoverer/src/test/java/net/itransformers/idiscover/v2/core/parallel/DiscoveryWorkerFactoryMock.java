package net.itransformers.idiscover.v2.core.parallel;

import net.itransformers.idiscover.v2.core.NodeDiscoverer;
import net.itransformers.idiscover.v2.core.factory.DiscoveryWorkerFactory;
import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetails;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

/**
 * Created by vasko on 22.06.16.
 */
public class DiscoveryWorkerFactoryMock extends DiscoveryWorkerFactory {

    private Map<String, JSONObject> ipAddress2JsonObject;

    public DiscoveryWorkerFactoryMock(Map<String, JSONObject> ipAddress2JsonObject) throws IOException, JSONException {
        this.ipAddress2JsonObject = ipAddress2JsonObject;
    }

    @Override
    public DiscoveryWorker createDiscoveryWorker(Map<String, NodeDiscoverer> nodeDiscoverers, ConnectionDetails connectionDetails, String parentNodeId) {
        String ipAddress = connectionDetails.getParam("ipAddress");
        JSONObject jsonObject = ipAddress2JsonObject.get(ipAddress);
        return new DiscoveryWorkerMock(jsonObject, nodeDiscoverers, connectionDetails, parentNodeId);
    }
}
