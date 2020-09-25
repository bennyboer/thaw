package de.be.thaw.style.parser.lexer;

import de.be.thaw.style.parser.lexer.impl.DefaultStyleFormatLexer;

import java.util.function.Supplier;

/**
 * Factory providing style format lexers.
 */
public class StyleFormatLexerFactory {

    /**
     * Supplier producing lexer instances.
     */
    private Supplier<StyleFormatLexer> lexerSupplier = DefaultStyleFormatLexer::new;

    private StyleFormatLexerFactory() {
        // Private constructor to prevent instantiation
    }

    /**
     * Get the factory instance.
     *
     * @return factory
     */
    public static StyleFormatLexerFactory getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * Get a lexer instance to use.
     *
     * @return lexer
     */
    public StyleFormatLexer getLexer() {
        return lexerSupplier.get();
    }

    /**
     * Set the lexer supplier that will produce future lexer instances.
     *
     * @param lexerSupplier to set
     */
    public void setLexerSupplier(Supplier<StyleFormatLexer> lexerSupplier) {
        this.lexerSupplier = lexerSupplier;
    }

    /**
     * Holder of the singleton instance.
     */
    private static final class InstanceHolder {

        /**
         * Instance of the singleton.
         */
        static final StyleFormatLexerFactory INSTANCE = new StyleFormatLexerFactory();

    }

}
