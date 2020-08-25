package de.be.thaw.math.mathml.parser;

/**
 * Configuration for the MathML parser.
 */
public class MathMLParserConfig {

    /**
     * The default line thickness to use.
     * For example for a fraction line.
     */
    private final double defaultLineThickness;

    public MathMLParserConfig(double defaultLineThickness) {
        this.defaultLineThickness = defaultLineThickness;
    }

    /**
     * Get the default line thickness to use.
     * For example for a fraction line.
     *
     * @return default line thickness
     */
    public double getDefaultLineThickness() {
        return defaultLineThickness;
    }

}
