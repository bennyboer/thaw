package de.be.thaw.core.document.builder.impl.exception;

/**
 * Exception thrown when the document could not be built properly.
 */
public class DocumentBuildException extends Exception {

    public DocumentBuildException(String message) {
        super(message);
    }

    public DocumentBuildException(Throwable cause) {
        super(cause);
    }

    public DocumentBuildException(String message, Throwable cause) {
        super(message, cause);
    }

}
