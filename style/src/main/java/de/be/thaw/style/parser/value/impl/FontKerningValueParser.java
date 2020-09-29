package de.be.thaw.style.parser.value.impl;

import de.be.thaw.font.util.KerningMode;
import de.be.thaw.style.model.style.value.KerningModeStyleValue;
import de.be.thaw.style.model.style.value.StyleValue;
import de.be.thaw.style.parser.value.StyleValueParser;
import de.be.thaw.style.parser.value.exception.StyleValueParseException;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Style value parser for kerning mode values.
 */
public class FontKerningValueParser implements StyleValueParser {

    @Override
    public StyleValue parse(String src, File workingDirectory) throws StyleValueParseException {
        try {
            return new KerningModeStyleValue(KerningMode.valueOf(src.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new StyleValueParseException(String.format(
                    "Could not find kerning mode with the name '%s'. Available values are: %s",
                    src,
                    Arrays.stream(KerningMode.values()).map(KerningMode::name).map(String::toLowerCase).collect(Collectors.joining(", "))
            ));
        }
    }

}
