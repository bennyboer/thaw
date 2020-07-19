package de.be.thaw.reference.citation.source.model.parser.exception;

/**
 * Exception thrown when a source model could not be parsed.
 */
public class ParseException extends Exception {

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

}
