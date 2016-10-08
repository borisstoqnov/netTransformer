package net.itransformers.connectiondetails.csvconnectiondetails;

import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetailsManager;
import net.itransformers.connectiondetails.connectiondetailsapi.ConnectionDetailsManagerFactory;
import net.itransformers.connectiondetails.csvconnectiondetails.CsvConnectionDetailsFileManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Map;

/**
 * Created by vasko on 30.09.16.
 */
public class CsvConnectionDetailsManagerFactory implements ConnectionDetailsManagerFactory {
    Logger logger = Logger.getLogger(CsvConnectionDetailsManagerFactory.class);
    @Override
    public ConnectionDetailsManager createConnectionDetailsManager(String type, Map<String, String> properties) {

        String projectPath = properties.get("projectPath");
        if (projectPath == null) {
            throw new IllegalArgumentException("projectPath is not specified");
        }
        String file = projectPath+"/iDiscover/conf/txt/connection-details.txt";
        CsvConnectionDetailsFileManager csvConnectionDetailsFileManager = new CsvConnectionDetailsFileManager(file);
        try {
            csvConnectionDetailsFileManager.load();
        } catch (IOException e) {
            logger.error(e.getMessage(),e);
        }
        return csvConnectionDetailsFileManager;
    }
}
