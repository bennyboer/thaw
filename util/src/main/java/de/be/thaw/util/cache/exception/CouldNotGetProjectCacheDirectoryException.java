package de.be.thaw.util.cache.exception;

/**
 * Exception thrown when the project caching directory could not be fetched.
 */
public class CouldNotGetProjectCacheDirectoryException extends Exception {

    public CouldNotGetProjectCacheDirectoryException(String message, Throwable cause) {
        super(message, cause);
    }

}
