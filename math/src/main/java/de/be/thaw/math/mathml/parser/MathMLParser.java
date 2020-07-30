package de.be.thaw.math.mathml.parser;

import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.tree.MathMLTree;

import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Parser for a MathML input.
 */
public interface MathMLParser {

    /**
     * Parse the MathML source text given by the passed stream.
     *
     * @param stream  to get MathML source text from
     * @param charset to use when parsing
     * @param config  to use during parsing
     * @return the parsed MathML tree
     * @throws ParseException in case the MathML tree could not be parsed
     */
    MathMLTree parse(InputStream stream, Charset charset, MathMLParserConfig config) throws ParseException;

}
