package de.be.thaw.export.pdf.font.exception;

/**
 * Exception thrown when a font could not be parsed.
 */
public class FontParseException extends Exception {

    public FontParseException(String message) {
        super(message);
    }

    public FontParseException(Throwable cause) {
        super(cause);
    }

}
