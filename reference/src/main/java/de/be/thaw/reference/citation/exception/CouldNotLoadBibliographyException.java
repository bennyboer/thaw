package de.be.thaw.reference.citation.exception;

/**
 * Exception thrown when a bibliography could not be loaded.
 */
public class CouldNotLoadBibliographyException extends Exception {

    public CouldNotLoadBibliographyException(String message, Throwable cause) {
        super(message, cause);
    }

}
