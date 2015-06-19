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

package net.itransformers.topologyviewer.rightclick.impl;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import net.itransformers.topologyviewer.rightclick.RightClickHandler;
import net.itransformers.topologyviewer.rightclick.impl.putty.Putty;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.File;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: niau
 * Date: 4/17/13
 * Time: 10:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class jsonRightClick implements RightClickHandler {
    static Logger logger = Logger.getLogger(jsonRightClick.class);

    public <G> void handleRightClick(JFrame parent, String v,
                                     Map<String, String> graphMLParams,
                                     Map<String, String> rightClickParams,
                                     File projectPath,
                                     java.io.File s){
        Map<String,String> connParams;

        final String url = rightClickParams.get("protocol") +"://" + graphMLParams.get("ipAddress")  + ":" + rightClickParams.get("port");

        WebResource resource = Client.create()
                .resource(url);
        String operation = rightClickParams.get("operation");

        if (operation.equals("get")){
            ClientResponse response = resource.accept("application/json")
                .get(ClientResponse.class);


            if (response.getStatus() != 200) {
                logger.error("Failed : HTTP error code : " + response.getStatus());
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatus());

            }
            String output = response.getEntity(String.class);
            response.close();

        }else if (operation.equals("post")){
            String input = "{\"singer\":\"Metallica\",\"title\":\"Fade To Black\"}";

            ClientResponse response = resource.type("application/json")
                    .post(ClientResponse.class, input);

            if (response.getStatus() != 201) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatus());
            }

        }   else if(operation.equals("put")){
            String input = "{\"singer\":\"Metallica\",\"title\":\"Fade To Black\"}";

            ClientResponse response = resource.type("application/json")
                    .put(ClientResponse.class, input);

            if (response.getStatus() != 201) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatus());
            }
        }
            else {
            ClientResponse  response = resource.accept("application/json")
                        .delete(ClientResponse.class);


                if (response.getStatus() != 200) {
                    logger.error("Failed : HTTP error code : " + response.getStatus());
                    throw new RuntimeException("Failed : HTTP error code : "
                            + response.getStatus());

                }
                String output = response.getEntity(String.class);
                response.close();
            }
        }


    protected void handleConnParams(JFrame parent, Map<String, String> connParams, Map<String, String> rightClickParams) {
            Putty putty = new Putty(rightClickParams);
            putty.openSession(connParams);
        }


    }
