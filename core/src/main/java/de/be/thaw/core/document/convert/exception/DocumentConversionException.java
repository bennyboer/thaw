package de.be.thaw.core.document.convert.exception;

/**
 * Exception thrown in case the document could not be converted properly.
 */
public class DocumentConversionException extends Exception {

    public DocumentConversionException(String message) {
        super(message);
    }

    public DocumentConversionException(Throwable cause) {
        super(cause);
    }

}
