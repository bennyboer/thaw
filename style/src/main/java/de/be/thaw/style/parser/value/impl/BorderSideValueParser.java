package de.be.thaw.style.parser.value.impl;

import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.value.StyleValue;
import de.be.thaw.style.model.style.value.StyleValueCollection;
import de.be.thaw.style.parser.value.StyleValueParser;
import de.be.thaw.style.parser.value.exception.StyleValueParseException;
import de.be.thaw.util.unit.Unit;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Style value parser for border sides.
 */
public class BorderSideValueParser implements StyleValueParser {

    private final DoubleValueParser doubleValueParser = new DoubleValueParser(Unit.MILLIMETER);

    private final ColorValueParser colorValueParser = new ColorValueParser();

    private final FillValueParser fillValueParser = new FillValueParser();

    private final StyleType[] widthTypes;

    private final StyleType[] styleTypes;

    private final StyleType[] colorTypes;

    public BorderSideValueParser(StyleType[] widthTypes, StyleType[] styleTypes, StyleType[] colorTypes) {
        this.widthTypes = widthTypes;
        this.styleTypes = styleTypes;
        this.colorTypes = colorTypes;
    }

    @Override
    public StyleValue parse(String src, File workingDirectory) throws StyleValueParseException {
        src = src.trim();

        String[] parts = src.split(" ");
        if (parts.length != 3) {
            throw new StyleValueParseException("A border specification expects the format 'border: border-width border-style border-color;'");
        }

        StyleValue borderWidth = doubleValueParser.parse(parts[0], workingDirectory);
        StyleValue fillValue = fillValueParser.parse(parts[1], workingDirectory);
        StyleValue colorValue = colorValueParser.parse(parts[2], workingDirectory);

        Map<StyleType, StyleValue> result = new HashMap<>();

        for (StyleType type : widthTypes) {
            result.put(type, borderWidth);
        }

        for (StyleType type : styleTypes) {
            result.put(type, fillValue);
        }

        for (StyleType type : colorTypes) {
            result.put(type, colorValue);
        }

        return new StyleValueCollection(result);
    }

}
