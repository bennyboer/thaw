package de.be.thaw.math.mathml.parser.impl;

import de.be.thaw.math.mathml.parser.MathMLParser;
import de.be.thaw.math.mathml.parser.MathMLParserConfig;
import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.context.MathMLParseContext;
import de.be.thaw.math.mathml.parser.impl.handler.MathMLNodeParseHandler;
import de.be.thaw.math.mathml.parser.impl.handler.MathMLNodeParseHandlers;
import de.be.thaw.math.mathml.tree.MathMLTree;
import de.be.thaw.math.mathml.tree.node.MathMLNode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

/**
 * Default implementation of the MathML parser.
 */
public class DefaultMathMLParser implements MathMLParser {

    @Override
    public MathMLTree parse(InputStream stream, Charset charset, MathMLParserConfig config) throws ParseException {
        try {
            Document document = Jsoup.parse(stream, charset.name(), "");

            Element bodyElement = document.body();
            if (bodyElement.childrenSize() != 1) {
                throw new ParseException("Expected exactly 1 <math> element as root node of the MathML source text");
            }

            Element rootElement = bodyElement.children().get(0);
            if (!rootElement.nodeName().equals("math")) {
                throw new ParseException("Expected the root node to have the name 'math'");
            }

            MathMLParseContext ctx = new MathMLParseContext(config);

            MathMLNodeParseHandler parseHandler = MathMLNodeParseHandlers.getParseHandler(rootElement.nodeName());
            MathMLNode root = parseHandler.parse(rootElement, ctx);

            return new MathMLTree(root);
        } catch (IOException e) {
            throw new ParseException(e);
        }
    }

}
