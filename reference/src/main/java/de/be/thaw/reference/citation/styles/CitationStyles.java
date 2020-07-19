package de.be.thaw.reference.citation.styles;

import de.be.thaw.reference.citation.CitationStyle;
import de.be.thaw.reference.citation.styles.apa.APA;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Collection of all available citation styles.
 */
public class CitationStyles {

    /**
     * Mapping of all available citation styles by their name.
     */
    private static final Map<String, CitationStyle> STYLE_LOOKUP = new HashMap<>();

    static {
        registerCitationStyle(new APA());
    }

    /**
     * Register a citation style.
     *
     * @param style to register
     */
    public static void registerCitationStyle(CitationStyle style) {
        if (getCitationStyle(style.getName()).isPresent()) {
            throw new IllegalStateException(String.format(
                    "Citation style naming collision detected: Citation style with name '%s' cannot be registered multiple times",
                    style.getName()
            ));
        }

        STYLE_LOOKUP.put(style.getName().toLowerCase(), style);
    }

    /**
     * Get the citation style by its name.
     *
     * @param name to get citation style with
     * @return the citation style of an empty optional if not found
     */
    public static Optional<CitationStyle> getCitationStyle(String name) {
        return Optional.ofNullable(STYLE_LOOKUP.get(name.toLowerCase()));
    }

    /**
     * Get the default citation style.
     *
     * @return default citation style
     */
    public static CitationStyle getDefault() {
        return getCitationStyle("APA").orElseThrow();
    }

}
