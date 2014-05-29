/*
 * iMap is an open source tool able to upload Internet BGP peering information
 *  and to visualize the beauty of Internet BGP Peering in 2D map.
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

package net.itransformers.topologyviewer.gui;

import javax.servlet.ServletContext;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;

// The Java class will be hosted at the URI path "/helloworld"
 @Path("/discovery")
 public class DiscoveryResource {
    @Context
    ServletContext context;
     // The Java method will process HTTP GET requests
     @POST
     // The Java method will produce content identified by the MIME Media
     // type "text/plain"
     @Produces("text/plain")
     public String start() {
        if (context.getAttribute("discovery") != null) {

        } else {

            context.setAttribute("discovery", null);
        }


         return "Hello World:"+ context.getInitParameter("config.file");
     }
 }