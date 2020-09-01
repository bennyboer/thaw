package de.be.thaw.code.syntax.exception;

/**
 * Exception thrown when code could not be highlighted.
 */
public class HighlightException extends Exception {

    public HighlightException(String message) {
        super(message);
    }

    public HighlightException(String message, Throwable cause) {
        super(message, cause);
    }

}
