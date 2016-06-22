package net.itransformers.idiscover.v2.core.parallel;

import net.itransformers.idiscover.v2.core.connection_details.IPNetConnectionDetails;
import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by vasko on 22.06.16.
 */
public class ConnectionDetailsJsonDeserializer {
    public ConnectionDetails deserialize(JSONObject jsonObject) throws JSONException {
        String connectionType = jsonObject.getString("connectionType");
        Map<String, String> params = new HashMap<String, String>();
        JSONObject jsonParams = jsonObject.getJSONObject("params");
        Iterator keys = jsonParams.keys();
        while (keys.hasNext()){
            String key = (String) keys.next();
            if (!jsonParams.isNull(key)){
                params.put(key, jsonParams.getString(key));
            } else {
                params.put(key, null);
            }

        }
        return new IPNetConnectionDetails(connectionType,params);
    }
}
