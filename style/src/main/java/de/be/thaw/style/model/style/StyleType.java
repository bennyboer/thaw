package de.be.thaw.style.model.style;

import de.be.thaw.style.parser.value.StyleValueParser;
import de.be.thaw.style.parser.value.impl.BooleanValueParser;
import de.be.thaw.style.parser.value.impl.ColorValueParser;
import de.be.thaw.style.parser.value.impl.DoubleValueParser;
import de.be.thaw.style.parser.value.impl.FontKerningValueParser;
import de.be.thaw.style.parser.value.impl.FontVariantValueParser;
import de.be.thaw.style.parser.value.impl.HorizontalAlignmentValueParser;
import de.be.thaw.style.parser.value.impl.StringValueParser;

import java.util.HashMap;
import java.util.Map;

/**
 * Available style types.
 */
public enum StyleType {

    FONT_FAMILY("font-family", new StringValueParser()),
    FONT_SIZE("font-size", new DoubleValueParser()),
    FONT_VARIANT("font-variant", new FontVariantValueParser()),
    FONT_KERNING("font-kerning", new FontKerningValueParser()),
    INLINE_CODE_FONT_FAMILY("inline-code-font-family", new StringValueParser()),

    WIDTH("width", new DoubleValueParser()),
    HEIGHT("height", new DoubleValueParser()),

    COLOR("color", new ColorValueParser()),

    BACKGROUND("background", new ColorValueParser()),
    BACKGROUND_COLOR("background-color", new ColorValueParser()),

    LINE_HEIGHT("line-height", new DoubleValueParser()),
    FIRST_LINE_INDENT("first-line-indent", new DoubleValueParser()),

    SHOW_LINE_NUMBERS("show-line-numbers", new BooleanValueParser()),
    LINE_NUMBER_FONT_FAMILY("line-number-font-family", new StringValueParser()),
    LINE_NUMBER_FONT_SIZE("line-number-font-size", new DoubleValueParser()),
    LINE_NUMBER_COLOR("line-number-color", new ColorValueParser()),

    TEXT_ALIGN("text-align", new HorizontalAlignmentValueParser()),
    TEXT_JUSTIFY("text-justify", new BooleanValueParser()),

    //    MARGIN("margin"),
    MARGIN_LEFT("margin-left", new DoubleValueParser()),
    MARGIN_RIGHT("margin-right", new DoubleValueParser()),
    MARGIN_TOP("margin-top", new DoubleValueParser()),
    MARGIN_BOTTOM("margin-bottom", new DoubleValueParser()),

    //    PADDING("padding"),
    PADDING_LEFT("padding-left", new DoubleValueParser()),
    PADDING_RIGHT("padding-right", new DoubleValueParser()),
    PADDING_TOP("padding-top", new DoubleValueParser()),
    PADDING_BOTTOM("padding-bottom", new DoubleValueParser()),

    HEADER("header", new StringValueParser()),
    FOOTER("footer", new StringValueParser()),

//    NUMBERING("numbering"),
//    COUNTER_STYLE("counter-style"),
//    LIST_STYLE_TYPE("list-style-type"),
//    FILL("fill"),

//    BORDER("border"),
//    BORDER_TOP("border-top"),
//    BORDER_BOTTOM("border-bottom"),
//    BORDER_LEFT("border-left"),
//    BORDER_RIGHT("border-right"),
//    BORDER_COLOR("border-color"),
//    BORDER_WIDTH("border-width"),
//    BORDER_STYLE("border-style"),
//    BORDER_RADIUS("border-radius"),

    INTERNAL_LINK_COLOR("internal-link-color", new ColorValueParser()),
    EXTERNAL_LINK_COLOR("external-link-color", new ColorValueParser());

    /**
     * Lookup of style types by their key.
     */
    private static Map<String, StyleType> LOOKUP;

    /**
     * Key of the style.
     */
    private final String key;

    /**
     * Parser for the style type.
     */
    private final StyleValueParser parser;

    StyleType(String key, StyleValueParser parser) {
        this.key = key;
        this.parser = parser;
    }

    public String getKey() {
        return key;
    }

    public StyleValueParser getParser() {
        return parser;
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
