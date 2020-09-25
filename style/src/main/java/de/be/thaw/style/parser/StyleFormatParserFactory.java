package de.be.thaw.style.parser;

import de.be.thaw.style.parser.impl.DefaultStyleFormatParser;

import java.util.function.Supplier;

/**
 * Factory providing style format parsers.
 */
public class StyleFormatParserFactory {

    /**
     * Supplier producing parser instances.
     */
    private Supplier<StyleFormatParser> parserSupplier = DefaultStyleFormatParser::new;

    private StyleFormatParserFactory() {
        // Private constructor to prevent instantiation
    }

    /**
     * Get the factory instance.
     *
     * @return factory
     */
    public static StyleFormatParserFactory getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * Get a parser instance.
     *
     * @return parser instance
     */
    public StyleFormatParser getParser() {
        return parserSupplier.get();
    }

    /**
     * Set the parser supplier that will produce future parser instances.
     *
     * @param parserSupplier to set
     */
    public void setParserSupplier(Supplier<StyleFormatParser> parserSupplier) {
        this.parserSupplier = parserSupplier;
    }

    /**
     * Holder of the singleton instance.
     */
    private static final class InstanceHolder {

        /**
         * Instance of the singleton.
         */
        static final StyleFormatParserFactory INSTANCE = new StyleFormatParserFactory();

    }

}
