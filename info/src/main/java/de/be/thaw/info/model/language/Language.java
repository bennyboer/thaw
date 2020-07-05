package de.be.thaw.info.model.language;

/**
 * Enumeration of languages supported by the application.
 */
public enum Language {

    GERMAN("de"),
    ENGLISH("en"),
    OTHER(null);

    /**
     * Code of the language.
     */
    private final String code;

    Language(String code) {
        this.code = code;
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
