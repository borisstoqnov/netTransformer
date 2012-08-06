package net.itransformers.ws.rs;
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

import com.sun.jersey.api.core.PackagesResourceConfig;

import java.util.HashSet;
import java.util.Set;
// http://jersey.java.net/nonav/documentation/latest/user-guide.html
// http://mworking.blogspot.com/2012/01/configuring-tomcat-7-as-jax-rs-server.html

@javax.ws.rs.ApplicationPath("rest")
public class MyApplication extends PackagesResourceConfig {
    public MyApplication() {
        super("net.itransformers.ws.rs;org.bar.rest");
    }
}