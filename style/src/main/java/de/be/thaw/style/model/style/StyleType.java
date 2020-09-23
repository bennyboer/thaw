package de.be.thaw.style.model.style;

import de.be.thaw.style.parser.value.StyleValueParser;
import de.be.thaw.style.parser.value.impl.BooleanValueParser;
import de.be.thaw.style.parser.value.impl.ColorValueParser;
import de.be.thaw.style.parser.value.impl.DoubleValueParser;
import de.be.thaw.style.parser.value.impl.FillValueParser;
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

    MARGIN("margin", new StringValueParser()), // TODO Define the margin property
    MARGIN_LEFT("margin-left", new DoubleValueParser()),
    MARGIN_RIGHT("margin-right", new DoubleValueParser()),
    MARGIN_TOP("margin-top", new DoubleValueParser()),
    MARGIN_BOTTOM("margin-bottom", new DoubleValueParser()),

    PADDING("padding", new StringValueParser()), // TODO Define the padding property
    PADDING_LEFT("padding-left", new DoubleValueParser()),
    PADDING_RIGHT("padding-right", new DoubleValueParser()),
    PADDING_TOP("padding-top", new DoubleValueParser()),
    PADDING_BOTTOM("padding-bottom", new DoubleValueParser()),

    HEADER("header", new StringValueParser()),
    FOOTER("footer", new StringValueParser()),

    FILL("fill", new FillValueParser()),
    FILL_SIZE("fill-size", new DoubleValueParser()),

    // TODO Define and implement the following four properties with a proper parser
    NUMBERING("numbering", new StringValueParser()),
    COUNTER_STYLE("counter-style", new StringValueParser()),
    LIST_STYLE_TYPE("list-style-type", new StringValueParser()),

    // TODO Define and implement the following border-related properties (including proper parsers)
    BORDER("border", new StringValueParser()),
    BORDER_TOP("border-top", new StringValueParser()),
    BORDER_BOTTOM("border-bottom", new StringValueParser()),
    BORDER_LEFT("border-left", new StringValueParser()),
    BORDER_RIGHT("border-right", new StringValueParser()),
    BORDER_COLOR("border-color", new StringValueParser()),
    BORDER_WIDTH("border-width", new StringValueParser()),
    BORDER_STYLE("border-style", new StringValueParser()),
    BORDER_RADIUS("border-radius", new StringValueParser()),

    INTERNAL_LINK_COLOR("internal-link-color", new ColorValueParser()),
    EXTERNAL_LINK_COLOR("external-link-color", new ColorValueParser());

    /**
     * Lookup of style types by their key.
     */
    private static final Map<String, StyleType> PROPERTY_KEY_LOOKUP = new HashMap<>();

    static {
        for (StyleType type : values()) {
            PROPERTY_KEY_LOOKUP.put(type.getKey(), type);
        }
    }

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
        return PROPERTY_KEY_LOOKUP.get(key);
    }

}
