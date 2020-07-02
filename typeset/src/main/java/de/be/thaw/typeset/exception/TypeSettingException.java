package de.be.thaw.typeset.exception;

/**
 * Exception thrown when typesetting failed.
 */
public class TypeSettingException extends Exception {

    public TypeSettingException(String message, Throwable cause) {
        super(message, cause);
    }

}
