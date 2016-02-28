/*
 * Main1.java
 *
 * This work is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 2 of the License,
 * or (at your option) any later version.
 *
 * This work is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA
 *
 * Copyright (c) 2010-2016 iTransformers Labs. All rights reserved.
 */

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import org.apache.commons.io.FileUtils;
//import org.neo4j.rest.graphdb.RequestResult;
//import org.neo4j.rest.graphdb.UserAgent;

import javax.ws.rs.core.MediaType;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;

@Deprecated
public class Main1 {
//    private static final UserAgent userAgent = new UserAgent();
//    public static final MediaType STREAMING_JSON_TYPE = new MediaType(APPLICATION_JSON_TYPE.getType(),APPLICATION_JSON_TYPE.getSubtype(), stringMap("stream","true"));
//
//    public static void main(String[] args) throws URISyntaxException, IOException {
//        String data = FileUtils.readFileToString(new File("ideas/src/test/test-rest.txt"));
//        Client client = createClient();
//        WebResource resource = client.resource(new URI("http://193.19.172.133:7474/db/data/batch"));
//        WebResource.Builder builder = resource.accept(STREAMING_JSON_TYPE).header("X-Stream", "true");
//        System.out.println("here1");
//        builder.entity( toInputStream(data), APPLICATION_JSON_TYPE );
//        System.out.println("here2");
//        System.out.println(RequestResult.extractFrom(builder.post(ClientResponse.class)));
//        System.out.println("here3");
//    }
//
//    private static InputStream toInputStream(String data) throws IOException {
//        System.out.println("date len="+data.getBytes().length);
//        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(4024 * 1024);
//        System.out.println("here4");
//        outputStream.write(data.getBytes());
//        System.out.println("here5");
//        byte[] buf = outputStream.toByteArray();
//        System.out.println("buf.len="+buf.length);
//        ByteArrayInputStream inputStream = new ByteArrayInputStream(buf);
//        System.out.println("here6");
//        return inputStream;
//    }
//
//    protected static Client createClient() {
//        Client client = Client.create();
//        client.setConnectTimeout(800);
//        client.setReadTimeout(800);
//        client.setChunkedEncodingSize(80 * 1024);
//        userAgent.install(client);
//        return client;
//    }
//
//    private static Map<String, String> stringMap(String stream, String aTrue) {
//        HashMap<String, String> result = new HashMap<String, String>();
//        result.put(stream, aTrue);
//        return result;
//    }
//
}
