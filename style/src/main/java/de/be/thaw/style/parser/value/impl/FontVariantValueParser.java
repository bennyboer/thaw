package de.be.thaw.style.parser.value.impl;

import de.be.thaw.font.util.FontVariant;
import de.be.thaw.style.model.style.value.FontVariantStyleValue;
import de.be.thaw.style.model.style.value.StyleValue;
import de.be.thaw.style.parser.value.StyleValueParser;
import de.be.thaw.style.parser.value.exception.StyleValueParseException;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Style value parser for font variant values.
 */
public class FontVariantValueParser implements StyleValueParser {

    @Override
    public StyleValue parse(String src, File workingDirectory) throws StyleValueParseException {
        try {
            return new FontVariantStyleValue(FontVariant.valueOf(src.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new StyleValueParseException(String.format(
                    "Could not find font variant with the name '%s'. Available values are: %s",
                    src,
                    Arrays.stream(FontVariant.values()).map(FontVariant::name).map(String::toLowerCase).collect(Collectors.joining(", "))
            ));
        }
    }

}
