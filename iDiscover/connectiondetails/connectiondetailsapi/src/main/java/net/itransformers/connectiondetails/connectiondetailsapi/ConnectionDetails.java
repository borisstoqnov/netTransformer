

/*
 * ConnectionDetails.java
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


package net.itransformers.connectiondetails.connectiondetailsapi;


import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConnectionDetails implements Cloneable, Serializable{
    protected String connectionType;
    protected Map<String,String> params = new LinkedHashMap<String, String>();

    public ConnectionDetails() {
    }

    public ConnectionDetails(String connectionType) {
        this.connectionType = connectionType;
    }



    public ConnectionDetails(String connectionType, Map<String, String> params) {
        this.connectionType = connectionType;
        for (String key : params.keySet()) {
            this.params.put(key,params.get(key));
        }
    }

    public ConnectionDetails(String name, String connectionType, Map<String, String> params){
        this.connectionType = connectionType;
        for (String key : params.keySet()) {
            this.params.put(key,params.get(key));
        }

    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getConnectionType() {
        return connectionType;
    }

    public void setConnectionType(String connectionType) {
        this.connectionType = connectionType;
    }

    public String getParam(String key) {
        return params.get(key);
    }
    public Map<String,String> getParams() {
        return Collections.unmodifiableMap(new LinkedHashMap<String, String>(params));
    }

    public void clear(){
        params.clear();
    }

    @Override
    public String toString() {
        StringBuilder paramsBuilder = new StringBuilder();
        boolean isFirst = true;
        for (Map.Entry<String, String> param : this.params.entrySet()) {
            if (!isFirst) paramsBuilder.append(",");
            isFirst = false;
            paramsBuilder.append("\"").append(param.getKey()).append("\":");
            if (param.getValue() == null) paramsBuilder.append("null");
            else paramsBuilder.append("\"").append(param.getValue()).append("\"");
        }
        return "{" +
                "\"connectionType\":\"" + connectionType + "\"" +
                ",\"params\":{" + paramsBuilder.toString() + "}"+
                '}';
    }

    public void put(String key, String value) {
        params.put(key,value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConnectionDetails details = (ConnectionDetails) o;

        if (connectionType != null ? !connectionType.equals(details.connectionType) : details.connectionType != null)
            return false;
        if (params != null ? !params.equals(details.params) : details.params != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = connectionType != null ? connectionType.hashCode() : 0;
        result = 31 * result + (params != null ? params.hashCode() : 0);
        return result;
    }


    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
