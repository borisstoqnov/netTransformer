package net.itransformers.resourcemanager;

/**
 * Created by vasko on 14.09.16.
 */
public class ResourceManagerException extends RuntimeException {
    public ResourceManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ResourceManagerException(String message) {
        super(message);
    }
}
