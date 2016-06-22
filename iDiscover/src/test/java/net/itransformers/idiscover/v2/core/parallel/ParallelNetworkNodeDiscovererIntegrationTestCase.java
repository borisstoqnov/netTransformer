package net.itransformers.idiscover.v2.core.parallel;

import net.itransformers.idiscover.v2.core.NetworkDiscoveryResult;
import net.itransformers.idiscover.v2.core.factory.NodeFactory;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by vasko on 22.06.16.
 */
public class ParallelNetworkNodeDiscovererIntegrationTestCase {

    Map<String, JSONObject> ipAddress2JsonObject = new HashMap<String, JSONObject>();
    protected ConnectionDetailsJsonDeserializer connectionDetailsJsonDeserializer = new ConnectionDetailsJsonDeserializer();

    public ParallelNetworkNodeDiscovererIntegrationTestCase() throws IOException, JSONException {
        String json = new String(Files.readAllBytes(Paths.get("iDiscover/src/test/java/net/itransformers/idiscover/v2/core/parallel/mock-discovery.json")));
        JSONArray jsonArray = new JSONArray(json);
        for (int i=0;i<jsonArray.length(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            JSONObject connDetails = jsonObject.getJSONObject("connectionDetails");
            JSONObject params = connDetails.getJSONObject("params");
            String ipAddress = params.getString("ipAddress");
            ipAddress2JsonObject.put(ipAddress, jsonObject);
        }
    }

    @Test
    public void testDiscovery() throws IOException, JSONException {
        ParallelNetworkNodeDiscovererImpl discovery = new ParallelNetworkNodeDiscovererImpl(
                new DiscoveryWorkerFactoryMock(ipAddress2JsonObject),new NodeFactory());
        Set<ConnectionDetails> connectionDetailsSet = new HashSet<ConnectionDetails>();

        {
            ConnectionDetails connectionDetails = connectionDetailsJsonDeserializer.deserialize(
                    ipAddress2JsonObject.get("10.192.6.11").getJSONObject("connectionDetails"));
            connectionDetailsSet.add(connectionDetails);
        }
        {
            ConnectionDetails connectionDetails = connectionDetailsJsonDeserializer.deserialize(
                    ipAddress2JsonObject.get("10.192.6.12").getJSONObject("connectionDetails"));
            connectionDetailsSet.add(connectionDetails);
        }
        NetworkDiscoveryResult result = discovery.discoverNetwork(connectionDetailsSet);
        Set<String> expectedNodes = new HashSet<String>();
        Collections.addAll(expectedNodes, "ssrv2noded", "ssrv1nodea", "srv2NodeC", "srv1nodeB", "ssrv1noded",
                "r3845", "ssrv2nodeb", "srv1NodeC", "sw0", "null");
        Assert.assertEquals(expectedNodes.toString(), result.getNodes().keySet().toString());
    }
}
