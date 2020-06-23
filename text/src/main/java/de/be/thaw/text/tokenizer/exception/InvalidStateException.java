package de.be.thaw.text.tokenizer.exception;

/**
 * Exception thrown when the tokenizer is in an invalid state.
 */
public class InvalidStateException extends TokenizeException {

    public InvalidStateException(String message) {
        super(message);
    }

    public InvalidStateException(String message, Throwable cause) {
        super(message, cause);
    }

}
