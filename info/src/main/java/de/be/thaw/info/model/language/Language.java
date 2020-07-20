package de.be.thaw.info.model.language;

import java.util.Locale;

/**
 * Enumeration of languages supported by the application.
 */
public enum Language {

    GERMAN("de", Locale.GERMAN),
    ENGLISH("en", Locale.ENGLISH),
    OTHER(null, Locale.getDefault());

    /**
     * Code of the language.
     */
    private final String code;

    /**
     * Locale of the language.
     */
    private final Locale locale;

    Language(String code, Locale locale) {
        this.code = code;
        this.locale = locale;
    }

    /**
     * Get the code of the language.
     *
     * @return code
     */
    public String getCode() {
        return code;
    }

    /**
     * Get the languages locale.
     *
     * @return locale
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Get the language for the passed code.
     *
     * @param code to get language for
     * @return the language or Language.OTHER
     */
    public static Language forCode(String code) {
        for (Language language : Language.values()) {
            if (language.getCode().equalsIgnoreCase(code)) {
                return language;
            }
        }

        return Language.OTHER;
    }

}
