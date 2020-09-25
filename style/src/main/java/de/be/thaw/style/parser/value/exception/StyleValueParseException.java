package de.be.thaw.style.parser.value.exception;

/**
 * Exception thrown when a style value could not be parsed.
 */
public class StyleValueParseException extends Exception {

    public StyleValueParseException(String message) {
        super(message);
    }

    public StyleValueParseException(Throwable cause) {
        super(cause);
    }

}
