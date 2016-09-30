package net.itransformers.connectiondetails.csvconnectiondetails;

import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetailsManager;
import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetailsManagerFactory;
import net.itransformers.connectiondetails.csvconnectiondetails.CsvConnectionDetailsFileManager;

import java.util.Map;

/**
 * Created by vasko on 30.09.16.
 */
public class CsvConnectionDetailsManagerFactory implements ConnectionDetailsManagerFactory {
    @Override
    public ConnectionDetailsManager createConnectionDetailsManager(String type, Map<String, String> properties) {

        String projectPath = properties.get("projectPath");
        if (projectPath == null) {
            throw new IllegalArgumentException("projectPath is not specified");
        }
//        String version = properties.get("version");
        String file = projectPath+"/iDiscover/conf/txt/connection-details.txt";
        CsvConnectionDetailsFileManager csvConnectionDetailsFileManager = new CsvConnectionDetailsFileManager(file);

        return csvConnectionDetailsFileManager;
    }
}
