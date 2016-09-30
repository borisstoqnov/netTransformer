package net.itransformers.connectiondetails.connectiondetailsapi;

import java.util.Map;

/**
 * Created by niau on 9/18/16.
 */
public interface ConnectionDetailsManager {
    Map<String,ConnectionDetails> getConnectionDetails();
    void createConnection(String name, ConnectionDetails connectionDetails);
    void updateConnection(String name, String newConnectionDetailName);
    void deleteConnection(String name);
    ConnectionDetails getConnection(String name);
    void createConnectionType(String name, String type);
    void updateConnectionType(String name, String type);
    String getConnectionType(String name);

    void createConnectionParam(String name, String paramName, String paramValue);
    void updateConnectionParam(String name, String paramName, String paramValue);
    void deleteConnectionParam(String name, String paramName);
    String getConnectionParam(String name, String paramName);
    Map<String, String> getConnectionParams(String name, String type);
}
