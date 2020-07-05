package de.be.thaw.hyphenation.loader.exception;

/**
 * Exception thrown when a hyphenation dictionary could not be loaded.
 */
public class HyphenationDictionaryLoadException extends Exception {

    public HyphenationDictionaryLoadException(String message, Throwable cause) {
        super(message, cause);
    }

}
