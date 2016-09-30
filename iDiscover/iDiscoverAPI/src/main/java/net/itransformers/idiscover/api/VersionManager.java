package net.itransformers.idiscover.api;

/**
 * Created by vasko on 9/30/2016.
 */
public interface VersionManager {
    String createVersion();
    void deleteVersion(String version);
}
