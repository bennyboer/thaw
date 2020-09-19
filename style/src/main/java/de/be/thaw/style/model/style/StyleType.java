package de.be.thaw.style.model.style;

import java.util.HashMap;
import java.util.Map;

/**
 * Available style types.
 */
public enum StyleType {

    FONT_FAMILY("font-family"),
    FONT_SIZE("font-size"),
    FONT_VARIANT("font-variant"),
    FONT_KERNING("font-kerning"),
    INLINE_CODE_FONT_FAMILY("inline-code-font-family"),

    WIDTH("width"),
    HEIGHT("height"),

    COLOR("color"),

    BACKGROUND("background"),
    BACKGROUND_COLOR("background-color"),

    LINE_HEIGHT("line-height"),

    TEXT_ALIGN("text-align"),
    TEXT_JUSTIFY("text-justify"),

    MARGIN("margin"),
    MARGIN_LEFT("margin-left"),
    MARGIN_RIGHT("margin-right"),
    MARGIN_TOP("margin-top"),
    MARGIN_BOTTOM("margin-bottom"),

    PADDING("padding"),
    PADDING_LEFT("padding-left"),
    PADDING_RIGHT("padding-right"),
    PADDING_TOP("padding-top"),
    PADDING_BOTTOM("padding-bottom"),

    HEADER("header"),
    FOOTER("footer"),

    NUMBERING("numbering"),
    COUNTER_STYLE("counter-style"),
    LIST_STYLE_TYPE("list-style-type"),
    FILL("fill"),

    BORDER("border"),
    BORDER_TOP("border-top"),
    BORDER_BOTTOM("border-bottom"),
    BORDER_LEFT("border-left"),
    BORDER_RIGHT("border-right"),
    BORDER_COLOR("border-color"),
    BORDER_WIDTH("border-width"),
    BORDER_STYLE("border-style"),
    BORDER_RADIUS("border-radius");

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
