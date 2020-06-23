package de.be.thaw.text.parser.exception;

/**
 * Exception thrown when an error occurred during parsing.
 */
public class ParseException extends Exception {

    public ParseException(String message) {
        super(message);
    }

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ParseException(Throwable cause) {
        super(cause);
    }

}
