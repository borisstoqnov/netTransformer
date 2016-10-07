package net.itransformers.connectiondetails.csvconnectiondetails;/*
 * net.itransformers.connectiondetails.csvconnectiondetails.CsvConnectionDetailsFileManager.java
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

import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetails;
import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetailsManager;
import net.itransformers.connectiondetails.connectiondetailsapi.IPNetConnectionDetails;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by niau on 6/18/15.
 */
public class CsvConnectionDetailsFileManager implements ConnectionDetailsManager {

    static Logger logger = Logger.getLogger(CsvConnectionDetailsFileManager.class);
    private Map<String,ConnectionDetails> connectionDetailsMap;
    private String file;


    public CsvConnectionDetailsFileManager(String file) {
        this.file = file;
        this.connectionDetailsMap = new LinkedHashMap<String,ConnectionDetails>();

    }

    public void save() throws FileNotFoundException {

        PrintWriter writer = new PrintWriter(file);
        for (String name : connectionDetailsMap.keySet()){
            ConnectionDetails connectionDetails = connectionDetailsMap.get(name);
            StringBuilder sb = new StringBuilder();
            sb.append("name=").append(name).append(",");
            sb.append("type=").append(connectionDetails.getConnectionType()).append(":");
            for (String paramKey : connectionDetails.getParams().keySet()) {
                sb.append(paramKey).append("=").append(connectionDetails.getParam(paramKey)).append(",");
            }
            writer.println(sb);
        }
        writer.flush();
    }

    public void load() throws IOException {


        List<String> lines = FileUtils.readLines(new File(file));
        for (String line : lines) {
            if (line.trim().equals("")) continue;
            if (line.startsWith("#")) continue;
            int headerSeparatorIndex = line.indexOf(":");
            if (headerSeparatorIndex == -1) {
                throw new RuntimeException("Can not find header separator ':', for connection details line: "+line);
            }
            String header = line.substring(0,headerSeparatorIndex);
            String body = line.substring(headerSeparatorIndex+1);
            Map<String, String> headerAttributes = parse(line, header);
            Map<String, String> attributes = parse(line, body);
            if (!headerAttributes.containsKey("type")) {
                throw new RuntimeException("Can not find 'type' attribute in header, for connection details line: "+line);
            }
            if (!headerAttributes.containsKey("name")) {
                throw new RuntimeException("Can not find 'name' attribute in header, for connection details line: "+line);
            }
            ConnectionDetails details = new IPNetConnectionDetails(headerAttributes.get("type"),attributes);
            connectionDetailsMap.put(headerAttributes.get("name"),details);
        }

    }
    private static Map<String, String> parse(String line, String body) {
        String[] fields = body.split(",");
        Map<String, String> attributes = new LinkedHashMap<String, String>();
        for (String field : fields) {
            String[] nameValPair = field.split("=");
            if (nameValPair.length != 2) {
                throw new RuntimeException("Missing '=' sign in field: "+ field+", for connection details line: "+line);
            }
            attributes.put(nameValPair[0].trim(),nameValPair[1].trim());
        }
        return attributes;
    }

    @Override
    public Map<String, ConnectionDetails> getConnectionDetails() {
        return connectionDetailsMap;
    }

    @Override
    public void createConnection(String name, ConnectionDetails connectionDetails) {
        connectionDetailsMap.put(name, connectionDetails);

    }


    @Override
    public void updateConnection(String name, String newConnectionDetailName) {

       ConnectionDetails connectionDetails = connectionDetailsMap.get(name);
       if (connectionDetails!=null) {
           connectionDetailsMap.put(newConnectionDetailName, connectionDetails);
           connectionDetailsMap.remove(name);
       }
    }

    @Override
    public void deleteConnection(String name) {

        connectionDetailsMap.remove(name);

    }

    @Override
    public ConnectionDetails getConnection(String name) {
        return connectionDetailsMap.get(name);
    }

    @Override
    public void createConnectionType(String name, String type) {

        ConnectionDetails connectionDetails =  connectionDetailsMap.get(name);

        connectionDetails.setConnectionType(type);

    }

    @Override
    public void updateConnectionType(String name, String type) {
        ConnectionDetails connectionDetails =  connectionDetailsMap.get(name);

        connectionDetails.setConnectionType(type);

    }




    @Override
    public String getConnectionType(String name) {
        ConnectionDetails connectionDetails =  connectionDetailsMap.get(name);

        return connectionDetails.getConnectionType();
    }

    @Override
    public void createConnectionParam(String name, String paramName, String paramValue) {

        ConnectionDetails connectionDetails = connectionDetailsMap.get(name);
        if (connectionDetails!=null)
            connectionDetails.put(paramName,paramValue);


    }

    @Override
    public void updateConnectionParam(String name, String paramName, String paramValue) {
        ConnectionDetails connectionDetails = connectionDetailsMap.get(name);
        if (connectionDetails.getParam(paramName) != null){
            connectionDetails.put(paramName, paramValue);
        }else {
        //  throw new NotFoundException;
        }
    }

    @Override
    public void deleteConnectionParam(String name,  String paramName) {
        ConnectionDetails connectionDetails = connectionDetailsMap.get(name);

        if (connectionDetails!=null)
             connectionDetails.getParams().remove(paramName);

    }

    @Override
    public String getConnectionParam(String name, String paramName) {

        ConnectionDetails connectionDetails = connectionDetailsMap.get(name);
        if (connectionDetails!=null)
            return connectionDetails.getParam(paramName);
        return null;
    }

    @Override
    public Map<String, String> getConnectionParams(String name, String type) {

        ConnectionDetails connectionDetails = connectionDetailsMap.get(name);
        if (connectionDetails!=null)
                return  connectionDetails.getParams();
        return null;
    }
}
