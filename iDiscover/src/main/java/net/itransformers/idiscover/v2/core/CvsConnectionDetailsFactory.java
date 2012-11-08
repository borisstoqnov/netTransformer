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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CvsConnectionDetailsFactory {
    @SuppressWarnings("unchecked")
    public static List<ConnectionDetails> createConnectionDetail(File file) throws IOException {
        List<String> lines = FileUtils.readLines(file);
        List<ConnectionDetails> result = new ArrayList<ConnectionDetails>();
        Map<String, String> attributes = new HashMap<String, String>();
        for (String line : lines) {
            if (line.trim().equals("")) continue;
            String[] fields = line.split(",");
            for (String field : fields) {
                String[] nameValPair = field.split("=");
                if (nameValPair.length != 2) {
                    throw new RuntimeException("Missing '=' sign in field: "+ field+", for connection details line: "+line);
                }
                attributes.put(nameValPair[0],nameValPair[1]);
            }
            String connectionType = attributes.remove("type");
            ConnectionDetails details = new ConnectionDetails(connectionType,attributes);
            result.add(details);
        }
        return result;
    }
}
