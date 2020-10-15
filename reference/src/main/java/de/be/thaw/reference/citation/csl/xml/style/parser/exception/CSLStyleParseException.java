package de.be.thaw.reference.citation.csl.xml.style.parser.exception;

/**
 * Exception thrown when a CSL style could not be parsed.
 */
public class CSLStyleParseException extends Exception {

    public CSLStyleParseException(String message) {
        super(message);
    }

    public CSLStyleParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public CSLStyleParseException(Throwable cause) {
        super(cause);
    }

}
