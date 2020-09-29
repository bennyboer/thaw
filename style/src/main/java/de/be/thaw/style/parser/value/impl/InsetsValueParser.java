package de.be.thaw.style.parser.value.impl;

import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.value.DoubleStyleValue;
import de.be.thaw.style.model.style.value.StyleValue;
import de.be.thaw.style.model.style.value.StyleValueCollection;
import de.be.thaw.style.parser.value.exception.StyleValueParseException;
import de.be.thaw.util.unit.Unit;

import java.io.File;
import java.util.Map;

/**
 * Parser for inset values (margin, padding for example).
 */
public class InsetsValueParser extends DoubleValueParser {

    /**
     * The style types that are set by the parser.
     */
    private final StyleType[] typesToSet;

    public InsetsValueParser(StyleType... typesToSet) {
        super(Unit.MILLIMETER);

        if (typesToSet.length != 4) {
            throw new IllegalArgumentException("The InsetsValueParser needs exactly four style types to set");
        }

        this.typesToSet = typesToSet;
    }

    @Override
    public StyleValue parse(String src, File workingDirectory) throws StyleValueParseException {
        src = src.trim();

        String[] parts = src.split(" ");
        DoubleStyleValue top;
        DoubleStyleValue right;
        DoubleStyleValue bottom;
        DoubleStyleValue left;
        if (parts.length == 1) {
            DoubleStyleValue value = (DoubleStyleValue) super.parse(parts[0], workingDirectory);

            top = value;
            right = value;
            bottom = value;
            left = value;
        } else if (parts.length == 2) {
            DoubleStyleValue topBottom = (DoubleStyleValue) super.parse(parts[0], workingDirectory);
            DoubleStyleValue leftRight = (DoubleStyleValue) super.parse(parts[1], workingDirectory);

            top = topBottom;
            bottom = topBottom;
            left = leftRight;
            right = leftRight;
        } else if (parts.length == 4) {
            top = (DoubleStyleValue) super.parse(parts[0], workingDirectory);
            right = (DoubleStyleValue) super.parse(parts[1], workingDirectory);
            bottom = (DoubleStyleValue) super.parse(parts[2], workingDirectory);
            left = (DoubleStyleValue) super.parse(parts[3], workingDirectory);
        } else {
            throw new StyleValueParseException(String.format(
                    "A insets value ('margin', 'padding', ...) must be in either of the following formats:\n" +
                            "margin: 5mm; // Applying margin on all four sides\n" +
                            "margin: 5mm 2mm; // Applying 5mm top and bottom, 2mm left and right\n" +
                            "margin: 1mm 2mm 3mm 4mm; // Applying 1mm top, 2mm right, 3mm bottom, 4mm left\n" +
                            "Instead got: '%s'",
                    src
            ));
        }

        return new StyleValueCollection(Map.ofEntries(
                Map.entry(typesToSet[0], top),
                Map.entry(typesToSet[1], right),
                Map.entry(typesToSet[2], bottom),
                Map.entry(typesToSet[3], left)
        ));
    }

}
