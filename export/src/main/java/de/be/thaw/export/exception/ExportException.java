package de.be.thaw.export.exception;

/**
 * Exception thrown when an error occurred in the export.
 */
public class ExportException extends Exception {

    public ExportException(String message) {
        super(message);
    }

    public ExportException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExportException(Throwable cause) {
        super(cause);
    }

}
