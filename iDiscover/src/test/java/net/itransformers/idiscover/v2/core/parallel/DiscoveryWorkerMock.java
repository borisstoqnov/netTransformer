package net.itransformers.idiscover.v2.core.parallel;

import net.itransformers.idiscover.v2.core.NodeDiscoverer;
import net.itransformers.idiscover.v2.core.NodeDiscoveryResult;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by vasko on 22.06.16.
 */
public class DiscoveryWorkerMock extends DiscoveryWorker  {


    protected JSONObject jsonObject;
    protected ConnectionDetailsJsonDeserializer connectionDetailsJsonDeserializer = new ConnectionDetailsJsonDeserializer();

    public DiscoveryWorkerMock(JSONObject jsonObject, Map<String, NodeDiscoverer> discoverers, ConnectionDetails connectionDetails, String parentId) {
        super(discoverers, connectionDetails, parentId);
        this.jsonObject = jsonObject;
    }

    @Override
    public NodeDiscoveryResult call()  {
        String nodeId = null;

        nodeId = jsonObject.getString("nodeId");

        Set<ConnectionDetails> neighbourConnectionDetailsSet = new HashSet<ConnectionDetails>();
        JSONArray neighbourJsonArray = jsonObject.getJSONArray("neighboursConnectionDetails");
        for (int i=0; i< neighbourJsonArray.length(); i++) {
            JSONObject neighbourJsonObject = neighbourJsonArray.getJSONObject(i);
            ConnectionDetails neighbourConnectionDetails = connectionDetailsJsonDeserializer.deserialize(neighbourJsonObject);
            neighbourConnectionDetailsSet.add(neighbourConnectionDetails);
        }

        NodeDiscoveryResult nodeDiscoveryResult = new NodeDiscoveryResult(nodeId, neighbourConnectionDetailsSet);
        nodeDiscoveryResult.setParentId(parentId);
        nodeDiscoveryResult.setDiscoveryConnectionDetails(connectionDetails);
        return nodeDiscoveryResult;
    }




}
