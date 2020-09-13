package de.be.thaw.reference.citation.exception;

/**
 * Exception thrown when a citation manager could not be instantiated.
 */
public class CitationManagerCreationException extends Exception {

    public CitationManagerCreationException(String message, Throwable cause) {
        super(message, cause);
    }

}
