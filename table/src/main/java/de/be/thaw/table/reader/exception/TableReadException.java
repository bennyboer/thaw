package de.be.thaw.table.reader.exception;

/**
 * Exception that should be thrown when a table could not be read.
 */
public class TableReadException extends Exception {

    public TableReadException(String message) {
        super(message);
    }

    public TableReadException(Throwable cause) {
        super(cause);
    }

}
