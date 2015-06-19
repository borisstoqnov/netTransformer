package net.itransformers.idiscover.v2.core;

import net.itransformers.idiscover.v2.core.model.ConnectionDetails;
import org.apache.commons.io.FileUtils;

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
public class CsvConnectionDetailsFileManager {

    public void save(File file, Map<String,ConnectionDetails> connectionDetailsMap) throws FileNotFoundException {
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

    public Map<String,ConnectionDetails> load(File file) throws IOException {
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
