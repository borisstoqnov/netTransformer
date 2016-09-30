package net.itransformers.resourcemanager;

import java.util.Map;

/**
 * Created by vasko on 9/30/2016.
 */
public interface ResourceManagerFactory {
    ResourceManager createResourceManager(String type, Map<String, String> properties);
}
