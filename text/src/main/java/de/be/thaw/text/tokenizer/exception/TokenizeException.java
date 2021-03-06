package de.be.thaw.text.tokenizer.exception;

/**
 * Exception thrown when a problem occurred in the tokenizing process.
 */
public class TokenizeException extends Exception {

    public TokenizeException(String message) {
        super(message);
    }

    public TokenizeException(String message, Throwable cause) {
        super(message, cause);
    }

    public TokenizeException(Throwable cause) {
        super(cause);
    }

}
