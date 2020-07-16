package de.be.thaw.typeset.exception;

/**
 * Exception thrown when typesetting failed.
 */
public class TypeSettingException extends Exception {

    public TypeSettingException() {
        super();
    }

    public TypeSettingException(String message) {
        super(message);
    }

    public TypeSettingException(Throwable cause) {
        super(cause);
    }

    public TypeSettingException(String message, Throwable cause) {
        super(message, cause);
    }

}
