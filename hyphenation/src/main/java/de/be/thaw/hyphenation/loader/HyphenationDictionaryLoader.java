package de.be.thaw.hyphenation.loader;

import de.be.thaw.hyphenation.HyphenationDictionary;
import de.be.thaw.hyphenation.loader.exception.HyphenationDictionaryLoadException;

import java.io.Reader;

/**
 * Loader for a hyphenation dictionary.
 */
public interface HyphenationDictionaryLoader {

    /**
     * Load a hyphenation dictionary from the passed reader.
     *
     * @param reader to use
     * @return the loaded hyphenation dictionary
     * @throws HyphenationDictionaryLoadException in case the dictionary could not be loaded
     */
    HyphenationDictionary load(Reader reader) throws HyphenationDictionaryLoadException;

}
