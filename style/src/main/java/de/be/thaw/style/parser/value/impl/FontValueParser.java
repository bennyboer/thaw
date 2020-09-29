package de.be.thaw.style.parser.value.impl;

import de.be.thaw.style.model.style.value.FontStyleValue;
import de.be.thaw.style.model.style.value.StyleValue;
import de.be.thaw.style.parser.value.StyleValueParser;
import de.be.thaw.style.parser.value.exception.StyleValueParseException;

import java.io.File;

/**
 * Parser for font style values.
 */
public class FontValueParser implements StyleValueParser {

    @Override
    public StyleValue parse(String src, File workingDirectory) throws StyleValueParseException {
        src = src.trim();

        boolean isURL = src.startsWith("url(");
        if (isURL) {
            String filePath = src.substring(4, src.length() - 1);
            File fontFile = new File(filePath);
            if (fontFile.exists()) {
                return new FontStyleValue(fontFile);
            } else {
                // Try a relative path.
                fontFile = new File(workingDirectory, filePath);
                if (fontFile.exists()) {
                    return new FontStyleValue(fontFile);
                } else {
                    throw new StyleValueParseException(String.format(
                            "Could not resolve font file at '%s'",
                            filePath
                    ));
                }
            }

        } else {
            return new FontStyleValue(src);
        }
    }

}
