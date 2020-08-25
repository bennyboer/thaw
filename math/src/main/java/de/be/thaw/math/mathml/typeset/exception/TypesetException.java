package de.be.thaw.math.mathml.typeset.exception;

/**
 * Exception thrown when a MathML expression cannot be typeset.
 */
public class TypesetException extends Exception {

    public TypesetException(String message) {
        super(message);
    }

    public TypesetException(Throwable cause) {
        super(cause);
    }

}
