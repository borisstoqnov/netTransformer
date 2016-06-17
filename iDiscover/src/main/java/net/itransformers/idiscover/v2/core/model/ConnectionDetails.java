

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

package net.itransformers.idiscover.v2.core.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConnectionDetails {
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
        return "ConnectionDetails{" +
                "connectionType='" + connectionType + '\'' +
                ", params=" + params +
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
}
