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

package net.itransformers.idiscover.v2.core;/*
 * iTransformer is an open source tool able to discover IP networks
 * and to perform dynamic data data population into a xml based inventory system.
 * Copyright (C) 2010  http://itransformers.net
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CvsConnectionDetailsFactory {
    @SuppressWarnings("unchecked")
    public static Map<String,ConnectionDetails> createConnectionDetail(File file) throws IOException {
        List<String> lines = FileUtils.readLines(file);
        Map<String,ConnectionDetails> result = new LinkedHashMap<String,ConnectionDetails>();
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
            ConnectionDetails details = new ConnectionDetails(headerAttributes.get("type"),attributes);
            result.put(headerAttributes.get("name"),details);
        }
        return result;
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
}
