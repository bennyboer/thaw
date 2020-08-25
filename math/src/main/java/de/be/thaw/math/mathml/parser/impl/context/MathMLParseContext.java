package de.be.thaw.math.mathml.parser.impl.context;

import de.be.thaw.math.mathml.parser.MathMLParserConfig;

/**
 * Context used during MathML parsing.
 */
public class MathMLParseContext {

    /**
     * Configuration used during parsing.
     */
    private final MathMLParserConfig config;

    public MathMLParseContext(MathMLParserConfig config) {
        this.config = config;
    }

    /**
     * Get the configuration used during parsing.
     *
     * @return configuration
     */
    public MathMLParserConfig getConfig() {
        return config;
    }

}
