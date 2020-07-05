package de.be.thaw.info.parser.exception;

/**
 * Exception thrown when the Thaw document info format could not be parsed.
 */
public class ParseException extends Exception {

    public ParseException(String message, Throwable cause) {
        super(message, cause);
    }

}
