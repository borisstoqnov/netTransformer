package net.itransformers.idiscover.v2.core.version_manager;

import net.itransformers.idiscover.api.VersionManager;
import net.itransformers.idiscover.api.VersionManagerFactory;

import java.util.Map;

/**
 * Created by vasko on 9/30/2016.
 */
public class DirectoryVersionManagerFactory implements VersionManagerFactory{
    @Override
    public VersionManager createVersionManager(String type, Map<String, String> properties) {
        String projectPath = properties.get("projectPath");
        return new DirectoryVersionManager(projectPath);
    }
}
