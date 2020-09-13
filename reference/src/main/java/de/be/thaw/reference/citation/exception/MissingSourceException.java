package de.be.thaw.reference.citation.exception;

/**
 * Exception thrown when a source should be cited, that is not listed in the bibliography.
 */
public class MissingSourceException extends Exception {

    public MissingSourceException(String message, Throwable cause) {
        super(message, cause);
    }

}
