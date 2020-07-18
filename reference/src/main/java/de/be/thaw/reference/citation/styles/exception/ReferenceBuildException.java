package de.be.thaw.reference.citation.styles.exception;

/**
 * Exception thrown when the reference or citation could not be build.
 */
public class ReferenceBuildException extends Exception {

    public ReferenceBuildException(String message) {
        super(message);
    }

    public ReferenceBuildException(String message, Throwable cause) {
        super(message, cause);
    }

}
