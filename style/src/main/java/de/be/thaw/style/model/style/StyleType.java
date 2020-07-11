package de.be.thaw.style.model.style;

import java.util.HashMap;
import java.util.Map;

/**
 * Available style types.
 */
public enum StyleType {

    FONT("font"),
    SIZE("size"),
    INSETS("insets"),
    BACKGROUND("background"),
    COLOR("color"),
    TEXT("text"),
    REFERENCE("reference");

    /**
     * Lookup of style types by their key.
     */
    private static Map<String, StyleType> LOOKUP;

    /**
     * Key of the style.
     */
    private final String key;

    StyleType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    /**
     * Get the style type for the passed key.
     *
     * @param key to get style type for
     * @return style type (or null)
     */
    public static StyleType forKey(String key) {
        if (LOOKUP == null) {
            LOOKUP = new HashMap<>();

            for (StyleType type : values()) {
                LOOKUP.put(type.getKey(), type);
            }
        }

        return LOOKUP.get(key);
    }

}
