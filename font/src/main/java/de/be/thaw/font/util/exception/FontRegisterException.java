package de.be.thaw.font.util.exception;

/**
 * Exception thrown when a font could not be registered.
 */
public class FontRegisterException extends Exception {

    public FontRegisterException(String message) {
        super(message);
    }

    public FontRegisterException(Throwable cause) {
        super(cause);
    }

}
