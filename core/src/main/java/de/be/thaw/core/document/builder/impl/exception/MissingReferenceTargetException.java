package de.be.thaw.core.document.builder.impl.exception;

/**
 * Exception thrown when a reference target is missing.
 */
public class MissingReferenceTargetException extends DocumentBuildException {

    public MissingReferenceTargetException(String message) {
        super(message);
    }

}
