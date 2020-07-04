package de.be.thaw.core.util.exception;

/**
 * Exception thrown when the current operating system could not be determined.
 */
public class CouldNotDetermineOperatingSystemException extends Exception {

    public CouldNotDetermineOperatingSystemException(String message) {
        super(message);
    }

}
