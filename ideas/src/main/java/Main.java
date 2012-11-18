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

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.client.apache.ApacheHttpClient;
import org.apache.commons.io.FileUtils;
import org.neo4j.rest.graphdb.RequestResult;
import org.neo4j.rest.graphdb.UserAgent;

import javax.ws.rs.core.MediaType;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

public class Main {
    private static final UserAgent userAgent = new UserAgent();
    public static final MediaType STREAMING_JSON_TYPE = new MediaType(APPLICATION_JSON_TYPE.getType(),APPLICATION_JSON_TYPE.getSubtype(), stringMap("stream","true"));

    public static void main(String[] args) throws URISyntaxException, IOException {
        String data = FileUtils.readFileToString(new File("ideas/src/test/test-rest.txt"));
        Client client = createClient();
        WebResource resource = client.resource(new URI("http://localhost:7474/db/data/batch"));
        WebResource.Builder builder = resource.accept(STREAMING_JSON_TYPE).header("X-Stream", "true");
        builder.entity( toInputStream(data), APPLICATION_JSON_TYPE );
        System.out.println(RequestResult.extractFrom(builder.post(ClientResponse.class)));
    }

    private static InputStream toInputStream(String data) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(4024 * 1024);
        outputStream.write(data.getBytes());
        byte[] buf = outputStream.toByteArray();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(buf);
        return inputStream;
    }


    protected static Client createClient1() {
        Client client = Client.create();
        client.setConnectTimeout(800);
        client.setReadTimeout(800);
        client.setChunkedEncodingSize(80 * 1024);
        userAgent.install(client);
        return client;
    }

    protected static Client createClient() {
        Client client = ApacheHttpClient.create();
        return client;
    }

    private static Map<String, String> stringMap(String stream, String aTrue) {
        HashMap<String, String> result = new HashMap<String, String>();
        result.put(stream, aTrue);
        return result;
    }

}
