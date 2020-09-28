package de.be.thaw.style.model.style;

import de.be.thaw.style.model.style.value.StyleValue;
import de.be.thaw.style.model.style.value.StyleValueCollection;
import de.be.thaw.style.parser.value.StyleValueParser;
import de.be.thaw.style.parser.value.exception.StyleValueParseException;
import de.be.thaw.style.parser.value.impl.BooleanValueParser;
import de.be.thaw.style.parser.value.impl.BorderSideValueParser;
import de.be.thaw.style.parser.value.impl.ColorValueParser;
import de.be.thaw.style.parser.value.impl.DoubleValueParser;
import de.be.thaw.style.parser.value.impl.FillValueParser;
import de.be.thaw.style.parser.value.impl.FontKerningValueParser;
import de.be.thaw.style.parser.value.impl.FontVariantValueParser;
import de.be.thaw.style.parser.value.impl.HorizontalAlignmentValueParser;
import de.be.thaw.style.parser.value.impl.InsetsValueParser;
import de.be.thaw.style.parser.value.impl.ListStyleTypeValueParser;
import de.be.thaw.style.parser.value.impl.StringValueParser;
import de.be.thaw.util.unit.Unit;

import java.util.HashMap;
import java.util.Map;

/**
 * Available style types.
 */
public enum StyleType {

    FONT_FAMILY("font-family", new StringValueParser()),
    FONT_SIZE("font-size", new DoubleValueParser(Unit.POINTS)),
    FONT_VARIANT("font-variant", new FontVariantValueParser()),
    FONT_KERNING("font-kerning", new FontKerningValueParser()),
    INLINE_CODE_FONT_FAMILY("inline-code-font-family", new StringValueParser()),

    WIDTH("width", new DoubleValueParser(Unit.MILLIMETER)),
    HEIGHT("height", new DoubleValueParser(Unit.MILLIMETER)),

    COLOR("color", new ColorValueParser()),

    BACKGROUND_COLOR("background-color", new ColorValueParser()),
    BACKGROUND("background", new StyleValueParser() {
        private final ColorValueParser colorValueParser = new ColorValueParser();

        @Override
        public StyleValue parse(String src) throws StyleValueParseException {
            return new StyleValueCollection(Map.of(StyleType.BACKGROUND_COLOR, colorValueParser.parse(src)));
        }
    }),

    LINE_HEIGHT("line-height", new DoubleValueParser(Unit.UNITARY)),
    FIRST_LINE_INDENT("first-line-indent", new DoubleValueParser(Unit.MILLIMETER)),

    SHOW_LINE_NUMBERS("show-line-numbers", new BooleanValueParser()),
    LINE_NUMBER_FONT_FAMILY("line-number-font-family", new StringValueParser()),
    LINE_NUMBER_FONT_SIZE("line-number-font-size", new DoubleValueParser(Unit.POINTS)),
    LINE_NUMBER_COLOR("line-number-color", new ColorValueParser()),

    TEXT_ALIGN("text-align", new HorizontalAlignmentValueParser()),
    TEXT_JUSTIFY("text-justify", new BooleanValueParser()),

    MARGIN_LEFT("margin-left", new DoubleValueParser(Unit.MILLIMETER)),
    MARGIN_RIGHT("margin-right", new DoubleValueParser(Unit.MILLIMETER)),
    MARGIN_TOP("margin-top", new DoubleValueParser(Unit.MILLIMETER)),
    MARGIN_BOTTOM("margin-bottom", new DoubleValueParser(Unit.MILLIMETER)),
    MARGIN("margin", new InsetsValueParser(MARGIN_TOP, MARGIN_RIGHT, MARGIN_BOTTOM, MARGIN_LEFT)),

    PADDING_LEFT("padding-left", new DoubleValueParser(Unit.MILLIMETER)),
    PADDING_RIGHT("padding-right", new DoubleValueParser(Unit.MILLIMETER)),
    PADDING_TOP("padding-top", new DoubleValueParser(Unit.MILLIMETER)),
    PADDING_BOTTOM("padding-bottom", new DoubleValueParser(Unit.MILLIMETER)),
    PADDING("padding", new InsetsValueParser(PADDING_TOP, PADDING_RIGHT, PADDING_BOTTOM, PADDING_LEFT)),

    HEADER("header", new StringValueParser()),
    FOOTER("footer", new StringValueParser()),

    FILL("fill", new FillValueParser()),
    FILL_SIZE("fill-size", new DoubleValueParser(Unit.MILLIMETER)),

    LIST_STYLE_TYPE("list-style-type", new ListStyleTypeValueParser()),
    COUNTER_STYLE("counter-style", new ListStyleTypeValueParser()),

    NUMBERING("numbering", new StringValueParser()), // TODO Implement

    BORDER_TOP_STYLE("border-top-style", new FillValueParser()),
    BORDER_TOP_WIDTH("border-top-width", new StringValueParser()),
    BORDER_TOP_COLOR("border-top-color", new ColorValueParser()),
    BORDER_TOP("border-top", new BorderSideValueParser(
            new StyleType[]{BORDER_TOP_WIDTH},
            new StyleType[]{BORDER_TOP_STYLE},
            new StyleType[]{BORDER_TOP_COLOR}
    )),
    BORDER_BOTTOM_STYLE("border-bottom-style", new FillValueParser()),
    BORDER_BOTTOM_WIDTH("border-bottom-width", new StringValueParser()),
    BORDER_BOTTOM_COLOR("border-bottom-color", new ColorValueParser()),
    BORDER_BOTTOM("border-bottom", new BorderSideValueParser(
            new StyleType[]{BORDER_BOTTOM_WIDTH},
            new StyleType[]{BORDER_BOTTOM_STYLE},
            new StyleType[]{BORDER_BOTTOM_COLOR}
    )),
    BORDER_LEFT_STYLE("border-left-style", new FillValueParser()),
    BORDER_LEFT_WIDTH("border-left-width", new StringValueParser()),
    BORDER_LEFT_COLOR("border-left-color", new ColorValueParser()),
    BORDER_LEFT("border-left", new BorderSideValueParser(
            new StyleType[]{BORDER_LEFT_WIDTH},
            new StyleType[]{BORDER_LEFT_STYLE},
            new StyleType[]{BORDER_LEFT_COLOR}
    )),
    BORDER_RIGHT_STYLE("border-right-style", new FillValueParser()),
    BORDER_RIGHT_WIDTH("border-right-width", new StringValueParser()),
    BORDER_RIGHT_COLOR("border-right-color", new ColorValueParser()),
    BORDER_RIGHT("border-right", new BorderSideValueParser(
            new StyleType[]{BORDER_RIGHT_WIDTH},
            new StyleType[]{BORDER_RIGHT_STYLE},
            new StyleType[]{BORDER_RIGHT_COLOR}
    )),
    BORDER("border", new BorderSideValueParser(
            new StyleType[]{BORDER_TOP_WIDTH, BORDER_RIGHT_WIDTH, BORDER_BOTTOM_WIDTH, BORDER_LEFT_WIDTH},
            new StyleType[]{BORDER_TOP_STYLE, BORDER_RIGHT_STYLE, BORDER_BOTTOM_STYLE, BORDER_LEFT_STYLE},
            new StyleType[]{BORDER_TOP_COLOR, BORDER_RIGHT_COLOR, BORDER_BOTTOM_COLOR, BORDER_LEFT_COLOR}
    )),
    BORDER_COLOR("border-color", new StyleValueParser() {
        private final ColorValueParser colorValueParser = new ColorValueParser();

        @Override
        public StyleValue parse(String src) throws StyleValueParseException {
            StyleValue color = colorValueParser.parse(src);

            return new StyleValueCollection(Map.ofEntries(
                    Map.entry(StyleType.BORDER_TOP_COLOR, color),
                    Map.entry(StyleType.BORDER_RIGHT_COLOR, color),
                    Map.entry(StyleType.BORDER_BOTTOM_COLOR, color),
                    Map.entry(StyleType.BORDER_LEFT_COLOR, color)
            ));
        }
    }),
    BORDER_WIDTH("border-width", new InsetsValueParser(BORDER_TOP_WIDTH, BORDER_RIGHT_WIDTH, BORDER_BOTTOM_WIDTH, BORDER_LEFT_WIDTH)),
    BORDER_STYLE("border-style", new StyleValueParser() {
        private final FillValueParser fillValueParser = new FillValueParser();

        @Override
        public StyleValue parse(String src) throws StyleValueParseException {
            StyleValue fillStyle = fillValueParser.parse(src);

            return new StyleValueCollection(Map.ofEntries(
                    Map.entry(StyleType.BORDER_TOP_STYLE, fillStyle),
                    Map.entry(StyleType.BORDER_RIGHT_STYLE, fillStyle),
                    Map.entry(StyleType.BORDER_BOTTOM_STYLE, fillStyle),
                    Map.entry(StyleType.BORDER_LEFT_STYLE, fillStyle)
            ));
        }
    }),
    BORDER_RADIUS_TOP("border-radius-top", new DoubleValueParser(Unit.MILLIMETER)),
    BORDER_RADIUS_RIGHT("border-radius-right", new DoubleValueParser(Unit.MILLIMETER)),
    BORDER_RADIUS_BOTTOM("border-radius-bottom", new DoubleValueParser(Unit.MILLIMETER)),
    BORDER_RADIUS_LEFT("border-radius-left", new DoubleValueParser(Unit.MILLIMETER)),
    BORDER_RADIUS("border-radius", new InsetsValueParser(BORDER_RADIUS_TOP, BORDER_RADIUS_RIGHT, BORDER_RADIUS_BOTTOM, BORDER_RADIUS_LEFT)),

    INTERNAL_LINK_COLOR("internal-link-color", new ColorValueParser()),
    EXTERNAL_LINK_COLOR("external-link-color", new ColorValueParser()),

    FOOT_NOTE_LINE_LENGTH("foot-note-line-length", new DoubleValueParser(Unit.MILLIMETER)),
    FOOT_NOTE_LINE_SIZE("foot-note-line-size", new DoubleValueParser(Unit.MILLIMETER)),
    FOOT_NOTE_LINE_COLOR("foot-note-line-color", new ColorValueParser()),

    HYPHENATION("hyphenation", new BooleanValueParser());

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
