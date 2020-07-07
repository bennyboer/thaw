package de.be.thaw.style.parser.exception;

/**
 * Exception thrown in case the style model could not be parsed.
 */
public class ParseException extends Exception {

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

}
