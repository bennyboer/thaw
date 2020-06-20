package de.be.thaw.text.tokenizer.exception;

/**
 * Exception thrown when the tokenizer is in an invalid state.
 */
public class InvalidStateException extends TokenizeException {

    public InvalidStateException() {
    }

    public InvalidStateException(String message) {
        super(message);
    }

    public InvalidStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidStateException(Throwable cause) {
        super(cause);
    }

    public InvalidStateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
