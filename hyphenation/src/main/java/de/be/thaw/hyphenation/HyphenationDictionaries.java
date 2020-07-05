package de.be.thaw.hyphenation;

import de.be.thaw.hyphenation.loader.exception.HyphenationDictionaryLoadException;
import de.be.thaw.hyphenation.loader.impl.FileHyphenationDictionaryLoader;
import de.be.thaw.info.model.language.Language;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Collection of available hyphenation dictionaries.
 */
public class HyphenationDictionaries {

    /**
     * Available dictionaries.
     */
    private static final Map<Language, HyphenationDictionary> DICTIONARIES = new HashMap<>();

    static {
        for (Language language : Language.values()) {
            addDictionaryForLanguage(language);
        }
    }

    /**
     * Get a hyphenation dictionary by the passed language.
     *
     * @param language to get dictionary for
     * @return dictionary
     */
    public static Optional<HyphenationDictionary> getDictionary(Language language) {
        return Optional.ofNullable(DICTIONARIES.get(language));
    }

    /**
     * Add a dictionary for the passed language.
     *
     * @param language to add dictionary for
     */
    private static void addDictionaryForLanguage(Language language) {
        String path = String.format("/hyphenation/%s/dictionary.dic", language.getCode());

        InputStream stream = HyphenationDictionaries.class.getResourceAsStream(path);
        if (stream != null) { // If the resource has been found
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                DICTIONARIES.put(language, new FileHyphenationDictionaryLoader().load(reader));
            } catch (HyphenationDictionaryLoadException | IOException e) {
                throw new RuntimeException(e); // Must not happen!
            }
        }
    }

}
