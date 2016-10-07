package net.itransformers.idiscover.api;

import java.util.Map;

/**
 * Created by vasko on 9/30/2016.
 */
public interface VersionManagerFactory {
    VersionManager createVersionManager(String type, Map<String, String> properties);
}
