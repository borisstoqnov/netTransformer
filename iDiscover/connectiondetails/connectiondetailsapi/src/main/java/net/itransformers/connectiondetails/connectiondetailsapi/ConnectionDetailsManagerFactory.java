package net.itransformers.connectiondetails.connectiondetailsapi;

import java.util.Map;

/**
 * Created by vasko on 30.09.16.
 */
public interface ConnectionDetailsManagerFactory {
    ConnectionDetailsManager createConnectionDetailsManager(String type, Map<String, String> properties);
}
